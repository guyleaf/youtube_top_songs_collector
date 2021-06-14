#!/bin/bash

set -euo pipefail

if ! [ -d "./db" ]; then
  mkdir -p "db"
fi

if [ $# -ge 1 ]; then
    if [ "$1" = "migration" ]; then
        rm -rf ./db/*
    fi
fi

docker compose up --build
