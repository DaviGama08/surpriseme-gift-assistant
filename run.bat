@echo off
setlocal enabledelayedexpansion

:: Configurações
set JAR_NAME=gps2526_g42-0.1.0-SNAPSHOT-all.jar
set MAIN_CLASS=pt.isec.gps2526_g42.surprise_me.SurpriseMeMain
set JAVAFX_SDK=javafx-sdk\lib
set JAVAFX_NATIVE=javafx-sdk\bin

echo ================================================
echo           SurpriseMe - Launcher
echo ================================================
echo.

:: Verificar se Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Java nao encontrado. Por favor instale o JDK 21 ou superior.
    pause
    exit /b 1
)

:: Procurar JAR
echo Procurando %JAR_NAME%...
set JAR_PATH=

:: Primeiro: verificar pasta atual
if exist "%JAR_NAME%" (
    set JAR_PATH=%CD%\%JAR_NAME%
)

:: Segundo: procurar na pasta target
if not defined JAR_PATH (
    if exist "target\%JAR_NAME%" (
        set JAR_PATH=%CD%\target\%JAR_NAME%
    )
)

:: Terceiro: procurar recursivamente
if not defined JAR_PATH (
    for /r %%i in (%JAR_NAME%) do (
        if exist "%%i" (
            set JAR_PATH=%%i
            goto :found
        )
    )
)

:found
if not defined JAR_PATH (
    echo [ERRO] JAR nao encontrado: %JAR_NAME%
    pause
    exit /b 1
)

echo Encontrado: !JAR_PATH!
echo.

:: Verificar JavaFX SDK
if not exist "%JAVAFX_SDK%" (
    echo [ERRO] Pasta javafx-sdk\lib nao encontrada.
    pause
    exit /b 1
)

if not exist "%JAVAFX_NATIVE%" (
    echo [ERRO] Pasta javafx-sdk\bin nao encontrada.
    pause
    exit /b 1
)

:: Executar aplicação
echo Iniciando SurpriseMe...
echo.

java --module-path "%JAVAFX_SDK%" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
     --add-opens javafx.graphics/com.sun.javafx.application=ALL-UNNAMED ^
     --enable-native-access=javafx.graphics ^
     -Djava.library.path="%JAVAFX_NATIVE%" ^
     -cp "!JAR_PATH!" ^
     %MAIN_CLASS%

if %errorlevel% neq 0 (
    echo.
    echo [ERRO] Falha ao executar a aplicacao.
    pause
    exit /b %errorlevel%
)

echo.
echo Aplicacao encerrada com sucesso.
pause
