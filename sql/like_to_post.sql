-- auto-generated definition
create table like_to_post
(
    like_id bigint auto_increment
        primary key,
    user_id    bigint       null,
    post_id    bigint       null,
    foreign key (post_id) references post (post_id)
);
