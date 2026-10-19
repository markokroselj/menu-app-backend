#!/bin/sh
set -eu

: "${SFTP_HOST:?SFTP_HOST is required}"
: "${SFTP_PORT:?SFTP_PORT is required}"
: "${SFTP_USER:?SFTP_USER is required}"
: "${SFTP_PASSWORD:?SFTP_PASSWORD is required}"
: "${VERCEL_DEPLOY_HOOK:?VERCEL_DEPLOY_HOOK is required}"
: "${SFTP_REMOTE_PATH:?SFTP_REMOTE_PATH is required}"

FILE="/data/output.json"

if [ ! -f "$FILE" ]; then
    echo "Error: File not found: $FILE" >&2
    exit 1
fi

printf 'put "%s" "%s"\n' "$FILE" "$SFTP_REMOTE_PATH" |
    sshpass -p "$SFTP_PASSWORD" sftp \
        -o BatchMode=no \
        -o StrictHostKeyChecking=no \
        -P "${SFTP_PORT:-22}" \
        -b - \
        "$SFTP_USER@$SFTP_HOST" >/dev/null 2>&1

echo "Upload successful ✅"

curl --fail --silent --show-error \
    --request POST \
    "$VERCEL_DEPLOY_HOOK"

echo "Deploy webhook called successfully"
