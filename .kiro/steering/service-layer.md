# Service Layer Architecture

## Overview

The data layer now has a **Service Layer** between Repository and Firebase for better separation of concerns and type safety.

## Architecture Flow

```
Presentation Layer (ViewModels)
    ↓
Domain Layer (Repository Interfaces)
    ↓
Data Layer:
  ├── Repository (Coordinates & Maps)
  ├── Service (Firebase Operations)
  └── Model (DTOs for Firebase)
    ↓
Firebase Backend
```

## Layer Responsibilities

### 1. Domain Layer
**Location**: `domain/`

- **Models**: Pure Kotlin domain entities (`User`, `ShopItem`)
- **Repositories**: Interface contracts (no implementation)
- **No Dependencies**: No Android, Firebase, or framework dependencies

### 2. Data Layer - Repository
**Location**: `data/repository/`

**Responsibilities**:
- Implements domain repository interfaces
- Coordinates between domain and service layers
- Maps `Result<T>` to `Response<T>` (domain model)
- Maps DTOs to domain models
- Handles Android-specific mappings (e.g., resource IDs)

**Example**: `AuthRepositoryImpl`, `ShopRepositoryImpl`

### 3. Data Layer - Service
**Location**: `data/service/` and `data/service/impl/`

**Responsibilities**:
- Abstracts Firebase operations
- Returns `Result<T>` for type-safe error handling
- Works with DTOs (Data Transfer Objects)
- No domain model knowledge
- Can be easily swapped (Firebase → REST API)

**Example**: `AuthService`, `AuthServiceImpl`

### 4. Data Layer - Models (DTOs)
**Location**: `data/model/`

**Responsibilities**:
- Data Transfer Objects for Firebase/Network
- Mappers to/from domain models
- Firebase-specific annotations (if needed)
- Serialization/Deserialization logic

**Example**: `UserDto`, `ShopItemDto`

## File Structure

```
data/
├── model/                  # DTOs
│   ├── UserDto.kt
│   └── ShopItemDto.kt
├── service/                # Service interfaces
│   ├── AuthService.kt
│   └── ShopService.kt
├── service/impl/           # Service implementations
│   ├── AuthServiceImpl.kt
│   └── ShopServiceImpl.kt
└── repository/             # Repository implementations
    ├── AuthRepositoryImpl.kt
    └── ShopRepositoryImpl.kt
```

## Benefits

### 1. **Better Separation of Concerns**
- Repository: Business logic coordination
- Service: Firebase operations
- Models: Data transformation

### 2. **Type Safety**
- Services return `Result<T>` for explicit error handling
- DTOs ensure Firebase data structure
- Domain models remain pure

### 3. **Testability**
- Mock services easily in repository tests
- Test DTOs independently
- No Firebase dependency in tests

### 4. **Flexibility**
- Swap Firebase for REST API by changing service implementation
- Repository layer remains unchanged
- Domain layer unaffected

### 5. **Clear Boundaries**
- Service knows Firebase
- Repository knows domain
- Models bridge the gap

## Code Examples

### Service Interface
```kotlin
interface AuthService {
    suspend fun signInWithEmailAndPassword(
        email: String, 
        password: String
    ): Result<Boolean>
}
```

### Service Implementation
```kotlin
@Singleton
class AuthServiceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthService {
    override suspend fun signInWithEmailAndPassword(
        email: String, 
        password: String
    ): Result<Boolean> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Repository Implementation
```kotlin
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {
    override suspend fun signInWithEmailAndPassword(
        email: String, 
        password: String
    ): SignInResponse {
        return authService.signInWithEmailAndPassword(email, password)
            .fold(
                onSuccess = { Response.Success(it) },
                onFailure = { Response.Failure(it as Exception) }
            )
    }
}
```

### DTO with Mappers
```kotlin
data class UserDto(
    val uid: String = "",
    val email: String? = null
) {
    fun toDomain(): User {
        return User(uid = uid, email = email)
    }
    
    companion object {
        fun fromDomain(user: User): UserDto {
            return UserDto(uid = user.uid, email = user.email)
        }
    }
}
```

## Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // 1. Firebase
    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth
    
    // 2. Service
    @Provides @Singleton
    fun provideAuthService(auth: FirebaseAuth): AuthService {
        return AuthServiceImpl(auth)
    }
    
    // 3. Repository
    @Provides @Singleton
    fun provideAuthRepository(service: AuthService): AuthRepository {
        return AuthRepositoryImpl(service)
    }
}
```

## Migration Path

### Current Implementation
```
Repository → Firebase (Direct)
```

### New Implementation
```
Repository → Service → Firebase
```

### Adding New Features

1. **Create DTO** in `data/model/`
2. **Create Service Interface** in `data/service/`
3. **Implement Service** in `data/service/impl/`
4. **Update Repository** to use service
5. **Add DI Bindings** in `AppModule`

## Firebase Integration

### Current Services:
- ✅ **AuthService**: Firebase Authentication
- ✅ **ShopService**: Ready for Firebase Firestore

### Future Services:
- **UserProfileService**: User data in Firestore
- **RecyclingService**: Recycling records
- **RewardsService**: Points and rewards
- **LocationService**: Recycling center locations

## Best Practices

1. **Services return `Result<T>`** for type-safe error handling
2. **DTOs have mappers** to/from domain models
3. **Repositories coordinate** between layers
4. **Keep Android resources** in repository layer (not service)
5. **Services are stateless** - no caching, just operations
6. **Use `@Singleton`** for services and repositories
7. **Inject interfaces** not implementations

## Testing Strategy

### Unit Tests:
- **Service Tests**: Mock Firebase, test operations
- **Repository Tests**: Mock service, test coordination
- **DTO Tests**: Test mappers

### Integration Tests:
- Test service with real Firebase (emulator)
- Test repository with real service

## Error Handling

### Service Layer:
```kotlin
return try {
    // Firebase operation
    Result.success(data)
} catch (e: Exception) {
    Result.failure(e)
}
```

### Repository Layer:
```kotlin
return service.operation()
    .fold(
        onSuccess = { Response.Success(it) },
        onFailure = { Response.Failure(it as Exception) }
    )
```

### ViewModel Layer:
```kotlin
when (val response = repository.operation()) {
    is Loading -> // Show loading
    is Success -> // Handle success
    is Failure -> // Handle error
}
```

## Summary

The service layer provides:
- ✅ Better separation of concerns
- ✅ Type-safe error handling
- ✅ Easy testing and mocking
- ✅ Flexibility to swap backends
- ✅ Clear architectural boundaries
- ✅ Firebase abstraction
- ✅ Domain model purity
