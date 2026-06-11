CREATE TABLE IF NOT EXISTS interview (
    id          VARCHAR(36)     NOT NULL PRIMARY KEY COMMENT '主键',
    title       VARCHAR(200)    NOT NULL COMMENT '题目标题',
    category_id VARCHAR(36)     NOT NULL COMMENT '关联 note_category.id',
    status      VARCHAR(20)     NOT NULL DEFAULT 'todo' COMMENT '状态：todo-待整理, learning-学习中, mastered-已掌握',
    difficulty  VARCHAR(10)     NOT NULL DEFAULT 'easy' COMMENT '难度：easy-简单, medium-中等, hard-困难',
    question    MEDIUMTEXT      NOT NULL COMMENT '题目（Markdown）',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_interview_category (category_id),
    INDEX idx_interview_status (status),
    INDEX idx_interview_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='面试刷题';

CREATE TABLE IF NOT EXISTS interview_ans (
    id            VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '主键',
    interview_id  VARCHAR(36) NOT NULL COMMENT '关联 interview.id',
    answer        MEDIUMTEXT  NULL COMMENT '我的作答（Markdown）',
    accuracy      DECIMAL(5,2) NULL DEFAULT NULL COMMENT '正确率（0.00-100.00）',
    solution      MEDIUMTEXT  NULL COMMENT 'AI生成的参考答案（Markdown）',
    created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_interview_ans_interview_id (interview_id),
    CONSTRAINT fk_interview_ans_interview FOREIGN KEY (interview_id)
        REFERENCES interview(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='面试作答';
