#!/bin/bash
set -e

docker compose up -d postgres
mvn spring-boot:run
