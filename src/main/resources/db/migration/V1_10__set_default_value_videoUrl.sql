UPDATE answer
SET video_url=''
WHERE video_url IS NULL;

ALTER TABLE answer
    MODIFY video_url VARCHAR(255) NOT NULL;

ALTER TABLE answer
    ALTER video_url SET DEFAULT '';