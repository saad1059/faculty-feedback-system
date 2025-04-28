-- Delete existing admin records to avoid duplicates
DELETE FROM admin;

-- Insert default admin user
INSERT INTO admin (username, password, email, name) 
VALUES ('admin', 'admin123', 'admin@example.com', 'System Admin'); 