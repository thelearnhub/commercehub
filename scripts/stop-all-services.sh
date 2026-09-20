#!/usr/bin/env bash

# CommerceHub Service Terminator

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/logs"

echo "============================================================"
echo "🛑 Stopping All CommerceHub Microservices"
echo "============================================================"

if [ -d "${LOG_DIR}" ]; then
  for pid_file in "${LOG_DIR}"/*.pid; do
    if [ -f "${pid_file}" ]; then
      pid=$(cat "${pid_file}")
      service=$(basename "${pid_file}" .pid)
      if kill -0 "${pid}" 2>/dev/null; then
        echo "Killing ${service} (PID: ${pid})..."
        kill -9 "${pid}" 2>/dev/null || true
      fi
      rm -f "${pid_file}"
    fi
  done
fi

echo "Stopping any remaining Java Boot processes..."
pkill -f 'bootRun' || true

echo "✅ All microservices stopped."
