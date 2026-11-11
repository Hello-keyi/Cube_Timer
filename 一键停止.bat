@echo off
chcp 65001 >nul
title 魔方计时器 - 一键停止
setlocal
set "WSL_DIR=/home/yi/workplace/project/Cube_Timer"
echo.
echo   正在停止魔方计时器的服务 ...
echo.
wsl -d Ubuntu-22.04 -u root bash "%WSL_DIR%/scripts/stop.sh"
echo.
timeout /t 3 /nobreak >nul
endlocal
