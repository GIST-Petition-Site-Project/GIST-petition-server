create table answer
(
    answer_id   bigint not null auto_increment,
    category    varchar(255),
    created     varchar(255),
    description varchar(255),
    title       varchar(255),
    user_id     bigint,
    primary key (answer_id)
)