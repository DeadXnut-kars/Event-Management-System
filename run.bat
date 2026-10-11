@echo off
setlocal
echo ===================================================================
echo  Starting EventHub (Spring Boot + MySQL)
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
echo Make sure MySQL is running on localhost:3306!
echo (Run 'run-h2.bat' if you want to test without a running MySQL server)
echo.
call .\mvnw.cmd spring-boot:run

pause
