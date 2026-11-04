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