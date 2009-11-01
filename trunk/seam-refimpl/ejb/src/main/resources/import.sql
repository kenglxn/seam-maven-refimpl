-- SQL statements which are executed at application startup if hibernate.hbm2ddl.auto is 'create' or 'create-drop'

-- admin password is blank
insert into useraccount    (id, username, passwordhash, enabled) values (1, 'admin', 'Ss/jICpf9c9GeJj8WKqx1hUClEE=', true);
insert into userrole       (id, name, conditional) values (1, 'admin', false);
insert into userrole       (id, name, conditional) values (2, 'member', false);
insert into userrole       (id, name, conditional) values (3, 'guest', true);
insert into useraccountrole(useraccountid, userroleid) values (1, 1);
insert into userrolegroup  (roleid, memberofroleid) values (1, 2);
