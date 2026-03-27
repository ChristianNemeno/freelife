# 📍 UserTrack — Complete Development To-Do Guide

> Real-Time Location Tracking App | Android Studio Panda · C# ASP.NET Core · Docker · Google Cloud

---

## 🗺️ How to Use This Guide

Each phase has its own markdown file with step-by-step tasks you can check off as you go.
Work through the phases **in order** — each one builds on the last.

---

## 📋 Phase Overview

| Phase | File | Focus | Est. Time |
|-------|------|--------|-----------|
| ⚙️ Phase 0 | [00-setup.md](./00-setup.md) | Install all tools & accounts | Week 1 |
| 🗺️ Phase 1 | [01-android-maps.md](./01-android-maps.md) | Android app + Google Maps | Week 1–2 |
| 🖥️ Phase 2 | [02-backend-auth.md](./02-backend-auth.md) | C# backend + auth + Docker locally | Week 3–4 |
| 🔗 Phase 3 | [03-connect-app.md](./03-connect-app.md) | Connect Android app to backend API | Week 5–6 |
| ⚡ Phase 4 | [04-realtime-signalr.md](./04-realtime-signalr.md) | Real-time location via SignalR | Week 7–8 |
| 👥 Phase 5 | [05-groups.md](./05-groups.md) | Groups, invite codes, member map | Week 9–10 |
| ☁️ Phase 6 | [06-gcp-deploy.md](./06-gcp-deploy.md) | Docker → Google Compute Engine VM | Week 11–12 |
| ✨ Phase 7 | [07-polish.md](./07-polish.md) | Notifications, history, UI polish | Week 13+ |

---

## ✅ Overall Progress Tracker

- [x] Phase 0 — Environment Setup
- [ ] Phase 1 — Android App & Maps
- [ ] Phase 2 — Backend & Docker Local
- [ ] Phase 3 — App Connected to Backend
- [ ] Phase 4 — Real-Time Location Working
- [ ] Phase 5 — Groups Feature Complete
- [ ] Phase 6 — Deployed to Google Cloud
- [ ] Phase 7 — Polish & Play Store Ready

---

## 🧰 Your Tech Stack (Quick Reference)

| Layer | Technology |
|-------|-----------|
| IDE | Android Studio Panda 2 (2025.3.2) |
| Mobile | Kotlin + Jetpack Compose (API 35 target) |
| Maps | Google Maps SDK for Android |
| Backend | C# ASP.NET Core Web API (.NET 8) |
| Real-Time | SignalR WebSockets |
| Database | PostgreSQL 16 |
| Containers | Docker + Docker Compose |
| Cloud | Google Cloud (Artifact Registry + Cloud Run + Cloud SQL) |
| CI/CD | GitHub Actions |

---

## 💡 Tips Before You Start

- **Use Claude Code for help at every step** — paste errors or code snippets and ask for help
- **Test locally first** — always get things working on your machine before touching Google Cloud
- **Commit to Git often** — after every working feature, run `git commit`
- **Keep Docker running** — use `docker compose up -d` and leave it in the background

---

*Start with [00-setup.md](./00-setup.md) →*


---

# ⚙️ Phase 0 — Environment Setup

> **Goal:** Get every tool installed and every account created before writing a single line of app code.

---

## 0.1 Install Core Tools

- [x] Download and install **Android Studio Panda 2 (2025.3.2)** from [developer.android.com/studio](https://developer.android.com/studio)
- [x] During Android Studio setup, install:
  - [x] Android SDK (API 35 — Android 15)
  - [x] Android Emulator
  - [x] Android SDK Platform-Tools
- [x] Download and install **.NET 8 SDK** from [dot.net/download](https://dot.net/download)
  - [x] Verify install: open terminal and run `dotnet --version` (should show `8.x.x`)
- [x] Download and install **Visual Studio 2022 Community** (free) from [visualstudio.microsoft.com](https://visualstudio.microsoft.com)
  - [x] During install, select workload: **ASP.NET and web development**
  - [x] Alternative: install **VS Code** + C# Dev Kit extension (lighter option)
- [x] Download and install **Docker Desktop** from [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)
  - [x] Open Docker Desktop and wait for it to show "Engine running"
  - [x] Verify: run `docker --version` and `docker compose version` in terminal
- [x] Install **Git** from [git-scm.com](https://git-scm.com)
  - [x] Verify: run `git --version`
- [x] Install **Postman** from [postman.com](https://www.postman.com) (for testing your API)

---

## 0.2 Create Required Accounts

- [x] **Google Cloud Console** — [console.cloud.google.com](https://console.cloud.google.com)
  - [x] Create a new project called `usertrack`
  - [x] Enable the **Maps SDK for Android** API
  - [x] Enable the **Geocoding API**
  - [x] Go to Credentials → Create API Key → copy and save it somewhere safe
- [x] **Google Cloud (Hosting)** — [console.cloud.google.com](https://console.cloud.google.com)
  - [x] Enable billing on your GCP project (requires a credit card — free tier won't charge for small usage)
  - [x] Install **gcloud CLI**: follow [cloud.google.com/sdk/docs/install](https://cloud.google.com/sdk/docs/install)
  - [x] Run `gcloud init` and sign in with your Google account
  - [x] Verify: `gcloud --version`
- [x] **GitHub** — [github.com](https://github.com)
  - [x] Create a free account if you don't have one

---

## 0.3 Set Up Your Project Repository

- [x] Create a new GitHub repository called `usertrack`
  - [x] Set it to **Private**
  - [x] Initialize with a README
- [x] Clone it to your machine:
  ```bash
  git clone https://github.com/YOUR_USERNAME/usertrack.git
  cd usertrack
  ```
- [x] Create the base folder structure:
  ```bash
  mkdir android-app backend docs .github
  mkdir .github/workflows
  ```
- [x] Create a `.gitignore` file in the root with these entries:
  ```
  # Android
  *.iml
  .gradle/
  local.properties
  android-app/.idea/
  android-app/build/

  # C# / .NET
  backend/bin/
  backend/obj/
  backend/*.user

  # Docker / Environment
  .env
  .env.local
  docker-compose.override.yml

  # General
  .DS_Store
  Thumbs.db
  ```
- [x] Commit and push:
  ```bash
  git add .
  git commit -m "Initial project structure"
  git push
  ```

---

## 0.4 Set Up an Android Emulator

- [x] Open Android Studio → Device Manager (right toolbar)
- [x] Click **Create Virtual Device**
- [x] Select: **Pixel 7** (good mid-range test device)
- [x] Select system image: **API 35 (Android 15)** — download it if needed
- [x] Click Finish, then click ▶ to start the emulator
- [x] Confirm the emulator boots to the Android home screen

---

## 0.5 Verify Everything Works

Run these checks before moving on:

- [x] `dotnet --version` → shows `8.x.x`
- [x] `docker --version` → shows Docker version
- [x] `docker compose version` → shows Compose version
- [x] `git --version` → shows Git version
- [x] `gcloud --version` → shows Google Cloud CLI version
- [x] Android Studio emulator boots successfully
- [x] Docker Desktop shows "Engine running"

---

## ✅ Phase 0 Complete!

**All tools installed. All accounts created. Repository set up.**

➡️ Next: [Phase 1 — Android App & Google Maps](./01-android-maps.md)


---

# 🗺️ Phase 1 — Android App & Google Maps

> **Goal:** Create the Android project in Android Studio Panda, set up navigation between screens, and display a working Google Map with a pinned location.

---

## 1.1 Create the Android Project

- [x] Open **Android Studio Panda 2**
- [x] Click **New Project → Empty Activity**
- [x] Fill in the project settings:
  - Name: `UserTrack`
  - Package name: `com.usertrack.app`
  - Save location: inside your `usertrack/android-app/` folder
  - Language: **Kotlin**
  - Minimum SDK: **API 26 (Android 8.0)**
  - Build configuration: **Kotlin DSL** (recommended in Panda)
- [x] Click **Finish** and wait for Gradle sync to complete
- [x] Run the default app on your emulator to confirm it works (should show "Hello Android!")

---

## 1.2 Add Dependencies to build.gradle.kts

- [x] Open `app/build.gradle.kts`
- [x] Add the following inside the `dependencies { }` block:

```kotlin
// Jetpack Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.09.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose:1.9.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// Google Maps
implementation("com.google.maps.android:maps-compose:4.3.0")
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.3.0")

// ViewModel + Coroutines
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

// Networking (for later phases)
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// SignalR (for later phases)
implementation("com.microsoft.signalr:signalr:8.0.0")
```

- [x] Click **Sync Now** when prompted — wait for Gradle sync to finish

---

## 1.3 Add Google Maps API Key

- [x] Open (or create) `local.properties` in the root of your Android project
- [x] Add this line (use the key from Google Cloud Console):
  ```
  MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY_HERE
  ```
- [x] Open `app/build.gradle.kts` and add inside `android { defaultConfig { ... } }`:
  ```kotlin
  manifestPlaceholders["MAPS_API_KEY"] = project.properties["MAPS_API_KEY"] ?: ""
  ```
- [x] Open `AndroidManifest.xml` and add inside `<application>`:
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="${MAPS_API_KEY}" />
  ```
- [x] **Important:** Make sure `local.properties` is listed in `.gitignore` — never commit API keys

---

## 1.4 Create the Folder Structure

- [x] Inside `java/com/freelife/app/`, create these packages (right-click → New → Package):
  - `ui` — Compose screens
  - `ui.theme` — app theming
  - `viewmodel` — ViewModels
  - `repository` — data access layer
  - `network` — Retrofit + SignalR
  - `model` — data classes
  - `service` — background location service

---

## 1.5 Set Up Navigation

- [x] Create `ui/Navigation.kt` with your screen routes:

```kotlin
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Map : Screen("map/{groupId}") {
        fun createRoute(groupId: Int) = "map/$groupId"
    }
    object Group : Screen("group/{groupId}") {
        fun createRoute(groupId: Int) = "group/$groupId"
    }
    object Settings : Screen("settings")
}
```

- [x] Update `MainActivity.kt` to use a `NavHost`:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserTrackTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) { LoginScreen(navController) }
                    composable(Screen.Register.route) { RegisterScreen(navController) }
                    composable(Screen.Home.route) { HomeScreen(navController) }
                    composable(Screen.Map.route) { MapScreen(navController) }
                    composable(Screen.Settings.route) { SettingsScreen(navController) }
                }
            }
        }
    }
}
```

---

## 1.6 Create Placeholder Screens

Create a basic composable for each screen so navigation compiles:

- [x] Create `ui/LoginScreen.kt`
- [x] Create `ui/RegisterScreen.kt`
- [x] Create `ui/HomeScreen.kt`
- [x] Create `ui/MapScreen.kt`
- [x] Create `ui/SettingsScreen.kt`

Each file should start like this (replace name accordingly):

```kotlin
@Composable
fun LoginScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Login Screen")
    }
}
```

- [x] Build and run — confirm the app launches to the Login placeholder screen

---

## 1.7 Build the Map Screen

- [x] Update `MapScreen.kt` to show a real Google Map:

```kotlin
@Composable
fun MapScreen(navController: NavController) {
    val cebuCity = LatLng(10.3157, 123.8854) // Cebu City coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cebuCity, 14f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = cebuCity),
            title = "My Location",
            snippet = "You are here"
        )
    }
}
```

- [x] Temporarily set `startDestination = Screen.Map.route` in MainActivity to test the map
- [x] Run on emulator — you should see a Google Map centered on a pin
- [x] Change `startDestination` back to `Screen.Login.route` when done

---

## 1.8 Request Location Permissions

- [x] Add permissions to `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.INTERNET" />
```

- [x] Create a helper composable in `ui/LocationPermission.kt` to request permissions at runtime:

```kotlin
@Composable
fun RequestLocationPermission(onGranted: @Composable () -> Unit) {
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) { permissionState.launchMultiplePermissionRequest() }
    if (permissionState.allPermissionsGranted) {
        onGranted()
    } else {
        Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Location permission is required to use UserTrack.")
        }
    }
}
```

- [x] Wrap your MapScreen content with `RequestLocationPermission { ... }`
- [x] Test on emulator — accept the permission dialog

---

## 1.9 Build the Login Screen UI

- [x] Update `LoginScreen.kt` with a real login form UI:
  - [x] Email TextField
  - [x] Password TextField (with hidden text)
  - [x] "Login" Button (navigates to HomeScreen for now)
  - [x] "Register" TextButton (navigates to RegisterScreen)
- [x] Build the Register screen with:
  - [x] Name TextField
  - [x] Email TextField
  - [x] Password TextField
  - [x] "Create Account" Button

> 💡 These screens won't actually log in yet — that comes in Phase 3. For now just wire up the navigation buttons.

---

## 1.10 Build the Home Screen UI

- [x] Update `HomeScreen.kt` with:
  - [x] A top app bar with "UserTrack" title and settings icon
  - [x] A `LazyColumn` showing a list of mock groups (hardcoded for now):
    ```kotlin
    val mockGroups = listOf("Family", "Work Team", "Friends")
    ```
  - [x] Each group card has a name and a "View Map" button that navigates to MapScreen
  - [x] A "+" FAB (Floating Action Button) for creating a new group (placeholder for now)

---

## ✅ Phase 1 Complete!

**Checklist before moving on:**
- [x] App launches without crashing
- [x] Navigation between Login → Home → Map works
- [x] Google Map renders correctly on the emulator
- [x] Location permissions requested properly
- [x] All placeholder screens created

➡️ Next: [Phase 2 — Backend & Docker Local](./02-backend-auth.md)


---

# 🖥️ Phase 2 — Backend & Docker (Local)

> **Goal:** Build the C# ASP.NET Core backend with user auth, run it alongside PostgreSQL using Docker Compose, and test every endpoint in Postman.

---

## 2.1 Create the C# Project

- [ ] Open a terminal, navigate to your `usertrack/backend/` folder
- [ ] Create the project:
  ```bash
  dotnet new webapi -n UserTrack.API --framework net8.0
  cd UserTrack.API
  ```
- [ ] Install required NuGet packages:
  ```bash
  dotnet add package Npgsql.EntityFrameworkCore.PostgreSQL
  dotnet add package Microsoft.EntityFrameworkCore.Design
  dotnet add package Microsoft.AspNetCore.Authentication.JwtBearer
  dotnet add package Microsoft.AspNetCore.SignalR
  dotnet add package BCrypt.Net-Next
  dotnet add package Microsoft.IdentityModel.Tokens
  ```
- [ ] Open the project in Visual Studio 2022 or VS Code

---

## 2.2 Create the Data Models

Create these files inside `Models/`:

- [ ] **Models/User.cs**
  ```csharp
  public class User {
      public int Id { get; set; }
      public string Name { get; set; } = string.Empty;
      public string Email { get; set; } = string.Empty;
      public string PasswordHash { get; set; } = string.Empty;
      public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
      public ICollection<GroupMember> GroupMemberships { get; set; } = new List<GroupMember>();
  }
  ```
- [ ] **Models/Group.cs**
  ```csharp
  public class Group {
      public int Id { get; set; }
      public string Name { get; set; } = string.Empty;
      public string InviteCode { get; set; } = string.Empty;
      public int CreatedByUserId { get; set; }
      public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
      public ICollection<GroupMember> Members { get; set; } = new List<GroupMember>();
  }
  ```
- [ ] **Models/GroupMember.cs**
  ```csharp
  public class GroupMember {
      public int Id { get; set; }
      public int UserId { get; set; }
      public int GroupId { get; set; }
      public User User { get; set; } = null!;
      public Group Group { get; set; } = null!;
  }
  ```
- [ ] **Models/Location.cs**
  ```csharp
  public class Location {
      public int Id { get; set; }
      public int UserId { get; set; }
      public double Latitude { get; set; }
      public double Longitude { get; set; }
      public DateTime Timestamp { get; set; } = DateTime.UtcNow;
      public User User { get; set; } = null!;
  }
  ```

---

## 2.3 Create the Database Context

- [ ] Create `Data/AppDbContext.cs`:
  ```csharp
  public class AppDbContext : DbContext {
      public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) {}
      
      public DbSet<User> Users { get; set; }
      public DbSet<Group> Groups { get; set; }
      public DbSet<GroupMember> GroupMembers { get; set; }
      public DbSet<Location> Locations { get; set; }
      
      protected override void OnModelCreating(ModelBuilder modelBuilder) {
          modelBuilder.Entity<User>()
              .HasIndex(u => u.Email).IsUnique();
          modelBuilder.Entity<Group>()
              .HasIndex(g => g.InviteCode).IsUnique();
      }
  }
  ```

---

## 2.4 Create DTOs (Data Transfer Objects)

Create `Models/DTOs/` folder and add:

- [x] **AuthDTOs.cs**
  ```csharp
  public record RegisterRequest(string Name, string Email, string Password);
  public record LoginRequest(string Email, string Password);
  public record AuthResponse(string Token, int UserId, string Name, string Email);
  ```
- [x] **GroupDTOs.cs**
  ```csharp
  public record CreateGroupRequest(string Name);
  public record JoinGroupRequest(string InviteCode);
  public record GroupResponse(int Id, string Name, string InviteCode, int MemberCount);
  ```
- [x] **LocationDTOs.cs**
  ```csharp
  public record UpdateLocationRequest(double Latitude, double Longitude);
  public record LocationResponse(int UserId, string UserName, double Latitude, double Longitude, DateTime Timestamp);
  ```

---

## 2.5 Create the Auth Controller

- [x] Create `Controllers/AuthController.cs`:
  - [x] `POST /api/auth/register` — hash password with BCrypt, save user to DB, return JWT
  - [x] `POST /api/auth/login` — verify email/password, return JWT on success
- [x] Create `Services/TokenService.cs` to generate JWT tokens:
  ```csharp
  public class TokenService {
      private readonly IConfiguration _config;
      public TokenService(IConfiguration config) { _config = config; }
      
      public string GenerateToken(User user) {
          var key = new SymmetricSecurityKey(
              Encoding.UTF8.GetBytes(_config["Jwt__Key"]!));
          var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
          var claims = new[] {
              new Claim(ClaimTypes.NameIdentifier, user.Id.ToString()),
              new Claim(ClaimTypes.Email, user.Email),
              new Claim(ClaimTypes.Name, user.Name)
          };
          var token = new JwtSecurityToken(
              claims: claims, expires: DateTime.UtcNow.AddDays(30),
              signingCredentials: creds);
          return new JwtSecurityTokenHandler().WriteToken(token);
      }
  }
  ```

---

## 2.6 Create Groups & Location Controllers

- [x] Create `Controllers/GroupsController.cs`:
  - [x] `GET /api/groups` — get all groups for the logged-in user
  - [x] `POST /api/groups` — create a group, generate a random 6-char invite code
  - [x] `POST /api/groups/join` — join a group using an invite code
  - [x] `GET /api/groups/{id}/members` — get all members of a group
- [x] Create `Controllers/LocationController.cs`:
  - [x] `POST /api/location` — save the user's current location to DB
  - [x] `GET /api/location/{userId}` — get the last known location of a user

---

## 2.7 Configure Program.cs

- [x] Open `Program.cs` and add the following services:

```csharp
// Database
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Authentication
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options => {
        options.TokenValidationParameters = new TokenValidationParameters {
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(builder.Configuration["Jwt__Key"]!)),
            ValidateIssuer = false,
            ValidateAudience = false
        };
    });

// SignalR
builder.Services.AddSignalR();
builder.Services.AddScoped<TokenService>();

// CORS (allow Android app)
builder.Services.AddCors(options => {
    options.AddPolicy("AllowAll", policy => 
        policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});
```

- [x] After `var app = builder.Build();` add:
  ```csharp
  app.UseCors("AllowAll");
  app.UseAuthentication();
  app.UseAuthorization();
  app.MapHub<LocationHub>("/locationHub");
  ```

---

## 2.8 Create the Dockerfile

- [x] Create `backend/Dockerfile` (in the UserTrack.API folder):

```dockerfile
# Stage 1: Build
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["UserTrack.API.csproj", "."]
RUN dotnet restore
COPY . .
RUN dotnet publish -c Release -o /app/publish

# Stage 2: Run
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
COPY --from=build /app/publish .
EXPOSE 8080
ENTRYPOINT ["dotnet", "UserTrack.API.dll"]
```

- [x] Create `.dockerignore` in the same folder:
  ```
  bin/
  obj/
  *.user
  .vs/
  ```

---

## 2.9 Create docker-compose.yml

- [x] Create `docker-compose.yml` in the **root** of your project (next to `android-app/` and `backend/`):

```yaml
version: '3.9'

services:
  db:
    image: postgres:16
    restart: unless-stopped
    environment:
      POSTGRES_DB: freelifedb
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: localdevpassword123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./backend/UserTrack.API
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      - ConnectionStrings__DefaultConnection=Host=db;Port=5432;Database=freelifedb;Username=postgres;Password=localdevpassword123
      - Jwt__Key=super-secret-dev-key-at-least-32-characters-long!
      - ASPNETCORE_ENVIRONMENT=Development
      - ASPNETCORE_URLS=http://+:8080
    depends_on:
      db:
        condition: service_healthy

volumes:
  postgres_data:
```

---

## 2.10 Run Everything with Docker Compose

- [ ] Make sure Docker Desktop is running
- [ ] In your project root, start the containers:
  ```bash
  docker compose up --build
  ```
- [ ] Wait for output showing both `db` and `backend` are running
- [ ] Apply database migrations:
  ```bash
  # In a new terminal tab
  docker compose exec backend dotnet ef database update
  ```
  > If EF tools aren't in the container, run migrations from your local machine with the connection string pointing to localhost:5432

- [ ] Confirm the API is running by visiting: `http://localhost:8080/swagger`
  - [ ] You should see the Swagger UI with all your endpoints listed

---

## 2.11 Test Endpoints in Postman

Create a Postman collection called **UserTrack** and test each endpoint:

- [ ] **Register a user:**
  ```
  POST http://localhost:8080/api/auth/register
  Body: { "name": "Test User", "email": "test@test.com", "password": "Password123!" }
  Expected: 200 OK with a JWT token
  ```
- [ ] **Login:**
  ```
  POST http://localhost:8080/api/auth/login
  Body: { "email": "test@test.com", "password": "Password123!" }
  Expected: 200 OK with a JWT token — copy this token
  ```
- [ ] **Create a group** (add JWT as Bearer token in Postman Authorization tab):
  ```
  POST http://localhost:8080/api/groups
  Auth: Bearer YOUR_JWT_TOKEN
  Body: { "name": "Family" }
  Expected: 200 OK with group details and invite code
  ```
- [ ] **Join a group:**
  ```
  POST http://localhost:8080/api/groups/join
  Auth: Bearer YOUR_JWT_TOKEN
  Body: { "inviteCode": "ABC123" }
  Expected: 200 OK
  ```
- [ ] **Post a location:**
  ```
  POST http://localhost:8080/api/location
  Auth: Bearer YOUR_JWT_TOKEN
  Body: { "latitude": 10.3157, "longitude": 123.8854 }
  Expected: 200 OK
  ```
- [ ] **Get a user's location:**
  ```
  GET http://localhost:8080/api/location/1
  Auth: Bearer YOUR_JWT_TOKEN
  Expected: 200 OK with lat/lng coordinates
  ```

---

## 2.12 Useful Docker Commands for This Phase

```bash
# Start containers
docker compose up --build

# Start in background
docker compose up -d

# View logs
docker compose logs backend -f

# Stop everything
docker compose down

# Wipe database and start fresh
docker compose down -v
docker compose up --build
```

---

## ✅ Phase 2 Complete!

**Checklist before moving on:**
- [ ] Docker Compose starts both `db` and `backend` containers successfully
- [ ] Swagger UI loads at `http://localhost:8080/swagger`
- [ ] Register endpoint creates a user and returns a JWT
- [ ] Login endpoint works and returns a JWT
- [ ] Groups endpoints work with a valid JWT
- [ ] Location endpoints work with a valid JWT
- [ ] All Postman tests pass

➡️ Next: [Phase 3 — Connect Android App to Backend](./03-connect-app.md)


---

# 🔗 Phase 3 — Connect Android App to Backend

> **Goal:** Wire the Android app to your live local backend API. Implement real login, registration, and group listing using Retrofit.

---

## 3.1 Create the Network Layer

- [ ] Create `network/ApiService.kt` — define all API calls using Retrofit:

```kotlin
interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("groups")
    suspend fun getGroups(@Header("Authorization") token: String): Response<List<GroupResponse>>

    @POST("groups")
    suspend fun createGroup(
        @Header("Authorization") token: String,
        @Body request: CreateGroupRequest
    ): Response<GroupResponse>

    @POST("groups/join")
    suspend fun joinGroup(
        @Header("Authorization") token: String,
        @Body request: JoinGroupRequest
    ): Response<Unit>

    @GET("groups/{id}/members")
    suspend fun getGroupMembers(
        @Header("Authorization") token: String,
        @Path("id") groupId: Int
    ): Response<List<MemberResponse>>

    @POST("location")
    suspend fun updateLocation(
        @Header("Authorization") token: String,
        @Body request: UpdateLocationRequest
    ): Response<Unit>
}
```

- [ ] Create `network/RetrofitClient.kt`:

```kotlin
object RetrofitClient {
    // Use 10.0.2.2 for Android emulator → points to your Mac/PC localhost
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val instance: ApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
```

> ⚠️ **Important:** Android emulators use `10.0.2.2` to reach your computer's localhost. If testing on a real phone, use your computer's local network IP (e.g., `192.168.1.x`).

---

## 3.2 Create Data Model Classes

Create these in `model/`:

- [ ] **model/AuthModels.kt**
  ```kotlin
  data class RegisterRequest(val name: String, val email: String, val password: String)
  data class LoginRequest(val email: String, val password: String)
  data class AuthResponse(val token: String, val userId: Int, val name: String, val email: String)
  ```
- [ ] **model/GroupModels.kt**
  ```kotlin
  data class GroupResponse(val id: Int, val name: String, val inviteCode: String, val memberCount: Int)
  data class CreateGroupRequest(val name: String)
  data class JoinGroupRequest(val inviteCode: String)
  data class MemberResponse(val userId: Int, val name: String, val email: String)
  ```
- [ ] **model/LocationModels.kt**
  ```kotlin
  data class UpdateLocationRequest(val latitude: Double, val longitude: Double)
  data class LocationResponse(val userId: Int, val userName: String, val latitude: Double, val longitude: Double, val timestamp: String)
  ```

---

## 3.3 Store the JWT Token Locally

- [ ] Create `repository/TokenRepository.kt` using SharedPreferences:

```kotlin
class TokenRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("usertrack_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString("jwt_token", token).apply()
    fun getToken(): String? = prefs.getString("jwt_token", null)
    fun getBearerToken(): String = "Bearer ${getToken()}"

    fun saveUser(userId: Int, name: String, email: String) {
        prefs.edit()
            .putInt("user_id", userId)
            .putString("user_name", name)
            .putString("user_email", email)
            .apply()
    }
    fun getUserId(): Int = prefs.getInt("user_id", -1)
    fun getUserName(): String = prefs.getString("user_name", "") ?: ""
    fun isLoggedIn(): Boolean = getToken() != null

    fun logout() = prefs.edit().clear().apply()
}
```

---

## 3.4 Create Repositories

- [ ] Create `repository/AuthRepository.kt`:

```kotlin
class AuthRepository(private val context: Context) {
    private val api = RetrofitClient.instance
    private val tokenRepo = TokenRepository(context)

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(name, email, password))
            if (response.isSuccessful && response.body() != null) {
                val auth = response.body()!!
                tokenRepo.saveToken(auth.token)
                tokenRepo.saveUser(auth.userId, auth.name, auth.email)
                Result.success(auth)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val auth = response.body()!!
                tokenRepo.saveToken(auth.token)
                tokenRepo.saveUser(auth.userId, auth.name, auth.email)
                Result.success(auth)
            } else {
                Result.failure(Exception("Login failed: check your email and password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

- [ ] Create `repository/GroupRepository.kt` with methods for:
  - [ ] `getGroups()` — calls `GET /api/groups`
  - [ ] `createGroup(name)` — calls `POST /api/groups`
  - [ ] `joinGroup(inviteCode)` — calls `POST /api/groups/join`
  - [ ] `getGroupMembers(groupId)` — calls `GET /api/groups/{id}/members`

---

## 3.5 Create ViewModels

- [ ] Create `viewmodel/AuthViewModel.kt`:

```kotlin
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthRepository(application)
    private val tokenRepo = TokenRepository(application)

    val isLoggedIn = tokenRepo.isLoggedIn()

    private val _loginState = MutableStateFlow<UiState<AuthResponse>>(UiState.Idle)
    val loginState = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            val result = authRepo.login(email, password)
            _loginState.value = if (result.isSuccess) {
                UiState.Success(result.getOrNull()!!)
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}
```

- [ ] Create a simple `UiState` sealed class in `model/UiState.kt`:

```kotlin
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

- [ ] Create `viewmodel/HomeViewModel.kt` for groups:
  - [ ] `loadGroups()` — fetches and stores group list in a StateFlow
  - [ ] `createGroup(name)` — creates group and refreshes list
  - [ ] `joinGroup(inviteCode)` — joins group and refreshes list

---

## 3.6 Wire Up the Login Screen

- [ ] Update `LoginScreen.kt` to use the `AuthViewModel`:

```kotlin
@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = viewModel()
    val loginState by viewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navigate to Home on success
    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("UserTrack", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))

        if (loginState is UiState.Error) {
            Text((loginState as UiState.Error).message, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login(email, password) },
            enabled = loginState !is UiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginState is UiState.Loading) CircularProgressIndicator(Modifier.size(20.dp))
            else Text("Login")
        }
        TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
            Text("Don't have an account? Register")
        }
    }
}
```

- [ ] Wire up `RegisterScreen.kt` similarly using `AuthViewModel.register()`
- [ ] Update `HomeScreen.kt` to load and display real groups from the API

---

## 3.7 Handle Auto-Login

- [ ] In `MainActivity.kt`, check if the user is already logged in and skip the Login screen:

```kotlin
val startDestination = if (TokenRepository(this).isLoggedIn()) {
    Screen.Home.route
} else {
    Screen.Login.route
}
```

---

## 3.8 Test the Full Auth Flow

- [ ] Make sure Docker Compose is running (`docker compose up -d`)
- [ ] Launch the app on the emulator
- [ ] Test registration:
  - [ ] Fill in the register form → tap Create Account
  - [ ] App should navigate to Home screen
  - [ ] Check Docker logs: `docker compose logs backend -f`
- [ ] Test login:
  - [ ] Kill and reopen the app → should skip login (auto-login)
  - [ ] Use logout button to clear the token → should return to Login screen
  - [ ] Log in again with your credentials
- [ ] Test groups:
  - [ ] Create a group from the Home screen
  - [ ] Confirm it appears in the list

---

## ✅ Phase 3 Complete!

**Checklist before moving on:**
- [ ] Register creates a user and navigates to Home
- [ ] Login authenticates and navigates to Home
- [ ] Auto-login works on app relaunch
- [ ] Groups list loads from the real API
- [ ] Create Group works and refreshes the list
- [ ] Error messages display when login fails
- [ ] Loading states show while API calls are in progress

➡️ Next: [Phase 4 — Real-Time Location with SignalR](./04-realtime-signalr.md)


---

# ⚡ Phase 4 — Real-Time Location with SignalR

> **Goal:** Broadcast and receive live GPS coordinates between users using SignalR. When you move, other group members see your pin update on the map in real-time.

---

## 4.1 Create the SignalR Hub (Backend)

- [ ] Create `Hubs/LocationHub.cs`:

```csharp
[Authorize]
public class LocationHub : Hub {
    
    // Called when a user joins a group's real-time channel
    public async Task JoinGroup(string groupId) {
        await Groups.AddToGroupAsync(Context.ConnectionId, $"group_{groupId}");
        await Clients.Group($"group_{groupId}").SendAsync(
            "UserJoined", Context.UserIdentifier);
    }

    // Called when a user leaves a group channel
    public async Task LeaveGroup(string groupId) {
        await Groups.RemoveFromGroupAsync(Context.ConnectionId, $"group_{groupId}");
    }

    // Called when a user sends their location
    public async Task SendLocation(string groupId, double latitude, double longitude) {
        await Clients.OthersInGroup($"group_{groupId}").SendAsync(
            "ReceiveLocation",
            Context.UserIdentifier,  // userId
            latitude,
            longitude,
            DateTime.UtcNow.ToString("O")  // ISO timestamp
        );
    }

    // Called when a user disconnects
    public override async Task OnDisconnectedAsync(Exception? exception) {
        await Clients.All.SendAsync("UserOffline", Context.UserIdentifier);
        await base.OnDisconnectedAsync(exception);
    }
}
```

- [ ] Make sure `Program.cs` maps the hub:
  ```csharp
  app.MapHub<LocationHub>("/locationHub");
  ```
- [ ] Update CORS policy to allow SignalR WebSocket connections:
  ```csharp
  options.AddPolicy("AllowAll", policy =>
      policy.AllowAnyOrigin()
            .AllowAnyMethod()
            .AllowAnyHeader()
            .AllowCredentials());  // Required for SignalR
  ```
  > ⚠️ Note: `AllowAnyOrigin()` and `AllowCredentials()` together will throw an error. For local dev, use a specific origin instead:
  ```csharp
  policy.WithOrigins("http://localhost", "http://10.0.2.2")
        .AllowAnyMethod().AllowAnyHeader().AllowCredentials()
  ```

---

## 4.2 Rebuild and Restart Docker

- [ ] After updating the backend, rebuild the Docker image:
  ```bash
  docker compose up --build
  ```
- [ ] Confirm the hub is registered — check the Swagger UI at `http://localhost:8080/swagger`
- [ ] Test SignalR is accessible at: `http://localhost:8080/locationHub`

---

## 4.3 Create the SignalR Client (Android)

- [ ] Create `network/LocationHubClient.kt`:

```kotlin
class LocationHubClient(private val token: String) {

    private lateinit var hubConnection: HubConnection
    
    var onLocationReceived: ((userId: String, lat: Double, lng: Double) -> Unit)? = null
    var onUserJoined: ((userId: String) -> Unit)? = null
    var onUserOffline: ((userId: String) -> Unit)? = null

    fun connect() {
        hubConnection = HubConnectionBuilder
            .create("http://10.0.2.2:8080/locationHub")
            .withAccessTokenProvider { Single.just(token) }
            .build()

        // Listen for incoming location updates
        hubConnection.on("ReceiveLocation",
            { userId: String, lat: Double, lng: Double, timestamp: String ->
                onLocationReceived?.invoke(userId, lat, lng)
            },
            String::class.java, Double::class.java,
            Double::class.java, String::class.java
        )

        hubConnection.on("UserJoined",
            { userId: String -> onUserJoined?.invoke(userId) },
            String::class.java
        )

        hubConnection.on("UserOffline",
            { userId: String -> onUserOffline?.invoke(userId) },
            String::class.java
        )

        hubConnection.start().blockingAwait()
    }

    fun joinGroup(groupId: Int) {
        hubConnection.invoke("JoinGroup", groupId.toString())
    }

    fun sendLocation(groupId: Int, latitude: Double, longitude: Double) {
        if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
            hubConnection.invoke("SendLocation", groupId.toString(), latitude, longitude)
        }
    }

    fun disconnect() {
        if (::hubConnection.isInitialized) {
            hubConnection.stop()
        }
    }
}
```

---

## 4.4 Create the Background Location Service

- [ ] Create `service/LocationService.kt`:

```kotlin
class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var hubClient: LocationHubClient? = null
    private var currentGroupId: Int = -1

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_GROUP_ID = "EXTRA_GROUP_ID"
        const val EXTRA_TOKEN = "EXTRA_TOKEN"
        const val NOTIF_ID = 1001
        const val CHANNEL_ID = "location_service_channel"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                currentGroupId = intent.getIntExtra(EXTRA_GROUP_ID, -1)
                val token = intent.getStringExtra(EXTRA_TOKEN) ?: return START_NOT_STICKY
                createNotificationChannel()
                startForeground(NOTIF_ID, buildNotification())
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                startLocationUpdates()
                connectSignalR(token)
            }
            ACTION_STOP -> stopSelf()
        }
        return START_STICKY
    }

    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(10_000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(5_000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                hubClient?.sendLocation(currentGroupId, location.latitude, location.longitude)
            }
        }
    }

    private fun connectSignalR(token: String) {
        Thread {
            hubClient = LocationHubClient(token)
            hubClient?.connect()
            hubClient?.joinGroup(currentGroupId)
        }.start()
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("UserTrack Active")
        .setContentText("Sharing your location with your group")
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .build()

    private fun createNotificationChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Location Sharing",
            NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?) = null

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        hubClient?.disconnect()
        super.onDestroy()
    }
}
```

- [ ] Register the service in `AndroidManifest.xml`:
  ```xml
  <service
      android:name=".service.LocationService"
      android:foregroundServiceType="location"
      android:exported="false" />
  ```

---

## 4.5 Create the Map ViewModel

- [ ] Create `viewmodel/MapViewModel.kt`:

```kotlin
class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenRepo = TokenRepository(application)
    
    // Map of userId -> LatLng for all group members
    private val _memberLocations = MutableStateFlow<Map<String, LatLng>>(emptyMap())
    val memberLocations = _memberLocations.asStateFlow()

    private var hubClient: LocationHubClient? = null

    fun startTracking(groupId: Int) {
        val token = tokenRepo.getToken() ?: return
        
        hubClient = LocationHubClient(token).apply {
            onLocationReceived = { userId, lat, lng ->
                _memberLocations.value = _memberLocations.value + (userId to LatLng(lat, lng))
            }
            connect()
            joinGroup(groupId)
        }

        // Start the background location service
        val intent = Intent(getApplication(), LocationService::class.java).apply {
            action = LocationService.ACTION_START
            putExtra(LocationService.EXTRA_GROUP_ID, groupId)
            putExtra(LocationService.EXTRA_TOKEN, token)
        }
        getApplication<Application>().startForegroundService(intent)
    }

    fun stopTracking() {
        hubClient?.disconnect()
        val intent = Intent(getApplication(), LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        getApplication<Application>().startService(intent)
    }

    override fun onCleared() {
        stopTracking()
        super.onCleared()
    }
}
```

---

## 4.6 Update the Map Screen

- [ ] Update `MapScreen.kt` to show live member pins:

```kotlin
@Composable
fun MapScreen(navController: NavController, groupId: Int) {
    val viewModel: MapViewModel = viewModel()
    val memberLocations by viewModel.memberLocations.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.startTracking(groupId)
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopTracking() }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(10.3157, 123.8854), 14f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        memberLocations.forEach { (userId, latLng) ->
            Marker(
                state = MarkerState(position = latLng),
                title = userId,
                snippet = "Last updated just now"
            )
        }
    }
}
```

---

## 4.7 Test Real-Time Location

- [ ] Open **two Android emulator instances** (or use one emulator + one real device)
- [ ] Log in with different accounts on each device
- [ ] Both devices join the same group
- [ ] Both devices open the Map screen for that group
- [ ] On Emulator 1: go to **emulator settings → Location** and move the GPS pin
- [ ] Confirm: Emulator 2's map shows Emulator 1's pin moving in real time

> 💡 **Emulator Location Tip:** In Android Studio, open the extended controls panel (three dots icon in emulator controls) → Location tab → drag the map pin to simulate movement.

---

## ✅ Phase 4 Complete!

**Checklist before moving on:**
- [ ] SignalR hub is registered and accessible on the backend
- [ ] Android app connects to SignalR hub on Map screen load
- [ ] App joins the correct group channel
- [ ] Location updates every ~10 seconds from the background service
- [ ] Other group members' pins appear and update on the map
- [ ] Foreground notification shows while location is being shared
- [ ] Location stops broadcasting when user leaves the Map screen

➡️ Next: [Phase 5 — Groups Feature](./05-groups.md)


---

# 👥 Phase 5 — Groups Feature

> **Goal:** Complete the groups flow — create groups, share invite codes, let others join, and see all members' live pins on the map.

---

## 5.1 Complete the Create Group Flow

- [ ] Update `HomeScreen.kt` — add a dialog for creating a new group:

```kotlin
var showCreateDialog by remember { mutableStateOf(false) }
var groupName by remember { mutableStateOf("") }

if (showCreateDialog) {
    AlertDialog(
        onDismissRequest = { showCreateDialog = false },
        title = { Text("Create Group") },
        text = {
            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                label = { Text("Group Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                viewModel.createGroup(groupName)
                showCreateDialog = false
                groupName = ""
            }) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
        }
    )
}
```

- [ ] Wire the FAB to `showCreateDialog = true`
- [ ] After `createGroup()` succeeds, refresh the group list

---

## 5.2 Complete the Join Group Flow

- [ ] Add a "Join Group" button or tab on the Home screen
- [ ] Create a join dialog similar to the create dialog:
  - [ ] Text field for the invite code (6-character string)
  - [ ] "Join" button calls `viewModel.joinGroup(inviteCode)`
  - [ ] Show error if invite code is invalid
  - [ ] On success, refresh the group list

---

## 5.3 Build the Group Detail Screen

- [ ] Create `ui/GroupScreen.kt` — shown when a user taps a group:

```kotlin
@Composable
fun GroupScreen(navController: NavController, groupId: Int) {
    val viewModel: GroupViewModel = viewModel()
    val group by viewModel.group.collectAsState()
    val members by viewModel.members.collectAsState()

    LaunchedEffect(groupId) { viewModel.loadGroup(groupId) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        
        // Group Name + Invite Code
        group?.let { g ->
            Text(g.name, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            
            // Invite code card with copy button
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Invite Code", style = MaterialTheme.typography.labelSmall)
                        Text(g.inviteCode, style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Monospace)
                    }
                    IconButton(onClick = { /* copy to clipboard */ }) {
                        Icon(Icons.Default.ContentCopy, "Copy invite code")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Members (${members.size})", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        // Member list
        LazyColumn {
            items(members) { member ->
                ListItem(
                    headlineContent = { Text(member.name) },
                    supportingContent = { Text(member.email) },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
                HorizontalDivider()
            }
        }

        Spacer(Modifier.weight(1f))

        // Open Map button
        Button(
            onClick = { navController.navigate(Screen.Map.createRoute(groupId)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Map, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("View Live Map")
        }
    }
}
```

---

## 5.4 Create the Group ViewModel

- [ ] Create `viewmodel/GroupViewModel.kt`:
  - [ ] `loadGroup(groupId)` — fetches group info and member list
  - [ ] `group: StateFlow<GroupResponse?>` — current group details
  - [ ] `members: StateFlow<List<MemberResponse>>` — member list
  - [ ] Auto-refreshes every 30 seconds to catch new members

---

## 5.5 Copy Invite Code to Clipboard

- [ ] In `GroupScreen.kt`, implement the clipboard copy:

```kotlin
val clipboardManager = LocalClipboardManager.current
val context = LocalContext.current

// In the copy button onClick:
clipboardManager.setText(AnnotatedString(group.inviteCode))
Toast.makeText(context, "Invite code copied!", Toast.LENGTH_SHORT).show()
```

---

## 5.6 Show Member Pins by Name on the Map

- [ ] Update the map markers to show the member's actual name:
  - [ ] When members join via SignalR, also fetch their names from the `/api/groups/{id}/members` endpoint
  - [ ] Display the member name as the marker title:
    ```kotlin
    Marker(
        state = MarkerState(position = latLng),
        title = memberName,  // e.g. "Juan dela Cruz"
        snippet = "Last seen: just now"
    )
    ```
  - [ ] Optionally use different marker colors per member using `BitmapDescriptorFactory.defaultMarker(hue)`

---

## 5.7 Add "You Are Here" Marker

- [ ] Add a distinct marker for the current user's location on the map:
  - [ ] Use the device's GPS to get current location
  - [ ] Show a blue marker (or a custom "Me" icon) at the user's position
  - [ ] Center the camera on the user's location when the map first opens

```kotlin
// Get current location and show as "Me" marker
val myLocation = remember { mutableStateOf<LatLng?>(null) }

LaunchedEffect(Unit) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let { myLocation.value = LatLng(it.latitude, it.longitude) }
    }
}

myLocation.value?.let { loc ->
    Marker(
        state = MarkerState(position = loc),
        title = "You",
        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
    )
}
```

---

## 5.8 Handle Group Leaving

- [ ] Add a "Leave Group" option in the Group screen (top menu or button)
- [ ] Create `DELETE /api/groups/{id}/leave` endpoint on the backend:
  - [ ] Removes the user from `GroupMembers` table
  - [ ] Returns 200 OK
- [ ] On success in Android: navigate back to Home and refresh group list
- [ ] If the user leaving is the group creator, show a warning: "You created this group — leaving will not delete it"

---

## 5.9 Polish the Home Screen Groups List

- [ ] Each group card should show:
  - [ ] Group name (bold)
  - [ ] Number of members
  - [ ] A small colored dot showing if any members are currently online/sharing location
  - [ ] Tap → navigates to GroupScreen
- [ ] Add a pull-to-refresh on the Home screen groups list
- [ ] Show a "No groups yet" empty state with instructions when the list is empty

---

## 5.10 End-to-End Test

- [ ] Use two devices/emulators with different accounts
- [ ] **Device A:** Create a group, note the invite code
- [ ] **Device B:** Join the group using the invite code
- [ ] Both devices open the Group screen — confirm both users appear in the member list
- [ ] Both devices open the Map screen for that group
- [ ] **Device A:** Simulate GPS movement
- [ ] **Device B:** Confirm the pin for Device A moves in real time
- [ ] **Device A:** Leave the group — confirm they disappear from Device B's member list

---

## ✅ Phase 5 Complete!

**Checklist before moving on:**
- [ ] Create group works and generates an invite code
- [ ] Join group by invite code works
- [ ] Group detail screen shows all members
- [ ] Invite code can be copied to clipboard
- [ ] Map shows all members' live pins with their names
- [ ] "You are here" blue marker shows on the map
- [ ] Leave group works
- [ ] End-to-end two-device test passes

➡️ Next: [Phase 6 — Deploy to Google Cloud](./06-gcp-deploy.md)


---

# ☁️ Phase 6 — Deploy to Google Compute Engine

> **Goal:** Deploy your backend to a free-tier Google Compute Engine VM using Docker Compose — the same setup you run locally, just in the cloud. No SignalR issues, no cold starts, no extra services.

---

## Why Compute Engine over Cloud Run?

| | Cloud Run | Compute Engine (e2-micro) |
|---|---|---|
| SignalR WebSockets | Problematic — multi-instance breaks in-memory groups | Works perfectly — single VM |
| Always on | No — scales to zero, drops connections | Yes |
| Setup complexity | High — needs Redis backplane for SignalR | Low — same docker compose as local |
| Cost | ~$8–15/month (with Cloud SQL) | **$0/month** (free tier e2-micro) |
| Control | Limited | Full VM access |

> 💡 **e2-micro is in GCP's always-free tier** — 1 vCPU, 1 GB RAM, 30 GB disk. Perfect for a real-time app with a small user base.

---

## 6.1 Set Up Google Cloud Prerequisites

- [ ] Go to [console.cloud.google.com](https://console.cloud.google.com)
- [ ] Make sure your existing `usertrack` GCP project is selected
- [ ] Enable billing on the project if not already done
- [ ] In your terminal, set your project and region:
  ```bash
  gcloud config set project YOUR_PROJECT_ID
  gcloud config set compute/region asia-southeast1
  gcloud config set compute/zone asia-southeast1-b
  ```
  > **asia-southeast1 (Singapore)** is the closest GCP region to Cebu

---

## 6.2 Enable Required GCP APIs

- [ ] Run this once:
  ```bash
  gcloud services enable \
    compute.googleapis.com \
    cloudbuild.googleapis.com
  ```

---

## 6.3 Create the Compute Engine VM

- [ ] Create a free-tier **e2-micro** VM:
  ```bash
  gcloud compute instances create freelife-vm \
    --machine-type=e2-micro \
    --zone=asia-southeast1-b \
    --image-family=ubuntu-2204-lts \
    --image-project=ubuntu-os-cloud \
    --boot-disk-size=30GB \
    --tags=http-server,https-server
  ```
- [ ] Note the **External IP** shown after creation — this is your production server address
- [ ] Verify the VM is running:
  ```bash
  gcloud compute instances list
  ```

---

## 6.4 Open Firewall Port 8080

- [ ] Allow traffic to your backend port:
  ```bash
  gcloud compute firewall-rules create allow-backend \
    --allow tcp:8080 \
    --target-tags=http-server \
    --description="Allow FreeLife backend traffic"
  ```

---

## 6.5 Install Docker on the VM

- [ ] SSH into your VM:
  ```bash
  gcloud compute ssh freelife-vm --zone=asia-southeast1-b
  ```
- [ ] Inside the VM, install Docker:
  ```bash
  curl -fsSL https://get.docker.com | sh
  sudo usermod -aG docker $USER
  newgrp docker
  ```
- [ ] Install Docker Compose plugin:
  ```bash
  sudo apt-get install -y docker-compose-plugin
  ```
- [ ] Verify:
  ```bash
  docker --version
  docker compose version
  ```

---

## 6.6 Copy Your Project to the VM

**Option A — Clone from GitHub (recommended):**
- [ ] On the VM:
  ```bash
  git clone https://github.com/YOUR_USERNAME/usertrack.git
  cd usertrack
  ```

**Option B — Copy files manually:**
- [ ] From your local machine:
  ```bash
  gcloud compute scp --recurse ./backend freelife-vm:~/usertrack/backend --zone=asia-southeast1-b
  gcloud compute scp ./docker-compose.yml freelife-vm:~/usertrack/ --zone=asia-southeast1-b
  ```

---

## 6.7 Create the Production Environment File

- [ ] On the VM, inside the `usertrack/` folder, create a `.env` file:
  ```bash
  nano .env
  ```
- [ ] Add these values (replace with your own strong password and JWT key):
  ```
  POSTGRES_DB=freelifedb
  POSTGRES_USER=postgres
  POSTGRES_PASSWORD=YOUR_STRONG_DB_PASSWORD
  JWT_KEY=your-production-jwt-key-at-least-32-characters-long!
  ```
- [ ] Save and close (`Ctrl+X`, `Y`, `Enter`)
- [ ] Make sure `.env` is in your `.gitignore` — **never commit this file**

---

## 6.8 Update docker-compose.yml for Production

- [ ] Update `docker-compose.yml` to use the `.env` file values:
  ```yaml
  services:
    db:
      image: postgres:16
      restart: unless-stopped
      environment:
        POSTGRES_DB: ${POSTGRES_DB}
        POSTGRES_USER: ${POSTGRES_USER}
        POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
      ports:
        - "5432:5432"
      volumes:
        - postgres_data:/var/lib/postgresql/data
      healthcheck:
        test: ["CMD-SHELL", "pg_isready -U postgres"]
        interval: 10s
        timeout: 5s
        retries: 5

    backend:
      build: ./backend/FreeLife.API
      restart: unless-stopped
      ports:
        - "8080:8080"
      environment:
        - ConnectionStrings__DefaultConnection=Host=db;Port=5432;Database=${POSTGRES_DB};Username=${POSTGRES_USER};Password=${POSTGRES_PASSWORD}
        - Jwt__Key=${JWT_KEY}
        - ASPNETCORE_ENVIRONMENT=Production
        - ASPNETCORE_URLS=http://+:8080
      depends_on:
        db:
          condition: service_healthy

  volumes:
    postgres_data:
  ```

---

## 6.9 Deploy with Docker Compose

- [ ] On the VM, inside the `usertrack/` folder:
  ```bash
  docker compose up --build -d
  ```
- [ ] Check both containers are running:
  ```bash
  docker compose ps
  ```
- [ ] View backend logs:
  ```bash
  docker compose logs backend -f
  ```

---

## 6.10 Apply Database Migrations

- [ ] Run migrations from inside the VM (using local dotnet tools):
  ```bash
  # Install dotnet SDK on VM first if needed
  sudo apt-get install -y dotnet-sdk-8.0

  cd ~/usertrack/backend/FreeLife.API
  dotnet ef database update \
    --connection "Host=localhost;Port=5432;Database=freelifedb;Username=postgres;Password=YOUR_STRONG_DB_PASSWORD"
  ```
- [ ] Confirm tables were created:
  ```bash
  docker compose exec db psql -U postgres -d freelifedb -c "\dt"
  ```

---

## 6.11 Test the Live API

- [ ] From your browser or Postman, hit your VM's external IP:
  ```
  http://YOUR_VM_EXTERNAL_IP:8080/swagger
  ```
  You should see the Swagger UI with all your endpoints.
- [ ] Test register and login to confirm the database is working

---

## 6.12 Set Up GitHub Actions CI/CD

Automatically redeploy on every push to `main`:

- [ ] Generate an SSH key pair **on your local machine** for GitHub Actions:
  ```bash
  ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions_key -N ""
  ```
- [ ] Copy the public key to the VM:
  ```bash
  gcloud compute ssh freelife-vm --zone=asia-southeast1-b \
    --command="echo '$(cat ~/.ssh/github_actions_key.pub)' >> ~/.ssh/authorized_keys"
  ```
- [ ] Add to **GitHub Secrets** (repo → Settings → Secrets → Actions):
  - `VM_HOST` — your VM's external IP address
  - `VM_USER` — your VM username (run `whoami` on the VM to get it)
  - `VM_SSH_KEY` — paste the **private key** contents (`cat ~/.ssh/github_actions_key`)

- [ ] Create `.github/workflows/deploy.yml`:
  ```yaml
  name: Deploy to Compute Engine

  on:
    push:
      branches: [main]

  jobs:
    deploy:
      runs-on: ubuntu-latest
      steps:
        - name: Deploy via SSH
          uses: appleboy/ssh-action@v1.0.3
          with:
            host: ${{ secrets.VM_HOST }}
            username: ${{ secrets.VM_USER }}
            key: ${{ secrets.VM_SSH_KEY }}
            script: |
              cd ~/usertrack
              git pull origin main
              docker compose up --build -d
              docker image prune -f
  ```

- [ ] Push to `main` → watch GitHub Actions tab → confirm green ✅
- [ ] Check `docker compose ps` on the VM to confirm the new container is running

---

## 6.13 Update Android App to Production URL

- [ ] Update `network/RetrofitClient.kt`:
  ```kotlin
  private const val BASE_URL = "http://YOUR_VM_EXTERNAL_IP:8080/api/"
  ```
- [ ] Update `network/LocationHubClient.kt`:
  ```kotlin
  .create("http://YOUR_VM_EXTERNAL_IP:8080/locationHub")
  ```
- [ ] Build and run on a real device (not emulator) — confirm login, groups, and real-time location all work in production

> 💡 For HTTPS (required for Play Store): point a domain at your VM IP and add an Nginx reverse proxy with Let's Encrypt SSL. This is optional for testing but required for production.

---

## 6.14 Estimated Google Cloud Monthly Cost

| Service | Details | Est. Cost |
|---------|---------|-----------|
| Compute Engine e2-micro | 1 vCPU, 1 GB RAM — always-free tier | **$0/month** |
| Boot disk 30 GB | Included in free tier | **$0/month** |
| Network egress | First 1 GB/month free | ~$0/month |
| **Total** | | **~$0/month** |

> 💡 The e2-micro free tier includes 1 non-preemptible VM per month in select regions (us-west1, us-central1, us-east1). For asia-southeast1, costs are very low (~$6/month) if it falls outside the free tier. Check the [GCP free tier page](https://cloud.google.com/free/docs/free-cloud-features#compute) for current limits.

---

## ✅ Phase 6 Complete!

**Checklist before moving on:**
- [ ] Compute Engine VM created and running
- [ ] Docker and Docker Compose installed on VM
- [ ] Firewall port 8080 open
- [ ] `.env` file created on VM with production secrets
- [ ] `docker compose up -d` running both `db` and `backend` containers
- [ ] Database migrations applied
- [ ] Swagger UI loads at `http://YOUR_VM_EXTERNAL_IP:8080/swagger`
- [ ] GitHub Actions deploys automatically on push to main
- [ ] Android app pointing to VM external IP
- [ ] Login, groups, and real-time location all working in production

➡️ Next: [Phase 7 — Polish & Notifications](./07-polish.md)


---

# ✨ Phase 7 — Polish, Notifications & Play Store

> **Goal:** Add push notifications, location history, UI improvements, and prepare the app for the Google Play Store.

---

## 7.1 Push Notifications (Firebase Cloud Messaging)

- [ ] Go to [console.firebase.google.com](https://console.firebase.google.com) and create a project called `usertrack`
- [ ] Add your Android app:
  - Package name: `com.usertrack.app`
  - Download `google-services.json` → place in `android-app/app/`
- [ ] Add Firebase dependencies to `build.gradle.kts`:
  ```kotlin
  implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
  implementation("com.google.firebase:firebase-messaging-ktx")
  ```
- [ ] Add to `app/build.gradle.kts` plugins:
  ```kotlin
  id("com.google.gms.google-services")
  ```

### Backend - Send Push Notifications

- [ ] Add Firebase Admin SDK to your C# backend:
  ```bash
  dotnet add package FirebaseAdmin
  ```
- [ ] Initialize Firebase in `Program.cs`:
  ```csharp
  FirebaseApp.Create(new AppOptions {
      Credential = GoogleCredential.FromFile("firebase-adminsdk.json")
  });
  ```
- [ ] Create `Services/NotificationService.cs`:
  ```csharp
  public class NotificationService {
      public async Task SendArrivalNotification(string fcmToken, string userName, string placeName) {
          var message = new Message {
              Token = fcmToken,
              Notification = new Notification {
                  Title = "📍 Arrival Alert",
                  Body = $"{userName} has arrived at {placeName}"
              }
          };
          await FirebaseMessaging.DefaultInstance.SendAsync(message);
      }
  }
  ```

### Android - Receive Notifications

- [ ] Create `service/UserTrackFirebaseService.kt`:
  ```kotlin
  class UserTrackFirebaseService : FirebaseMessagingService() {
      override fun onMessageReceived(remoteMessage: RemoteMessage) {
          val title = remoteMessage.notification?.title ?: "UserTrack"
          val body = remoteMessage.notification?.body ?: ""
          showNotification(title, body)
      }
  
      override fun onNewToken(token: String) {
          // Send this token to your backend so it can send notifications to this device
          // Call: POST /api/users/fcm-token with the new token
      }
  
      private fun showNotification(title: String, body: String) {
          val notif = NotificationCompat.Builder(this, "general")
              .setContentTitle(title)
              .setContentText(body)
              .setSmallIcon(R.drawable.ic_notification)
              .setAutoCancel(true)
              .build()
          NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notif)
      }
  }
  ```
- [ ] Register in `AndroidManifest.xml`:
  ```xml
  <service android:name=".service.UserTrackFirebaseService"
      android:exported="false">
      <intent-filter>
          <action android:name="com.google.firebase.MESSAGING_EVENT" />
      </intent-filter>
  </service>
  ```

---

## 7.2 Store FCM Tokens on the Backend

- [ ] Add `FcmToken` column to the `Users` table (add via EF Core migration)
- [ ] Create `PUT /api/users/fcm-token` endpoint:
  ```csharp
  [Authorize]
  [HttpPut("fcm-token")]
  public async Task<IActionResult> UpdateFcmToken([FromBody] string token) {
      var userId = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
      var user = await _db.Users.FindAsync(userId);
      user!.FcmToken = token;
      await _db.SaveChangesAsync();
      return Ok();
  }
  ```
- [ ] Call this endpoint from Android after login and whenever `onNewToken()` fires

---

## 7.3 Location History

### Backend

- [ ] Make sure the `Locations` table stores all location updates with timestamps (not just the latest)
- [ ] Create `GET /api/location/{userId}/history?hours=24` endpoint:
  - Returns all location points for a user in the last N hours
  - Limit to last 500 points to avoid huge responses

### Android

- [ ] Create a "History" tab or button on the Map screen
- [ ] Draw a polyline on the map showing a user's movement path:
  ```kotlin
  Polyline(
      points = locationHistory,  // List<LatLng>
      color = Color.Blue,
      width = 4f
  )
  ```
- [ ] Add a time range picker (last 1h / 6h / 24h)

---

## 7.4 UI Polish

### App Icon

- [ ] Design a custom app icon (use a map pin / location symbol)
- [ ] In Android Studio: right-click `res` → New → Image Asset → Launcher Icons
- [ ] Generate all required sizes

### Splash Screen

- [ ] Add splash screen using the Android 12+ API:
  ```kotlin
  // In themes.xml or via SplashScreen API
  installSplashScreen()
  ```

### Dark Mode

- [ ] Test the app in dark mode (Settings → Display → Dark theme on emulator)
- [ ] Fix any color issues — ensure all text is readable in both modes
- [ ] Use `MaterialTheme.colorScheme` colors everywhere instead of hardcoded colors

### Loading States

- [ ] Make sure every screen shows a `CircularProgressIndicator` while loading data
- [ ] Add `LinearProgressIndicator` at the top of the map when connecting to SignalR

### Error States

- [ ] Show a "No internet connection" banner when the device is offline
- [ ] Show a "Could not connect to server" screen with a Retry button
- [ ] Show a "Location permission denied" screen with a button to open settings

### Empty States

- [ ] Home screen with no groups: show an illustration + "Create your first group to get started"
- [ ] Map with no members online: show "No members are currently sharing their location"
- [ ] Location history with no data: show "No location history for this time period"

---

## 7.5 Settings Screen

- [ ] Build a proper Settings screen with:
  - [ ] **Profile section:** Show name, email, profile picture placeholder
  - [ ] **Location update interval:** Slider (5 sec / 10 sec / 30 sec / 1 min)
  - [ ] **Notification preferences:** Toggles for arrival/departure alerts
  - [ ] **Battery saver mode:** Reduces location update frequency
  - [ ] **Logout button:** Clears JWT, navigates to Login screen
  - [ ] **App version:** Display current version number

---

## 7.6 Performance Improvements

- [ ] **Limit location update frequency** when battery is low (check `BatteryManager`)
- [ ] **Stop SignalR when app goes to background** if user hasn't explicitly enabled background sharing
- [ ] **Debounce map updates** — don't re-render the map for every tiny location change, only update if moved > 10 meters
- [ ] **Paginate location history** — don't load all history at once, use lazy loading
- [ ] **Cache group and member data** locally using Room database for offline viewing

---

## 7.7 Security Hardening

- [ ] Enable **certificate pinning** in OkHttp to prevent MITM attacks:
  ```kotlin
  val certificatePinner = CertificatePinner.Builder()
      .add("api.usertrack.com", "sha256/YOUR_CERT_HASH")
      .build()
  val client = OkHttpClient.Builder()
      .certificatePinner(certificatePinner)
      .build()
  ```
- [ ] Add **rate limiting** to your backend auth endpoints (e.g., max 5 login attempts per minute per IP)
- [ ] Validate all inputs on the backend (email format, password length, etc.)
- [ ] Add **request logging** to CloudWatch for monitoring suspicious activity

---

## 7.8 Testing

- [ ] Write unit tests for ViewModels using `kotlinx-coroutines-test`
- [ ] Write unit tests for C# services using xUnit
- [ ] Test on real physical Android device (not just emulator)
- [ ] Test on different Android versions: API 26, 30, 33, 35
- [ ] Test with poor network conditions (Android emulator can simulate slow network)
- [ ] Test battery usage over a 1-hour location sharing session

---

## 7.9 Prepare for Google Play Store

- [ ] **Increment version:**
  - Update `versionCode` and `versionName` in `build.gradle.kts`
  - e.g., `versionCode = 1`, `versionName = "1.0.0"`

- [ ] **Create a signing keystore:**
  ```bash
  keytool -genkey -v -keystore usertrack-release.jks \
    -alias usertrack -keyalg RSA -keysize 2048 -validity 10000
  ```
  - Store this file safely — you'll need it for every update

- [ ] **Configure release signing** in `build.gradle.kts`:
  ```kotlin
  signingConfigs {
      create("release") {
          storeFile = file("usertrack-release.jks")
          storePassword = "YOUR_KEYSTORE_PASSWORD"
          keyAlias = "usertrack"
          keyPassword = "YOUR_KEY_PASSWORD"
      }
  }
  buildTypes {
      release {
          signingConfig = signingConfigs.getByName("release")
          isMinifyEnabled = true
          proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
      }
  }
  ```

- [ ] **Build a release APK or AAB:**
  - Android Studio → Build → Generate Signed Bundle/APK
  - Choose **Android App Bundle (AAB)** — required for Play Store
  - Select your keystore and generate

- [ ] **Google Play Console:**
  - [ ] Create account at [play.google.com/console](https://play.google.com/console) ($25 one-time fee)
  - [ ] Create a new app: UserTrack
  - [ ] Fill in store listing: description, screenshots, icon, feature graphic
  - [ ] Upload your AAB to Internal Testing first
  - [ ] Add yourself as a tester and install via Play Store link
  - [ ] Once tested, promote to Production

---

## ✅ Phase 7 Complete!

**Checklist:**
- [ ] Push notifications working (arrival/departure alerts)
- [ ] FCM tokens stored and updated on backend
- [ ] Location history visible as a map polyline
- [ ] Custom app icon set
- [ ] Dark mode works correctly
- [ ] All loading, error, and empty states handled
- [ ] Settings screen complete with logout
- [ ] App tested on a real physical device
- [ ] Release AAB built and signed
- [ ] App submitted to Google Play Store (Internal Testing)

---

## 🎉 Congratulations!

You've built a full real-time location tracking app from scratch:
- ✅ Android app with Kotlin + Jetpack Compose
- ✅ C# ASP.NET Core backend with SignalR
- ✅ PostgreSQL database
- ✅ Dockerized and running on Google Cloud
- ✅ CI/CD pipeline via GitHub Actions
- ✅ Push notifications via Firebase
- ✅ Published on Google Play Store

**Built with:** Android Studio Panda · Kotlin · C# .NET 8 · SignalR · PostgreSQL · Docker · Google Cloud Run + Cloud SQL + Artifact Registry


---
