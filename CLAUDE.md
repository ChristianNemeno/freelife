# FreeLife — Claude Code Notes

## Project Structure
- `web/` — React + TypeScript + Vite frontend (pure CSS, no Tailwind)
- `backend/` — .NET 10 API
- `android-app/` — Android app
- `docker-compose.yml` — orchestrates frontend, backend, db (postgres), caddy
- `.github/workflows/` — CI (build check) → Deploy (SSH into GCP VM, git pull, docker compose up)

## Deployment Flow
1. Push to `main` → CI runs (`ci.yml`)
2. CI passes → Deploy runs (`deploy.yml`) — SSHs into GCP VM, does `git pull` + `docker compose up --build -d`
3. VM reads secrets from `~/freelife/.env` (gitignored, manually maintained on VM)

## Environment Variables
- Vite env vars must be prefixed `VITE_` and are **baked into the JS bundle at build time**
- For CI builds: add as GitHub Secret, reference in `ci.yml` via `env: VITE_FOO: ${{ secrets.VITE_FOO }}`
- For Docker builds: add `ARG VITE_FOO` + `ENV VITE_FOO=$VITE_FOO` in `web/Dockerfile`, add `VITE_FOO: ${VITE_FOO}` under `build.args` in `docker-compose.yml`, and add the value to `~/freelife/.env` on the VM
- Always verify with `sudo docker compose --env-file .env config | grep FOO` before rebuilding

## Known Gotchas
- `git pull` on the VM can fail if Docker left behind a modified `web/package-lock.json` — fix with `git checkout web/package-lock.json` then `git pull`
- `sudo docker compose` may not resolve `.env` automatically — always pass `--env-file .env` explicitly when running as sudo
- Docker layer cache can serve stale builds — use `--no-cache` flag: `sudo docker compose --env-file .env build --no-cache frontend`
- The VM only gets new commits if the deploy workflow ran successfully — if CI failed or deploy was skipped, manually run `git pull` on the VM

## Google Maps 3D
- Uses `@vis.gl/react-google-maps`
- Requires a real **Map ID** with **Vector rendering** enabled (created in Google Cloud Console → Map Styles)
- `defaultTilt={45}` only shows 3D buildings at zoom ~14+ in cities with 3D data
- API key is client-side (visible in bundle) — restrict it in GCP Console to your domain + Maps JavaScript API only
- Map ID `e697ea99deb4aaf99897e227` is the current vector map ID

## Tech Stack (Frontend)
- React 18 + TypeScript + Vite
- `@vis.gl/react-google-maps` for maps (replaced Leaflet)
- SignalR (`@microsoft/signalr`) for real-time location + chat
- Pure CSS with glassmorphism design system (dark, `#05080e` bg, `#38bdf8` accent)
- No Tailwind, no CSS framework
