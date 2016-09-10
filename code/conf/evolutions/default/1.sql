# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table useraccount (
  id                            bigserial not null,
  email                         varchar(256) not null,
  username                      varchar(256) not null,
  bcrypt_password               text,
  registration_key              text,
  registration_key_date         timestamp,
  creation_date                 timestamp not null,
  last_updated_date             timestamp not null,
  constraint uq_useraccount_email unique (email),
  constraint uq_useraccount_username unique (username),
  constraint pk_useraccount primary key (id)
);


# --- !Downs

drop table if exists useraccount cascade;

