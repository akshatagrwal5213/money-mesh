# 🚀 MoneyMesh Banking System - Quick Start Guide

## Single Command Startup

Start the entire application (database + backend + frontend) with just **ONE command**:

```bash
npm start
```

or

```bash
./start-all.sh
```

---

## 📋 What Happens When You Run `npm start`

The startup script automatically:

1. ✅ **Checks MySQL Database**
   - Verifies MySQL is running
   - Starts MySQL if needed (via Homebrew on macOS)
   - Creates `banking_system` database if it doesn't exist

2. ✅ **Starts Spring Boot Backend**
   - Runs `mvn clean install`
   - Starts the backend on `http://localhost:8080`
   - Connects to MySQL database automatically
   - Logs output to `backend.log`

3. ✅ **Starts React Frontend**
   - Installs dependencies if needed
   - Starts Vite dev server on `http://localhost:5173`
   - Logs output to `frontend.log`

---

## 🎯 Prerequisites

### Required Software

1. **Java 17+** (JDK 24 preferred)
   ```bash
   java -version
   ```

2. **Node.js 16+** (LTS recommended)
   ```bash
   node -v
   npm -v
   ```

3. **MySQL 8.0+**
   ```bash
   mysql --version
   ```

4. **Maven** (via wrapper, included in project)

### MySQL Setup

**Option 1: Install via Homebrew (macOS)**
```bash
brew install mysql
brew services start mysql
```

**Option 2: Download from MySQL Website**
- Download: https://dev.mysql.com/downloads/mysql/
- Install and start the MySQL server

**Initial MySQL Configuration:**
```bash
# Login to MySQL (default: no password)
mysql -u root

# Create database (script does this automatically, but you can do it manually)
CREATE DATABASE IF NOT EXISTS banking_system;
exit;
```

---

## 🎮 Usage

### Start Everything
```bash
npm start
```

### Stop Everything
```bash
npm run stop
```

### Individual Services

**Backend Only:**
```bash
npm run backend
```

**Frontend Only:**
```bash
npm run frontend
```

**Install Frontend Dependencies:**
```bash
npm run install-all
```

**Build Frontend for Production:**
```bash
npm run build-frontend
```

**Build Backend JAR:**
```bash
npm run build-backend
```

---

## 🌐 Access URLs

After running `npm start`, access:

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:5173 | React application |
| **Backend API** | http://localhost:8080/api | REST API endpoints |
| **Database** | localhost:3306 | MySQL database |

---

## 🔐 Default Login Credentials

**Admin Account:**
- Username: `admin`
- Password: `password`

**Test Customer Account:**
- Username: `customer1`
- Password: `password123`

---

## 📊 Database Configuration

The application automatically connects to MySQL using these settings (from `application.properties`):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

### Change MySQL Password

If your MySQL has a password, update `src/main/resources/application.properties`:

```properties
spring.datasource.password=your_mysql_password
```

---

## 📝 Logs

View real-time logs:

**Backend Logs:**
```bash
tail -f backend.log
```

**Frontend Logs:**
```bash
tail -f frontend.log
```

---

## ❗ Troubleshooting

### MySQL Not Starting

**Check if MySQL is running:**
```bash
brew services list | grep mysql
```

**Start MySQL manually:**
```bash
brew services start mysql
# or
mysql.server start
```

### Port Already in Use

**Check what's using port 8080 (backend):**
```bash
lsof -i :8080
```

**Check what's using port 5173 (frontend):**
```bash
lsof -i :5173
```

**Kill process on port:**
```bash
kill $(lsof -ti:8080)  # Backend
kill $(lsof -ti:5173)  # Frontend
```

### Backend Fails to Start

1. Check Java version:
   ```bash
   java -version  # Should be 17+
   ```

2. Check Maven:
   ```bash
   ./mvnw -v
   ```

3. Check backend logs:
   ```bash
   cat backend.log
   ```

### Frontend Fails to Start

1. Clear node_modules and reinstall:
   ```bash
   cd frontend-redesign
   rm -rf node_modules package-lock.json
   npm install
   cd ..
   npm start
   ```

2. Check Node version:
   ```bash
   node -v  # Should be 16+
   ```

### Database Connection Error

1. Verify MySQL is running:
   ```bash
   mysql -u root -e "SELECT 1"
   ```

2. Check if database exists:
   ```bash
   mysql -u root -e "SHOW DATABASES LIKE 'banking_system'"
   ```

3. Manually create database:
   ```bash
   mysql -u root -e "CREATE DATABASE banking_system"
   ```

---

## 🏗️ Project Structure

```
bankingsystem/
├── src/                          # Spring Boot backend source
│   ├── main/java/com/bank/      # Java source files
│   └── main/resources/          # Application properties, SQL scripts
├── frontend-redesign/           # React frontend
│   ├── src/                     # React components, pages, services
│   └── package.json            # Frontend dependencies
├── start-all.sh                # 🚀 Start all services script
├── stop-all.sh                 # 🛑 Stop all services script
├── package.json                # Root package.json (npm scripts)
├── pom.xml                     # Maven configuration
├── backend.log                 # Backend logs
└── frontend.log                # Frontend logs
```

---

## 🎨 Features

### Module 1-12 Complete Implementation
- ✅ Customer Management
- ✅ Account Management
- ✅ Transactions & Transfers
- ✅ Bill Payments & UPI
- ✅ Investments (FD, RD, Mutual Funds, SIP)
- ✅ Analytics & Budgeting
- ✅ Insurance & Claims
- ✅ Tax Planning
- ✅ Wealth Management
- ✅ Loan Management
- ✅ Credit & Loan Eligibility
- ✅ Rewards & Redemption

### Latest Features
- ✅ **User Registration System** - Self-service account creation
- ✅ **Multi-step Registration Form** - 6-step guided process
- ✅ **KYC Validation** - PAN & Aadhar verification
- ✅ **Account Types** - Savings & Current accounts
- ✅ **Initial Deposit** - Configurable minimum deposit

---

## 🔒 Security

- JWT-based authentication
- BCrypt password encryption
- Role-based access control (ADMIN, CUSTOMER)
- CORS configuration for frontend-backend communication
- Secure API endpoints

---

## 📦 Building for Production

### Backend JAR
```bash
./mvnw clean package -DskipTests
# JAR file: target/moneymesh-0.0.1-SNAPSHOT.jar
```

### Frontend Build
```bash
cd frontend-redesign
npm run build
# Output: frontend-redesign/dist/
```

### Run Production Build
```bash
# Backend
java -jar target/moneymesh-0.0.1-SNAPSHOT.jar

# Frontend (serve static files)
cd frontend-redesign
npm run preview
```

---

## 📞 Support

For issues or questions:
1. Check the troubleshooting section above
2. Review log files (`backend.log`, `frontend.log`)
3. Check `application.properties` for configuration
4. Verify all prerequisites are installed

---

## 🎉 Quick Start Summary

```bash
# 1. Clone/Download the project
cd /Users/akshatsanjayagrwal/Desktop/bankingsystem

# 2. Make scripts executable
chmod +x start-all.sh stop-all.sh

# 3. Start everything!
npm start

# 4. Open browser to http://localhost:5173

# 5. Login with admin/password

# 6. Stop everything when done
npm run stop
```

**That's it! Your complete banking system is now running! 🚀**
