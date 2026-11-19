CREATE DATABASE IF NOT EXISTS cube_timer
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;

USE cube_timer;

CREATE TABLE cube_record (
  id         INT PRIMARY KEY AUTO_INCREMENT,
  time_ms    INT NOT NULL COMMENT '成绩，单位毫秒',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='魔方成绩记录';

-- 验证
DESC cube_record;
SELECT * FROM cube_record;


CREATE TABLE cube_scramble (
  id        INT PRIMARY KEY AUTO_INCREMENT,
  record_id INT NOT NULL COMMENT '关联 cube_record.id',
  scramble  VARCHAR(255) NOT NULL COMMENT '打乱公式，如 R U R'' U'' F2',
  seq       INT NOT NULL DEFAULT 1 COMMENT '第几段（同一成绩可有多段）',
  KEY idx_record_id (record_id),
  CONSTRAINT fk_scramble_record
    FOREIGN KEY (record_id) REFERENCES cube_record(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩对应的打乱公式';

CREATE TABLE `user` (
  id          INT          NOT NULL AUTO_INCREMENT,
  username    VARCHAR(50)  NOT NULL COMMENT '用户名',
  password    VARCHAR(100) NOT NULL COMMENT 'BCrypt 密文（约 60 字符，留余量）',
  nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1=正常 0=禁用',
  create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';




ALTER TABLE cube_record
  ADD COLUMN user_id INT NULL COMMENT '所属用户 id' AFTER id;


ALTER TABLE cube_record CHANGE COLUMN created_at create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';


ALTER TABLE cube_record
  ADD COLUMN update_time DATETIME NULL COMMENT '更新时间' AFTER create_time,
  ADD COLUMN create_user INT NULL COMMENT '创建人 id' AFTER update_time,
  ADD COLUMN update_user INT NULL COMMENT '更新人 id' AFTER create_user;


UPDATE cube_record SET create_user = 2, update_user = 2 WHERE create_user IS NULL;


SHOW COLUMNS FROM cube_record;