@echo off
echo Starting crash debugging for Fossify Launcher...
echo.

REM Set Android SDK path
set ANDROID_HOME=%LOCALAPPDATA%\Android\Sdk
set ADB=%ANDROID_HOME%\platform-tools\adb.exe

echo Checking connected devices...
"%ADB%" devices
echo.

echo Installing debug APK...
"%ADB%" install -r app\build\outputs\apk\core\debug\app-core-debug.apk
echo.

echo Clearing logcat buffer...
"%ADB%" logcat -c

echo Starting logcat monitoring (Ctrl+C to stop)...
echo Look for lines containing "fossify", "FATAL", or "AndroidRuntime"
echo.
"%ADB%" logcat | findstr /i "fossify FATAL AndroidRuntime"
