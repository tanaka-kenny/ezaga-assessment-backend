create database if not exists ezaga;

use ezaga;

create table user (
    first_name varchar(255) not null,
    surname varchar(255) not null,
    email varchar(255) not null,
    password varchar(255) not null,
    primary key key(email)
)