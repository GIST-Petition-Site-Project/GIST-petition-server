DELETE from answer;
DELETE from agreement;
DELETE from petition;

DELETE from sign_up_verification_info;
DELETE from password_verification_info;
DELETE from user;

DELETE from answer_aud;
DELETE from petition_aud;
DELETE from revinfo;

insert into user (created_at, updated_at, password, user_role, username)
values ("2022-03-08 13:41:30.036848", "2022-03-08 13:41:30.036848",
        "$2a$10$XD375IMzQuKFSe0aeFRbmOZAkb86zdp/Q9gBJeK3U/P90NxLNQh/.", "ADMIN", "testAdmin@gist.ac.kr");

insert into user (created_at, updated_at, password, user_role, username)
values ("2022-03-08 13:41:30.036848", "2022-03-08 13:41:30.036848",
        "$2a$10$KbfxgQq7l9V9zEKXODOcVO7oibR2TIBRSOs6NfkPh7N0lmhapM.0e", "MANAGER", "testManager@gist.ac.kr");