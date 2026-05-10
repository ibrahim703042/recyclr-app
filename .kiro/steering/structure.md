# Project Structure

## Root Package
`com.gdsc.recyclr` - Base package for all application code

## Architecture Overview
The project combines **MVVM (Model-View-ViewModel)** with **Clean Architecture** principles:

- **MVVM**: UI pattern where ViewModels manage UI state and business logic
- **Clean Architecture**: Layered architecture with clear separation of concerns

This combination provides:
- **Testability**: Each layer can be tested independently
- **Maintainability**: Clear boundaries between UI, business logic, and data
- **Scalability**: Easy to add new features without affecting existing code
- **Flexibility**: Can swap implementations (e.g., change data sources) without affecting other layers

```
app/src/main/java/com/gdsc/recyclr/
├── activities/          # Android Activity components
├── components/          # Reusable UI components
├── core/               # Core utilities and helpers
├── data/               # Data layer (implementations)
├── di/                 # Dependency injection modules
├── domain/             # Domain layer (interfaces & models)
├── navigation/         # Navigation configuration
├── screens/            # Feature screens (UI layer with MVVM)
└── RecyclrApp.kt       # Application class
```

### Architecture Layers

**Clean Architecture Layers:**
1. **Presentation Layer** (screens/) - MVVM pattern with Compose UI
2. **Domain Layer** (domain/) - Business logic and contracts
3. **Data Layer** (data/) - Data sources and repository implementations

**MVVM Components:**
- **View**: Jetpack Compose UI (Screen composables)
- **ViewModel**: State management and UI logic coordination
- **Model**: Domain models and repository interfaces

## Layer Responsibilities

### Domain Layer (`domain/`)
- **Interfaces**: Repository contracts defining business operations
- **Models**: Core business entities and data structures
- **Type Aliases**: Domain-specific type definitions
- **No Android dependencies** - pure Kotlin

**Structure:**
```
domain/
├── model/              # Domain models (e.g., Response sealed class)
└── repository/         # Repository interfaces (e.g., AuthRepository)
```

### Data Layer (`data/`)
- **Repository Implementations**: Concrete implementations of domain repositories
- **Data Sources**: Firebase, API clients, local storage
- **Mappers**: Convert between data and domain models (if needed)

**Structure:**
```
data/
└── repository/         # Repository implementations (e.g., AuthRepositoryImpl)
```

### Presentation Layer (`screens/`) - MVVM Pattern
This layer implements the **MVVM pattern** within Clean Architecture:

- **View (Composables)**: UI components that observe ViewModel state
- **ViewModel**: Manages UI state, handles user actions, coordinates with repositories
- **Components**: Reusable UI elements for the feature

**Structure:**
```
screens/
└── [feature]/          # Feature-based organization
    ├── components/     # Feature-specific composables (View)
    ├── [Feature]Screen.kt      # Main screen composable (View)
    └── [Feature]ViewModel.kt   # State & logic (ViewModel)
```

**Example:**
```
screens/auths/sign_in/
├── components/         # View components
│   ├── SignIn.kt
│   ├── SignInContent.kt
│   └── SignDetail.kt
├── SignInScreen.kt     # View (observes ViewModel)
└── SignInViewModel.kt  # ViewModel (manages state, calls repositories)
```

**MVVM Flow:**
1. **View** (Screen) displays UI and captures user input
2. **ViewModel** receives actions, updates state, calls domain/repository
3. **Model** (Repository) provides data through domain interfaces
4. **ViewModel** updates state based on repository response
5. **View** recomposes based on new state

### Dependency Injection (`di/`)
- **Hilt Modules**: Provide dependencies for ViewModels and repositories
- **Scoping**: Use appropriate Hilt scopes (`@ViewModelComponent`, `@Singleton`)

### Components (`components/`)
Reusable UI components shared across features:
```
components/
├── composable/         # Reusable Compose components
│   ├── BackIcon.kt
│   ├── ButtonComposable.kt
│   ├── CardComposable.kt
│   ├── EmailField.kt
│   ├── PasswordField.kt
│   └── ...
└── ext/               # Extension functions
    └── ModifierExt.kt
```

### Navigation (`navigation/`)
- **NavGraph.kt**: Main navigation graph with all routes
- **BottomNavGraph.kt**: Bottom navigation configuration
- **Screen.kt**: Screen route definitions
- **OnBoardingPage.kt**: Onboarding flow configuration

### Core (`core/`)
- **Utils.kt**: Application-wide utility functions
- **Constants**: App-level constants (if needed)
- **Extensions**: Kotlin extension functions

## Naming Conventions

### Files
- **Screens**: `[Feature]Screen.kt` (e.g., `SignInScreen.kt`)
- **ViewModels**: `[Feature]ViewModel.kt` (e.g., `SignInViewModel.kt`)
- **Repositories**: `[Domain]Repository.kt` (interface), `[Domain]RepositoryImpl.kt` (implementation)
- **Composables**: Descriptive names (e.g., `ButtonComposable.kt`, `EmailField.kt`)

### Classes & Functions
- **Classes**: PascalCase (e.g., `AuthRepository`, `SignInViewModel`)
- **Functions**: camelCase (e.g., `signInWithEmailAndPassword`, `navigateToSignUpScreen`)
- **Composables**: PascalCase (e.g., `SignInScreen`, `EmailField`)
- **Constants**: UPPER_SNAKE_CASE

### Packages
- **Feature-based**: Group by feature/domain (e.g., `screens/auths/sign_in/`)
- **Layer-based**: Separate by architectural layer (e.g., `domain/`, `data/`)

## Code Organization Patterns

### MVVM + Clean Architecture Flow

```
User Interaction
    ↓
View (Composable Screen)
    ↓
ViewModel (Presentation Logic)
    ↓
Repository Interface (Domain Contract)
    ↓
Repository Implementation (Data Layer)
    ↓
Data Source (Firebase, API, Database)
```

### ViewModel Pattern (MVVM)
```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {
    var state by mutableStateOf<Response>(Success(false))
        private set
    
    fun performAction() = viewModelScope.launch {
        state = Loading
        state = repository.doSomething()
    }
}
```

### Screen Pattern (MVVM View)
```kotlin
@Composable
fun FeatureScreen(
    viewModel: FeatureViewModel = hiltViewModel(), // ViewModel injection
    navigateToNext: () -> Unit
) {
    val context = LocalContext.current
    
    // View observes ViewModel state and passes actions to ViewModel
    FeatureContent(
        onAction = { viewModel.performAction() }, // User action → ViewModel
        navigateToNext = navigateToNext
    )
    
    // View reacts to ViewModel state changes
    HandleResponse(
        showMessage = { message -> showMessage(context, message) }
    )
}
```

### Repository Pattern (Clean Architecture - Domain & Data Layers)
```kotlin
// Domain interface (Contract - no implementation details)
interface AuthRepository {
    suspend fun doSomething(): Response<Boolean>
}

// Data implementation (Concrete implementation with data source)
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth // Data source dependency
): AuthRepository {
    override suspend fun doSomething(): Response<Boolean> {
        return try {
            // Data source interaction
            Success(true)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}
```

**Key Principles:**
- **Domain layer** defines the contract (interface)
- **Data layer** provides the implementation
- **ViewModel** depends on domain interface, not implementation
- **Dependency Injection** wires implementation to interface

### Response Handling
Use the sealed `Response<T>` class for async operations:
- `Response.Loading` - Operation in progress
- `Response.Success<T>` - Operation succeeded with data
- `Response.Failure` - Operation failed with exception

### Dependency Injection (Wiring MVVM + Clean Architecture)
- Use `@HiltViewModel` for ViewModels (MVVM layer)
- Use `@Inject constructor()` for repository dependencies
- Define providers in `AppModule` to bind interfaces to implementations
- Install modules in correct components (`@InstallIn(ViewModelComponent::class)`)

**Example:**
```kotlin
@Module
@InstallIn(ViewModelComponent::class)
class AppModule {
    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl(
        auth = Firebase.auth
    )
}
```

This ensures:
- **ViewModels** receive repository interfaces (domain layer)
- **Repositories** receive data sources (Firebase, APIs)
- **Clean separation** between layers is maintained

## Testing Structure
```
app/src/
├── androidTest/        # Instrumented tests (UI, integration)
│   └── java/com/gdsc/recyclr/
│       └── ExampleInstrumentedTest.kt
└── test/              # Unit tests (ViewModels, repositories)
    └── java/com/gdsc/recyclr/
        └── ExampleUnitTest.kt
```

## Resource Organization
```
app/src/main/res/
├── drawable/          # Vector drawables, images
├── mipmap/           # App icons
├── values/           # Strings, colors, themes, dimensions
└── xml/              # Backup rules, data extraction rules
```

## Configuration Files
- **AndroidManifest.xml**: App configuration, permissions, activities
- **build.gradle** (project): Plugin versions, dependencies
- **build.gradle** (app): App configuration, dependencies
- **google-services.json**: Firebase configuration
- **proguard-rules.pro**: ProGuard/R8 rules for release builds
