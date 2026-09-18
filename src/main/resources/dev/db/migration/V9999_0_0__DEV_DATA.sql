-- /r/ex -> https://example.org
INSERT INTO bookmark (slug, target) VALUES
    ('ex', 'https://example.org');

-- dev/dev
INSERT INTO owner (name, hashed_password) VALUES
    ('dev', '$argon2id$v=19$m=16384,t=2,p=1$QcnCePIBWXNA1hgIKhJHxA$dRfyxkA8bTWZS9QoOcLXvmiJFtREwn1C3r6QOGnf+4o');
