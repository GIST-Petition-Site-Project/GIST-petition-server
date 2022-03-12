ALTER TABLE petition
    ADD answer_id BIGINT NULL;

ALTER TABLE petition
    ADD CONSTRAINT FK_PETITION_ON_ANSWER FOREIGN KEY (answer_id) REFERENCES answer (id);

ALTER TABLE petition
    DROP COLUMN answered;

ALTER TABLE answer
    DROP COLUMN petition_id;