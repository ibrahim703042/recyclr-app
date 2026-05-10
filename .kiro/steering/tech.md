# Technology Stack

## Build System
- **Gradle**: 8.9
- **Android Gradle Plugin**: 8.5.2
- **Kotlin**: 1.9.24
- **Java/JVM Target**: 17
- **Compile SDK**: 34 (Android 14)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34

## Core Technologies
- **Language**: Kotlin 1.9.24
- **UI Framework**: Jetpack Compose (1.6.8 via BOM 2024.06.00)
- **Architecture**: MVVM + Clean Architecture
  - **MVVM**: View (Compose) ↔ ViewModel ↔ Model (Repository)
  - **Clean Architecture**: Presentation → Domain → Data layers
- **Dependency Injection**: Dagger Hilt (2.51.1)
- **Async Operations**: Kotlin Coroutines (1.8.1) with Flow

## Key Libraries

### Firebase (via BOM 33.1.2)
- `firebase-auth-ktx` - Authentication
- `firebase-firestore-ktx` - Cloud database

### Jetpack/AndroidX
- `core-ktx` (1.13.1) - Core Android extensions
- `lifecycle-runtime-ktx` (2.8.3) - Lifecycle management
- `activity-compose` (1.9.0) - Compose integration
- `navigation-compose` (2.7.7) - Navigation
- `hilt-navigation-compose` (1.2.0) - Hilt integration
- `datastore-preferences` (1.1.1) - Data persistence
- `core-splashscreen` (1.0.1) - Splash screen API

### Compose (via BOM 2024.06.00)
- `compose-ui` - Core UI components
- `compose-material` - Material Design components
- `compose-material-icons-extended` - Extended icon set
- `compose-ui-tooling` - Preview and debugging tools

### Google Services
- `play-services-auth` (21.2.0) - Google Sign-In
- `play-services-maps` (19.0.0) - Google Maps
- `play-services-location` (21.3.0) - Location services

### Google Maps for Compose
- `maps-compose` (4.4.1) - Compose integration
- `maps-ktx` (5.1.1) - Kotlin extensions
- `maps-utils-ktx` (5.1.1) - Utility library

### UI/UX
- `accompanist-pager` (0.34.0) - Pager layouts
- `accompanist-pager-indicators` (0.34.0) - Page indicators
- `accompanist-systemuicontroller` (0.34.0) - System UI control
- `accompanist-navigation-animation` (0.34.0) - Navigation animations
- `coil-compose` (2.6.0) - Image loading

### Coroutines
- `kotlinx-coroutines-core` (1.8.1) - Core coroutines
- `kotlinx-coroutines-android` (1.8.1) - Android support
- `kotlinx-coroutines-play-services` (1.8.1) - Firebase integration

### Testing
- `junit` (4.13.2) - Unit testing
- `androidx.test.ext:junit` (1.2.1) - Android testing
- `espresso-core` (3.6.1) - UI testing

## Common Commands

### Build & Run
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install and run on connected device
./gradlew installDebug

# Clean build
./gradlew clean
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.gdsc.recyclr.ExampleTest"
```

### Code Quality
```bash
# Run lint checks
./gradlew lint

# Generate lint report
./gradlew lintDebug
```

### Dependencies
```bash
# View dependency tree
./gradlew app:dependencies

# Check for dependency updates
./gradlew dependencyUpdates
```

## Configuration Requirements
- **Google Maps API Key**: Required in `local.properties` as `GOOGLE_MAPS_API_KEY`
- **Firebase Configuration**: `google-services.json` must be present in `app/` directory
- **Java Version**: JDK 8 or higher (JVM target 1.8)
