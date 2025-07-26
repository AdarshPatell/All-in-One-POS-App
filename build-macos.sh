#!/bin/bash
echo "Building ChronoPos for macOS..."
echo

# Clean and compile the project
echo "Step 1: Cleaning and compiling..."
./mvnw clean compile

# Create a JAR with dependencies
echo "Step 2: Creating JAR with dependencies..."
./mvnw dependency:copy-dependencies package

# Create JLink runtime image
echo "Step 3: Creating custom runtime image..."
jlink --module-path "target/classes:target/dependency:$JAVA_HOME/jmods" \
      --add-modules java.base,java.desktop,java.logging,java.sql,javafx.controls,javafx.fxml \
      --output target/runtime \
      --compress=2 \
      --no-header-files \
      --no-man-pages

# Create macOS app bundle with JPackage
echo "Step 4: Creating macOS app bundle..."
jpackage --type dmg \
         --name "ChronoPos" \
         --app-version "1.0.0" \
         --vendor "ChronoPos Systems" \
         --description "Point of Sale System with Licensing" \
         --copyright "Copyright 2025 ChronoPos Systems" \
         --input target/classes \
         --main-jar "../NewChronoPos-1.0-SNAPSHOT.jar" \
         --main-class org.example.newchronopos.MainApplication \
         --runtime-image target/runtime \
         --dest target/dist \
         --mac-package-name "ChronoPos" \
         --mac-package-identifier "com.chronopos.app" \
         --icon src/main/resources/icon.icns

echo
echo "Build completed!"
echo "macOS installer: target/dist/ChronoPos-1.0.0.dmg"
echo
