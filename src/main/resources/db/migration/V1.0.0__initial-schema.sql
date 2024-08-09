create table app_user (
    id varchar(255) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    age integer not null,
    primary key (id)
);

create table address (
    id varchar(255) not null,
    user_id varchar(255) not null,
    street varchar(255) not null,
    city varchar(255) not null,
    postal_code varchar(255) not null,
    country varchar(255) not null,
    primary key (id)
);

alter table address
   add constraint addresses_user
   foreign key (user_id)
   references app_user;
