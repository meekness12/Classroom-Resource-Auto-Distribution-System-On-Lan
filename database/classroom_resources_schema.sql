-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 18, 2025 at 12:33 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `classroom_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `announcements`
--

CREATE TABLE `announcements` (
  `announcement_id` int(11) NOT NULL,
  `sender_role` enum('ADMIN','LECTURER') NOT NULL,
  `sender_id` int(11) NOT NULL,
  `target` enum('STUDENTS','LECTURERS','ALL') NOT NULL,
  `message` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `announcements`
--

INSERT INTO `announcements` (`announcement_id`, `sender_role`, `sender_id`, `target`, `message`, `created_at`) VALUES
(1, 'LECTURER', 3, 'STUDENTS', 'hey', '2025-11-16 05:40:41'),
(2, 'ADMIN', 4, 'STUDENTS', 'hey', '2025-11-16 05:55:40'),
(3, 'ADMIN', 4, 'ALL', 'welcome to ', '2025-11-16 05:56:06'),
(4, 'ADMIN', 4, 'STUDENTS', 'wapi', '2025-11-17 22:32:57'),
(5, 'ADMIN', 4, 'STUDENTS', 'hey all of you', '2025-11-17 23:06:38'),
(6, 'LECTURER', 3, 'STUDENTS', 'how are you', '2025-11-17 23:14:17');

-- --------------------------------------------------------

--
-- Table structure for table `backup_logs`
--

CREATE TABLE `backup_logs` (
  `backup_id` int(11) NOT NULL,
  `performed_by` int(11) NOT NULL,
  `backup_type` varchar(50) DEFAULT 'MANUAL',
  `backup_location` varchar(255) DEFAULT NULL,
  `backup_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('SUCCESS','FAIL') DEFAULT 'SUCCESS',
  `files` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `backup_logs`
--

INSERT INTO `backup_logs` (`backup_id`, `performed_by`, `backup_type`, `backup_location`, `backup_time`, `status`, `files`) VALUES
(1, 4, 'FULL', NULL, '2025-11-17 20:16:33', 'FAIL', ';C:\\ClassroomBackups\\files_backup_20251117_221633'),
(2, 4, 'FULL', NULL, '2025-11-17 20:18:57', 'SUCCESS', 'C:\\ClassroomBackups\\classroom_db_backup_20251117_221857.sql;C:\\ClassroomBackups\\files_backup_20251117_221857'),
(3, 4, 'FULL', NULL, '2025-11-17 20:43:34', 'SUCCESS', 'C:\\ClassroomBackups\\classroom_db_backup_20251117_224330.sql;C:\\ClassroomBackups\\files_backup_20251117_224330'),
(4, 4, 'FULL', NULL, '2025-11-17 20:50:32', 'SUCCESS', 'C:\\ClassroomBackups\\classroom_db_backup_20251117_225026.sql;C:\\ClassroomBackups\\files_backup_20251117_225029.zip'),
(5, 4, 'FULL', NULL, '2025-11-17 21:12:18', 'SUCCESS', 'C:\\ClassroomBackups\\classroom_db_backup_20251117_231212.sql;C:\\ClassroomBackups\\files_backup_20251117_231212'),
(6, 4, 'FULL', NULL, '2025-11-17 21:43:08', 'SUCCESS', 'C:\\ClassroomBackups\\classroom_db_backup_20251117_234256.sql;C:\\ClassroomBackups\\files_backup_20251117_234256');

--
-- Triggers `backup_logs`
--
DELIMITER $$
CREATE TRIGGER `trg_backup_logs_delete` AFTER DELETE ON `backup_logs` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (OLD.performed_by, CONCAT('DELETE from backup_logs: status=', OLD.status), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_backup_logs_insert` AFTER INSERT ON `backup_logs` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.performed_by, CONCAT('INSERT into backup_logs: status=', NEW.status), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_backup_logs_update` AFTER UPDATE ON `backup_logs` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.performed_by, CONCAT('UPDATE on backup_logs: status=', NEW.status), NOW());
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `backup_schedule`
--

CREATE TABLE `backup_schedule` (
  `id` int(11) NOT NULL,
  `day_of_week` varchar(20) NOT NULL,
  `hour` int(11) NOT NULL,
  `minute` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `classes`
--

CREATE TABLE `classes` (
  `class_id` int(11) NOT NULL,
  `class_name` varchar(50) NOT NULL,
  `lecturer_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `classes`
--

INSERT INTO `classes` (`class_id`, `class_name`, `lecturer_id`, `created_at`) VALUES
(1, 'information y2 a', NULL, '2025-11-08 11:01:26'),
(2, 'IT Y1', NULL, '2025-11-08 13:25:32');

--
-- Triggers `classes`
--
DELIMITER $$
CREATE TRIGGER `trg_classes_delete` AFTER DELETE ON `classes` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (1, CONCAT('DELETE from classes: ', OLD.class_name), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_classes_insert` AFTER INSERT ON `classes` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (1, CONCAT('INSERT into classes: ', NEW.class_name), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_classes_update` AFTER UPDATE ON `classes` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (1, CONCAT('UPDATE on classes: ', NEW.class_name), NOW());
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `resources`
--

CREATE TABLE `resources` (
  `resource_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `type` enum('PDF','MP4','URL') NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `uploaded_by` int(11) NOT NULL,
  `size_in_bytes` bigint(20) DEFAULT NULL,
  `mime_type` varchar(50) DEFAULT NULL,
  `status` enum('ACTIVE','ARCHIVED') DEFAULT 'ACTIVE',
  `uploaded_on` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `resources`
--

INSERT INTO `resources` (`resource_id`, `title`, `type`, `file_path`, `uploaded_by`, `size_in_bytes`, `mime_type`, `status`, `uploaded_on`) VALUES
(1, 'Windows server administrator.docx', 'URL', 'C:\\Users\\user\\OneDrive\\Dokumente\\Windows server administrator.docx', 3, NULL, NULL, 'ACTIVE', '2025-11-09 13:57:53'),
(2, 'question and worls.docx', 'URL', 'D:\\Learn\\researchmethodology\\question and worls.docx', 3, NULL, NULL, 'ACTIVE', '2025-11-09 13:58:54'),
(3, 'question and worls.docx', 'URL', 'D:\\Learn\\researchmethodology\\question and worls.docx', 3, NULL, NULL, 'ACTIVE', '2025-11-09 14:17:06'),
(4, 'README.md', 'URL', 'C:\\Users\\user\\OneDrive\\Documents\\java_cat_24rp01885\\README.md', 9, NULL, NULL, 'ACTIVE', '2025-11-10 09:25:59'),
(5, 'Introduction to Research Methodology Sylabus_2022.pdf', 'PDF', 'D:\\Learn\\Semester 1\\Semester 1\\CCMRM601 Research Methodology\\Introduction to Research Methodology Sylabus_2022.pdf', 3, NULL, NULL, 'ACTIVE', '2025-11-10 21:44:38');

--
-- Triggers `resources`
--
DELIMITER $$
CREATE TRIGGER `trg_resources_delete` AFTER DELETE ON `resources` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (OLD.uploaded_by, CONCAT('DELETE from resources: ', OLD.title), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_resources_insert` AFTER INSERT ON `resources` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.uploaded_by, CONCAT('INSERT into resources: ', NEW.title), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_resources_update` AFTER UPDATE ON `resources` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.uploaded_by, CONCAT('UPDATE on resources: ', NEW.title), NOW());
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `resource_access`
--

CREATE TABLE `resource_access` (
  `access_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `resource_id` int(11) NOT NULL,
  `access_time` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `resource_access`
--
DELIMITER $$
CREATE TRIGGER `trg_resource_access_delete` AFTER DELETE ON `resource_access` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (OLD.student_id, CONCAT('DELETE from resource_access: resource_id=', OLD.resource_id), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_resource_access_insert` AFTER INSERT ON `resource_access` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.student_id, CONCAT('INSERT into resource_access: resource_id=', NEW.resource_id), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_resource_access_update` AFTER UPDATE ON `resource_access` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.student_id, CONCAT('UPDATE on resource_access: resource_id=', NEW.resource_id), NOW());
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `resource_assignments`
--

CREATE TABLE `resource_assignments` (
  `assignment_id` int(11) NOT NULL,
  `resource_id` int(11) DEFAULT NULL,
  `class_id` int(11) DEFAULT NULL,
  `lecturer_id` int(11) DEFAULT NULL,
  `assigned_on` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `resource_assignments`
--

INSERT INTO `resource_assignments` (`assignment_id`, `resource_id`, `class_id`, `lecturer_id`, `assigned_on`) VALUES
(1, 1, 1, 3, '2025-11-09 14:13:31'),
(2, 1, 2, 3, '2025-11-09 14:13:56'),
(3, 2, 1, 3, '2025-11-09 14:14:58'),
(4, 3, 2, 3, '2025-11-09 14:17:32'),
(5, 1, 1, 3, '2025-11-09 14:32:29'),
(6, 1, 1, 3, '2025-11-09 14:51:56'),
(7, 1, 1, 3, '2025-11-09 14:59:03'),
(8, 2, 1, 3, '2025-11-09 14:59:29'),
(9, 2, 1, 3, '2025-11-09 15:00:53'),
(10, 3, 1, 3, '2025-11-09 15:03:03'),
(11, 3, 1, 3, '2025-11-09 15:08:17'),
(12, 2, 1, 3, '2025-11-09 15:13:25'),
(13, 2, 1, 3, '2025-11-09 15:21:12'),
(14, 1, 1, 3, '2025-11-09 15:56:10'),
(15, 1, 1, 3, '2025-11-09 15:57:20'),
(16, 1, 1, 3, '2025-11-09 15:57:26'),
(17, 2, 1, 3, '2025-11-09 15:58:26'),
(18, 1, 1, 3, '2025-11-10 09:24:14'),
(19, 4, 1, 9, '2025-11-10 09:26:15'),
(20, 1, 1, 3, '2025-11-10 20:34:25'),
(21, 1, 1, 3, '2025-11-10 20:40:25'),
(22, 2, 1, 3, '2025-11-10 20:40:53'),
(23, 1, 1, 3, '2025-11-10 20:47:23'),
(24, 3, 1, 3, '2025-11-10 20:47:28'),
(25, 1, 1, 3, '2025-11-10 21:04:26'),
(26, 2, 1, 3, '2025-11-10 21:04:39'),
(27, 2, 1, 3, '2025-11-10 21:09:15'),
(28, 1, 1, 3, '2025-11-10 21:09:19'),
(29, 1, 1, 3, '2025-11-10 21:11:18'),
(30, 1, 1, 3, '2025-11-10 21:19:36'),
(31, 1, 1, 3, '2025-11-10 21:21:18'),
(32, 1, 1, 3, '2025-11-10 21:35:09'),
(33, 1, 1, 3, '2025-11-10 21:41:16'),
(34, 2, 1, 3, '2025-11-10 21:43:07'),
(35, 5, 1, 3, '2025-11-10 21:44:46'),
(36, 5, 1, 3, '2025-11-10 21:45:16'),
(37, 5, 1, 3, '2025-11-11 07:36:27'),
(38, 1, 1, 3, '2025-11-11 07:47:41'),
(39, 1, 1, 3, '2025-11-11 08:02:35'),
(40, 1, 1, 3, '2025-11-11 08:22:06'),
(41, 1, 1, 3, '2025-11-11 08:23:35');

-- --------------------------------------------------------

--
-- Table structure for table `resource_classes`
--

CREATE TABLE `resource_classes` (
  `resource_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `assigned_on` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `student_classes`
--

CREATE TABLE `student_classes` (
  `student_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `enrolled_on` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `student_classes`
--

INSERT INTO `student_classes` (`student_id`, `class_id`, `enrolled_on`) VALUES
(1, 1, '2025-11-08 13:28:58'),
(5, 1, '2025-11-08 11:05:17'),
(6, 1, '2025-11-09 14:45:39'),
(9, 1, '2025-11-10 09:23:36'),
(10, 1, '2025-11-11 08:21:36');

--
-- Triggers `student_classes`
--
DELIMITER $$
CREATE TRIGGER `trg_student_classes_delete` AFTER DELETE ON `student_classes` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (OLD.student_id, CONCAT('DELETE from student_classes: class_id=', OLD.class_id), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_student_classes_insert` AFTER INSERT ON `student_classes` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.student_id, CONCAT('INSERT into student_classes: class_id=', NEW.class_id), NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_student_classes_update` AFTER UPDATE ON `student_classes` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.student_id, CONCAT('UPDATE on student_classes: class_id=', NEW.class_id), NOW());
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `role` enum('ADMIN','LECTURE','STUDENT') NOT NULL,
  `password_hash` varchar(256) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `full_name`, `role`, `password_hash`, `status`, `created_at`, `updated_at`) VALUES
(1, 'meekthinker', 'Meekness bonheur', 'STUDENT', '-517733580', 'ACTIVE', '2025-11-08 07:36:23', '2025-11-08 08:38:00'),
(3, 'meeklecture', 'meekness lecture', 'LECTURE', '48690', 'ACTIVE', '2025-11-08 07:55:11', '2025-11-08 07:55:11'),
(4, 'classroomadmin', 'Default Admin', 'ADMIN', 'Java@admins', 'ACTIVE', '2025-11-08 07:58:58', '2025-11-08 07:58:58'),
(5, 'meekstudent', 'Meek student', 'STUDENT', '123', 'ACTIVE', '2025-11-08 08:40:52', '2025-11-08 08:40:52'),
(6, 'meekthinker1', 'meek thinker', 'STUDENT', '48690', 'ACTIVE', '2025-11-09 14:21:36', '2025-11-09 14:21:36'),
(7, 'lecture1', 'lecture me', 'LECTURE', '48690', 'ACTIVE', '2025-11-10 08:57:21', '2025-11-10 08:57:21'),
(8, 'me', 'me', 'STUDENT', '49', 'ACTIVE', '2025-11-10 09:21:55', '2025-11-10 09:21:55'),
(9, 'sylvie', 'masengesho', 'STUDENT', '48690', 'ACTIVE', '2025-11-10 09:22:23', '2025-11-10 09:22:23'),
(10, 'niyi', 'niyitanga', 'STUDENT', '48690', 'ACTIVE', '2025-11-11 08:17:36', '2025-11-11 08:17:36'),
(11, 'richieden', 'rich rich', 'STUDENT', '123', 'ACTIVE', '2025-11-17 22:57:35', '2025-11-17 22:57:35');

--
-- Triggers `users`
--
DELIMITER $$
CREATE TRIGGER `trg_users_delete` AFTER DELETE ON `users` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (OLD.user_id, 'DELETE from users', NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_users_insert` AFTER INSERT ON `users` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.user_id, 'INSERT into users', NOW());
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_users_update` AFTER UPDATE ON `users` FOR EACH ROW BEGIN
    INSERT INTO user_logs(user_id, action, action_time)
    VALUES (NEW.user_id, 'UPDATE on users', NOW());
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `user_logs`
--

CREATE TABLE `user_logs` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `action` varchar(255) NOT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `action_time` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_logs`
--

INSERT INTO `user_logs` (`log_id`, `user_id`, `action`, `ip_address`, `action_time`) VALUES
(1, 11, 'INSERT into users', NULL, '2025-11-17 22:57:35');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `announcements`
--
ALTER TABLE `announcements`
  ADD PRIMARY KEY (`announcement_id`),
  ADD KEY `sender_id` (`sender_id`);

--
-- Indexes for table `backup_logs`
--
ALTER TABLE `backup_logs`
  ADD PRIMARY KEY (`backup_id`),
  ADD KEY `idx_backup_logs_user` (`performed_by`);

--
-- Indexes for table `backup_schedule`
--
ALTER TABLE `backup_schedule`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `classes`
--
ALTER TABLE `classes`
  ADD PRIMARY KEY (`class_id`),
  ADD UNIQUE KEY `class_name` (`class_name`);

--
-- Indexes for table `resources`
--
ALTER TABLE `resources`
  ADD PRIMARY KEY (`resource_id`),
  ADD KEY `idx_resources_uploaded_by` (`uploaded_by`);

--
-- Indexes for table `resource_access`
--
ALTER TABLE `resource_access`
  ADD PRIMARY KEY (`access_id`),
  ADD KEY `idx_resource_access_student` (`student_id`),
  ADD KEY `idx_resource_access_resource` (`resource_id`);

--
-- Indexes for table `resource_assignments`
--
ALTER TABLE `resource_assignments`
  ADD PRIMARY KEY (`assignment_id`);

--
-- Indexes for table `resource_classes`
--
ALTER TABLE `resource_classes`
  ADD PRIMARY KEY (`resource_id`,`class_id`),
  ADD KEY `fk_class` (`class_id`);

--
-- Indexes for table `student_classes`
--
ALTER TABLE `student_classes`
  ADD PRIMARY KEY (`student_id`,`class_id`),
  ADD KEY `idx_student_classes_class` (`class_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD KEY `idx_users_role` (`role`);

--
-- Indexes for table `user_logs`
--
ALTER TABLE `user_logs`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `idx_user_logs_user` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `announcements`
--
ALTER TABLE `announcements`
  MODIFY `announcement_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `backup_logs`
--
ALTER TABLE `backup_logs`
  MODIFY `backup_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `backup_schedule`
--
ALTER TABLE `backup_schedule`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `classes`
--
ALTER TABLE `classes`
  MODIFY `class_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `resources`
--
ALTER TABLE `resources`
  MODIFY `resource_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `resource_access`
--
ALTER TABLE `resource_access`
  MODIFY `access_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `resource_assignments`
--
ALTER TABLE `resource_assignments`
  MODIFY `assignment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `user_logs`
--
ALTER TABLE `user_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `announcements`
--
ALTER TABLE `announcements`
  ADD CONSTRAINT `announcements_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `backup_logs`
--
ALTER TABLE `backup_logs`
  ADD CONSTRAINT `backup_logs_ibfk_1` FOREIGN KEY (`performed_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `resources`
--
ALTER TABLE `resources`
  ADD CONSTRAINT `resources_ibfk_1` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `resource_access`
--
ALTER TABLE `resource_access`
  ADD CONSTRAINT `resource_access_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `resource_access_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`resource_id`);

--
-- Constraints for table `resource_classes`
--
ALTER TABLE `resource_classes`
  ADD CONSTRAINT `fk_class` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `fk_resource` FOREIGN KEY (`resource_id`) REFERENCES `resources` (`resource_id`);

--
-- Constraints for table `student_classes`
--
ALTER TABLE `student_classes`
  ADD CONSTRAINT `student_classes_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `student_classes_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`);

--
-- Constraints for table `user_logs`
--
ALTER TABLE `user_logs`
  ADD CONSTRAINT `user_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
