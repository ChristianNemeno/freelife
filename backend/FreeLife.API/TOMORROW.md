# 📋 Tomorrow's Plan — FreeLife

## Where You Left Off
Phases 0–5 are fully implemented and working locally.
Phase 6 guide has been updated to use **Compute Engine** instead of Cloud Run.

---

## What to Do Tomorrow — Phase 6 (Deploy to GCP)

### Step 1 — Make sure everything works locally first
```bash
cd C:\Nemeno\freelife
docker compose up --build
```
- Open `http://localhost:8080/swagger` — confirm it loads
- Test register + login + create group in Swagger or the app

---

### Step 2 — Create the Compute Engine VM
```bash
gcloud config set project YOUR_PROJECT_ID
gcloud config set compute/zone asia-southeast1-b

gcloud compute instances create freelife-vm \
  --machine-type=e2-micro \
  --zone=asia-southeast1-b \
  --image-family=ubuntu-2204-lts \
  --image-project=ubuntu-os-cloud \
  --boot-disk-size=30GB \
  --tags=http-server,https-server
```
> Note the **External IP** — you'll need it for the Android app

---

### Step 3 — Open firewall port 8080
```bash
gcloud compute firewall-rules create allow-backend \
  --allow tcp:8080 \
  --target-tags=http-server
```

---

### Step 4 — SSH into the VM and install Docker
```bash
gcloud compute ssh freelife-vm --zone=asia-southeast1-b
```
Then inside the VM:
```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
newgrp docker
sudo apt-get install -y docker-compose-plugin docker-sdk-8.0
```

---

### Step 5 — Clone your repo on the VM
```bash
git clone https://github.com/YOUR_USERNAME/usertrack.git
cd usertrack
```

---

### Step 6 — Create the .env file on the VM
```bash
nano .env
```
Paste:
```
POSTGRES_DB=freelifedb
POSTGRES_USER=postgres
POSTGRES_PASSWORD=SomeStrongPassword123!
JWT_KEY=your-production-jwt-key-at-least-32-characters!
```

---

### Step 7 — Start the containers
```bash
docker compose up --build -d
docker compose ps   # both db and backend should show "running"
```

---

### Step 8 — Apply database migrations
```bash
sudo apt-get install -y dotnet-sdk-8.0
cd ~/usertrack/backend/FreeLife.API
dotnet ef database update \
  --connection "Host=localhost;Port=5432;Database=freelifedb;Username=postgres;Password=SomeStrongPassword123!"
```

---

### Step 9 — Test the live API
Open in browser:
```
http://YOUR_VM_EXTERNAL_IP:8080/swagger
```
Should show Swagger UI ✅

---

### Step 10 — Update Android app URL
In `network/RetrofitClient.kt`:
```kotlin
private const val BASE_URL = "http://YOUR_VM_EXTERNAL_IP:8080/api/"
```
In `network/LocationHubClient.kt`:
```kotlin
.create("http://YOUR_VM_EXTERNAL_IP:8080/locationHub")
```
Build and test on a real device or emulator.

---

### Step 11 — Set up GitHub Actions (optional, do last)
Follow section **6.12** in `FreeLife_Complete_Todo_Guide.md` — auto-deploy on every git push.

---

## If You Get Stuck
- Docker not starting → `docker compose logs backend -f`
- Can't connect to Swagger → check firewall rule and confirm port 8080 is open
- Migration fails → make sure `docker compose ps` shows `db` is healthy first
- Android can't connect → use the VM's **External IP**, not localhost

---

## Full guide reference
`FreeLife.API/FreeLife_Complete_Todo_Guide.md` → Phase 6

Good luck! 🚀
