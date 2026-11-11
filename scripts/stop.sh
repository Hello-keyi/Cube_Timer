#!/usr/bin/env bash
# 一键停止：后端 jar + Nginx + Redis + MySQL（需 root 运行）
set -u
echo "[1/4] 停止后端 ..."
pkill -f 'backend-0.0.1-SNAPSHOT.jar' 2>/dev/null && echo "  已停止" || echo "  未在运行"

echo "[2/4] 停止 Nginx ..."
nginx -s stop 2>/dev/null && echo "  已停止" || echo "  未在运行"

echo "[3/4] 停止 Redis ..."
service redis-server stop >/dev/null 2>&1 && echo "  已停止" || echo "  未在运行"

echo "[4/4] 停止 MySQL ..."
service mysql stop >/dev/null 2>&1 && echo "  已停止" || echo "  未在运行"

echo
echo "全部停止完成。重新启动请双击「一键启动.bat」。"
