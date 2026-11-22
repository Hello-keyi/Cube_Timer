#!/usr/bin/env bash
# 一键启动：MySQL + Redis + Nginx + 后端 jar（需 root 运行）
set -u
SELF_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJ="$(dirname "$SELF_DIR")"
JAR="$PROJ/backend/server/target/server-0.0.1-SNAPSHOT.jar"

echo "[1/4] 启动 MySQL ..."
service mysql start >/dev/null 2>&1 || true

echo "[2/4] 启动 Redis ..."
service redis-server start >/dev/null 2>&1 || true

echo "[3/4] 启动 Nginx ..."
if pgrep -x nginx >/dev/null 2>&1; then
  nginx -s reload 2>/dev/null || true
else
  nginx 2>/dev/null || echo "  ⚠️ Nginx 启动失败"
fi

echo "[4/4] 启动后端 ..."
if pgrep -f 'backend-0.0.1-SNAPSHOT.jar' >/dev/null 2>&1; then
  echo "  后端已在运行"
else
  if [ ! -f "$JAR" ]; then
    echo "  ❌ 找不到 jar：$JAR（先在 backend 目录执行 mvn -DskipTests package）"
    exit 1
  fi
  runuser -u yi -- bash -c "cd '$PROJ/backend' && nohup java -jar '$JAR' > /tmp/cube-timer-backend.log 2>&1 &"
  echo "  后端启动中（日志 /tmp/cube-timer-backend.log）"
fi

echo
echo "等待后端就绪 ..."
READY=0
for i in $(seq 1 60); do
  if curl -s --max-time 1 http://127.0.0.1:8081/hello >/dev/null 2>&1; then READY=1; break; fi
  sleep 1
done
[ "$READY" = "1" ] && echo "✅ 后端就绪（8081）" || echo "⚠️ 后端 60 秒内未就绪，日志：tail -50 /tmp/cube-timer-backend.log"

if curl -s --max-time 2 http://127.0.0.1/ >/dev/null 2>&1; then
  echo "✅ 前端网页就绪（http://127.0.0.1/）"
else
  echo "⚠️ 前端网页未就绪（检查 Nginx 配置）"
fi
