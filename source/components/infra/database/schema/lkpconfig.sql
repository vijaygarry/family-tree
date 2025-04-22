CREATE TABLE `lkpconfig` (
  `configname` varchar(255) NOT NULL COMMENT 'Main config name e.g. COUNTRYNAME',
  `paramname` varchar(255) NOT NULL COMMENT 'config parameter name e.g. HINDI. This will be the same as config name if no sub config name',
  `paramvalue` varchar(500) NOT NULL COMMENT 'Config value for config name and config param name',
  `enable` tinyint(1) NOT NULL COMMENT 'True means enabled and should be pickup by application',
  `listorderseq` tinyint(4) DEFAULT NULL COMMENT 'If config has multiple param, then this will be order of config.',
  PRIMARY KEY (`configname`,`paramname`)
);