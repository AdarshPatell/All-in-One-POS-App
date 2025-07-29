@echo off
echo Building ChronoPos for Windows...
echo.

REM Clean and compile the project
echo Step 1: Cleaning and compiling...
call mvnw.cmd clean compile

REM Create a JAR with dependencies
echo Step 2: Creating JAR with dependencies...
call mvnw.cmd dependency:copy-dependencies package

REM Create JLink runtime image
echo Step 3: Creating custom runtime image...
jlink --module-path "target/dependency;%JAVA_HOME%/jmods" ^
      --add-modules java.base,java.desktop,java.logging,java.sql,java.naming,java.management,jdk.unsupported ^
      --output target/runtime ^
      --compress=2 ^
      --no-header-files ^
      --no-man-pages

REM Check if icon exists, if not skip it
if exist "src\main\resources\icon.ico" (
    set ICON_PARAM=--icon src/main/resources/icon.ico
) else (
    set ICON_PARAM=
    echo Warning: Icon file not found, building without custom icon...
)

REM Clean previous builds
if exist "target\dist\" rmdir /s /q "target\dist\"
mkdir "target\dist"

REM Step 4a: First create app-image (this always works)
echo Step 4a: Creating Windows application bundle...
jpackage --type app-image ^
         --name "ChronoPos" ^
         --app-version "1.0.0" ^
         --vendor "ChronoPos Systems" ^
         --description "Point of Sale System with Licensing" ^
         --copyright "Copyright 2025 ChronoPos Systems" ^
         --input target ^
         --main-jar "ChronoPos.jar" ^
         --main-class org.example.newchronopos.MainApplication ^
         --runtime-image target/runtime ^
         --dest target/dist ^
         %ICON_PARAM%

if not exist "target\dist\ChronoPos\" (
    echo ❌ App-image creation failed!
    goto :error
)

echo ✅ App-image created successfully!

REM Step 4b: Now create MSI from the app-image
echo Step 4b: Creating MSI installer from app-image...
jpackage --type msi ^
         --name "ChronoPos" ^
         --app-version "1.0.0" ^
         --vendor "ChronoPos Systems" ^
         --description "Point of Sale System with Licensing" ^
         --copyright "Copyright 2025 ChronoPos Systems" ^
         --app-image "target\dist\ChronoPos" ^
         --dest target/dist ^
         --win-dir-chooser ^
         --win-menu ^
         --win-shortcut ^
         --win-upgrade-uuid "12345678-1234-1234-1234-123456789012"

echo.
if exist "target\dist\ChronoPos-1.0.0.msi" (
    echo ✅ MSI Build completed successfully!
    echo Windows MSI installer: target\dist\ChronoPos-1.0.0.msi
    echo File size:
    dir "target\dist\ChronoPos-1.0.0.msi" | find "ChronoPos"
    echo.
    echo ✅ Portable app also available: target\dist\ChronoPos\ChronoPos.exe
    goto :success
) else (
    echo ⚠️ MSI creation failed, but portable app is available
    echo Portable app folder: target\dist\ChronoPos\
    echo Run: target\dist\ChronoPos\ChronoPos.exe
    goto :success
)

:error
echo ❌ Build failed
echo Checking for errors...
if exist "target\dist\" (
    echo Contents of target\dist\:
    dir "target\dist\"
) else (
    echo target\dist\ folder was not created
)
goto :end

:success
echo.
echo 📦 Distribution ready!
echo You can share the installer/folder with users.

:end
echo.
pause
