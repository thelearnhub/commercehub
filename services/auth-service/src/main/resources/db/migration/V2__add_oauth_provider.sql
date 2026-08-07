-- Google-authenticated users have no local password, so it must become
-- nullable. auth_provider distinguishes how the account was created.
ALTER TABLE users
    MODIFY COLUMN password_hash VARCHAR(255) NULL;

ALTER TABLE users
    ADD COLUMN auth_provider VARCHAR(32) NOT NULL DEFAULT 'LOCAL' AFTER password_hash;
