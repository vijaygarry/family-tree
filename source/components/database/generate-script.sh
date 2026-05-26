./pg-dump.sh --mode schema --dbname family_tree --username postgres --outdir ./schema

./pg-dump.sh --mode data --dbname family_tree --username postgres --outdir ./data
