SET FOREIGN_KEY_CHECKS = 0;

DELETE
FROM refresh_token;
DELETE
FROM native_users;
DELETE
FROM social_users;
DELETE
FROM users;

ALTER TABLE refresh_token
    AUTO_INCREMENT = 1;
ALTER TABLE social_users
    AUTO_INCREMENT = 1;
ALTER TABLE users
    AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users
    (nick_name, gender, role)
VALUES ('관리자', 'M', 'ADMIN'),
       ('테스트', 'M', 'USER'),
       ('테스트2', 'F', 'USER'),
       ('테스트3', 'F', 'USER');

INSERT INTO native_users
    (id, email, password_hash)
VALUES (1, 'admin', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (2, 'email@email.com', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (3, 'email2@email.com', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (4, 'email3@email.com', '$2a$12$DetD6G5ghlXmwFzKHEYOPemI/YMCfo8UYfLc6YAwCt7YB3QS1YbW6');