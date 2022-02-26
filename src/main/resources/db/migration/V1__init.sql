create table if not exists answer
(
    id bigint auto_increment primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    content longtext null,
    petition_id bigint null,
    constraint UK_8o2ycrbsqgsqe35984pfkhhlg
        unique (petition_id)
);

create table if not exists password_verification_info
(
    id bigint auto_increment primary key,
    confirmed_at datetime(6) null,
    created_at datetime(6) null,
    username varchar(255) null,
    verification_code varchar(255) null
);

create table if not exists petition
(
    id bigint auto_increment primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    agree_count int null,
    answered bit null,
    category varchar(255) null,
    description longtext null,
    expired_at datetime(6) null,
    released bit null,
    temp_url varchar(255) null,
    title varchar(255) null,
    user_id bigint null,
    constraint UK_lbp7lx6brl567xxtwq9trc8t7
        unique (temp_url)
);

create table if not exists agreement
(
    id bigint auto_increment primary key,
    created_at datetime(6) null,
    description longtext null,
    user_id bigint null,
    petition_id bigint null,
    constraint UK6b2jp4145qyhk2hsvxoes773p
        unique (user_id, petition_id),
    constraint FK38mj0s6cdysv5aescp81w989v
        foreign key (petition_id) references petition (id)
);

create table if not exists revinfo
(
    rev bigint auto_increment primary key,
    revtstmp bigint null,
    user_id bigint null
);

create table if not exists answer_aud
(
    id bigint not null,
    rev bigint not null,
    revtype tinyint null,
    content longtext null,
    petition_id bigint null,
    primary key (id, rev),
    constraint FK93g4u12fhre47t3kpaag6r60c
        foreign key (rev) references revinfo (rev)
);

create table if not exists petition_aud
(
    id bigint not null,
    rev bigint not null,
    revtype tinyint null,
    answered bit null,
    category varchar(255) null,
    description longtext null,
    expired_at datetime(6) null,
    released bit null,
    temp_url varchar(255) null,
    title varchar(255) null,
    user_id bigint null,
    primary key (id, rev),
    constraint FKpubpwgg3epkfagt9psdcxdeeh
        foreign key (rev) references revinfo (rev)
);

create table if not exists sign_up_verification_info
(
    id bigint auto_increment primary key,
    confirmed_at datetime(6) null,
    created_at datetime(6) null,
    username varchar(255) null,
    verification_code varchar(255) null
);

create table if not exists user
(
    id bigint auto_increment primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    password varchar(255) null,
    user_role varchar(255) null,
    username varchar(255) null,
    constraint UK_sb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);