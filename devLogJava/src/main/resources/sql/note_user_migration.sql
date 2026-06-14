ALTER TABLE note_note
ADD COLUMN user_id VARCHAR(36) NOT NULL DEFAULT '' COMMENT '用户ID' AFTER category_id;

CREATE INDEX idx_note_user_id ON note_note (user_id);
