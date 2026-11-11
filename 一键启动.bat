@echo off
chcp 65001 >nul
title 魔方计时器 - 一键启动
setlocal
rem 本机专用启动器：项目固定位于 WSL 的这个路径（如移动过项目，请改这一行）
set "WSL_DIR=/home/yi/workplace/project/Cube_Timer"
echo.
echo   ==========================================
echo      魔方计时器 · 一键启动
echo   ==========================================
echo.
echo   正在启动服务（首次约 20~40 秒，请稍候）...
echo.
wsl -d Ubuntu-22.04 -u root bash "%WSL_DIR%/scripts/start.sh"
echo.
if /i "%~1"=="--no-browser" goto :skip_browser
echo   正在打开浏览器 ...
start "" "http://127.0.0.1/"
:skip_browser
echo.
echo   如果没有自动打开浏览器，请手动访问： http://127.0.0.1/
echo   停止服务请双击「一键停止.bat」
timeout /t 4 /nobreak >nul
endlocal
