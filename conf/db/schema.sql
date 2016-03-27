DROP SCHEMA IF EXISTS school;

CREATE SCHEMA school;

CREATE TABLE school.staff
(
  id varchar(15) NOT NULL,
  firstName varchar(50) NOT NULL,
  lastName varchar(50) NOT NULL,
  nickName varchar(50) NOT NULL,
  birthDate date NOT NULL,
  pictureUri varchar(500),
  email varchar(100),
  password varchar(100) NOT NULL,
  cellPhoneNumber varchar(50),
  CONSTRAINT staff_id_pk PRIMARY KEY (id),
  CONSTRAINT staff_email_key UNIQUE (email)
);
COMMENT ON TABLE school.staff
  IS 'Staff are employees of school whom plays a role in egress procedure';

CREATE TABLE school.gate
(
  id varchar(15) NOT NULL,
  label varchar(50) NOT NULL,
  address varchar(100) NOT NULL,
  coords varchar(100),
  gatekeeper varchar(15) NOT NULL,
  CONSTRAINT gate_id_pk PRIMARY KEY (id),
  CONSTRAINT gate_gatekeeper_fk FOREIGN KEY (gatekeeper)
      REFERENCES school.staff (id) 
      ON UPDATE CASCADE ON DELETE RESTRICT
);
COMMENT ON TABLE school.gate
  IS 'A gate through which children leave school';
COMMENT ON COLUMN school.gate.gatekeeper
  IS 'Staff peson who is in charge of supervise children departure thru this gate';
  
CREATE TABLE school.classroom
(
  id varchar(15) NOT NULL,
  name varchar(50) NOT NULL,
  dispatcher varchar(15) NOT NULL,
  CONSTRAINT classroom_id_pk PRIMARY KEY (id),
  CONSTRAINT classroom_dispatcher_fk FOREIGN KEY (dispatcher)
      REFERENCES school.staff (id) 
      ON UPDATE CASCADE ON DELETE RESTRICT
);
COMMENT ON TABLE school.classroom
  IS 'The room to which children belong and the responsible staff person of it';
COMMENT ON COLUMN school.classroom.dispatcher
  IS 'Staff peson who is in charge of dispatch out children of this classroom.';

CREATE TABLE school.children
(
  id varchar(15) NOT NULL,
  firstName varchar(50) NOT NULL,
  lastName varchar(50) NOT NULL,
  nickName varchar(50) NOT NULL,
  birthDate date NOT NULL,
  pictureUri varchar(500),
  classroom varchar(15),
  CONSTRAINT children_id_pk PRIMARY KEY (id),
  CONSTRAINT children_classroom_fk FOREIGN KEY (classroom)
      REFERENCES school.classroom (id) 
      ON UPDATE CASCADE ON DELETE RESTRICT
);
COMMENT ON TABLE school.children
  IS 'Children are students of school and they belong to classroom';
  
CREATE TABLE school.parent
(
  id varchar(15) NOT NULL,
  firstName varchar(50) NOT NULL,
  lastName varchar(50) NOT NULL,
  nickName varchar(50) NOT NULL,
  birthDate date NOT NULL,
  pictureUri varchar(500),
  email varchar(100) NOT NULL,
  password varchar(100) NOT NULL,
  cellPhoneNumber varchar(50),
  CONSTRAINT parent_id_pk PRIMARY KEY (id),
  CONSTRAINT parent_email_key UNIQUE (email)
);
COMMENT ON TABLE school.parent
  IS 'The term parent refers to a parent itslef or any other adult person who is in charge of pickup children from school as aunts, grand maothers etc';

CREATE TABLE school.permanent_authorization
(
  parent varchar(15) NOT NULL,
  children varchar(15) NOT NULL,
  schedule varchar(50),
  CONSTRAINT permanent_authorization_parent_children_pk PRIMARY KEY (parent, children)
);
CREATE INDEX permanent_authorization_parent_idx ON school.permanent_authorization (parent);
COMMENT ON TABLE school.permanent_authorization
  IS 'Defines a relationship between a child and a parent who can pick him from school in a permanent way';

CREATE TABLE school.temporary_authorization
(
  parent varchar(15) NOT NULL,
  children varchar(15) NOT NULL,
  valid_date date NOT NULL,
  CONSTRAINT temporary_authorization_parent_children_pk PRIMARY KEY (parent, children)
);
CREATE INDEX temporary_authorization_parent_idx ON school.temporary_authorization (parent);
COMMENT ON TABLE school.temporary_authorization
  IS 'Defines a relationship between a child and a parent who can pick him from school just for the specified date';

CREATE TABLE school.shift
(
  id varchar(15) NOT NULL,
  classroom varchar(15) NOT NULL,
  gate varchar(15) NOT NULL,
  startTime time NOT NULL, 
  endTime time NOT NULL,
  CONSTRAINT shift_id_pk PRIMARY KEY (id)
);
COMMENT ON TABLE school.shift
  IS 'Defines through which gate and at which time, children of a classroom must leave school';

  
CREATE OR REPLACE VIEW "school"."user" AS 
 SELECT id, firstName, lastName, nickName, 'PARENT'::text AS role
 FROM school.parent
UNION ALL
 SELECT id, firstName, lastName, nickName, 'STAFF'::text AS role
 FROM school.staff;
COMMENT ON VIEW "school"."user"
  IS 'A user refers to any person who plays a role in egress procedure, that is a parent or staff person';

CREATE OR REPLACE VIEW school.person AS 
 SELECT id, firstName, lastName, nickName, 'CHILD'::text AS role
 FROM school.children
UNION ALL
 SELECT id, firstName, lastName, nickName, role
 FROM "school"."user";
COMMENT ON VIEW school.person
  IS 'A person refers entity taht represents a human being no matter is if it plays a role in egress procedure, that is a parent or staff or children';
  