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

ALTER TABLE chat_message
    AUTO_INCREMENT = 1;
ALTER TABLE chat_member
    AUTO_INCREMENT = 1;
ALTER TABLE chat_room
    AUTO_INCREMENT = 1;
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

-- 2. users 5~50,000번 대량 생성
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
                 FROM digits d0
                          CROSS JOIN digits d1
                          CROSS JOIN digits d2
                          CROSS JOIN digits d3
                          CROSS JOIN digits d4)
SELECT n,
       CONCAT('테스트', n - 1),
       IF(n % 2 = 0, 'M', 'F'),
       'USER'
FROM seq
WHERE n BETWEEN 5 AND 50000
ORDER BY n;

-- 3. 다음 auto_increment 시작값 설정
ALTER TABLE users
    AUTO_INCREMENT = 1;

-- 4. native_users 기본 데이터 삽입
INSERT INTO native_users (id, email, password_hash)
VALUES (1, 'admin', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (2, 'email@email.com', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (3, 'email2@email.com', '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'),
       (4, 'email3@email.com', '$2a$12$Jyz/IhFJrfOmvNdekDXALup3fvvqiYvle64CIBDVY560bFT1sBaDG');

-- 5. native_users 5~50,000번 대량 생성
INSERT INTO native_users (id, email, password_hash)
SELECT id,
       CONCAT('email', id - 1, '@email.com'),
       '$2a$10$3I4CT9WUIy..IHBvJkbDT.IVaXSP7CjLfUgNB3EZtitup8BgSv6Y2'
FROM users
WHERE id BETWEEN 5 AND (SELECT id FROM users ORDER BY id DESC LIMIT 1)
ORDER BY id;

-- 6. chat_room 대량 생성 - 1
INSERT INTO chat_room (valid)
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
                 FROM digits d0
                          CROSS JOIN digits d1
                          CROSS JOIN digits d2
                          CROSS JOIN digits d3
                          CROSS JOIN digits d4)
SELECT 'Y'
FROM seq
WHERE n BETWEEN 1 AND (((SELECT id FROM users ORDER BY id DESC LIMIT 1) - 4) / 2)
ORDER BY n;

ALTER TABLE chat_room
    AUTO_INCREMENT = 1;

-- 7. chat_member 대량 생성
INSERT INTO chat_member (chat_room_id, user_id, accepted)
WITH user_info AS (SELECT MAX(id) AS max_id, (MAX(id) - 4) AS user_count
                   FROM users),
     ranked_room AS (SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
                     FROM chat_room)
SELECT cr.id                                                                       AS chat_room_id,
       5 + CAST(MOD(((cr.rn - 1) * 2 + m.member_offset), ui.user_count) AS SIGNED) AS user_id,
       'Y'
FROM ranked_room cr
         CROSS JOIN user_info ui
         CROSS JOIN (SELECT 0 AS member_offset
                     UNION ALL
                     SELECT 1) m
ORDER BY cr.id, member_offset;

ALTER TABLE chat_member
    AUTO_INCREMENT = 1;

-- 6. chat_room 대량 생성 - 2
INSERT INTO chat_room (valid)
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
                 FROM digits d0
                          CROSS JOIN digits d1
                          CROSS JOIN digits d2
                          CROSS JOIN digits d3
                          CROSS JOIN digits d4)
SELECT 'Y'
FROM seq
WHERE n BETWEEN 1 AND (((SELECT id FROM users ORDER BY id DESC LIMIT 1) - 4) / 2)
ORDER BY n;

ALTER TABLE chat_room
    AUTO_INCREMENT = 1;


-- 7. chat_member 대량 생성 - 2
INSERT INTO chat_member (chat_room_id, user_id, accepted)
VALUES ((((SELECT MAX(id) FROM users) - 4) / 2) + 1,
        5,
        'Y'),
       ((((SELECT MAX(id) FROM users) - 4) / 2) + 1,
        (SELECT MAX(id) FROM users),
        'Y');

INSERT INTO chat_member (chat_room_id, user_id, accepted)
WITH target_rooms AS (SELECT id,
                             ROW_NUMBER() OVER (ORDER BY id) AS rn
                      FROM chat_room
                      WHERE id > (((SELECT MAX(id) FROM users) - 4) / 2) + 1)
SELECT r.id,
       6 + ((r.rn - 1) * 2 + m.member_offset),
       'Y'
FROM target_rooms r
         CROSS JOIN (SELECT 0 AS member_offset
                     UNION ALL
                     SELECT 1) m
ORDER BY r.id, m.member_offset;
ALTER TABLE chat_member
    AUTO_INCREMENT = 1;

-- 8. chat_message 대량 생성 - 1
INSERT INTO chat_message (chat_room_id, chat_member_id, message, unread)
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
     seq (n) AS (SELECT d3.d * 1000
                            + d2.d * 100
                            + d1.d * 10
                            + d0.d
                 FROM digits d0
                          CROSS JOIN digits d1
                          CROSS JOIN digits d2
                          CROSS JOIN digits d3),
     messages AS (SELECT 1 AS message_order, 1 AS member_id, '안녕하세요' AS message
                  UNION ALL
                  SELECT 2, 2, '안녕하세요'
                  UNION ALL
                  SELECT 3, 1, '어디사세요?'
                  UNION ALL
                  SELECT 4, 2, '서울이요'
                  UNION ALL
                  SELECT 5, 1, '저는 경기도에요'
                  UNION ALL
                  SELECT 6, 2, '경기도 어디요?'
                  UNION ALL
                  SELECT 7, 1, '용인이에요. 서울어디에요?'
                  UNION ALL
                  SELECT 8, 2, '중랑구요'
                  UNION ALL
                  SELECT 9, 1, '밥은 드셨어요?'
                  UNION ALL
                  SELECT 10, 2, '아뇨')
SELECT 1,
       m.member_id,
       m.message,
       0
FROM seq
         CROSS JOIN messages m
ORDER BY seq.n, m.message_order;

ALTER TABLE chat_message
    AUTO_INCREMENT = 1;

UPDATE chat_message
SET unread = 1
ORDER BY id DESC
LIMIT 2;

-- 9. alarm 대량 생성 - 1
INSERT INTO alarm (my_user_id,
                   other_user_id,
                   chat_room_id,
                   type)
SELECT MIN(cm.user_id),
       MAX(cm.user_id),
       cm.chat_room_id,
       'CREATE'
FROM chat_member cm
GROUP BY cm.chat_room_id
HAVING COUNT(*) = 2
ORDER BY cm.chat_room_id;
ALTER TABLE alarm
    AUTO_INCREMENT = 1;

-- 10. alarm 대량 생성 - 2
INSERT INTO alarm (my_user_id,
                   other_user_id,
                   chat_room_id,
                   type)
SELECT MAX(cm.user_id),
       MIN(cm.user_id),
       cm.chat_room_id,
       'ACCEPT'
FROM chat_member cm
GROUP BY cm.chat_room_id
HAVING COUNT(*) = 2
ORDER BY cm.chat_room_id;
ALTER TABLE alarm
    AUTO_INCREMENT = 1;