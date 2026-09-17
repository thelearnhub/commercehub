#!/usr/bin/env bash
set -e

# CommerceHub Sequential Service Launcher

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/logs"
mkdir -p "${LOG_DIR}"

echo "============================================================"
echo "🚀 Starting CommerceHub Platform & Microservices Sequentially"
echo "============================================================"

# 1. Start Infrastructure Docker Containers (MySQL, Redis, Kafka, Schema Registry)
echo "📦 Step 1: Spinning up Docker Compose infrastructure..."
docker compose -f "${ROOT_DIR}/infra/docker-compose/docker-compose.yml" up -d

echo "⏳ Waiting for MySQL (3306) and Redis (6379) to be ready..."
until nc -z localhost 3306 2>/dev/null; do sleep 2; done
until nc -z localhost 6379 2>/dev/null; do sleep 2; done
echo "✅ Database & Cache infrastructure ready."

# Helper function to launch service and optionally wait for HTTP port
start_service() {
  local service_name="$1"
  local gradle_task="$2"
  local port="$3"

  echo ""
  echo "▶️  Launching ${service_name} (Port ${port})..."
  nohup "${ROOT_DIR}/gradlew" "${gradle_task}" > "${LOG_DIR}/${service_name}.log" 2>&1 &
  local pid=$!
  echo "${pid}" > "${LOG_DIR}/${service_name}.pid"

  if [ -n "${port}" ]; then
    echo "⏳ Waiting for ${service_name} on port ${port}..."
    local count=0
    until nc -z localhost "${port}" 2>/dev/null || [ $count -gt 30 ]; do
      sleep 2
      count=$((count + 1))
    done
    if nc -z localhost "${port}" 2>/dev/null; then
      echo "✅ ${service_name} is UP on port ${port}."
    else
      echo "⚠️  ${service_name} taking longer than expected. Check logs at ${LOG_DIR}/${service_name}.log"
    fi
  fi
}

# 2. Platform Infrastructure Services
start_service "eureka-server" ":platform:eureka-server:bootRun" 8761
start_service "config-server" ":platform:config-server:bootRun" 8888
start_service "api-gateway" ":platform:api-gateway:bootRun" 8080

# 3. Core Business Microservices
start_service "auth-service" ":services:auth-service:bootRun" 8081
start_service "user-service" ":services:user-service:bootRun" 8082
start_service "product-service" ":services:product-service:bootRun" 8083
start_service "cart-service" ":services:cart-service:bootRun" 8084
start_service "inventory-service" ":services:inventory-service:bootRun" 8085
start_service "payment-service" ":services:payment-service:bootRun" 8086
start_service "order-service" ":services:order-service:bootRun" 8087
start_service "shipping-service" ":services:shipping-service:bootRun" 8089
start_service "notification-service" ":services:notification-service:bootRun" 8089

echo ""
echo "============================================================"
echo "🎉 All 12 CommerceHub Services launched in ordered sequence!"
echo "📄 Logs saved in: ${LOG_DIR}/"
echo "🛑 To stop all services run: ./scripts/stop-all-services.sh"
echo "============================================================"
