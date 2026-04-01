# Android Studio Sync Guide for MindMirror

## If Android Studio doesn't show "Sync Project with Gradle Files" option:

### Option 1: Close and Reopen Project (Best)
1. **File > Close Project**
2. Click **Open** 
3. Navigate to `/Users/ASHTNAG/AndroidStudioProjects/MindMirror` (the **root folder**, not `app/`)
4. Click **Open as Project**
5. Wait 30-60 seconds for Gradle to sync automatically
6. If sync doesn't start, go to **File > Sync Project with Gradle Files**

### Option 2: Manual Invalidate Cache
1. **File > Invalidate Caches > Invalidate and Restart**
2. Click **Invalidate and Restart**
3. Wait for Android Studio to restart
4. Automatically syncs on startup

### Option 3: If Still Stuck
```bash
cd "/Users/ASHTNAG/AndroidStudioProjects/MindMirror"
./gradlew --stop
```
Then restart Android Studio.

## Key Setup Steps After Sync:
1. **Settings > Build, Execution, Deployment > Build Tools > Gradle**
   - Set **Gradle JDK** to JDK 17 or newer
2. **File > Settings > Languages & Frameworks > Android SDK**
   - Ensure Android SDK is installed and path is correct
3. **Device Manager** (top-right corner)
   - Create/launch an Android Emulator
4. **Run Configuration** (top toolbar)
   - Select `app` as the run config
5. Click **Run** (green play icon)

## What I Fixed:
- ✅ Added `local.properties` with Android SDK path
- ✅ Updated `gradle.properties` to suppress compileSdk warning
- ✅ Fixed `MainActivity.kt` to properly initialize Room database
- ✅ All gradle files present and valid
- ✅ App builds successfully: `./gradlew assembleDebug`

The app should now run on Android Studio without any Gradle file recognition issues.

