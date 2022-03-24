CREATE TABLE IF NOT EXISTS rejection
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_at    datetime              NULL,
    updated_at    datetime              NULL,
    `description` LONGTEXT              NOT NULL,
    CONSTRAINT pk_rejection PRIMARY KEY (id)
);

ALTER TABLE petition
    ADD rejection_id BIGINT NULL;

ALTER TABLE petition
    ADD CONSTRAINT FK_PETITION_ON_REJECTION FOREIGN KEY (rejection_id) REFERENCES rejection (id);