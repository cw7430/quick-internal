SET FOREIGN_KEY_CHECKS = 0;

DELETE
FROM chat_message;
DELETE
FROM chat_member;
DELETE
FROM chat_room;
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

-- 1. users 기본 데이터 삽입
INSERT INTO users (id, nick_name, gender, role)
VALUES (1, '관리자', 'M', 'ADMIN'),
       (2, '테스트', 'M', 'USER'),
       (3, '테스트2', 'F', 'USER'),
       (4, '테스트3', 'F', 'USER');

-- 2. users 5~50000번 대량 생성
INSERT INTO users (id, nick_name, gender, role)
WITH digits (d) AS (SELECT 0
                    UNION ALL
                    SELECT 1
                    UNION ALL
                    SELECT 2
                    UNION ALL
                    SELECT 3
                    UNION ALL
                    SELECT 4
                    UNION ALL
                    SELECT 5
                    UNION ALL
                    SELECT 6
                    UNION ALL
                    SELECT 7
                    UNION ALL
                    SELECT 8
                    UNION ALL
                    SELECT 9),
     seq (n) AS (SELECT d4.d * 10000 + d3.d * 1000 + d2.d * 100 + d1.d * 10 + d0.d
                 FROM digits d0,
                      digits d1,
                      digits d2,
                      digits d3,
                      digits d4)
SELECT n,
       CONCAT('테스트', n - 1),
       IF(n % 2 = 0, 'M', 'F'),
       'USER'
FROM seq
WHERE n BETWEEN 5 AND 50000
ORDER BY n;

-- 3. 다음 auto_increment 시작값 설정
ALTER TABLE users
    AUTO_INCREMENT = 50001;

-- 4. native_users 기본 데이터 삽입
INSERT INTO native_users (id, email, password_hash)
VALUES (1, 'admin', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (2, 'email@email.com', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (3, 'email2@email.com', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (4, 'email3@email.com', '$2a$12$Jyz/IhFJrfOmvNdekDXALup3fvvqiYvle64CIBDVY560bFT1sBaDG');

-- 5. native_users 5~50000번 대량 생성
INSERT INTO native_users (id, email, password_hash)
SELECT id,
       CONCAT('email', id - 1, '@email.com'),
       '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'
FROM users
WHERE id BETWEEN 5 AND 50000
ORDER BY id;