# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **UserTrack** — a real-time location tracking Android app. The Android project lives in this directory (`android-app/`). The full system also includes a C# ASP.NET Core backend, PostgreSQL, SignalR for real-time updates, and Google Cloud deployment — all defined in the sibling `docs/` folder and tracked in `app/UserTrack_Complete_Todo_Guide.md`.

**Current state:** Early Phase 1. The app is the default Android Studio scaffold with all Phase 1 dependencies added and Maps API key wired up. No screens or ViewModels exist yet.

## Build & Run Commands

All Gradle commands must be run from this directory (`android-app/`).

```bash
# Build debug APK
./gradlew assembleDebug

# Install and run on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.freelife.app.ExampleUnitTest"

# Run instrumented tests (requires running emulator)
./gradlew connectedAndroidTest

# Lint check
./gradlew lint

# Clean build
./gradlew clean assembleDebug
```

## Maps API Key Setup

The Maps API key is **not committed to git** and must be set in `local.properties` (git-ignored):

```
MAPS_API_KEY=your_key_here
```

It flows into the app via `manifestPlaceholders["MAPS_API_KEY"]` in `app/build.gradle.kts` and is read by `AndroidManifest.xml` as `${MAPS_API_KEY}`.

## Architecture

The app uses **Kotlin + Jetpack Compose** with a planned MVVM architecture. Packages to be created under `com.freelife.app`:

| Package | Purpose |
|---------|---------|
| `ui/` | Compose screens (Login, Register, Home/Map) |
| `ui/theme/` | Material3 theming (already exists) |
| `viewmodel/` | ViewModels per screen |
| `repository/` | Data access — wraps network + local data |
| `network/` | Retrofit API client + SignalR hub connection |
| `model/` | Data classes (User, Group, Location, etc.) |
| `service/` | Background `ForegroundService` for location updates |

Navigation is handled by **Navigation Compose** with a `sealed class Screen` defining routes.

## Key Dependencies

| Dependency | Purpose |
|-----------|---------|
| `maps-compose:4.3.0` + `play-services-maps:18.2.0` | Google Maps in Compose |
| `play-services-location:21.3.0` | Fused Location Provider |
| `navigation-compose:2.7.7` | Screen navigation |
| `lifecycle-viewmodel-compose:2.8.0` | ViewModel scoped to Compose |
| `retrofit2:2.9.0` + `converter-gson` | REST API calls to C# backend |
| `okhttp3:logging-interceptor` | HTTP request logging (debug) |
| `microsoft/signalr:8.0.0` | Real-time WebSocket connection to backend |
| `kotlinx-coroutines-android:1.8.0` | Async/coroutine support |

## Tech Stack Context

- **Min SDK 26** (Android 8.0) — safe to use all modern Jetpack APIs
- **Target SDK 36** — edge-to-edge display enabled in `MainActivity`
- **AGP 9.1.0 / Kotlin 2.2.10** — use version catalog (`gradle/libs.versions.toml`) for managed deps; add raw strings for unmanaged ones
- **Compose BOM `2024.09.00`** — do not specify individual Compose library versions; let BOM manage them
- Backend communicates over REST (Retrofit) for auth/data and SignalR WebSockets for live location streaming

## Multi-Agent Workflow

This project uses **three agents in combination**:

| Agent | Role |
|-------|------|
| **Claude Code** (you) | Senior developer — architecture decisions, code review, fixes |
| **Codex CLI** | Code generation for boilerplate and repetitive tasks |
| **GitHub Copilot** | Inline completions and quick implementations |

**Your role as senior developer:**
- Treat all code from Codex CLI and Copilot as junior developer output — it must be reviewed before committing
- Enforce the MVVM package structure strictly: no business logic in Composables, no direct network calls from ViewModels (go through Repository), no state exposed as `var` from ViewModels
- Prefer Kotlin idioms: `data class`, `sealed class`, `Flow`, `StateFlow`, extension functions over utility classes
- Flag any Compose anti-patterns: missing `key` in lists, side effects outside `LaunchedEffect`/`SideEffect`, improper state hoisting

**Workflow convention:**
1. Codex CLI or Copilot implements a feature
2. Run `/review` — Claude reviews all uncommitted changes as a senior dev
3. Fix any 🔴 Critical or 🟡 Warning issues
4. Commit only after `/review` gives ✅ LGTM or ⚠️ Minor fixes verdict
