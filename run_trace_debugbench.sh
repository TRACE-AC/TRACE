#!/usr/bin/env bash
set -euo pipefail

model=$(python3 - <<'PYCODE'
import yaml, sys
cfg = yaml.safe_load(open("config.yaml"))
print(cfg["engine_config"]["model"])
PYCODE
)

max_try=$(python3 - <<'PYCODE'
import yaml, sys
cfg = yaml.safe_load(open("config.yaml"))
print(cfg["engine_config"]["max_try"])
PYCODE
)

python3 -m engine.trace_debugbench \
  --model "$model" \
  --max_try "$max_try"
