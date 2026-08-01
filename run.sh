#!/bin/bash

# Configurações
JAR_NAME="gps2526_g42-0.1.0-SNAPSHOT-all.jar"
MAIN_CLASS="pt.isec.gps2526_g42.surprise_me.SurpriseMeMain"
JAVAFX_SDK="javafx-sdk/mac/lib"

echo "================================================"
echo "           SurpriseMe - Launcher"
echo "================================================"
echo ""

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo "[ERRO] Java não encontrado. Por favor instale o JDK 21 ou superior."
    exit 1
fi

# Procurar JAR
echo "Procurando $JAR_NAME..."
JAR_PATH=""

# Primeiro: verificar pasta atual
if [ -f "$JAR_NAME" ]; then
    JAR_PATH="$PWD/$JAR_NAME"
fi

# Segundo: procurar na pasta target
if [ -z "$JAR_PATH" ] && [ -f "target/$JAR_NAME" ]; then
    JAR_PATH="$PWD/target/$JAR_NAME"
fi

# Terceiro: procurar recursivamente
if [ -z "$JAR_PATH" ]; then
    JAR_PATH=$(find . -name "$JAR_NAME" -type f 2>/dev/null | head -n 1)
fi

if [ -z "$JAR_PATH" ]; then
    echo "[ERRO] JAR não encontrado: $JAR_NAME"
    exit 1
fi

echo "Encontrado: $JAR_PATH"
echo ""

# Verificar JavaFX SDK
if [ ! -d "$JAVAFX_SDK" ]; then
    echo "[ERRO] Pasta javafx-sdk/mac/lib não encontrada."
    exit 1
fi

# Executar aplicação
echo "Iniciando SurpriseMe..."
echo ""

java --module-path "$JAVAFX_SDK" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics \
     --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
     -cp "$JAR_PATH" \
     $MAIN_CLASS

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERRO] Falha ao executar a aplicação."
    exit $?
fi

echo ""
echo "Aplicação encerrada com sucesso."
