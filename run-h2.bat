@echo off
setlocal
echo ===================================================================
echo  Starting EventHub with In-Memory H2 Database
echo  (Zero-Configuration Mode - No MySQL server required)
echo ===================================================================

if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-21.0.12.1" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12.1"
    ) else if exist "C:\Program Files\Java\latest" (
        set "JAVA_HOME=C:\Program Files\Java\latest"
    )
)

if "%JAVA_HOME%"=="" (
    echo [INFO] JAVA_HOME not explicitly set; using default java from PATH.
) else (
    echo [INFO] Using JAVA_HOME: %JAVA_HOME%
)

echo.
echo Launching with H2 In-Memory Database...
echo Web UI: http://localhost:8080
echo H2 DB Console: http://localhost:8080/h2-console
echo.
call .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=h2"

pause
