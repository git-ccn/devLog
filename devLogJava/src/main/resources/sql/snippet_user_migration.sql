ALTER TABLE devlog_snippet
ADD COLUMN user_id VARCHAR(36) NOT NULL DEFAULT '' COMMENT '用户ID' AFTER language;

CREATE INDEX idx_snippet_user_id ON devlog_snippet (user_id);
