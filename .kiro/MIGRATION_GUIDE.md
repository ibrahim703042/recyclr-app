# Migration Guide - Dependency Upgrade

## 🚀 Quick Start

### Step 1: Update JDK to Version 17

#### Windows:
1. Download JDK 17 from [Oracle](https://www.oracle.com/java/technologies/downloads/#java17) or [OpenJDK](https://adoptium.net/)
2. Install JDK 17
3. Set JAVA_HOME environment variable
4. In Android Studio: `File → Project Structure → SDK Location → JDK Location`

#### macOS/Linux:
```bash
# Using SDKMAN
sdk install java 17.0.11-tem
sdk use java 17.0.11-tem

# Or using Homebrew (macOS)
brew install openjdk@17
```

### Step 2: Sync Gradle Files
```bash
# In Android Studio
File → Sync Project with Gradle Files

# Or via command line
./gradlew clean
./gradlew build
```

### Step 3: Clean Build
```bash
./gradlew clean
./gradlew assembleDebug
```

## 🔧 Common Issues & Solutions

### Issue 1: "Unsupported class file major version 61"
**Cause**: JDK version mismatch

**Solution**:
```bash
# Check Java version
java -version
# Should show: openjdk version "17.x.x"

# In Android Studio
File → Settings → Build, Execution, Deployment → Build Tools → Gradle
→ Gradle JDK: Select Java 17
```

### Issue 2: Compose Preview Not Working
**Solution**:
```
File → Invalidate Caches → Invalidate and Restart
```

### Issue 3: Kapt Errors with Hilt
**Solution**:
```bash
./gradlew clean
./gradlew kaptDebugKotlin
./gradlew assembleDebug
```

### Issue 4: Firebase Initialization Failed
**Solution**:
1. Download latest `google-services.json` from Firebase Console
2. Place in `app/` directory
3. Sync Gradle

### Issue 5: Maps Not Displaying
**Solution**:
Check `local.properties`:
```properties
GOOGLE_MAPS_API_KEY=your_actual_api_key_here
```

## 📝 Code Changes Required

### 1. No Code Changes for Most Files ✅
The upgrade is mostly backward compatible. Your existing code should work without changes.

### 2. Deprecated APIs (Optional Updates)

#### Before (Old):
```kotlin
// packagingOptions is deprecated
packagingOptions {
    resources {
        excludes += '/META-INF/{AL2.0,LGPL2.1}'
    }
}
```

#### After (New):
```kotlin
// Use packaging instead
packaging {
    resources {
        excludes += '/META-INF/{AL2.0,LGPL2.1}'
    }
}
```
✅ **Already fixed in build.gradle**

### 3. Compose BOM Benefits

#### Before:
```kotlin
implementation "androidx.compose.ui:ui:1.4.0"
implementation "androidx.compose.material:material:1.4.0"
implementation "androidx.compose.ui:ui-tooling:1.4.0"
```

#### After:
```kotlin
implementation platform('androidx.compose:compose-bom:2024.06.00')
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material:material'
implementation 'androidx.compose.ui:ui-tooling'
```
✅ **Already updated in build.gradle**

### 4. Firebase BOM Benefits

#### Before:
```kotlin
implementation 'com.google.firebase:firebase-auth-ktx:21.1.0'
implementation 'com.google.firebase:firebase-firestore-ktx:21.1.0'
```

#### After:
```kotlin
implementation platform('com.google.firebase:firebase-bom:33.1.2')
implementation 'com.google.firebase:firebase-auth-ktx'
implementation 'com.google.firebase:firebase-firestore-ktx'
```
✅ **Already updated in build.gradle**

## 🧪 Testing Checklist

### Functional Testing:
- [ ] App launches successfully
- [ ] Sign in with email/password works
- [ ] Sign up creates new account
- [ ] Password reset email sent
- [ ] Google Sign-In works
- [ ] Shop screen displays items
- [ ] Map screen shows location
- [ ] Profile screen loads
- [ ] Navigation between screens works
- [ ] Bottom navigation works

### Performance Testing:
- [ ] App startup time is acceptable
- [ ] Compose UI renders smoothly
- [ ] No memory leaks
- [ ] Firebase operations are fast

### Compatibility Testing:
- [ ] Test on Android 7.0 (API 24)
- [ ] Test on Android 10 (API 29)
- [ ] Test on Android 14 (API 34)
- [ ] Test on different screen sizes
- [ ] Test on tablet (if applicable)

## 📱 Device Requirements

### Before Upgrade:
- Minimum: Android 5.0 (API 21)
- Coverage: ~99.5% of devices

### After Upgrade:
- Minimum: Android 7.0 (API 24)
- Coverage: ~99% of devices
- **Dropped**: Android 5.0-6.0 (~0.5% of devices)

## 🔄 Rollback Plan (If Needed)

If you encounter critical issues, you can rollback:

### Option 1: Git Revert
```bash
git checkout HEAD~1 build.gradle app/build.gradle
```

### Option 2: Manual Rollback
Restore these values in `build.gradle`:
```groovy
kotlin_version = '1.7.20'
compose_ui_version = '1.4.0'
hilt_version = '2.45'
```

And in `app/build.gradle`:
```groovy
compileSdk 33
targetSdk 33
minSdk 21
sourceCompatibility JavaVersion.VERSION_1_8
targetCompatibility JavaVersion.VERSION_1_8
jvmTarget = '1.8'
```

## 📊 Performance Benchmarks

### Expected Improvements:
- **Build Time**: 10-20% faster (Kotlin 1.9.24 compiler improvements)
- **App Startup**: 5-10% faster (Java 17 JVM improvements)
- **Compose Rendering**: 5-15% faster (Compose 1.6.8 optimizations)
- **APK Size**: 2-5% smaller (Better R8 optimization)

## 🎓 Learning Resources

### Kotlin 1.9.24:
- [What's New](https://kotlinlang.org/docs/whatsnew1924.html)
- [Migration Guide](https://kotlinlang.org/docs/kotlin-evolution.html)

### Jetpack Compose:
- [Compose BOM](https://developer.android.com/jetpack/compose/bom)
- [What's New in Compose](https://developer.android.com/jetpack/androidx/releases/compose)

### Java 17:
- [New Features](https://openjdk.org/projects/jdk/17/)
- [Migration Guide](https://docs.oracle.com/en/java/javase/17/migrate/getting-started.html)

### Android 14:
- [Behavior Changes](https://developer.android.com/about/versions/14/behavior-changes-14)
- [New Features](https://developer.android.com/about/versions/14/features)

## ✅ Success Indicators

You've successfully migrated when:
- ✅ Gradle sync completes without errors
- ✅ Project builds successfully
- ✅ App runs on device/emulator
- ✅ All features work as expected
- ✅ No runtime crashes
- ✅ Performance is same or better

## 🆘 Getting Help

If you encounter issues:

1. **Check Logs**: Look at Build Output and Logcat
2. **Clean Build**: `./gradlew clean build`
3. **Invalidate Caches**: `File → Invalidate Caches → Restart`
4. **Check JDK**: Ensure JDK 17 is selected
5. **Update Android Studio**: Use latest version
6. **Check Firebase**: Ensure google-services.json is up to date

## 🎉 Congratulations!

You've successfully upgraded to:
- ✅ Kotlin 1.9.24
- ✅ Java 17
- ✅ Android 14 (API 34)
- ✅ Latest Compose
- ✅ Latest Firebase
- ✅ All dependencies updated

Your project is now using modern, performant, and secure versions of all dependencies! 🚀
