CREATE TABLE if not exists answer2
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NULL,
    updated_at  datetime              NULL,
    content     longtext              NULL,
    petition_id BIGINT                NULL,
    CONSTRAINT pk_answer2 PRIMARY KEY (id)
);

ALTER TABLE answer2
    ADD CONSTRAINT uk_answer2_petition_id UNIQUE (petition_id);

ALTER TABLE answer2
    ADD CONSTRAINT fk_answer2_on_petition FOREIGN KEY (petition_id) REFERENCES petition (id);

create table if not exists answer2_aud
(
    id          bigint   not null,
    rev         bigint   not null,
    revtype     tinyint  null,
    content     longtext null,
    petition_id bigint   null,
    primary key (id, rev),
    constraint fk_answer2_aud_revinfo_rev
        foreign key (rev) references revinfo (rev)
);