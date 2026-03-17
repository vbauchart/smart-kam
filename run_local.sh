#!/bin/bash
set -e

docker compose up -d
mvn spring-boot:run
