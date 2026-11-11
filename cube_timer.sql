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