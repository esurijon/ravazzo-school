INSERT INTO aulatec.responsable (id, familia, nombre, apellido, dni, celular, email, password, pais, es_docente)
VALUES
(1, 1, 'Ezequiel', 'Surijon', 26348014, '11-6463-1122', 'surijon@gmail.com', 'fedeputo', 'AR', false);

INSERT INTO aulatec.colegio(id, nombre, pais)
VALUES
(1, 'LOMITAS', 'AR');

INSERT INTO aulatec.turno(id, cole, texto, hora_inicio, hora_fin)
VALUES
(1, 1, 'MAÑANA', time '13:00:00', time '14:00:00');

INSERT INTO aulatec.puerta(id, cole, texto)
VALUES
(1, 1, 'Conde 2340');

INSERT INTO aulatec.aula(id, cole, txt1, turno, puerta)
VALUES
(1, 1, '2doA', 1, 1);

INSERT INTO  aulatec.alumno(id, cole, familia, nombre, apellido, aula, pais)
VALUES
(1, 1, 1, 'Guadalupe', 'Surijon', 1, 'AR');
