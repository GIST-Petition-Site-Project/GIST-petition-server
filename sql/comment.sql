-- auto-generated definition
create table comment
(
    comment_id bigint auto_increment
        primary key,
    content    varchar(255) null,
    created    varchar(255) null,
    user_id    bigint       null,
    post_id    bigint       null,
    constraint FKs1slvnkuemjsq2kj4h3vhx7i1
        foreign key (post_id) references post (post_id)
);

