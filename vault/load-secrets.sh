#!/bin/bash
set -e

# Path to the JSON template
TEMPLATE_FILE=${TEMPLATE_FILE:-/app/secrets-template.json}

echo "🔐 Loading secrets from: $TEMPLATE_FILE"

# ---- Dependencies Check ----
command -v jq >/dev/null 2>&1 || { echo "❌ jq required"; exit 1; }
command -v curl >/dev/null 2>&1 || { echo "❌ curl required"; exit 1; }

# ---- Vault Config ----
VAULT_ADDR=$(jq -r '.vault.url' "$TEMPLATE_FILE")
AUTH_METHOD=$(jq -r '.vault.auth.method' "$TEMPLATE_FILE")

echo "Vault: $VAULT_ADDR"

# ---- Authenticate ----
if [ "$AUTH_METHOD" == "userpass" ]; then
  USERNAME=$(jq -r '.vault.auth.username' "$TEMPLATE_FILE")
  PASSWORD=$(jq -r '.vault.auth.password' "$TEMPLATE_FILE")

  echo "🔑 Logging in (userpass) for user: $USERNAME..."

  LOGIN_RESPONSE=$(curl -s --request POST \
    --data "{\"password\":\"$PASSWORD\"}" \
    "$VAULT_ADDR/v1/auth/userpass/login/$USERNAME")

  VAULT_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.auth.client_token')

elif [ "$AUTH_METHOD" == "token" ]; then
  VAULT_TOKEN=$(jq -r '.vault.auth.token' "$TEMPLATE_FILE")

else
  echo "❌ Unsupported auth method: $AUTH_METHOD"
  exit 1
fi

if [ -z "$VAULT_TOKEN" ] || [ "$VAULT_TOKEN" == "null" ]; then
  echo "❌ Vault authentication failed. Check your credentials/token."
  exit 1
fi

echo "✅ Authenticated successfully"

# ---- Get secrets array length ----
if [ ! -r "$TEMPLATE_FILE" ]; then
  echo "❌ ERROR: Cannot read $TEMPLATE_FILE"
  exit 1
fi

SECRET_COUNT=$(jq '.secrets | length' "$TEMPLATE_FILE")
echo "📊 Found $SECRET_COUNT secrets to process."

# ---- Fetching Logic ----
# We use >&2 for logging so the function only "returns" the JSON string
get_secret_data() {
  local path=$1
  echo "📦 Fetching: $path" >&2

  RESPONSE=$(curl -s -H "X-Vault-Token: $VAULT_TOKEN" "$VAULT_ADDR/v1/$path")

  # Validate JSON response
  if ! echo "$RESPONSE" | jq -e . >/dev/null 2>&1; then
    echo "❌ Error: Vault returned non-JSON response for $path" >&2
    echo "Dump: $RESPONSE" >&2
    exit 1
  fi

  # Extract data (Handles both KV-V1 and KV-V2 structures)
  CLEAN_DATA=$(echo "$RESPONSE" | jq -r '.data.data // .data')

  if [ "$CLEAN_DATA" == "null" ]; then
    echo "❌ Error: No data found at $path. Ensure the path is correct." >&2
    exit 1
  fi

  # Output ONLY the JSON data to stdout
  echo "$CLEAN_DATA"
}

# ---- Process secrets loop ----
for (( i=0; i<$SECRET_COUNT; i++ ))
do
  # Extract metadata for current secret index
  PATH_VAL=$(jq -r ".secrets[$i].path" "$TEMPLATE_FILE")
  KEY_VAL=$(jq -r ".secrets[$i].key" "$TEMPLATE_FILE")
  TARGET=$(jq -r ".secrets[$i].to" "$TEMPLATE_FILE")

  # Fetch the JSON block from Vault
  DATA=$(get_secret_data "$PATH_VAL")

  # Extract the specific key value from that block
  VALUE=$(echo "$DATA" | jq -r --arg KEY "$KEY_VAL" '.[$KEY]')

  if [ "$VALUE" == "null" ] || [ -z "$VALUE" ]; then
    echo "❌ Missing key '$KEY_VAL' in $PATH_VAL"
    exit 1
  fi

  # Inject into Environment or File
  if [ "$TARGET" == "env" ]; then
    NAME=$(jq -r ".secrets[$i].name" "$TEMPLATE_FILE")
    export "$NAME"="$VALUE"
    echo "   ✔ ENV: $NAME"

  elif [ "$TARGET" == "file" ]; then
    DEST=$(jq -r ".secrets[$i].destination" "$TEMPLATE_FILE")
    mkdir -p "$(dirname "$DEST")"
    echo "$VALUE" > "$DEST"
    echo "   ✔ FILE: $DEST"
  fi
done

echo "🎉 All secrets loaded successfully into the environment."