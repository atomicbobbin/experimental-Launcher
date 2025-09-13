# Troubleshooting Guide - Fossify Launcher

## App Crashes on Startup

If the app keeps stopping when you try to run it, follow these steps:

### Step 1: Get Crash Logs
1. Connect your phone to your computer via USB
2. Enable USB Debugging on your phone (Developer Options > USB Debugging)
3. Run the debug script: `debug_crash.bat`
4. Try to launch the app on your phone
5. Look for error messages in the console output

### Step 2: Common Crash Causes & Solutions

**ServiceLocator Initialization Issues**
- **Symptoms**: App crashes immediately on startup
- **Solution**: The app should now have safety checks, but if it still crashes, try clearing app data

**Configuration Access Issues**  
- **Symptoms**: App crashes when opening settings or changing layouts
- **Solution**: Clear app data and restart the app

**Permission Issues**
- **Symptoms**: App crashes when trying to access notifications or device admin
- **Solution**: Grant permissions manually in Android Settings > Apps > Fossify Launcher > Permissions

**Memory Issues**
- **Symptoms**: App crashes after running for a while or when opening large folders
- **Solution**: Restart your phone and try again

### Step 3: Reset App Data
If the app continues to crash:
1. Go to Android Settings > Apps > Fossify Launcher
2. Tap "Storage"
3. Tap "Clear Storage" or "Clear Data"
4. Restart the app

### Step 4: Check Compatibility
**Minimum Requirements:**
- Android 7.0 (API 24) or higher
- At least 2GB RAM
- 100MB free storage

**Recommended:**
- Android 12+ for blur effects
- Android 13+ for themed icons

### Step 5: Debug Mode Features
The debug build includes:
- Enhanced error logging
- Graceful fallbacks for missing features
- Safety checks for ServiceLocator access
- Exception handling for configuration access

### Step 6: Get Help
If the issue persists:
1. Note the exact error message from logcat
2. Note your Android version and device model
3. Note what you were doing when the crash occurred
4. Check if the crash happens in specific scenarios (settings, folder opening, etc.)

## Feature-Specific Issues

### Icon Size/Grid Margin Settings
- If settings don't apply: Try toggling the setting off and on again
- If icons look distorted: Reset to default values (100% icon size, 0dp margin)

### Transition Effects
- If animations are choppy: Try "None" or "Fade" transition mode
- If drawer doesn't open: Disable transition effects temporarily

### Notification Badges
- If badges don't appear: Check notification access in Android Settings
- If app crashes when enabling badges: Keep the feature disabled for now

### Background Blur
- If blur doesn't work: Your device may not support Android 12+ blur effects
- If performance is poor: Disable blur effects or reduce intensity

## Safe Mode
If you need to reset everything to a working state:
1. Uninstall the app completely
2. Reinstall from the APK
3. Don't change any settings initially
4. Test basic functionality first
5. Enable features one by one to identify problematic ones
