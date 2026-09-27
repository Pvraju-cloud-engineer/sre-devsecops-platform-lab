#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
service_dir="${repo_root}/services/claims-api"
env_file="${service_dir}/.env"

if [[ -e "$env_file" ]]; then
  echo "Refusing to overwrite existing services/claims-api/.env" >&2
  exit 1
fi

if ! command -v openssl >/dev/null 2>&1; then
  echo "openssl is required to generate the local database password" >&2
  exit 1
fi

umask 077
db_password="$(openssl rand -hex 24)"
{
  sed '/^DB_PASSWORD=/d' "${service_dir}/.env.example"
  printf 'DB_PASSWORD=%s\n' "$db_password"
} > "$env_file"
unset db_password

echo "Created services/claims-api/.env; secret values were not displayed."
