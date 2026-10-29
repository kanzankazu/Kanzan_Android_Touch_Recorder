# Kanzan Touch Recorder

Android application untuk merekam dan memutar ulang gestures, dengan auto-clicker functionality.

## Features

### MVP (Phase 1)
- ✅ **Macro Recorder**: Record and replay touch gestures
- ✅ **Auto Clicker**: Single & multi-target automatic clicking
- ✅ **Floating UI**: Compact overlay panel for quick access
- ✅ **Freemium Model**: Free tier with ads, premium for unlimited access

## Tech Stack

- **Architecture**: Clean Architecture + MVVM + UDF
- **UI**: Jetpack Compose + Material Design 3 + Atomic Design
- **DI**: Hilt
- **Data**: Kotlin Serialization (JSON) + DataStore
- **Minimum SDK**: 24 (Android 7.0+)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
Kanzan_Android_Touch_Recorder/
├── app/                        # Main application module
├── core/
│   ├── designsystem/          # Atoms, Molecules, Theme (Material 3)
│   ├── ui/                    # Shared Organisms
│   ├── domain/                # Business logic & entities
│   ├── data/                  # Data layer & repositories
│   ├── service/               # AccessibilityService
│   └── ads/                   # AdMob integration
└── feature/
    ├── macro/                 # Macro recorder feature
    ├── autoclicker/           # Auto clicker feature
    ├── settings/              # Settings screen
    └── onboarding/            # Onboarding flow
```

## Build

```bash
./gradlew build
```

## Development Guidelines

### Compose Preview Convention

**Setiap screen atau component Compose WAJIB menyertakan preview dengan semua state yang tersedia.**

**Contoh untuk Atoms:**
```kotlin
@Preview(name = "Button - Enabled", showBackground = true)
@Composable
fun AppButtonEnabledPreview() {
    KanzanTheme {
        AppButton(text = "Click Me", onClick = {}, enabled = true)
    }
}

@Preview(name = "Button - Disabled", showBackground = true)
@Composable
fun AppButtonDisabledPreview() {
    KanzanTheme {
        AppButton(text = "Click Me", onClick = {}, enabled = false)
    }
}

@Preview(name = "Button - Loading", showBackground = true)
@Composable
fun AppButtonLoadingPreview() {
    KanzanTheme {
        AppButton(text = "Loading...", onClick = {}, isLoading = true)
    }
}
```

**Contoh untuk Screens:**
```kotlin
@Preview(name = "Empty State", showBackground = true)
@Composable
fun MacroListEmptyPreview() {
    KanzanTheme {
        MacroListTemplate(
            macros = emptyList(),
            isLoading = false,
            isRecording = false,
            onStartRecording = {},
            onStopRecording = {},
            onPlayMacro = {},
            onDeleteMacro = {}
        )
    }
}

@Preview(name = "With Data", showBackground = true)
@Composable
fun MacroListWithDataPreview() {
    KanzanTheme {
        MacroListTemplate(
            macros = listOf(
                Macro(id = "1", name = "Test Macro", events = emptyList(), duration = 1000, createdAt = 0)
            ),
            isLoading = false,
            isRecording = false,
            onStartRecording = {},
            onStopRecording = {},
            onPlayMacro = {},
            onDeleteMacro = {}
        )
    }
}

@Preview(name = "Recording", showBackground = true)
@Composable
fun MacroListRecordingPreview() {
    KanzanTheme {
        MacroListTemplate(
            macros = emptyList(),
            isLoading = false,
            isRecording = true,
            onStartRecording = {},
            onStopRecording = {},
            onPlayMacro = {},
            onDeleteMacro = {}
        )
    }
}
```

**Preview Requirements:**
- ✅ Setiap state variant harus ada preview
- ✅ Gunakan `@Preview` annotation dengan `name` descriptor
- ✅ Wrap dalam `KanzanTheme { }` untuk konsistensi
- ✅ Gunakan `showBackground = true` untuk atoms/molecules
- ✅ Light & Dark theme preview untuk screens (opsional)
- ✅ Preview harus bisa di-render tanpa error di Android Studio

**Preview Naming Convention:**
- `[ComponentName][State]Preview`
- Examples: `AppButtonEnabledPreview`, `MacroListEmptyPreview`, `LoginScreenLoadingPreview`

## Development Status

**Current**: Task 1 - Project Setup & Multi-Module Foundation ✅

See `.kiro/docs/IMPLEMENTATION_PLAN.md` for full implementation plan.

## License

Copyright © 2026 Kanzan Touch Recorder
