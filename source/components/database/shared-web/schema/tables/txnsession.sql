CREATE TABLE shared_schema.txtsession
(
  sessionid bigserial NOT NULL,
  userid integer, 
  channelid character varying(64) NOT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y',
  sessioncreationtime timestamp with time zone NOT NULL,
  logouttime timestamp with time zone,
  lastaccesstime timestamp with time zone,
  exitcode smallint DEFAULT 1,
  apphostname character varying(80) NOT NULL,
  clientipaddress character varying(55) NOT NULL,
  user_agent clientbrowser character varying(100),
  PRIMARY KEY (sessionid),
  FOREIGN KEY (userid)
      REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
      ON UPDATE RESTRICT ON DELETE RESTRICT
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.txtsession
    OWNER to familytree_master;

COMMENT ON TABLE shared_schema.txtsession
  IS 'Maintain session created in application';
COMMENT ON COLUMN shared_schema.txtsession.sessionid IS 'Unique session id.';
COMMENT ON COLUMN shared_schema.txtsession.channelid IS 'Channel this session is created for. E.g. Server, Browser, Mobile';
COMMENT ON COLUMN shared_schema.txtsession.userid IS 'User this session belongs to';
COMMENT ON COLUMN shared_schema.txtsession.exitcode IS 'How the session is terminated. Possible values 1-> User Logout, 2-> Session Timeout, 3-> Override by new session, 4-> Kill by Admin';
COMMENT ON COLUMN shared_schema.txtsession.sessioncreationtime IS 'Session creation time';
COMMENT ON COLUMN shared_schema.txtsession.logouttime IS 'Session logout time';
COMMENT ON COLUMN shared_schema.txtsession.lastaccesstime IS 'Last access session time to see if session should be kept active.';
COMMENT ON COLUMN shared_schema.txtsession.isactive IS 'To see if session is active.';
COMMENT ON COLUMN shared_schema.txtsession.apphostname IS 'application Hostname or IP of Application from where this session is created';
COMMENT ON COLUMN shared_schema.txtsession.clientipaddress IS 'Client IP address';
COMMENT ON COLUMN shared_schema.txtsession.user_agent IS 'Client user agent including browser name and version, client OS name and version. Obtain from user-agent header of HTTP request. This may not be accurate.';
