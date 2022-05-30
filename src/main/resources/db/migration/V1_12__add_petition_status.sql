ALTER TABLE petition
    ADD status VARCHAR(255) NULL DEFAULT 'TEMPORARY';

UPDATE petition
SET status = 'RELEASED'
WHERE released = 1;

UPDATE petition
SET status = 'REJECTED'
WHERE rejection_id IS NOT NULL;

UPDATE petition
SET status = 'ANSWERED'
WHERE answer_id IS NOT NULL;