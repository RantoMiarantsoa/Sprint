#!/bin/bash

set -euo pipefail

JAR_NAME="alpha"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/src"
BUILD_DIR="$SCRIPT_DIR/build"
JAR_DIR="/home/ranto/S4/spring/Sprint/lib"
WEB_LIB_DIR="/home/ranto/S4/spring/testSprint/src/main/webapp/WEB-INF/lib"
ALPHA_JAR_PATH="/home/ranto/S4/spring/Sprint/lib/alpha.jar"  # ← AJOUTER CETTE LIGNE
LIB_DIR="$SCRIPT_DIR/lib"
TOMCAT_SERVLET_API_JAR="/home/ranto/tomcat/lib/servlet-api.jar"
SOURCES_FILE="$BUILD_DIR/sources.txt"
CLASSPATH=""
CLASSPATH_ENTRIES=()

# Nettoyage et création des répertoires de sortie
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/classes" "$JAR_DIR" "$WEB_LIB_DIR"

# Compilation des fichiers Java avec les dépendances disponibles
find "$SRC_DIR" -name "*.java" > "$SOURCES_FILE"

# AJOUTER alpha.jar AU CLASSPATH
if [ -f "$ALPHA_JAR_PATH" ]; then
    CLASSPATH_ENTRIES+=("$ALPHA_JAR_PATH")
    echo "✓ alpha.jar ajouté au classpath"
fi

if [ -d "$LIB_DIR" ]; then
    while IFS= read -r jar_file; do
        CLASSPATH_ENTRIES+=("$jar_file")
    done < <(find "$LIB_DIR" -name "*.jar")
fi

if [ -f "$TOMCAT_SERVLET_API_JAR" ]; then
    CLASSPATH_ENTRIES+=("$TOMCAT_SERVLET_API_JAR")
fi

if [ "${#CLASSPATH_ENTRIES[@]}" -gt 0 ]; then
    CLASSPATH="$(IFS=:; echo "${CLASSPATH_ENTRIES[*]}")"
fi

if [ -n "$CLASSPATH" ]; then
    javac -cp "$CLASSPATH" -d "$BUILD_DIR/classes" @"$SOURCES_FILE"
else
    javac -d "$BUILD_DIR/classes" @"$SOURCES_FILE"
fi

# Générer le fichier .jar dans le dossier jar
jar -cvf "$JAR_DIR/$JAR_NAME.jar" -C "$BUILD_DIR/classes" .

# Copier le JAR vers WEB-INF/lib pour le projet web
cp "$JAR_DIR/$JAR_NAME.jar" "$WEB_LIB_DIR/$JAR_NAME.jar"

echo ""
echo "✓ Jar créé : $JAR_DIR/$JAR_NAME.jar"
echo "✓ Copié vers : $WEB_LIB_DIR/$JAR_NAME.jar"
echo ""