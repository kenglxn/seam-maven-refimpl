-- SQL statements which are executed at application startup if hibernate.hbm2ddl.auto is 'create' or 'create-drop'

-- admin password is blank
insert into useraccount    (id, version, username, passwordhash, enabled) values (1, 1, 'admin', 'Ss/jICpf9c9GeJj8WKqx1hUClEE=', true);
insert into userrole       (id, version, name, conditional) values (1, 1, 'admin', false);
insert into userrole       (id, version, name, conditional) values (2, 1, 'member', false);
insert into userrole       (id, version, name, conditional) values (3, 1, 'guest', true);
insert into useraccountrole(useraccountid, userroleid) values (1, 1);
insert into userrolegroup  (roleid, memberofroleid) values (1, 2);

insert into Movie (version, director, title, year, plot) values (0, 'Joel Coen', 'The Big Lebowski', 1998, '"Dude" Lebowski, mistaken for a millionaire Lebowski, seeks restitution for his ruined rug and enlists his bowling buddies to help get it.')
insert into Movie (version, director, title, year, plot) values (0, 'Quentin Tarantino', 'Reservoir Dogs', 1992, 'After a simple jewelery heist goes terribly wrong, the surviving criminals begin to suspect that one of them is a police informant.')
insert into Movie (version, director, title, year, plot) values (0, 'Joel Coen', 'Fargo', 1996, 'Jerry Lundegaard''s inept crime falls apart due to his and his henchmen''s bungling and the persistent police work of pregnant Marge Gunderson.')
--insert into Movie (version, director, title, year, plot) values (0, 'Alan Parker', 'The Wall', 1992, 'A troubled rock star descends into madness in the midst of his physical and social isolation from everyone.')
