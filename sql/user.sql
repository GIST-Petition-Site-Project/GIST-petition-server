create table user
(
    id            bigint auto_increment
        primary key,
    email         varchar(255) null,
    enabled       bit          not null,
    user_id       varchar(255) null,
    user_password varchar(255) not null,
    username      varchar(255) null,
    user_role     varchar(255) not null,
    locked        bit          not null

);
