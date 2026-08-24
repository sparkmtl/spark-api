-- Converts legacy boolean checked_in to integer (0/1) expected by the Java entity.
ALTER TABLE users ALTER COLUMN checked_in DROP DEFAULT;
ALTER TABLE users
    ALTER COLUMN checked_in TYPE integer
    USING (CASE WHEN checked_in THEN 1 ELSE 0 END);
ALTER TABLE users ALTER COLUMN checked_in SET DEFAULT 0;
ALTER TABLE users ALTER COLUMN checked_in SET NOT NULL;
