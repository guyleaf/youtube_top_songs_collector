#!/usr/bin/env bash

set -euo pipefail

export PATH="$PATH:/hbase/bin"

bash start-hbase.sh

jps