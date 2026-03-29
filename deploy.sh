#!/usr/bin/env bash
# =============================================================================
# deploy.sh — FreeLife GCP Compute Engine deployment
#
# USAGE:
#   1. Copy deploy.env.example → deploy.env and fill in your values
#   2. bash deploy.sh
# =============================================================================

set -euo pipefail

# ── Load config ───────────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${SCRIPT_DIR}/deploy.env"

if [[ ! -f "${ENV_FILE}" ]]; then
    echo "[ERROR] deploy.env not found."
    echo "        cp deploy.env.example deploy.env"
    exit 1
fi

# shellcheck source=/dev/null
source "${ENV_FILE}"

# Validate required values
: "${GCP_PROJECT:?  Set GCP_PROJECT in deploy.env}"
: "${GCP_ZONE:?     Set GCP_ZONE in deploy.env}"
: "${VM_NAME:?      Set VM_NAME in deploy.env}"
: "${REUSE_VM:?     Set REUSE_VM in deploy.env (true or false)}"
: "${GITHUB_REPO:?  Set GITHUB_REPO in deploy.env}"
: "${POSTGRES_PASSWORD:?  Set POSTGRES_PASSWORD in deploy.env}"
: "${JWT_KEY:?      Set JWT_KEY in deploy.env}"

if [[ "${GITHUB_REPO}" == *"YOUR_USERNAME"* ]]; then
    echo "[ERROR] GITHUB_REPO still has the placeholder value. Update deploy.env."
    exit 1
fi
if [[ ${#JWT_KEY} -lt 32 ]]; then
    echo "[ERROR] JWT_KEY must be at least 32 characters."
    exit 1
fi

# ── Colours ───────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
info()  { echo -e "${GREEN}[INFO]${NC}  $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
step()  { echo -e "${CYAN}[STEP]${NC}  $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*"; exit 1; }

echo ""
info "Project  : ${GCP_PROJECT}"
info "Zone     : ${GCP_ZONE}"
info "VM       : ${VM_NAME}  (reuse=${REUSE_VM})"
info "Repo     : ${GITHUB_REPO}"
echo ""

# ── Step 1: VM ────────────────────────────────────────────────────────────────
step "1/5 — VM"
if [[ "${REUSE_VM}" == "true" ]]; then
    warn "REUSE_VM=true — skipping VM creation."
    gcloud compute instances describe "${VM_NAME}" \
        --zone="${GCP_ZONE}" --project="${GCP_PROJECT}" &>/dev/null \
        || error "VM '${VM_NAME}' not found in zone ${GCP_ZONE}."
    info "Found existing VM '${VM_NAME}'."
else
    if gcloud compute instances describe "${VM_NAME}" \
        --zone="${GCP_ZONE}" --project="${GCP_PROJECT}" &>/dev/null; then
        warn "VM '${VM_NAME}' already exists — skipping creation."
    else
        info "Creating VM '${VM_NAME}'..."
        gcloud compute instances create "${VM_NAME}" \
            --machine-type="e2-micro" \
            --zone="${GCP_ZONE}" \
            --project="${GCP_PROJECT}" \
            --image-family=ubuntu-2204-lts \
            --image-project=ubuntu-os-cloud \
            --boot-disk-size="30GB" \
            --tags=http-server,https-server
        info "VM created. Waiting 20s for SSH to become available..."
        sleep 20
    fi
fi

# ── Step 2: Firewall ──────────────────────────────────────────────────────────
step "2/5 — Firewall (ports 80 + 443)"
if gcloud compute firewall-rules describe "allow-freelife-http" \
    --project="${GCP_PROJECT}" &>/dev/null; then
    warn "Firewall rule 'allow-freelife-http' already exists — skipping."
else
    gcloud compute firewall-rules create "allow-freelife-http" \
        --project="${GCP_PROJECT}" \
        --allow tcp:80,tcp:443,udp:443 \
        --target-tags=http-server \
        --description="FreeLife HTTP/HTTPS via Caddy"
    info "Firewall rule created."
fi

# ── Step 3: External IP ───────────────────────────────────────────────────────
step "3/5 — External IP"
EXTERNAL_IP=$(gcloud compute instances describe "${VM_NAME}" \
    --zone="${GCP_ZONE}" --project="${GCP_PROJECT}" \
    --format="get(networkInterfaces[0].accessConfigs[0].natIP)")
info "External IP: ${EXTERNAL_IP}"

# ── Step 4: Bootstrap VM ──────────────────────────────────────────────────────
# Write the bootstrap script to a temp file locally, scp it to the VM,
# then execute it. This avoids heredoc piping issues on Windows/Git Bash.
step "4/5 — Bootstrap VM (may take 3–5 min on first run)"

BOOTSTRAP_FILE="$(mktemp /tmp/freelife-bootstrap.XXXXXX.sh)"

cat > "${BOOTSTRAP_FILE}" << BOOTSTRAP
#!/usr/bin/env bash
set -euo pipefail

# a) Docker
if ! command -v docker &>/dev/null; then
    echo "[VM] Installing Docker..."
    curl -fsSL https://get.docker.com | sh
    sudo usermod -aG docker \$USER
else
    echo "[VM] Docker already installed — skipping."
fi

# b) Docker Compose plugin
if ! docker compose version &>/dev/null 2>&1; then
    echo "[VM] Installing Docker Compose plugin..."
    sudo apt-get update -qq
    sudo apt-get install -y docker-compose-plugin
else
    echo "[VM] Docker Compose plugin already installed — skipping."
fi

# c) Clone or pull repo
REPO_DIR="\$HOME/freelife"
if [ -d "\$REPO_DIR/.git" ]; then
    echo "[VM] Repo exists — pulling latest..."
    git -C "\$REPO_DIR" pull
else
    echo "[VM] Cloning repo..."
    git clone "${GITHUB_REPO}" "\$REPO_DIR"
fi
cd "\$REPO_DIR"

# d) Write .env — overwritten every deploy so secrets stay current
echo "[VM] Writing .env..."
printf 'POSTGRES_DB=freelifedb\nPOSTGRES_USER=postgres\nPOSTGRES_PASSWORD=${POSTGRES_PASSWORD}\nJWT_KEY=${JWT_KEY}\nASPNETCORE_ENVIRONMENT=Development\n' > .env

# e) Build and start containers (frontend built inside Docker via web/Dockerfile)
echo "[VM] Building and starting containers..."
sudo docker compose up --build -d

echo "[VM] Done. Migrations will run automatically when the backend container starts."
BOOTSTRAP

# Copy the script to the VM then execute it
info "Copying bootstrap script to VM..."
gcloud compute scp "${BOOTSTRAP_FILE}" "${VM_NAME}:/tmp/freelife-bootstrap.sh" \
    --zone="${GCP_ZONE}" --project="${GCP_PROJECT}"

rm -f "${BOOTSTRAP_FILE}"

info "Running bootstrap script on VM..."
gcloud compute ssh "${VM_NAME}" \
    --zone="${GCP_ZONE}" \
    --project="${GCP_PROJECT}" \
    --command="bash /tmp/freelife-bootstrap.sh && rm /tmp/freelife-bootstrap.sh"

# ── Step 5: Summary ───────────────────────────────────────────────────────────
step "5/5 — Done"
echo ""
echo -e "${GREEN}══════════════════════════════════════════════════════════${NC}"
echo -e "${GREEN}  FreeLife deployed!${NC}"
echo -e "${GREEN}══════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "  Web       →  ${YELLOW}https://nenome.online${NC}"
echo -e "  Swagger   →  ${YELLOW}https://nenome.online/swagger${NC}"
echo -e "  API       →  ${YELLOW}https://nenome.online/api/${NC}"
echo -e "  SignalR   →  ${YELLOW}https://nenome.online/locationHub${NC}"
echo ""
echo "  Update Android app to use HTTPS:"
echo "    RetrofitClient.kt    →  BASE_URL = \"https://nenome.online/api/\""
echo "    LocationHubClient.kt →  .create(\"https://nenome.online/locationHub\")"
echo ""
echo "  Useful commands:"
echo "    Logs    : gcloud compute ssh ${VM_NAME} --zone=${GCP_ZONE} --project=${GCP_PROJECT} --command='cd ~/freelife && sudo docker compose logs -f backend'"
echo "    SSH in  : gcloud compute ssh ${VM_NAME} --zone=${GCP_ZONE} --project=${GCP_PROJECT}"
echo "    Redeploy: bash deploy.sh"
echo -e "${GREEN}══════════════════════════════════════════════════════════${NC}"
