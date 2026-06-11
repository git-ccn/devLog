ALTER TABLE note_note
ADD COLUMN category_id VARCHAR(36) NULL AFTER category;

UPDATE note_note n
JOIN note_category c ON c.name = n.category
SET n.category_id = c.id
WHERE (n.category_id IS NULL OR n.category_id = '')
  AND n.category IS NOT NULL
  AND n.category != '';

CREATE INDEX idx_note_deleted_category_id ON note_note (deleted, category_id);

ALTER TABLE note_note
ADD CONSTRAINT fk_note_note_category_id FOREIGN KEY (category_id) REFERENCES note_category (id);

