-- ========================================
-- Classroom Resource Distribution System
-- Oracle Database Schema
-- ========================================

-- 1️⃣ USERS TABLE
CREATE TABLE users (
    user_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    full_name VARCHAR2(100) NOT NULL,
    role VARCHAR2(20) NOT NULL CHECK (role IN ('ADMIN','LECTURE','STUDENT')),
    password_hash VARCHAR2(256) NOT NULL,
    status VARCHAR2(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP,
    updated_at TIMESTAMP
);

-- 2️⃣ CLASSES TABLE
CREATE TABLE classes (
    class_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    class_name VARCHAR2(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 3️⃣ RESOURCES TABLE
CREATE TABLE resources (
    resource_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title VARCHAR2(100) NOT NULL,
    type VARCHAR2(20) NOT NULL CHECK (type IN ('PDF','MP4','URL')),
    file_path VARCHAR2(255) NOT NULL,
    uploaded_by NUMBER NOT NULL,
    size_in_bytes NUMBER,
    mime_type VARCHAR2(50),
    status VARCHAR2(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','ARCHIVED')),
    uploaded_on TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users(user_id)
);

-- 4️⃣ STUDENT-CLASS MAPPING (Many-to-Many)
CREATE TABLE student_classes (
    student_id NUMBER NOT NULL,
    class_id NUMBER NOT NULL,
    enrolled_on TIMESTAMP DEFAULT SYSTIMESTAMP,
    PRIMARY KEY (student_id, class_id),
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES users(user_id),
    CONSTRAINT fk_class FOREIGN KEY (class_id) REFERENCES classes(class_id)
);

-- 5️⃣ USER LOGS TABLE
CREATE TABLE user_logs (
    log_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id NUMBER NOT NULL,
    action VARCHAR2(255) NOT NULL,
    ip_address VARCHAR2(45),
    action_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 6️⃣ BACKUP LOGS TABLE
CREATE TABLE backup_logs (
    backup_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    performed_by NUMBER NOT NULL,
    backup_type VARCHAR2(50) DEFAULT 'MANUAL',
    backup_location VARCHAR2(255),
    backup_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    status VARCHAR2(20) DEFAULT 'SUCCESS' CHECK (status IN ('SUCCESS','FAIL')),
    CONSTRAINT fk_backup_user FOREIGN KEY (performed_by) REFERENCES users(user_id)
);

-- 7️⃣ RESOURCE ACCESS TABLE
CREATE TABLE resource_access (
    access_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id NUMBER NOT NULL,
    resource_id NUMBER NOT NULL,
    access_time TIMESTAMP DEFAULT SYSTIMESTAMP,
    CONSTRAINT fk_access_student FOREIGN KEY (student_id) REFERENCES users(user_id),
    CONSTRAINT fk_access_resource FOREIGN KEY (resource_id) REFERENCES resources(resource_id)
);

-- 8️⃣ INDEXES FOR PERFORMANCE
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_resources_uploaded_by ON resources(uploaded_by);
CREATE INDEX idx_student_classes_class ON student_classes(class_id);
CREATE INDEX idx_user_logs_user ON user_logs(user_id);
CREATE INDEX idx_backup_logs_user ON backup_logs(performed_by);
CREATE INDEX idx_resource_access_student ON resource_access(student_id);
CREATE INDEX idx_resource_access_resource ON resource_access(resource_id);

-- 9️⃣ TRIGGERS FOR UPDATED_AT
CREATE OR REPLACE TRIGGER trg_users_update
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_resources_update
BEFORE UPDATE ON resources
FOR EACH ROW
BEGIN
    :NEW.uploaded_on := SYSTIMESTAMP;
END;
/

CREATE OR REPLACE TRIGGER trg_classes_update
BEFORE UPDATE ON classes
FOR EACH ROW
BEGIN
    :NEW.created_at := SYSTIMESTAMP;
END;
/

-- 🔹 TRIGGER TO LOG RESOURCE ACCESS
CREATE OR REPLACE TRIGGER trg_resource_access_log
AFTER INSERT ON resource_access
FOR EACH ROW
BEGIN
    INSERT INTO user_logs (user_id, action, ip_address, action_time)
    VALUES (
        :NEW.student_id,
        'Accessed resource ID: ' || :NEW.resource_id,
        NULL,
        :NEW.access_time
    );
END;
/
