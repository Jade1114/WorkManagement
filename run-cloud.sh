#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
CLOUD_DIR="$ROOT_DIR/cloud"

if ! command -v mvn >/dev/null 2>&1; then
  echo "错误：未找到 mvn，请先安装 Maven。"
  exit 1
fi

if [ ! -d "$CLOUD_DIR" ]; then
  echo "错误：未找到 cloud 目录：$CLOUD_DIR"
  exit 1
fi

PIDS=()

cleanup() {
  echo
  echo "正在停止 Cloud 服务..."
  for pid in "${PIDS[@]:-}"; do
    if kill -0 "$pid" >/dev/null 2>&1; then
      kill "$pid" >/dev/null 2>&1 || true
    fi
  done
  wait || true
}

trap cleanup INT TERM EXIT

start_service() {
  local module="$1"
  local port="$2"

  echo "启动 $module (port: $port)..."
  (
    cd "$CLOUD_DIR"
    mvn -pl "$module" spring-boot:run
  ) &
  PIDS+=("$!")
}

start_service "user-service" "8081"
sleep 2
start_service "education-service" "8082"
sleep 2
start_service "gateway-service" "8083"

echo

echo "三个 Cloud 服务已启动："
echo "- user-service: http://localhost:8081"
echo "- education-service: http://localhost:8082"
echo "- gateway-service: http://localhost:8083"
echo
echo "按 Ctrl+C 可停止全部服务。"

wait
