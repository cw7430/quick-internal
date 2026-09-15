CREATE DATABASE `quick-chat-test`;
CREATE USER 'test_user'@'localhost' IDENTIFIED BY 'test1234';
USE `quick-chat-test`;
GRANT ALL PRIVILEGES ON `quick-chat-test`.* TO 'test_user'@'localhost';
FLUSH PRIVILEGES;