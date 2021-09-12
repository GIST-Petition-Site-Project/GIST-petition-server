create table user
(
    id        bigint not null auto_increment,
    email     varchar(255),
    enabled   bit    not null,
    locked    bit    not null,
    password  varchar(255),
    user_role varchar(255),
    username  varchar(255),
    primary key (id)
)