#!/bin/bash

set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
-- revoke privileges on public, but keep it to install extensions

REVOKE CREATE ON SCHEMA public FROM PUBLIC;

-- create users

CREATE USER $RINHA_USER WITH ENCRYPTED PASSWORD '$RINHA_PASSWORD';

-- create all schemas

CREATE SCHEMA $RINHA_SCHEMA AUTHORIZATION $RINHA_USER;

GRANT $RINHA_USER TO $POSTGRES_USER;

-- create extensions

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA $RINHA_SCHEMA; -- provides cryptographic functions for PostgresSQL
CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA $RINHA_SCHEMA; -- provides GIN index operator, used for improving similarity searches using LIKE

-- configure $RINHA_USER

GRANT ALL PRIVILEGES ON SCHEMA $RINHA_SCHEMA TO $RINHA_USER;
GRANT CONNECT ON DATABASE $POSTGRES_DB TO $RINHA_USER;
EOSQL