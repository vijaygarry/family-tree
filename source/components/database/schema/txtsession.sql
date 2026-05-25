CREATE TABLE shared_schema.txtsession (
    sessionid bigint NOT NULL,
    userid integer,
    channelid character varying(64) NOT NULL,
    active boolean DEFAULT true NOT NULL,
    authenticated boolean DEFAULT false NOT NULL,
    sessioncreationtime timestamp with time zone NOT NULL,
    logouttime timestamp with time zone,
    lastaccesstime timestamp with time zone,
    exitcode smallint DEFAULT 1,
    apphostname character varying(80) NOT NULL,
    clientipaddress character varying(55) NOT NULL,
    user_agent text
);

ALTER TABLE shared_schema.txtsession OWNER TO familytree_master;

COMMENT ON TABLE shared_schema.txtsession IS 'Maintain session created in application';

COMMENT ON COLUMN shared_schema.txtsession.sessionid IS 'Unique session id.';

COMMENT ON COLUMN shared_schema.txtsession.userid IS 'User this session belongs to';

COMMENT ON COLUMN shared_schema.txtsession.channelid IS 'Channel this session is created for. E.g. Server, Browser, Mobile';

COMMENT ON COLUMN shared_schema.txtsession.active IS 'Flag indicate if this is active session.';

COMMENT ON COLUMN shared_schema.txtsession.authenticated IS 'Flag indicate if this is authenticated session.';

COMMENT ON COLUMN shared_schema.txtsession.sessioncreationtime IS 'Session creation time';

COMMENT ON COLUMN shared_schema.txtsession.logouttime IS 'Session logout time';

COMMENT ON COLUMN shared_schema.txtsession.lastaccesstime IS 'Last access session time to see if session should be kept active.';

COMMENT ON COLUMN shared_schema.txtsession.exitcode IS 'How the session is terminated. Possible values 1-> User Logout, 2-> Session Timeout, 3-> Override by new session, 4-> Kill by Admin';

COMMENT ON COLUMN shared_schema.txtsession.apphostname IS 'application Hostname or IP of Application from where this session is created';

COMMENT ON COLUMN shared_schema.txtsession.clientipaddress IS 'Client IP address';

COMMENT ON COLUMN shared_schema.txtsession.user_agent IS 'Client user agent including browser name and version, client OS name and version. Obtain from user-agent header of HTTP request. This may not be accurate.';

CREATE SEQUENCE shared_schema.txtsession_sessionid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE shared_schema.txtsession_sessionid_seq OWNER TO familytree_master;

ALTER SEQUENCE shared_schema.txtsession_sessionid_seq OWNED BY shared_schema.txtsession.sessionid;

ALTER TABLE ONLY shared_schema.txtsession ALTER COLUMN sessionid SET DEFAULT nextval('shared_schema.txtsession_sessionid_seq'::regclass);

ALTER TABLE ONLY shared_schema.txtsession
    ADD CONSTRAINT txtsession_pkey PRIMARY KEY (sessionid);

ALTER TABLE ONLY shared_schema.txtsession
    ADD CONSTRAINT txtsession_userid_fkey FOREIGN KEY (userid) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.txtsession TO familytree_app_role;

GRANT ALL ON SEQUENCE shared_schema.txtsession_sessionid_seq TO familytree_app_role;
