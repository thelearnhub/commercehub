#!/usr/bin/env bash
# Run this from inside the commercehub/ repo root.
# Creates the Phase 0 folder skeleton with .gitkeep placeholders so empty
# directories survive the first git commit (git doesn't track empty folders).
set -euo pipefail

dirs=(
  "services/auth-service"
  "services/user-service"
  "services/product-service"
  "services/cart-service"
  "services/order-service"
  "services/payment-service"
  "services/inventory-service"
  "services/shipping-service"
  "services/notification-service"
  "services/review-service"
  "services/search-service"
  "services/recommendation-service"
  "services/analytics-service"
  "services/fraud-service"
  "platform/api-gateway"
  "platform/eureka-server"
  "platform/config-server"
  "platform/scheduler"
  "libs/common-dto"
  "libs/common-exceptions"
  "libs/common-tracing"
  "libs/common-kafka"
  "libs/common-security"
  "libs/common-testing"
  "infra/docker-compose"
  "infra/k8s"
  "infra/helm"
  "infra/terraform"
  "infra/db/migrations"
  "contracts"
  "docs/adr"
  "docs/runbooks"
  "docs/feature-specs"
  ".github/workflows"
)

for d in "${dirs[@]}"; do
  mkdir -p "$d"
  touch "$d/.gitkeep"
done

echo "Skeleton created."
echo "Next: git add -A && git commit -m 'chore: Phase 0 repo skeleton' && git push"
