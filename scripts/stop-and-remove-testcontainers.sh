#!/bin/bash

declare -a labels=(
  # keep in sync with io.github.kotoant.crud.test.BaseTestcontainersTest and scripts/calc-local-docker-env.sh:
  "io.github.kotoant.crud.testcontainers.postgres=crud"
)

for label in "${labels[@]}"; do
  # https://stackoverflow.com/a/32074098
  docker rm $(docker stop $(docker ps -a -q --filter "label=$label" --format="{{.ID}}")) || true
done
