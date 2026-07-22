# Copilot Instructions for Kollama

Kollama is a private, local AI companion Android app that connects to Ollama instances via the network. This document provides essential context for working effectively in this codebase.

## Project Overview

**Type**: Android Mobile Application (Kotlin)  
**Architecture**: Clean Architecture with MVVM + Koin DI  
**UI Framework**: Jetpack Compose with Material 3  
**Min SDK**: 35 | **Target SDK**: 36 | **Compile SDK**: 37  
**Database**: Room with Kotlin Serialization  
**Networking**: Ollama-Kotlin (Ktor-based)

## Build, Test, and Lint Commands

### Build

```bash
# Full build (debug)
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean and rebuild
./gradlew clean build
```

### Tests

```bash
# Run all unit tests
./gradlew test

# Run single test class
./gradlew test --tests com.udhay.kollama.ExampleUnitTest

# Run instrumentation tests on connected device/emulator
./gradlew connectedAndroidTest

# Run tests with verbose output
./gradlew test --info
```

**Current Status**: Minimal test coverage (only example unit test exists). Tests run via AndroidJUnitRunner.

### Lint and Code Quality

```bash
# Check for lint issues
./gradlew lint

# Build and generate lint report
./gradlew build --no-daemon
```

**KSP Compiler**: KSP 2.3.4 is configured for annotation processing (Koin, Room).

### Other Useful Commands

```bash
# Clean Gradle cache
./gradlew clean

# Force refresh of dependencies
./gradlew build --refresh-dependencies

# Sync Gradle (after dependency changes)
./gradlew syncDebugSources
```

## High-Level Architecture

### Layered Structure

The app follows **Clean Architecture** with clear separation of concerns:

```
feature/
├── chat/                          # Chat feature module
│   ├── data/                      # Data layer (API calls, mappers)
│   │   ├── model/                 # Data models (ChatMessage, etc.)
│   │   └── repository/            # Repository implementations
│   ├── domain/                    # Business logic
│   │   ├── model/                 # Domain models (OllamaModel, etc.)
│   │   ├── repository/            # Repository interfaces
│   │   └── usecase/               # Use cases (GetModelsUseCase, GetStatusUseCase)
│   └── presentation/              # UI layer
│       ├── components/            # Reusable Compose components
│       ├── screen/                # Screen-level composables
│       ├── state/                 # UI state sealed classes
│       └── viewmodel/             # ViewModels
│
└── settings/                      # Settings feature module (identical structure)
    ├── data/
    ├── domain/
    └── presentation/

core/
├── di/                           # Dependency injection (Koin)
├── database/                     # Room database setup
├── ui/                           # Shared UI components and theme
│   ├── navigation/               # Navigation routes and host
│   ├── common/                   # Reusable UI components (Loader, ErrorView, etc.)
│   └── theme/                    # Material 3 theming (Color, Type, Theme)
└── utils/                        # Utilities (date formatting, JSON printing, file size)
```

### Feature Modules

Two main features:
1. **Chat**: Manages Ollama model selection, connection status, and chat interface
2. **Settings**: Manages user configuration (server host, headers, profiles, system instructions)

### Dependency Injection (Koin)

- **Entry point**: `KollamaApp` class with `@KoinApplication` annotation
- **Modules**: `DatabaseModule` and `NetworkModule`
- **Key singletons**:
  - `OllamaClient`: Configured dynamically from user settings via `UserSettingsRepository`
  - `AppDatabase`: Room database instance
  - ViewModels: Annotated with `@KoinViewModel` for automatic integration

### State Management

- **UI State**: Sealed classes (e.g., `ModelsUiState`) with states: `Loading`, `Success(data)`, `Error(message)`
- **State Flows**: ViewModels use `MutableStateFlow` for reactive state. Collectors in Compose use `.stateAsState()` to read values
- **ViewModelScope**: All async work (Coroutines) runs in `viewModelScope` to auto-cancel on ViewModel destruction

### Navigation

- **Routes**: `Routes` sealed class defines all screen destinations (Chat, Settings, Personalization, ConnectionSettings)
- **Host**: `AppNavHost` composable handles navigation logic
- **Type-safe**: Serialization-based navigation via Kotlin Serialization

### Database

- **ORM**: Room with Room-Kotlin extensions
- **Entity**: `UserSettingsEntity` (expandable for future entities)
- **Type Converters**: Custom converters in `Converters.kt` for complex types
- **Version**: v1 (set in `@Database` annotation; increment on schema changes)

## Key Conventions

### Naming Patterns

- **ViewModels**: `{Feature}ViewModel` (e.g., `ModelsViewModel`, `UserSettingsViewModel`)
- **Use Cases**: `{Action}UseCase` (e.g., `GetModelsUseCase`, `SaveUserSettingsUseCase`)
- **Composables**: PascalCase matching purpose (e.g., `ChatBubble`, `ErrorView`, `ModelSelectorBottomSheet`)
- **Screen Components**: `{Feature}Page` (e.g., `ChatPage`, `SettingsPage`)
- **Repository Interfaces**: `{Entity}Repository` in domain layer; implementations in data layer

### Code Patterns

1. **Sealed Classes for UI State**
   ```kotlin
   sealed class ModelsUiState {
       object Loading : ModelsUiState()
       data class Success(val models: List<OllamaModel>) : ModelsUiState()
       data class Error(val message: String) : ModelsUiState()
   }
   ```

2. **ViewModel Pattern with StateFlow**
   ```kotlin
   @KoinViewModel
   class ModelsViewModel(...) : ViewModel() {
       private val _uiState = MutableStateFlow<ModelsUiState>(ModelsUiState.Loading)
       val uiState: StateFlow<ModelsUiState> = _uiState
       
       fun loadData() {
           viewModelScope.launch {
               try {
                   _uiState.value = ModelsUiState.Loading
                   val result = useCase()
                   _uiState.value = ModelsUiState.Success(result)
               } catch (e: Exception) {
                   _uiState.value = ModelsUiState.Error(e.message ?: "Unknown error")
               }
           }
       }
   }
   ```

3. **Repository Pattern**
   - Define interface in `domain/repository/`
   - Implement in `data/repository/` with concrete dependencies
   - Inject via Koin DI

4. **Mapper Pattern**
   - Transform DTOs/Entities to Domain Models
   - Files named `{Feature}Mapper.kt` in data layer
   - Single responsibility: one mapper per transformation

5. **Composable Conventions**
   - Mark all composables with `@Composable`
   - Avoid side effects; use `LaunchedEffect` for coroutines
   - State flows consumed with `.collectAsStateValue()` or `.collectAsState()`
   - Preview composables with `@Preview` for testing in IDE

### Data Flow

1. **UI → ViewModel**: User actions trigger ViewModel functions
2. **ViewModel → UseCase**: ViewModel calls use cases for business logic
3. **UseCase → Repository**: Use cases call repository methods
4. **Repository → Data Source**: Repositories fetch from API (OllamaClient) or Database (Room)
5. **Back to UI**: Results flow back via StateFlow emissions, UI recomposes

### Async Patterns

- All database and network calls are `suspend` functions
- Launch from ViewModels using `viewModelScope.launch`
- Wrap in try-catch to emit error states
- Never block the main thread

### Testing Notes

- Minimal test coverage currently; growth area
- Use `AndroidJUnitRunner` for instrumentation tests
- Unit tests live in `src/test/java/`
- Instrumentation tests live in `src/androidTest/java/`

### Kotlin Language Version

- **Version**: 2.3.21 (with Compose support via `kotlin-plugin-compose`)
- **Code Style**: "official" (configured in `gradle.properties`)
- **Serialization**: `kotlinx.serialization` for JSON and database type conversion

### Gradle Configuration

- **Build System**: Gradle with Kotlin DSL (.kts files)
- **Version Catalog**: Uses `libs` catalog (defined in `gradle/libs.versions.toml`)
- **Plugins**:
  - `android.application` - Android app build
  - `kotlin.compose` - Compose compiler integration
  - `ksp` - Annotation processing (KSP 2.3.4)
  - `koin.compiler` - Koin DI code generation
  - `kotlin.serialization` - Kotlin serialization plugin
- **JVM Target**: Java 11

### Common Debugging Points

- **OllamaClient Config**: Dynamically pulled from `UserSettingsRepository` at runtime; ensure server host/headers are correct
- **State Updates**: Always update `_state.value` in try-catch blocks; emit error state on exceptions
- **Compose Recomposition**: Verify StateFlow is properly collected in composable
- **Database Migrations**: Increment `@Database` version and provide migration if schema changes
- **Network Issues**: Check OLLAMA_HOST env variable on Ollama server; must be `0.0.0.0` for Android connectivity

## Additional Resources

- **README.md**: Feature overview and setup instructions
- **Kotlin**: https://kotlinlang.org
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Room**: https://developer.android.com/jetpack/androidx/releases/room
- **Koin**: https://insert-koin.io
- **Ollama-Kotlin**: Part of external dependency; check docs for API patterns
