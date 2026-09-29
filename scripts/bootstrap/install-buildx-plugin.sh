#!/usr/bin/env bash
set -euo pipefail

version="v0.17.1"

case "$(uname -m)" in
  x86_64)
    binary_arch="amd64"
    expected_sha256="aa7a9778349e1a8ace685e4c51a1d33e7a9b0aa6925d1c625b09cb3800eba696"
    ;;
  aarch64)
    binary_arch="arm64"
    expected_sha256="de05dccd47932eb9fd6e63781ab29d2b0b2c834bbdd19b51d7ea452b1fe378d3"
    ;;
  *)
    echo "Unsupported CPU architecture: $(uname -m)" >&2
    exit 1
    ;;
esac

asset="buildx-v0.17.1.linux-${binary_arch}"
url="https://github.com/docker/buildx/releases/download/${version}/${asset}"
plugin_dir="${HOME}/.docker/cli-plugins"
plugin_path="${plugin_dir}/docker-buildx"
download="$(mktemp)"
trap 'rm -f "$download"' EXIT

curl --fail --location --silent --show-error "$url" --output "$download"
printf '%s  %s\n' "$expected_sha256" "$download" | sha256sum --check
mkdir -p "$plugin_dir"
install -m 0755 "$download" "$plugin_path"

docker buildx version
