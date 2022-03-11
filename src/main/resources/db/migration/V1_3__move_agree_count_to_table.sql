CREATE TABLE IF NOT EXISTS agree_count
(
    id    BIGINT AUTO_INCREMENT NOT NULL,
    count INT                   NOT NULL,
    CONSTRAINT pk_agree_count PRIMARY KEY (id)
);

ALTER TABLE petition
    DROP COLUMN agree_count;

ALTER TABLE petition
    ADD agree_count_id BIGINT NOT NULL;

ALTER TABLE petition
    ADD CONSTRAINT FK_PETITION_ON_AGREE_COUNT FOREIGN KEY (agree_count_id) REFERENCES agree_count (id);