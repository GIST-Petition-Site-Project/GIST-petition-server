-- auto-generated definition
create table email_confirmation_token
(
    token_id     bigint auto_increment
        primary key,
    confirmed_at datetime(6)  null,
    created_at   datetime(6)  not null,
    expired_at   datetime(6)  not null,
    token        varchar(255) not null,
    id           bigint       not null,
    constraint FK6gvxqh0pisuxrgtixofmrw8lp
        foreign key (id) references user (id)
);


