-- connect as spring_security_owner

create table spring_security_schema.users(
username varchar(75) not null primary key,
password varchar(150) not null,
enabled boolean not null
);

--Beware No update and no delete
grant select, insert on table spring_security_schema.users to spring_security_crud_role;

grant select on table spring_security_schema.users to spring_security_read_role;

create table spring_security_schema.authorities (
username varchar(75) not null,
authority varchar(50) not null,
constraint fk_authorities_users foreign key(username) references
users(username)
);

--Beware No update and no delete
grant select, insert on table spring_security_schema.authorities to spring_security_crud_role;

grant select on table spring_security_schema.authorities to spring_security_read_role;

commit;
