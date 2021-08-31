create table post
(
    post_id     bigint auto_increment
        primary key,
    accepted    int          not null,
    answered    bit          not null,
    category    varchar(255) null,
    created     varchar(255) null,
    description varchar(255) null,
    title       varchar(255) null,
    user_id     bigint       null
);
