#!/usr/bin/env bash
set -euo pipefail

version="v5.5.1"

case "$(uname -m)" in
  x86_64)
    binary_arch="x86_64"
    expected_sha256="db1889184726840f75c4f9c001048430d4f25b3be3cb084d3ddd762bc0aed576"
    ;;
  aarch64)
    binary_arch="aarch64"
    expected_sha256="732e3a84c1a0f67256ce80bc2598a24546b10ca05f9faa97efceb1171ece2ef7"
    ;;
  *)
    echo "Unsupported CPU architecture: $(uname -m)" >&2
    exit 1
    ;;
esac

protocol="https"
host="github.com"
release_path="docker/compose/releases/download"
url="${protocol}://${host}/${release_path}/${version}/docker-compose-linux-${binary_arch}"

plugin_dir="${HOME}/.docker/cli-plugins"
plugin_path="${plugin_dir}/docker-compose"
download="$(mktemp)"
trap 'rm -f "$download"' EXIT

curl --fail --location --silent --show-error "$url" --output "$download"
printf '%s  %s\n' "$expected_sha256" "$download" | sha256sum --check
mkdir -p "$plugin_dir"
install -m 0755 "$download" "$plugin_path"

docker compose version
