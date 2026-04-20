#!/usr/bin/env sh
set -eu

NACOS_URL="${NACOS_URL:-http://nacos:8848}"
GROUP="${NACOS_GROUP:-DEFAULT_GROUP}"
CONFIG_DIR="/config"

wait_for_nacos() {
  echo "Waiting for Nacos at ${NACOS_URL} ..."
  until curl -fsS "${NACOS_URL}/nacos/" >/dev/null 2>&1; do
    sleep 2
  done
  echo "Nacos is ready."
}

publish_config() {
  data_id="$1"
  file_path="$2"
  content="$(cat "$file_path")"

  curl -fsS -X POST "${NACOS_URL}/nacos/v1/cs/configs" \
    --data-urlencode "dataId=${data_id}" \
    --data-urlencode "group=${GROUP}" \
    --data-urlencode "type=yaml" \
    --data-urlencode "content=${content}" >/dev/null

  echo "Published ${data_id}"
}

wait_for_nacos
publish_config "db-config.yaml" "${CONFIG_DIR}/db-config.yaml"
publish_config "jwt-config.yaml" "${CONFIG_DIR}/jwt-config.yaml"

echo "All Nacos configs published."
