-- 创建测试用户表
CREATE TABLE IF NOT EXISTS test_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    mobile VARCHAR(20),
    status INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO test_user (username, email, mobile, status) VALUES
('test_user_1', 'test1@example.com', '13800138000', 0),
('test_user_2', 'test2@example.com', '13800138001', 1),
('test_user_3', 'test3@example.com', '13800138002', 0);
