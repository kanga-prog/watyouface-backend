#!/bin/sh
set -eu

if [ "$(id -u)" -ne 0 ]; then
    echo "docker-entrypoint: must start as root to initialize /app/media" >&2
    exit 1
fi

if ! command -v setpriv >/dev/null 2>&1; then
    echo "docker-entrypoint: setpriv is required to drop privileges" >&2
    exit 1
fi

# A Railway volume mounted here hides the directory prepared at image build
# time and can be root-owned. Restrict this ownership change to the media
# mount; application files remain immutable to the runtime process.
mkdir -p /app/media
chown -R spring:spring /app/media

exec setpriv --reuid=spring --regid=spring --init-groups "$@"
