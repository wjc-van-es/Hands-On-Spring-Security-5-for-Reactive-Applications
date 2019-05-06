-- connect as spring_security_app

insert into spring_security_schema.users(username, password, enabled)
values('admin', '$2a$04$lcVPCpEk5DOCCAxOMleFcOJvIiYURH01P9rx1Y/pl.wJpkNTfWO6u', true);

insert into spring_security_schema.authorities(username, authority)
values('admin','ROLE_ADMIN');

insert into spring_security_schema.users(username, password, enabled)
values('user', '$2a$04$nbz5hF5uzq3qsjzY8ZLpnueDAvwj4x0U9SVtLPDROk4vpmuHdvG3a', true);

insert into spring_security_schema.authorities(username,authority)
values('user','ROLE_USER');

commit;
