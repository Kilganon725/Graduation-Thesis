CREATE DATABASE IF NOT EXISTS aifomo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aifomo;

DROP TABLE IF EXISTS `ai_chat`;
DROP TABLE IF EXISTS `fomo_intervention`;
DROP TABLE IF EXISTS `recommendation`;
DROP TABLE IF EXISTS `fomo_test`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  major VARCHAR(128) DEFAULT NULL,
  learning_goal VARCHAR(255) DEFAULT NULL,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `fomo_test` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  short_video_minutes INT NOT NULL DEFAULT 0,
  learning_switch_times INT NOT NULL DEFAULT 0,
  anxiety_frequency INT NOT NULL DEFAULT 0,
  ai_usage_times INT NOT NULL DEFAULT 0,
  short_video_time INT NOT NULL,
  learning_switch INT NOT NULL,
  anxiety_level VARCHAR(32) NOT NULL,
  ai_usage INT NOT NULL,
  total_score INT NOT NULL,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_fomo_user_id (user_id),
  CONSTRAINT fk_fomo_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `fomo_intervention` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  test_id BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  content TEXT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT '待执行',
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_time DATETIME DEFAULT NULL,
  INDEX idx_intervention_user_id (user_id),
  INDEX idx_intervention_test_id (test_id),
  CONSTRAINT fk_intervention_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
  CONSTRAINT fk_intervention_test FOREIGN KEY (test_id) REFERENCES `fomo_test`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `recommendation` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  type VARCHAR(64) NOT NULL,
  INDEX idx_rec_user_id (user_id),
  CONSTRAINT fk_rec_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ai_chat` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  question TEXT NOT NULL,
  answer TEXT NOT NULL,
  created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_chat_user_id (user_id),
  CONSTRAINT fk_chat_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
