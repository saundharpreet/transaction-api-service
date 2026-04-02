FROM eclipse-temurin:21-jdk

# 1. Install jq and curl using apt (Debian/Ubuntu)
RUN apt-get update && apt-get install -y \
    curl \
    jq \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 2. Copy your artifacts
COPY target/*.jar app.jar
COPY vault/load-secrets.sh /app/load-secrets.sh
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
COPY ocp/secrets/vault-secrets.json /app/secrets-template.json

RUN sed -i 's/\r$//' /app/*.sh && \
    tr -cd '[:print:]\n' < /app/secrets-template.json > /app/secrets-template.json.tmp && \
    mv /app/secrets-template.json.tmp /app/secrets-template.json && \
    echo "" >> /app/secrets-template.json && \
    chmod +x /app/*.sh

ENTRYPOINT ["/bin/bash", "/app/docker-entrypoint.sh"]