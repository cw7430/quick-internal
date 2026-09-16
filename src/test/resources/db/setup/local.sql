CREATE DATABASE `quick-chat`;
CREATE USER 'test_user'@'localhost' IDENTIFIED BY 'test1234';
USE `quick-chat`;
GRANT ALL PRIVILEGES ON `quick-chat`.* TO 'test_user'@'localhost';
FLUSH PRIVILEGES;