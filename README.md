# 📚 Classroom Resource Auto-Distribution System (LAN-Based)

<p align="center">
  <img src="https://img.shields.io/badge/Java-Swing%20%7C%20AWT-red?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Database-MySQL-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Architecture-Client%20Server-green?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Network-LAN-orange?style=for-the-badge" />
</p>

A desktop application engineered to automate the distribution of academic resources (PDFs, videos, links, announcements) across classrooms using **Local Area Network (LAN)**. Designed with an **offline-first** architecture, it enables seamless communication between **Lecturers**, **Students**, and **Admins**—even without internet.

Google Drive cloud backup is supported when internet connectivity becomes available.

---

## ✨ Overview

The system eliminates the inefficiencies of manual resource sharing by providing:

* Centralized resource management
* Instant distribution across LAN
* Role-based dashboards
* Secure storage and tracking
* Offline learning continuity

It is ideal for universities, colleges, and secondary schools that need efficient, reliable classroom content delivery without depending on the internet.

---

## 🧩 Key Features

### 🧑‍💼 Admin

* Manage users (add/edit/delete)
* Manage classes
* View system logs
* Perform local & cloud backups

### 🧑‍🏫 Lecturer

* Upload PDFs, videos, or links
* Assign resources to specific students or classes
* Share announcements
* Track resource distribution

### 🎓 Student

* View/download assigned learning materials
* Read announcements
* Access previously downloaded content offline

### ⚙️ System Capabilities

* Offline-first LAN functionality
* Centralized MySQL storage
* Auto-refreshed student dashboards
* Google Drive backup integration
* Secure Role-Based Access Control (RBAC)

---

## 🏗️ System Architecture

* **Architecture:** Client–Server
* **Frontend:** Java Swing & AWT
* **Backend:** Java (Core Java)
* **Database:** MySQL (XAMPP server)
* **Network:** LAN for offline communication
* **Cloud Backup:** Google Drive API

---

## 🗄️ Database Model (Summary)

| Table             | Purpose                         |
| ----------------- | ------------------------------- |
| **Users**         | Admins, Lecturers, Students     |
| **Classes**       | Class records & lecturer links  |
| **Resources**     | PDFs, videos, links, file paths |
| **Announcements** | Messages to users or classes    |
| **Logs**          | System activity tracking        |
| **Backups**       | Local & cloud backup metadata   |

Relationships support:

* Many-to-many mapping of users ↔ classes
* Resources & announcements can be assigned to classes or individuals

---

## 🔧 Technologies Used

| Layer       | Technology           |
| ----------- | -------------------- |
| UI          | Java Swing & AWT     |
| Logic       | Java Core            |
| Database    | MySQL (XAMPP)        |
| Cloud       | Google Drive API     |
| Development | VS Code, Git, GitHub |
| Network     | LAN (Offline-first)  |

---

## 📸 Screenshots

> *(Add your system screenshots in `screenshots/` and I will embed them here.)*

Example placeholders:

* Login screen
* Admin dashboard
* Lecturer resource upload
* Student dashboard
* Backup manager

---

## 🚀 Installation & Setup

### **1. Requirements**

* Windows 10/11
* JRE 8+ installed
* XAMPP (MySQL running)
* LAN connection

### **2. Database Setup**

1. Open XAMPP → Start Apache & MySQL
2. Create a database (e.g., `classroom_system`)
3. Import the provided `.sql` file
4. Update DB credentials in your configuration file

### **3. Running the Application**

```bash
java -jar ClassroomResourceSystem.jar
```

### **4. LAN Deployment**

* Place the server instance on the admin/teacher machine
* Ensure all client devices are connected to the same LAN
* Each client runs its local JAR but connects to the same database

---

## 🧪 Testing Summary

### ✔ Unit Tests

* User management
* Resource upload & assignment
* Announcement module
* Backup module

### ✔ Integration Tests

* User actions syncing with DB
* Lecturer uploads reflecting in student dashboards
* Backup read/write operations

### ✔ System Tests

* Performance across multiple LAN clients
* Offline access behavior
* Concurrent resource downloads

---

## 📊 Test Results (Examples)

| Module          | Input             | Expected Output              |
| --------------- | ----------------- | ---------------------------- |
| Login           | Valid credentials | Redirect to dashboard        |
| Resource Upload | PDF + Class       | Visible on student dashboard |
| Announcement    | Message + Class   | Students receive alert       |
| Backup          | Trigger backup    | Local + Cloud sync           |

---

## 🛡️ Troubleshooting

| Issue                | Solution                               |
| -------------------- | -------------------------------------- |
| Cannot log in        | Check username/password & LAN          |
| DB error             | Ensure XAMPP + MySQL running           |
| Resource not showing | Lecturer must assign it                |
| Backup fails         | Check internet & Google Drive API auth |

---

## 🧭 Roadmap (Future Improvements)

* Mobile App (Android/iOS)
* Real-time analytics dashboard
* Email/SMS notifications
* Multi-institution support
* AI-powered resource recommendations
* Advanced cloud sync
* Multi-language interface

---

## 👥 Contributors

Team Members:

* **Meekness Bonheur**
* **Niyitanga Gilbert**
* **Masengesho Sylvie**
* **Narame Marie Jose**
* **Mugisha Augustin**

---

## 📜 License

This project was developed for academic and educational purposes.
A formal license can be added if required.

