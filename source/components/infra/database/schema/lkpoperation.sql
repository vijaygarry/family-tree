CREATE TABLE `lkpoperation` (
  `operationid` varchar(55) NOT NULL COMMENT 'Unique alpha numeric opearation id',
  `description` varchar(255) DEFAULT NULL COMMENT 'Operation description',
  `beanname` varchar(255) NOT NULL COMMENT 'Bean name to invoke for this operation',
  `isloginrequired` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'True indicate user has to login to execute this transaction. i.e. session should be cretaed before executing this transaction',
  `isauditrequired` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'True indicate audit this transaction',
  `authorizationtype` varchar(100) DEFAULT 'ROLE_BASE' NOT NULL COMMENT 'Type of authorization required for this operation. Possible values are NO_AUTHORIZATION, IP_BASE, ROLE_BASE', 
  `groupname` varchar(100) DEFAULT NULL COMMENT 'Group Name to display in menu',
  `sortseq` tinyint(4) DEFAULT NULL COMMENT 'Sequenece in group menu',
  `ismenutxn` tinyint(1) DEFAULT NULL COMMENT 'True indicate this is menu transaction',
  `userfriendlyurl` varchar(100) DEFAULT NULL COMMENT 'User friendly url for this operation',
  `active` char(1) DEFAULT 'Y' NOT NULL COMMENT 'If this value is true, then application will load this operation and check for autorization, if this flag is false, then user is not allowed to perform this operation.', 
  PRIMARY KEY (`operationid`),
  UNIQUE (`userfriendlyurl`)
);


COMMENT ON TABLE `lkpoperation`
  IS 'Main operation table.';