insert into Person(id, version, uuid, name, email) values (1, 0, '1', 'Leif', 'leif@loo.no')
insert into Person(id, version, uuid, name, email) values (2, 0, '2', 'Ken', 'ken@krg.no')
insert into Person(id, version, uuid, name, email) values (3, 0, '3', 'Jon', 'jon@jv.no')
insert into Person(id, version, uuid, name, email) values (4, 0, '4', 'Sten Aksel', 'stenaxl@sah.no')
insert into Person(id, version, uuid, name, email) values (5, 0, '5', 'Tommy', 'tommy@toj.no')
insert into Person(id, version, uuid, name, email) values (6, 0, '6', 'Kristian', 'kristian@kb.no')
insert into Person(id, version, uuid, name, email) values (7, 0, '7', 'Erling', 'erling@er.no')
insert into Person(id, version, uuid, name, email) values (8, 0, '8', 'Bengt', 'bengt@bq.no')
insert into Person(id, version, uuid, name, email) values (9, 0, '9', 'Eivind', 'eivind@eg.no')

insert into Interest(id, version, uuid, name) values (1, 0, '11', 'Sport')
insert into Interest(id, version, uuid, name) values (2, 0, '12', 'Computers')
insert into Interest(id, version, uuid, name) values (3, 0, '13', 'Fishing')
insert into Interest(id, version, uuid, name) values (4, 0, '14', 'Music')
insert into Interest(id, version, uuid, name) values (5, 0, '15', 'Film')
insert into Interest(id, version, uuid, name) values (6, 0, '16', 'Skydiving')
insert into Interest(id, version, uuid, name) values (7, 0, '17', 'Heavvy Mettall')
insert into Interest(id, version, uuid, name) values (8, 0, '18', 'Hacking')
insert into Interest(id, version, uuid, name) values (9, 0, '19', 'Boat Racing')

insert into Language(id, version, uuid, code, name) values (1, 0, '21', 'no', 'Norwegian')
insert into Language(id, version, uuid, code, name) values (2, 0, '22', 'dk', 'Danish')
insert into Language(id, version, uuid, code, name) values (3, 0, '23', 'en', 'English')
insert into Language(id, version, uuid, code, name) values (4, 0, '24', 'fr', 'Frensh')
insert into Language(id, version, uuid, code, name) values (5, 0, '25', 'de', 'German')

insert into Person_Interest(person_id, interest_id) values (1, 2)
insert into Person_Interest(person_id, interest_id) values (1, 3)
insert into Person_Interest(person_id, interest_id) values (2, 2)
insert into Person_Interest(person_id, interest_id) values (3, 7)
insert into Person_Interest(person_id, interest_id) values (8, 9)

insert into Person_Language(person_id, language_id) values (1, 1)
insert into Person_Language(person_id, language_id) values (2, 1)
insert into Person_Language(person_id, language_id) values (3, 1)
insert into Person_Language(person_id, language_id) values (4, 1)
insert into Person_Language(person_id, language_id) values (5, 1)
insert into Person_Language(person_id, language_id) values (6, 1)
insert into Person_Language(person_id, language_id) values (7, 1)
insert into Person_Language(person_id, language_id) values (8, 1)
insert into Person_Language(person_id, language_id) values (9, 1)
insert into Person_Language(person_id, language_id) values (1, 3)
insert into Person_Language(person_id, language_id) values (2, 3)
insert into Person_Language(person_id, language_id) values (3, 3)
insert into Person_Language(person_id, language_id) values (4, 3)
insert into Person_Language(person_id, language_id) values (5, 3)
insert into Person_Language(person_id, language_id) values (6, 3)
insert into Person_Language(person_id, language_id) values (7, 3)
insert into Person_Language(person_id, language_id) values (8, 3)
insert into Person_Language(person_id, language_id) values (9, 3)
insert into Person_Language(person_id, language_id) values (4, 2)

insert into Company(id, version, uuid, name) values (1, 0, '111', 'KnowIt')
insert into Company(id, version, uuid, name) values (2, 0, '112', 'LogIt')
insert into Company(id, version, uuid, name) values (3, 0, '113', 'PT')
insert into Company(id, version, uuid, name) values (4, 0, '114', 'Red Hat')

insert into Company_Contact(company_id, person_id) values (1, 1)
insert into Company_Contact(company_id, person_id) values (1, 2)
insert into Company_Contact(company_id, person_id) values (1, 3)
insert into Company_Contact(company_id, person_id) values (1, 4)
insert into Company_Contact(company_id, person_id) values (2, 1)
insert into Company_Contact(company_id, person_id) values (2, 4)