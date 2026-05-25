CREATE TABLE shared_schema.memberrelationship (
    memberid integer NOT NULL,
    relationshiptype character varying(55) NOT NULL,
    relatedmemberid integer NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL
);

ALTER TABLE shared_schema.memberrelationship OWNER TO postgres;

COMMENT ON TABLE shared_schema.memberrelationship IS 'Table to maintain relationship.
Read the record as 
memberid''s relationshiptype is relatedmemberid
E.g: memberid = Bhagwatnarayan, relationshiptype = Son and relatedmemberid = Vijay
then read this record as 
Bhagwatnarayan''s Son is Vijay.';

COMMENT ON COLUMN shared_schema.memberrelationship.memberid IS 'The person whose relationship is being defined. memberid''s relationshiptype is relatedmemberid';

COMMENT ON COLUMN shared_schema.memberrelationship.relationshiptype IS 'Relationship type for this member. Possible options Son, Daughter, Husband, Wife';

COMMENT ON COLUMN shared_schema.memberrelationship.relatedmemberid IS 'The other person in the relationship. relatedmemberid is relationshiptype of memberid';

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_pkey PRIMARY KEY (memberid, relatedmemberid);

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_createdby_fkey FOREIGN KEY (createdby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_lastupdatedby_fkey FOREIGN KEY (lastupdatedby) REFERENCES shared_schema.appuser(userid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_memberid_fkey FOREIGN KEY (memberid) REFERENCES shared_schema.familymember(memberid) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY shared_schema.memberrelationship
    ADD CONSTRAINT memberrelationship_relatedmemberid_fkey FOREIGN KEY (relatedmemberid) REFERENCES shared_schema.familymember(memberid) ON UPDATE RESTRICT ON DELETE RESTRICT;

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE shared_schema.memberrelationship TO familytree_app_role;
