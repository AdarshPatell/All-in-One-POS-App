# ChronoPos - Build Guide for Executables

## 🚀 Building Native Executables for Windows and Mac

### Prerequisites
- **Java 17+** (with JDK, not just JRE)
- **JavaFX SDK** (if not using Maven dependencies)
- **Windows**: Visual Studio Build Tools or Visual Studio Community
- **Mac**: Xcode Command Line Tools

### Method 1: Using Build Scripts (Recommended)

#### For Windows (.exe):
```batch
# Run the build script
build-windows.bat
```
**Output**: `target/dist/ChronoPos-1.0.0.exe` (Windows Installer)

#### For Mac (.dmg):
```bash
# Make script executable and run
chmod +x build-macos.sh
./build-macos.sh
```
**Output**: `target/dist/ChronoPos-1.0.0.dmg` (Mac Installer)

---

### Method 2: Manual Maven Commands

#### Step-by-Step Process:

1. **Clean and Compile**
```bash
./mvnw clean compile package
```

2. **Create Custom Runtime (JLink)**
```bash
# Windows
jlink --module-path "target/classes;%JAVA_HOME%/jmods" --add-modules java.base,java.desktop,java.logging,java.sql,javafx.controls,javafx.fxml --output target/runtime --compress=2 --no-header-files --no-man-pages

# Mac/Linux
jlink --module-path "target/classes:$JAVA_HOME/jmods" --add-modules java.base,java.desktop,java.logging,java.sql,javafx.controls,javafx.fxml --output target/runtime --compress=2 --no-header-files --no-man-pages
```

3. **Create Native Installer (JPackage)**
```bash
# Windows EXE
jpackage --type exe --name "ChronoPos" --app-version "1.0.0" --vendor "ChronoPos Systems" --input target/classes --main-jar "../NewChronoPos-1.0-SNAPSHOT.jar" --main-class org.example.newchronopos.MainApplication --runtime-image target/runtime --dest target/dist --win-dir-chooser --win-menu --win-shortcut

# Mac DMG
jpackage --type dmg --name "ChronoPos" --app-version "1.0.0" --vendor "ChronoPos Systems" --input target/classes --main-jar "../NewChronoPos-1.0-SNAPSHOT.jar" --main-class org.example.newchronopos.MainApplication --runtime-image target/runtime --dest target/dist --mac-package-name "ChronoPos"
```

---

### Method 3: Simple JAR Distribution (Cross-Platform)

If you want a simpler approach that works on any system with Java:

```bash
# Create fat JAR with all dependencies
./mvnw clean compile assembly:single

# Create launch scripts
```

**Windows Launch Script (run-chronopos.bat):**
```batch
@echo off
java -jar NewChronoPos-1.0-SNAPSHOT-jar-with-dependencies.jar
pause
```

**Mac/Linux Launch Script (run-chronopos.sh):**
```bash
#!/bin/bash
java -jar NewChronoPos-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

### 📁 Output Files

After building, you'll find:

**Windows:**
- `ChronoPos-1.0.0.exe` - Windows installer
- `ChronoPos/` - Installed application folder

**Mac:**
- `ChronoPos-1.0.0.dmg` - Mac disk image installer
- `ChronoPos.app` - Mac application bundle

**Cross-Platform:**
- `NewChronoPos-1.0-SNAPSHOT-jar-with-dependencies.jar` - Executable JAR

---

### 🎯 Distribution Tips

1. **Windows**: Share the `.exe` file - users can install like any Windows software
2. **Mac**: Share the `.dmg` file - users can drag to Applications folder
3. **Cross-Platform**: Share the JAR + launch script + instructions to install Java

### 🔧 Troubleshooting

- **"jpackage not found"**: Ensure you're using JDK 17+ (not JRE)
- **Build fails**: Run `./mvnw clean` first
- **Missing dependencies**: Check if all modules are included in jlink command
- **Mac signing issues**: Add `--mac-sign` if you have Apple Developer certificate

### 📦 App Icons

Add these files for better-looking installers:
- `src/main/resources/icon.ico` (Windows, 256x256)
- `src/main/resources/icon.icns` (Mac, 1024x1024)
- `src/main/resources/icon.png` (Cross-platform, 256x256)
