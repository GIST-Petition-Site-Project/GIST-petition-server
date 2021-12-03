create table post
(
    id     bigint  not null auto_increment,
    accepted    integer not null,
    answered    bit     not null,
    category    varchar(255),
    created     varchar(255),
    description varchar(255),
    title       varchar(255),
    user_id     bigint,
    primary key (id)
)
