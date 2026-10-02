@echo off
echo ==============================================================================
echo DAVIS: Digital Attribution ^& Verification Intelligence System
echo SIH 2026 ^| PS ID: SIH26151 ^| Team: JugaaduSloths (185528)
echo ==============================================================================
echo.

cd /d "%~dp0\..\backend"
echo [INFO] Starting Spring Boot application on port 8080...
echo [INFO] Access UI at: http://localhost:8080
echo [INFO] Press Ctrl+C to terminate.
echo.

mvn spring-boot:run
