-- Delete existing admin records to avoid duplicates
DELETE FROM admin;

-- Insert default admin user
INSERT INTO admin (username, password, email, name) 
VALUES ('admin', 'admin123', 'admin@example.com', 'System Admin')
ON DUPLICATE KEY UPDATE username = username;

-- Insert subjects
INSERT INTO subject (subject_code, subject_name, description, credits) VALUES
('CS101', 'Introduction to Programming', 'Basic programming concepts and problem-solving techniques', 3),
('CS102', 'Data Structures', 'Study of fundamental data structures and algorithms', 3),
('CS201', 'Database Management', 'Database design, implementation, and management', 3),
('CS202', 'Web Development', 'Web technologies and development practices', 3),
('CS301', 'Software Engineering', 'Software development methodologies and practices', 3),
('CS302', 'Computer Networks', 'Network protocols and communication systems', 3),
('CS401', 'Artificial Intelligence', 'AI concepts and machine learning algorithms', 3),
('CS402', 'Operating Systems', 'OS concepts and system programming', 3),
('CS501', 'Cloud Computing', 'Cloud platforms and distributed systems', 3),
('CS502', 'Cybersecurity', 'Security principles and practices', 3)
ON DUPLICATE KEY UPDATE subject_code = subject_code; 