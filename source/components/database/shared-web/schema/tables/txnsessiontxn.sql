-- Table: shared_schema.txtsessiontxn

-- DROP TABLE IF EXISTS shared_schema.txtsessiontxn;

CREATE TABLE IF NOT EXISTS shared_schema.txtsessiontxn
(
    txnid bigint NOT NULL DEFAULT nextval('shared_schema.txtsessiontxn_txnid_seq'::regclass),
    sessionid bigint NOT NULL,
    operationid character varying(55) COLLATE pg_catalog."default" NOT NULL,
    userid integer,
    txnstarttime timestamp with time zone NOT NULL,
    txnlatencymillis bigint,
    httpresponsecode integer,
    request text COLLATE pg_catalog."default",
    response text COLLATE pg_catalog."default",
    CONSTRAINT txtsessiontxn_pkey PRIMARY KEY (txnid),
    CONSTRAINT txtsessiontxn_operationid_fkey FOREIGN KEY (operationid)
        REFERENCES shared_schema.lkpoperation (operationid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT txtsessiontxn_sessionid_fkey FOREIGN KEY (sessionid)
        REFERENCES shared_schema.txtsession (sessionid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT txtsessiontxn_userid_fkey FOREIGN KEY (userid)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.txtsessiontxn
    OWNER to familytree_master;

COMMENT ON TABLE shared_schema.txtsessiontxn
    IS 'Table holds list of transactions perform in session';

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnid
    IS 'Unique transaction id.';

COMMENT ON COLUMN shared_schema.txtsessiontxn.sessionid
    IS 'Session id.';

COMMENT ON COLUMN shared_schema.txtsessiontxn.operationid
    IS 'Operation id';

COMMENT ON COLUMN shared_schema.txtsessiontxn.userid
    IS 'User who performaing this operation';

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnstarttime
    IS 'Transaction start time';

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnlatencymillis
    IS 'Time took to process request in millis';

COMMENT ON COLUMN shared_schema.txtsessiontxn.httpresponsecode
    IS 'HTTP response code for this operation';

COMMENT ON COLUMN shared_schema.txtsessiontxn.request
    IS 'Request object. This can be complete json object';

COMMENT ON COLUMN shared_schema.txtsessiontxn.response
    IS 'Response object. This can be complete json object';