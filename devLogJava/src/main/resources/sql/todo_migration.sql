CREATE TABLE IF NOT EXISTS devlog_todo (
    id          VARCHAR(36)     NOT NULL PRIMARY KEY COMMENT '主键',
    title       VARCHAR(500)    NOT NULL COMMENT '标题',
    status      VARCHAR(20)     NOT NULL DEFAULT 'pending' COMMENT '状态：pending-待办, done-已完成',
    user_id     VARCHAR(36)     NULL COMMENT '用户ID',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_devlog_todo_user (user_id),
    INDEX idx_devlog_todo_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='待办事项';
