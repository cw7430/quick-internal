SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM refresh_token;
DELETE FROM native_users;
DELETE FROM social_users;
DELETE FROM users;

ALTER TABLE refresh_token AUTO_INCREMENT = 1;
ALTER TABLE social_users AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users
(id, nick_name, gender, role)
VALUES (1, '관리자', 'M', 'ADMIN'),
       (2, '테스트', 'M', 'USER'),
       (3, '테스트2', 'F', 'USER');

INSERT INTO native_users
(id, email, password_hash)
VALUES (1, 'admin', '$2b$10$sVXWhDmhKu5JpPOhuqhKQOuIl2NKNEv6T9jjMhTnZJSAgAapWDMiu'),
       (2, 'email@email.com', '$2b$10$sVXWhDmhKu5JpPOhuqhKQOuIl2NKNEv6T9jjMhTnZJSAgAapWDMiu'),
       (3, 'email2@email.com', '$2b$10$sVXWhDmhKu5JpPOhuqhKQOuIl2NKNEv6T9jjMhTnZJSAgAapWDMiu');