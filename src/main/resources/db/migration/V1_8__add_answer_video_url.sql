ALTER TABLE answer
    ADD video_url VARCHAR(255) NULL;

ALTER TABLE answer_aud
    ADD video_url VARCHAR(255) NULL;

ALTER TABLE answer
    MODIFY description LONGTEXT NOT NULL;

ALTER TABLE petition
    MODIFY title VARCHAR(255) NOT NULL;