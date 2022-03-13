ALTER TABLE agree_count
    ADD petition_id BIGINT NULL;

ALTER TABLE agree_count
    MODIFY petition_id BIGINT NOT NULL;

ALTER TABLE agree_count
    ADD CONSTRAINT uc_agreecount_petitionid UNIQUE (petition_id);

ALTER TABLE petition
    DROP FOREIGN KEY FK_PETITION_ON_AGREE_COUNT;

ALTER TABLE petition
    DROP COLUMN agree_count_id;