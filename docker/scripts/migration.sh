#!/usr/bin/env bash

set -euo pipefail

mongoimport --host localhost --db youtube --collection category --type json --file /docker-entrypoint-initdb.d/category.json --jsonArray
