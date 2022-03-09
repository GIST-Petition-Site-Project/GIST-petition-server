CREATE TABLE answer2
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NULL,
    updated_at  datetime              NULL,
    content     VARCHAR(255)          NULL,
    petition_id BIGINT                NULL,
    CONSTRAINT pk_answer2 PRIMARY KEY (id)
);

ALTER TABLE answer2
    ADD CONSTRAINT uk_answer2_petition_id UNIQUE (petition_id);

ALTER TABLE answer2
    ADD CONSTRAINT fk_answer2_on_petition FOREIGN KEY (petition_id) REFERENCES petition (id);