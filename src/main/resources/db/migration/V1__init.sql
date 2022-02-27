create table if not exists answer
(
    id bigint auto_increment primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    content longtext null,
    petition_id bigint null,
    constraint uk_answer_petition_id
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
    constraint uk_petition_temp_url
        unique (temp_url)
);

create table if not exists agreement
(
    id bigint auto_increment primary key,
    created_at datetime(6) null,
    description longtext null,
    user_id bigint null,
    petition_id bigint null,
    constraint uk_agreement_user_id_petition_id
        unique (user_id, petition_id),
    constraint fk_agreement_petition_petition_id
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
    constraint fk_answer_aud_revinfo_rev
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
    constraint fk_petition_aud_revinfo_rev
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
    constraint uk_user_username
        unique (username)
);