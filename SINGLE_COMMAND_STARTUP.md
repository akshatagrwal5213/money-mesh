# 🎯 Single Command Startup - Complete Guide

## ✨ The Easiest Way to Start

You now have **THREE ways** to start your entire MoneyMesh Banking System with a single command:

### Option 1: NPM Command (Recommended)
```bash
npm start
```

### Option 2: Direct Script
```bash
./start-all.sh
```

### Option 3: Interactive Menu
```bash
./menu.sh
```

---

## 🎪 What Each Option Does

### Option 1: `npm start` (Easiest)
- Just type `npm start` from the root directory
- Automatically runs `start-all.sh`
- Best for quick launches
- Works like any Node.js project

### Option 2: `./start-all.sh` (Most Automated)
- Complete automation of all services
- Checks MySQL and starts if needed
- Creates database if it doesn't exist
- Builds and starts backend (Spring Boot)
- Installs dependencies and starts frontend (React)
- Shows beautiful status dashboard
- Real-time progress updates
- Keeps running until you press Ctrl+C

### Option 3: `./menu.sh` (Most Flexible)
- Interactive menu with 8 options
- Start all services
- Stop all services
- Start individual services
- View logs in real-time
- Check status of all services
- Perfect for development and debugging

---

## 🚀 Quick Start in 3 Steps

```bash
# Step 1: Navigate to project
cd /Users/akshatsanjayagrwal/Desktop/bankingsystem

# Step 2: Start everything
npm start

# Step 3: Open browser
# http://localhost:5173
```

**That's it! Your entire banking system is now running! 🎉**

---

## 📋 What Happens Behind the Scenes

When you run `npm start`, the script automatically:

### 1. Database Check ✅
```
✓ Checking MySQL status
✓ Starting MySQL (if not running)
✓ Creating banking_system database
✓ Verifying connection
```

### 2. Backend Startup ✅
```
✓ Running Maven clean install
✓ Building Spring Boot application
✓ Starting server on port 8080
✓ Connecting to MySQL
✓ Creating/updating database tables
✓ Loading initial data
```

### 3. Frontend Startup ✅
```
✓ Installing dependencies (if needed)
✓ Starting Vite dev server
✓ Running on port 5173
✓ Hot-reload enabled
```

---

## 🎮 All Available Commands

### Start/Stop Commands
```bash
npm start              # Start all services
npm run stop           # Stop all services
npm run dev            # Same as npm start
./menu.sh              # Interactive menu
```

### Individual Services
```bash
npm run backend        # Backend only (port 8080)
npm run frontend       # Frontend only (port 5173)
```

### Build Commands
```bash
npm run install-all       # Install frontend dependencies
npm run build-frontend    # Build production frontend
npm run build-backend     # Build backend JAR file
npm run test-backend      # Run backend tests
npm run clean            # Clean all build artifacts
```

---

## 🌐 Access Points After Startup

| Service | URL | Purpose |
|---------|-----|---------|
| **Frontend App** | http://localhost:5173 | Main user interface |
| **Backend API** | http://localhost:8080/api | REST API |
| **MySQL DB** | localhost:3306 | Database |
| **API Docs** | http://localhost:8080/api-docs | Swagger UI (if enabled) |

---

## 🔐 Login Credentials

After startup, login with:

**Admin Account:**
```
Username: admin
Password: password
```

**Or create a new account:**
- Click "Create New Account" button on login page
- Fill the 6-step registration form
- Start banking immediately!

---

## 📊 Monitoring

### View Real-time Logs

**Backend logs:**
```bash
tail -f backend.log
```

**Frontend logs:**
```bash
tail -f frontend.log
```

### Check Status
```bash
./menu.sh  # Choose option 7
```

Or manually:
```bash
# Check MySQL
mysql -u root -e "SELECT 1"

# Check Backend
curl http://localhost:8080/actuator/health

# Check Frontend
curl http://localhost:5173
```

---

## 🛑 Stopping Services

### Stop All
```bash
npm run stop
```
or
```bash
./stop-all.sh
```

### Stop Individual Services

**Stop Backend:**
```bash
kill $(lsof -ti:8080)
```

**Stop Frontend:**
```bash
kill $(lsof -ti:5173)
```

**Stop MySQL:**
```bash
brew services stop mysql
# or
mysql.server stop
```

---

## ❗ Troubleshooting

### Issue: Port Already in Use

**Error:** "Port 8080 is already in use"

**Solution:**
```bash
# Kill the process
kill $(lsof -ti:8080)

# Then restart
npm start
```

### Issue: MySQL Not Found

**Error:** "MySQL is not running or not accessible"

**Solution:**
```bash
# Install MySQL via Homebrew
brew install mysql

# Start MySQL
brew services start mysql

# Verify
mysql -u root -e "SELECT 1"
```

### Issue: Frontend Dependencies Missing

**Error:** "Cannot find module..."

**Solution:**
```bash
npm run install-all
npm start
```

### Issue: Backend Build Fails

**Error:** "mvn build failed"

**Solution:**
```bash
# Clean and rebuild
./mvnw clean install -DskipTests

# Then start
npm start
```

---

## 🎨 Feature Highlights

### What You Can Do After Startup

✅ **User Registration** - Create new customer accounts  
✅ **Account Management** - Multiple accounts per customer  
✅ **Transactions** - Deposits, withdrawals, transfers  
✅ **Bill Payments** - Electricity, water, mobile, etc.  
✅ **UPI Payments** - Quick mobile payments  
✅ **Investments** - FD, RD, Mutual Funds, SIP  
✅ **Loans** - Personal, home, car loans  
✅ **Credit Score** - Check eligibility  
✅ **Rewards** - Earn and redeem points  
✅ **Analytics** - Financial insights  
✅ **Budgeting** - Track expenses  
✅ **Tax Planning** - Calculate tax savings  

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     User's Browser                          │
│              http://localhost:5173                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ HTTP/REST
                     │
┌────────────────────▼────────────────────────────────────────┐
│              React Frontend (Vite)                          │
│  • TypeScript • Material-UI • React Router                  │
│  • Axios • State Management                                 │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ API Calls
                     │
┌────────────────────▼────────────────────────────────────────┐
│         Spring Boot Backend (Port 8080)                     │
│  • REST Controllers • JWT Security                          │
│  • JPA/Hibernate • Business Logic                           │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ JDBC
                     │
┌────────────────────▼────────────────────────────────────────┐
│            MySQL Database (Port 3306)                       │
│  • banking_system database                                  │
│  • 50+ tables for complete banking                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Project Files Created

### Startup Scripts
- ✅ `start-all.sh` - Main startup script (8KB)
- ✅ `stop-all.sh` - Shutdown script (2KB)
- ✅ `menu.sh` - Interactive menu (3KB)
- ✅ `package.json` - NPM scripts (root level)

### Documentation
- ✅ `QUICK_START.md` - This guide (15KB)
- ✅ `SINGLE_COMMAND_STARTUP.md` - Complete reference

### Logs
- 📝 `backend.log` - Backend console output
- 📝 `frontend.log` - Frontend console output

---

## 🎓 Usage Examples

### Example 1: Development Workflow
```bash
# Morning: Start everything
npm start

# Work on code...
# (Backend auto-reloads on compile)
# (Frontend hot-reloads automatically)

# Evening: Stop everything
npm run stop
```

### Example 2: Debug Backend
```bash
# Start all
npm start

# In another terminal, watch logs
tail -f backend.log

# Check specific endpoint
curl http://localhost:8080/api/customers
```

### Example 3: Test Frontend Only
```bash
# Start backend separately (if already running)
npm run backend

# In another terminal, start frontend
npm run frontend
```

---

## 🔧 Configuration

### Database Configuration
File: `src/main/resources/application.properties`

```properties
# MySQL Connection
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=root
spring.datasource.password=

# Change password if your MySQL has one:
# spring.datasource.password=your_password
```

### Frontend Configuration
File: `frontend-redesign/src/services/api.ts`

```typescript
// Base URL for API calls
const BASE_URL = 'http://localhost:8080/api';

// Change if backend runs on different port
```

---

## 🎯 Success Indicators

After running `npm start`, you should see:

```
✅ MySQL is running and accessible
✅ Database 'banking_system' already exists
✅ Backend started successfully on http://localhost:8080
✅ Frontend started successfully on http://localhost:5173

┌─────────────────────────────────────────────────────────────┐
│  Service          │  Status   │  URL                       │
├─────────────────────────────────────────────────────────────┤
│  MySQL Database   │  Running  │  localhost:3306            │
│  Spring Boot API  │  Running  │  http://localhost:8080     │
│  React Frontend   │  Running  │  http://localhost:5173     │
└─────────────────────────────────────────────────────────────┘

🌐 Open your browser to: http://localhost:5173
✨ Happy Banking!
```

---

## 💡 Tips & Best Practices

1. **Always stop services properly**
   - Use `npm run stop` instead of killing terminal
   - Prevents orphaned processes

2. **Check logs for errors**
   - Backend issues: `tail -f backend.log`
   - Frontend issues: `tail -f frontend.log`

3. **Use the menu for development**
   - `./menu.sh` gives you quick access to all operations
   - No need to remember commands

4. **Keep MySQL running**
   - Optional: Set MySQL to auto-start on boot
   - `brew services start mysql` (stays running)

5. **Clean rebuild occasionally**
   ```bash
   npm run clean
   npm run install-all
   npm start
   ```

---

## 🎉 You're All Set!

Your MoneyMesh Banking System is ready to use with:

✅ **Single command startup** - `npm start`  
✅ **Automatic database setup** - Creates DB if needed  
✅ **Complete logging** - Track all operations  
✅ **Easy shutdown** - `npm run stop`  
✅ **Development-ready** - Hot reload enabled  
✅ **Production-ready** - Build scripts included  

### Quick Reference Card

```bash
# Start everything
npm start

# Stop everything
npm run stop

# View backend logs
tail -f backend.log

# View frontend logs
tail -f frontend.log

# Check status
./menu.sh  # Option 7

# Access app
http://localhost:5173

# Login
admin / password
```

**Enjoy your complete banking system! 🚀**
