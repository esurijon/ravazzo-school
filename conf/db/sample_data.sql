INSERT INTO school.staff (id, firstName, lastName, nickName, birthDate, email, password) values 
('mtevere', 'Maria Eugenia', 'Tevere', 'Maru', '1981-10-29', 'maruru@gmail.com', 'AAAA'),
('szukier', 'Susana', 'Zukier', 'Bobe', '1943-09-01', 'bobe@gmail.com', 'AAAA'); 

INSERT INTO school.gate(id, label, address, gatekeeper) values 
('conde', 'Puerta conde', 'conde 1896', 'mtevere'); 

INSERT INTO school.classroom (id, name, dispatcher) values
('1A', '1ro A', 'szukier');

INSERT INTO school.children (id, firstName, lastName, nickName, birthDate, classroom) values
('tgavuzzo', 'Tomas', 'Ravazzo', 'tomi', '1977-09-17', '1A'), 
('gsurijon', 'Guadalupe', 'Surijon', 'lupe', '1977-09-17', '1A'); 
  
INSERT INTO school.parent (id, firstName, lastName, nickName, birthDate, email, password) values 
('esurijon', 'Ezequiel', 'Surijon', 'suri', '1981-10-29', 'maruru@gmail.com', 'AAAA');

INSERT INTO school.permanent_authorization (parent, children) values
('esurijon', 'gsurijon');

INSERT INTO school.permanent_authorization (parent, children) values
('esurijon', 'tgavuzzo');

INSERT INTO school.shift (id, classroom, gate, startTime, endTime) values 
('mañanaconde', '1A', 'conde', '12:00:00', '14:00:00');