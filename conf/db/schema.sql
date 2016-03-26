DROP SCHEMA school CASCADE;

CREATE SCHEMA school;

CREATE TABLE school.gate
(
  id varchar(15) NOT NULL,
  label varchar(50) NOT NULL,
  address varchar(100) NOT NULL,
  coords varchar(100),
  assigned_staff varchar(50) NOT NULL,
  CONSTRAINT gate_id_pk PRIMARY KEY ("id")
);
COMMENT ON TABLE school.gate
  IS 'A gate through which children leave school';
COMMENT ON COLUMN school.gate.assigned_staff
  IS 'Staff peson who is in charge of supervise children departure thru this gate. Gatekeeper role';
  
CREATE TABLE school.classroom
(
  id varchar(15) NOT NULL,
  name varchar(50) NOT NULL,
  assigned_staff varchar(50) NOT NULL,
  CONSTRAINT classroom_id_pk PRIMARY KEY (id)
);
COMMENT ON TABLE school.classroom
  IS 'The room to which children belong and the responsible staff person of it';
COMMENT ON COLUMN school.classroom.assigned_staff
  IS 'Staff peson who is in charge of dispatch out children of this classroom. Dispatcher role';

CREATE TABLE school.children
(
  id varchar(15) NOT NULL,
  first_name varchar(50) NOT NULL,
  last_name varchar(50) NOT NULL,
  nick_name varchar(50) NOT NULL,
  birth_date date NOT NULL,
  picture_uri varchar(500),
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
  first_name varchar(50) NOT NULL,
  last_name varchar(50) NOT NULL,
  nick_name varchar(50) NOT NULL,
  birth_date date NOT NULL,
  picture_uri varchar(500),
  email varchar(100) NOT NULL,
  password varchar(100) NOT NULL,
  cellPhoneNumber varchar(50),
  CONSTRAINT parent_id_pk PRIMARY KEY (id),
  CONSTRAINT parent_email_key UNIQUE (email)
);
COMMENT ON TABLE school.parent
  IS 'The term parent refers to a parent itslef or any other adult person who is in charge of pickup children from school as aunts, grand maothers etc';

CREATE TABLE school.staff
(
  id varchar(15) NOT NULL,
  first_name varchar(50) NOT NULL,
  last_name varchar(50) NOT NULL,
  nick_name varchar(50) NOT NULL,
  birth_date date NOT NULL,
  picture_uri varchar(500),
  email varchar(100),
  password varchar(100) NOT NULL,
  cellPhoneNumber varchar(50),
  CONSTRAINT staff_id_pk PRIMARY KEY (id),
  CONSTRAINT staff_email_key UNIQUE (email)
);
COMMENT ON TABLE school.staff
  IS 'Staff are employees of school whom plays a role in egress procedure';

CREATE TABLE school.permanent_authorization
(
  parent varchar(15) NOT NULL,
  children varchar(15) NOT NULL,
  schedule varchar(50),
  CONSTRAINT permanent_authorization_parent_children_pk PRIMARY KEY (parent, children)
);
CREATE UNIQUE INDEX permanent_authorization_parent_idx ON school.permanent_authorization (parent);
COMMENT ON TABLE school.permanent_authorization
  IS 'Defines a relationship between a child and a parent who can pick him from school in a permanent way';

CREATE TABLE school.temporary_authorization
(
  parent varchar(15) NOT NULL,
  children varchar(15) NOT NULL,
  valid_date date NOT NULL,
  CONSTRAINT temporary_authorization_parent_children_pk PRIMARY KEY (parent, children)
);
CREATE UNIQUE INDEX temporary_authorization_parent_idx ON school.temporary_authorization (parent);
COMMENT ON TABLE school.temporary_authorization
  IS 'Defines a relationship between a child and a parent who can pick him from school just for the specified date';

CREATE TABLE school.turn
(
  id varchar(15) NOT NULL,
  classromm varchar(15) NOT NULL,
  gate varchar(15) NOT NULL,
  start_time time NOT NULL, 
  end_time time NOT NULL,
  CONSTRAINT turn_id_pk PRIMARY KEY (id)
);
COMMENT ON TABLE school.turn
  IS 'Defines through which gate and at which time, children of a classroom must leave school';

  
CREATE OR REPLACE VIEW "school"."user" AS 
 SELECT id, first_name, last_name, nick_name, 'PARENT'::text AS role
 FROM school.parent
UNION ALL
 SELECT id, first_name, last_name, nick_name, 'STAFF'::text AS role
 FROM school.staff;
COMMENT ON VIEW "school"."user"
  IS 'A user refers to any person who plays a role in egress procedure, that is a parent or staff person';

CREATE OR REPLACE VIEW school.person AS 
 SELECT id, first_name, last_name, nick_name, 'CHILD'::text AS role
 FROM school.children
UNION ALL
 SELECT id, first_name, last_name, nick_name, role
 FROM "school"."user";
COMMENT ON VIEW school.person
  IS 'A person refers entity taht represents a human being no matter is if it plays a role in egress procedure, that is a parent or staff or children';
  