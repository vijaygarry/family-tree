#!/usr/bin/env bash
# =============================================================================
# pg-dump.sh — Generic PostgreSQL per-table dump utility
#
# Dumps each table into its own <tableName>.sql file.
# If no tables are specified, all user tables found in
# information_schema.tables are discovered and dumped automatically.
#
# Usage:
#   ./pg-dump.sh --mode <schema|data> [OPTIONS] [--table <table>] ...
#
# Required:
#   -m, --mode        schema   Export DDL only (tables, indexes, sequences,
#                              constraints, grants, etc.)
#                     data     Export data only as INSERT statements with
#                              explicit column names (no DDL)
#
# Connection options (all optional — fall back to pg defaults / env vars):
#   -h, --host        DB host            (default: localhost, or PGHOST)
#   -p, --port        DB port            (default: 5432,     or PGPORT)
#   -d, --dbname      Database name      (required,          or PGDATABASE)
#   -U, --username    DB username        (default: current OS user, or PGUSER)
#   -W, --password    DB password        (set PGPASSWORD env var instead to
#                                         avoid the value appearing in history)
#
# Scope options:
#   -n, --schema      Limit discovery / dump to this PostgreSQL schema
#                     When discovering tables, filters information_schema.tables
#                     by this schema; otherwise all non-system schemas are used
#   -t, --table       Dump this specific table; can be repeated (optional)
#                     If omitted, all user tables are discovered automatically
#                     Format: table_name  or  schema_name.table_name
#
# Output options:
#   -o, --outdir      Directory to write <tableName>.sql files (default: .)
#   --no-owner        Omit ownership commands (default: included)
#   --no-privileges   Omit GRANT/REVOKE commands (default: included)
#
# Examples:
#   # Dump schema DDL for every table in 'finance' database into current dir
#   ./pg-dump.sh --mode data --dbname finance --username postgres --outdir ./data
#   ./pg-dump.sh --mode schema --dbname finance --username postgres --outdir ./schema
#   # Dump data for two specific tables into ./dumps/
#   ./pg-dump.sh --mode data --dbname finance --username postgres \
#                --table public.users --table public.orders --outdir ./dumps
#
#   # Dump schema for all tables in one PostgreSQL schema
#   ./pg-dump.sh --mode schema --dbname finance --schema public --outdir ./out
#
#   # Set credentials via env vars (recommended for CI/CD)
#   export PGHOST=db.example.com PGDATABASE=mydb PGUSER=myuser PGPASSWORD=secret
#   ./pg-dump.sh --mode data --outdir /tmp/dump
# =============================================================================

set -euo pipefail

# ── helpers ───────────────────────────────────────────────────────────────────

usage() {
    sed -n '/^# Usage:/,/^# ====/p' "$0" | sed 's/^# \{0,3\}//' | sed '$d'
    exit 1
}

err()  { echo "ERROR: $*" >&2; exit 1; }
info() { echo "[pg-dump] $*"; }

# Strip all pg_dump boilerplate from plain-format output:
#   1. Section-header blocks:  --\n-- Name: ...\n--
#   2. Standalone metadata:    -- Dumped from/by database version X
#   3. Session-setup statements emitted before the actual DDL/data
strip_pg_headers() {
    awk '
        # ── section-header blocks: --\n-- <text>\n-- ─────────────────────
        /^--$/ {
            saved = $0
            ret = (getline line)
            if (ret <= 0)        { print saved; next }   # EOF right after --
            if (line ~ /^-- /) {
                # header block: skip everything up to and including closing --
                do { ret = (getline) } while (ret > 0 && $0 !~ /^--$/)
            } else {
                print saved      # plain standalone -- comment, keep it
                print line
            }
            next
        }

        # ── standalone pg_dump metadata comment lines ─────────────────────
        /^-- Dumped (from|by) /  { next }

        # ── session-setup boilerplate SET / SELECT statements ─────────────
        /^SET (statement_timeout|lock_timeout|idle_in_transaction_session_timeout|client_encoding|standard_conforming_strings|check_function_bodies|xmloption|client_min_messages|row_security|default_tablespace|default_table_access_method)[ \t]*=/ { next }
        /^SELECT pg_catalog\.set_config\(/ { next }
        /^SELECT pg_catalog\.setval\(/    { next }

        # ── blank-line squeeze ────────────────────────────────────────────
        # Skip leading blank lines; collapse consecutive blanks into one.
        /^[[:space:]]*$/ {
            if (content_seen) blank_pending = 1
            next
        }
        {
            if (blank_pending) { print ""; blank_pending = 0 }
            content_seen = 1
            print
        }
    '
}

require_cmd() {
    command -v "$1" &>/dev/null \
        || err "'$1' not found. Install PostgreSQL client tools and ensure they are on your PATH."
}

# ── defaults ──────────────────────────────────────────────────────────────────

MODE=""
DB_HOST="${PGHOST:-localhost}"
DB_PORT="${PGPORT:-5432}"
DB_NAME="${PGDATABASE:-}"
DB_USER="${PGUSER:-}"
DB_PASS=""
PG_SCHEMA=""
OUT_DIR="."
NO_OWNER=""
NO_PRIVS=""
TABLE_NAMES=()   # plain table names as supplied by the user (or discovered)

# ── argument parsing ──────────────────────────────────────────────────────────

while [[ $# -gt 0 ]]; do
    case "$1" in
        -m|--mode)        MODE="$2";          shift 2 ;;
        -h|--host)        DB_HOST="$2";       shift 2 ;;
        -p|--port)        DB_PORT="$2";       shift 2 ;;
        -d|--dbname)      DB_NAME="$2";       shift 2 ;;
        -U|--username)    DB_USER="$2";       shift 2 ;;
        -W|--password)    DB_PASS="$2";       shift 2 ;;
        -n|--schema)      PG_SCHEMA="$2";     shift 2 ;;
        -t|--table)       TABLE_NAMES+=("$2"); shift 2 ;;
        -o|--outdir)      OUT_DIR="$2";       shift 2 ;;
        --no-owner)       NO_OWNER="--no-owner";      shift ;;
        --no-privileges)  NO_PRIVS="--no-privileges"; shift ;;
        --help)           usage ;;
        *) err "Unknown option: $1. Run with --help for usage." ;;
    esac
done

# ── validation ────────────────────────────────────────────────────────────────

require_cmd pg_dump
require_cmd psql

[[ -z "$MODE" ]]    && err "--mode is required (schema | data). Run with --help."
[[ -z "$DB_NAME" ]] && err "--dbname is required (or set PGDATABASE). Run with --help."

case "$MODE" in
    schema|data) ;;
    *) err "Invalid --mode '$MODE'. Must be 'schema' or 'data'." ;;
esac

# ── export password (keep it out of shell history) ────────────────────────────

[[ -n "$DB_PASS" ]] && export PGPASSWORD="$DB_PASS"

# ── ensure output directory exists ───────────────────────────────────────────

mkdir -p "$OUT_DIR"

# ── shared connection args (reused by both psql and pg_dump) ──────────────────

CONN_ARGS=(
    --host="$DB_HOST"
    --port="$DB_PORT"
    --dbname="$DB_NAME"
)
[[ -n "$DB_USER" ]] && CONN_ARGS+=(--username="$DB_USER")

# ── discover tables when none were supplied ───────────────────────────────────

if [[ ${#TABLE_NAMES[@]} -eq 0 ]]; then
    info "No tables specified — querying information_schema.tables …"

    if [[ -n "$PG_SCHEMA" ]]; then
        SCHEMA_FILTER="AND table_schema = '$PG_SCHEMA'"
    else
        # Exclude built-in system schemas
        SCHEMA_FILTER="AND table_schema NOT IN ('pg_catalog', 'information_schema')"
    fi

    DISCOVERY_SQL="
        SELECT table_schema || '.' || table_name
        FROM   information_schema.tables
        WHERE  table_type = 'BASE TABLE'
        $SCHEMA_FILTER
        ORDER  BY table_schema, table_name;"

    # Use 'while read' instead of mapfile/readarray — works on bash 3.x (macOS default)
    while IFS= read -r line; do
        # Strip stray whitespace and carriage returns; skip blank lines
        line="${line//[$'\r\n\t ']/}"
        [[ -n "$line" ]] && TABLE_NAMES+=("$line")
    done < <(
        psql "${CONN_ARGS[@]}" \
             --no-psqlrc --tuples-only --no-align \
             --command="$DISCOVERY_SQL"
    )

    [[ ${#TABLE_NAMES[@]} -eq 0 ]] \
        && err "No tables found. Check --schema and connection settings."

    info "Found ${#TABLE_NAMES[@]} table(s)."
fi

# ── base pg_dump flags (table and output file are added per iteration) ─────────

BASE_DUMP_ARGS=(
    "${CONN_ARGS[@]}"
    --format=plain
)

[[ -n "$NO_OWNER" ]] && BASE_DUMP_ARGS+=("$NO_OWNER")
[[ -n "$NO_PRIVS" ]] && BASE_DUMP_ARGS+=("$NO_PRIVS")

case "$MODE" in
    schema)
        BASE_DUMP_ARGS+=(--schema-only)
        ;;
    data)
        # --inserts + --column-inserts: portable, human-readable INSERT statements
        BASE_DUMP_ARGS+=(
            --data-only
            --inserts
            --column-inserts
        )
        ;;
esac

# ── print run summary ─────────────────────────────────────────────────────────

info "Mode       : $MODE"
info "Host       : $DB_HOST:$DB_PORT"
info "Database   : $DB_NAME"
[[ -n "$DB_USER"   ]] && info "User       : $DB_USER"
[[ -n "$PG_SCHEMA" ]] && info "PG Schema  : $PG_SCHEMA"
info "Tables     : ${#TABLE_NAMES[@]}"
info "Output dir : $OUT_DIR"
echo ""

# ── dump each table into its own file ────────────────────────────────────────

PASS=0
FAIL=0
FAILED_TABLES=()

for QUALIFIED_TABLE in "${TABLE_NAMES[@]}"; do
    [[ -z "$QUALIFIED_TABLE" ]] && continue

    # File name uses only the bare table name (the part after the last dot)
    TABLE_BASENAME="${QUALIFIED_TABLE##*.}"
    OUT_FILE="${OUT_DIR}/${TABLE_BASENAME}.sql"

    info "Dumping → $QUALIFIED_TABLE  ▸  $OUT_FILE"

    if pg_dump "${BASE_DUMP_ARGS[@]}" \
               --table="$QUALIFIED_TABLE" \
               | strip_pg_headers > "$OUT_FILE"; then
        FILE_SIZE=$(du -sh "$OUT_FILE" 2>/dev/null | cut -f1)
        info "  ✔  $OUT_FILE ($FILE_SIZE)"
        PASS=$((PASS + 1))
    else
        info "  ✗  Failed: $QUALIFIED_TABLE"
        FAILED_TABLES+=("$QUALIFIED_TABLE")
        FAIL=$((FAIL + 1))
    fi

    echo ""
done

# ── final summary ─────────────────────────────────────────────────────────────

info "════════════════════════════════════════════"
info "Complete. Success: $PASS  |  Failed: $FAIL"
info "Output dir: $OUT_DIR"

if [[ ${#FAILED_TABLES[@]} -gt 0 ]]; then
    echo ""
    info "Failed tables:"
    for t in "${FAILED_TABLES[@]}"; do
        info "  - $t"
    done
    exit 1
fi
