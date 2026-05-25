CREATE TABLE shared_schema.txtsessiontxn (
    txnid bigint NOT NULL,
    sessionid bigint NOT NULL,
    operationid character varying(55) NOT NULL,
    userid integer,
    txnstarttime timestamp with time zone NOT NULL,
    txnlatencymillis bigint,
    httpresponsecode integer,
    request text,
    response text
);

ALTER TABLE shared_schema.txtsessiontxn OWNER TO familytree_master;

COMMENT ON TABLE shared_schema.txtsessiontxn IS 'Table holds list of transactions perform in session';

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnid IS 'Unique transaction id.';

COMMENT ON COLUMN shared_schema.txtsessiontxn.sessionid IS 'Session id.';

COMMENT ON COLUMN shared_schema.txtsessiontxn.operationid IS 'Operation id';

COMMENT ON COLUMN shared_schema.txtsessiontxn.userid IS 'User who performaing this operation';

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnstarttime IS 'Transaction start time';

COMMENT ON COLUMN shared_schema.txtsessiontxn.txnlatencymillis IS 'Time took to process request in millis';

COMMENT ON COLUMN shared_schema.txtsessiontxn.httpresponsecode IS 'HTTP response code for this operation';

COMMENT ON COLUMN shared_schema.txtsessiontxn.request IS 'Request object. This can be complete json object';

COMMENT ON COLUMN shared_schema.txtsessiontxn.response IS 'Response object. This can be complete json object';

CREATE SEQUENCE shared_schema.txtsessiontxn_txnid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.txtsessiontxn_txnid_seq OWNER TO familytree_master;

ALTER SEQUENCE shared_schema.txtsessiontxn_txnid_seq OWNED BY shared_schema.txtsessiontxn.txnid;

ALTER TABLE ONLY shared_schema.txtsessiontxn ALTER COLUMN txnid SET DEFAULT nextval('shared_schema.txtsessiontxn_txnid_seq'::regclass);

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_pkey PRIMARY KEY (txnid);

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_operationid_fkey FOREIGN KEY (operationid) REFERENCES shared_schema.lkpoperation(operationid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_sessionid_fkey FOREIGN KEY (sessionid) REFERENCES shared_schema.txtsession(sessionid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.txtsessiontxn
    ADD CONSTRAINT txtsessiontxn_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.txtsessiontxn TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.txtsessiontxn_txnid_seq TO familytree_app_role;
