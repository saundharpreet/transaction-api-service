#!/bin/bash

set -e

echo "🚀 Starting container..."

# ---- Load secrets ----
if [ -f "/app/load-secrets.sh" ]; then
  echo "🔐 Running secrets loader..."
  source /app/load-secrets.sh
else
  echo "⚠️ No secrets script found, skipping..."
fi

# ---- Set Spring profile ----
SPRING_PROFILE=${SPRING_PROFILES_ACTIVE:-default}

echo "🌱 Using Spring profile: $SPRING_PROFILE"

# ---- Start application ----
echo "▶️ Starting Spring Boot application..."

exec java -jar /app/app.jar --spring.profiles.active=$SPRING_PROFILE
