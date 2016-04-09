DROP SCHEMA IF EXISTS aulatec;

CREATE SCHEMA aulatec; 

CREATE TABLE aulatec.custom_cole(
	id serial,
	cus_puerta boolean, 
	cus_turno boolean, 
	cus_aula boolean, 
	cus_padre_mas boolean, 
	cus_padre_ind boolean, 
	cus_docente boolean, 
	cus_evento boolean, 
	cus_factu boolean,
	CONSTRAINT custom_cole_pk PRIMARY KEY (id)
);
COMMENT ON TABLE aulatec.custom_cole
  IS 'Opciones customizables habilitadas para cada colegio - nueva sección será un nuevo campo';

CREATE TABLE aulatec.colegio(
	id serial,
	nombre varchar(80) NOT NULL,
	logo varchar(80),
	pais char(2) NOT NULL,
	CONSTRAINT colegio_id_pk PRIMARY KEY (id)
);
COMMENT ON TABLE aulatec.colegio
  IS 'Tabla maestra de colegios con su nombre, logo y pais';

CREATE TABLE aulatec.turno(
	id serial,
	cole integer,
	texto varchar(40) NOT NULL,
	hora_inicio time NOT NULL,
	hora_fin time NOT NULL,
	CONSTRAINT turnos_id_pk PRIMARY KEY (id),
	CONSTRAINT turnos_colegio_fk FOREIGN KEY (cole) 
		REFERENCES aulatec.colegio (id)
);
COMMENT ON TABLE aulatec.turno
  IS 'Tabla maestra de turnos por colegio - no admite solapamiento horario para un mismo colegio';


CREATE TABLE aulatec.puerta(
	id serial,
	cole integer,
	texto varchar(40) NOT NULL,
	CONSTRAINT puerta_id_pk PRIMARY KEY (id),
	CONSTRAINT puerta_cole_fk FOREIGN KEY (cole) 
		REFERENCES aulatec.colegio (id)
);
COMMENT ON TABLE aulatec.puerta
  IS 'Tabla maestra de puertas por colegio';

CREATE TABLE aulatec.aula(
	id serial,
	cole integer,
	txt1 varchar(40) NOT NULL,
	txt2 varchar (80),
	turno integer,
	puerta integer,
	CONSTRAINT aula_id_pk PRIMARY KEY (id),
	CONSTRAINT aula_puerta_aula_uk UNIQUE (puerta),
	CONSTRAINT aula_cole_fk FOREIGN KEY (cole) 
		REFERENCES aulatec.colegio (id),
	CONSTRAINT aula_turno_fk FOREIGN KEY (turno) 
		REFERENCES aulatec.turno (id),
	CONSTRAINT aula_puerta_fk FOREIGN KEY (puerta)
		REFERENCES aulatec.puerta (id)
);
COMMENT ON TABLE aulatec.aula
  IS 'Tabla maestra de puertas por colegio';

CREATE TABLE aulatec.responsable(
	id serial,
	familia integer UNIQUE,
	nombre varchar(40) NOT NULL,
	apellido varchar(40) NOT NULL,
	dni integer NOT NULL UNIQUE,
	celular varchar(20) NOT NULL UNIQUE,
	email varchar(80) NOT NULL UNIQUE,
	password varchar(80) NOT NULL UNIQUE,
	pais char(2) NOT NULL,
	es_docente boolean  NOT NULL DEFAULT false,
	device_type char(8),
	device_reg_id varchar(120),
	CONSTRAINT responsable_id_pk PRIMARY KEY (id)
);
COMMENT ON TABLE aulatec.responsable
  IS 'Tabla de Responsables (Padres, Autorizados, Docentes)';


CREATE TABLE aulatec.alumno(
	id serial,
	cole integer,
	familia integer,
	nombre varchar(40) NOT NULL,
	apellido varchar(40) NOT NULL,
	aula  integer,
	pais char(2) NOT NULL,
	CONSTRAINT alumno_id_pk PRIMARY KEY (id),
	CONSTRAINT alumno_cole_fk FOREIGN KEY (cole) 
		REFERENCES aulatec.colegio (id),
	CONSTRAINT alumno_familia_fk FOREIGN KEY (familia) 
		REFERENCES aulatec.responsable (id),
	CONSTRAINT aula_aula_fk FOREIGN KEY (aula)
		REFERENCES aulatec.aula (id)
);
COMMENT ON TABLE aulatec.alumno
  IS 'Tabla maestra de Alumnos';


CREATE TABLE aulatec.cole_resp(
	cole integer,
	resp integer,
	esTitular boolean,
	CONSTRAINT cole_resp_cole_resp_pk PRIMARY KEY (cole, resp),
	CONSTRAINT cole_resp_cole_fk FOREIGN KEY (cole) 
		REFERENCES aulatec.colegio (id),
	CONSTRAINT cole_resp_resp_fk FOREIGN KEY (resp) 
		REFERENCES aulatec.responsable (id)
);
COMMENT ON TABLE aulatec.cole_resp
  IS 'Tabla que relaciona a cada responsable con los colegios en los que puede operar y bajo que Rol (Titular, Autorizado o Docente)';

CREATE TABLE aulatec.fam_alu_resp(
	familia integer,
	alumno  integer,
	aula  integer,
	resp  integer,
	valido_desde date,
	valido_hasta date,
	CONSTRAINT fam_alu_resp_familia_alumno_pk PRIMARY KEY (familia, alumno),
	CONSTRAINT fam_alu_resp_familia_fk FOREIGN KEY (familia) 
		REFERENCES aulatec.responsable (familia),
	CONSTRAINT fam_alu_resp_alumno_fk FOREIGN KEY (alumno) 
		REFERENCES aulatec.alumno (id),
	CONSTRAINT fam_alu_resp_aula_fk FOREIGN KEY (aula)
		REFERENCES aulatec.aula (id),
	CONSTRAINT fam_alu_resp_resp_fk FOREIGN KEY (resp) 
		REFERENCES aulatec.responsable (id)
);
COMMENT ON TABLE aulatec.fam_alu_resp
  IS 'Tabla que administra los autorizados a retirar que no son Titulares (padre o madre)';
  
CREATE TABLE aulatec.log_alumnos_retira(
	alumno  integer NOT NULL,
	resp integer NOT NULL,
	fecha_retiro date NOT NULL,
	hora_retiro time NOT NULL,
	checkout boolean DEFAULT false,
	CONSTRAINT log_alumnos_retira_alumno_fecha_retiro_pk PRIMARY KEY (alumno, fecha_retiro),
	CONSTRAINT log_alumnos_retira_resp_fk FOREIGN KEY (resp) 
		REFERENCES aulatec.responsable (id)
);
COMMENT ON TABLE aulatec.log_alumnos_retira
  IS 'Tabla de Log para el retiro de alumnos con responsable y fecha';
  
