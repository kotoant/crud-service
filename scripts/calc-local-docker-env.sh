#!/bin/bash

# keep in sync with io.github.kotoant.crud.test.BaseTestcontainersTest and scripts/stop-and-remove-testcontainers.sh:
DB_PORT=$(docker ps -a -q -f "label=io.github.kotoant.crud.testcontainers.postgres=crud" | xargs -I{} docker port {} 5432 | cut -c9-)

echo -n 'DB_HOST=localhost:' >local.env
echo "$DB_PORT" >>local.env
