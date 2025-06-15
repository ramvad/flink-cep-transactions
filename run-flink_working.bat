@echo off
echo Starting Flink CEP Transaction Detector...

set FLINK_HOME=C:\Users\svgks\flink-1.17.1
set JAR_PATH=C:\Users\svgks\Downloads\flink-cep-transactions\target\flink-cep-transactions-1.0.jar
set LOG_FILE=cep_bat_output.log

:: Determine input CSV (first CLI arg or default)
if "%~1"=="" (
    set INPUT_FILE=data\transactions_bulk.csv
    echo No CSV path provided, defaulting to %INPUT_FILE%
) else (
    set INPUT_FILE=%~1
    echo Using CSV path from command line: %INPUT_FILE%
)

:: Delete existing log file if it exists
if exist %LOG_FILE% (
    echo Cleaning up old log file...
    del /f %LOG_FILE%
)

:: Delete existing log file if it exists
if exist %LOG_FILE% (
    echo Cleaning up old log file...
    del /f %LOG_FILE%
)

:: Run the application and redirect output to log file
echo Running application, output will be logged to %LOG_FILE%
java -Dlog4j.configurationFile=src/main/resources/log4j2.properties ^
     --add-opens java.base/java.lang=ALL-UNNAMED ^
     --add-opens java.base/java.util=ALL-UNNAMED ^
     -cp "%JAR_PATH%;%FLINK_HOME%\lib\*" ^
     com.example.FlinkCEPTransactionDetector "%INPUT_FILE%" > %LOG_FILE% 2>&1

if errorlevel 1 (
    echo Error running Flink application. Check %LOG_FILE% for details.
    pause
    exit /b 1
)

echo Flink application completed successfully. See %LOG_FILE% for details.
pause