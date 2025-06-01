-- Table: shared_schema.memberrelationship

-- DROP TABLE IF EXISTS shared_schema.memberrelationship;

CREATE TABLE IF NOT EXISTS shared_schema.memberrelationship
(
    memberid integer NOT NULL,
    relationshiptype character varying(55) COLLATE pg_catalog."default" NOT NULL,
    relatedmemberid integer NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp with time zone NOT NULL,
    lastupdatedby integer NOT NULL,
    lastupdateddate timestamp with time zone NOT NULL,
    CONSTRAINT memberrelationship_pkey PRIMARY KEY (memberid, relatedmemberid),
    CONSTRAINT memberrelationship_createdby_fkey FOREIGN KEY (createdby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT memberrelationship_lastupdatedby_fkey FOREIGN KEY (lastupdatedby)
        REFERENCES shared_schema.appuser (userid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT memberrelationship_memberid_fkey FOREIGN KEY (memberid)
        REFERENCES shared_schema.familymember (memberid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT,
    CONSTRAINT memberrelationship_relatedmemberid_fkey FOREIGN KEY (relatedmemberid)
        REFERENCES shared_schema.familymember (memberid) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS shared_schema.memberrelationship
    OWNER to postgres;

COMMENT ON TABLE shared_schema.memberrelationship
    IS 'Table to maintain relationship.
Read the record as 
memberid is relationshiptype of relatedmemberid
E.g: memberid = Vijay, relationshiptype = Son and relatedmemberid = Bhagwatnarayan
then read this record as 
Vijay is Son of Bhagwatnarayan.';


COMMENT ON COLUMN shared_schema.memberrelationship.memberid
    IS 'The person whose relationship is being defined. relatedmemberid is relationshiptype of memberid';
COMMENT ON COLUMN shared_schema.memberrelationship.relationshiptype
    IS 'Relationship type for this member. Possible options Father, Mother, Son, Daughter, Husband, Wife';
COMMENT ON COLUMN shared_schema.memberrelationship.relatedmemberid
    IS 'The other person in the relationship. relatedmemberid''s relationshiptype is memberid';
