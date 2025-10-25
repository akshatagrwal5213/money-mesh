# MoneyMesh Banking System - Startup Guide

## 🚀 Quick Start

### Start Everything (Recommended)
```bash
./start-all.sh
```

This single command will:
1. ✅ Check MySQL database connection
2. ✅ Stop any existing services on required ports
3. ✅ Start Spring Boot Backend (port 8080)
4. ✅ Start React Frontend (port 5175 or auto-selected)
5. ✅ Show live logs

### Stop Everything
```bash
./stop-all.sh
```

## 📋 Prerequisites

### Required:
- ✅ **MySQL 8.0+** - Must be running
- ✅ **Java 17+** - For Spring Boot backend
- ✅ **Node.js 16+** - For React frontend
- ✅ **Maven** - Included via mvnw wrapper

### Start MySQL (if not running):
```bash
# macOS (Homebrew)
brew services start mysql

# Linux
sudo systemctl start mysql

# Windows
net start MySQL80
```

## 🎯 Ports Configuration

| Service | Default Port | Alternative Ports |
|---------|--------------|-------------------|
| MySQL | 3306 | N/A |
| Backend | 8080 | N/A |
| Frontend | 5175 | 5174, 5173, 5176 (auto-selected) |

**Note:** If port 5175 is busy, Vite will automatically select the next available port.

## 📝 Command Options

### Start with Clean Build
```bash
./start-all.sh --clean
# OR
./start-all.sh -c
```

This will:
- Clean previous Maven build
- Recompile everything from scratch
- Useful after major code changes

### Normal Start
```bash
./start-all.sh
```

Uses existing compiled classes (faster startup).

## 📊 Monitoring

### View Logs
The scripts store logs in the `logs/` directory:

```bash
# View backend logs (live)
tail -f logs/backend.log

# View frontend logs (live)
tail -f logs/frontend.log

# View both simultaneously (done automatically by start-all.sh)
tail -f logs/backend.log logs/frontend.log
```

### Check Status
```bash
# Check what's running on each port
lsof -ti:8080  # Backend
lsof -ti:5175  # Frontend
lsof -ti:3306  # MySQL
```

## 🔧 Troubleshooting

### MySQL Not Running
```
❌ MySQL is not running on port 3306
```

**Solution:**
```bash
brew services start mysql
# OR
sudo systemctl start mysql
```

### Port Already in Use
```
⚠️  Backend is already running on port 8080. Stopping it...
```

**Solution:** The script automatically handles this. If it fails:
```bash
# Manual cleanup
./stop-all.sh

# Or kill specific port
lsof -ti:8080 | xargs kill -9
```

### Backend Won't Start
**Check logs:**
```bash
tail -50 logs/backend.log
```

**Common issues:**
1. MySQL not accessible
2. Database credentials wrong
3. Port 8080 in use by another app

**Solution:**
```bash
# Verify MySQL connection
mysql -u root -e "SELECT 1;"

# Clean build
./start-all.sh --clean
```

### Frontend Won't Start
**Check logs:**
```bash
tail -50 logs/frontend.log
```

**Common issues:**
1. Node modules not installed
2. Port conflicts

**Solution:**
```bash
# Reinstall dependencies
cd frontend-redesign
npm install

# Try different port manually
cd frontend-redesign
npm run dev -- --port 5176
```

## 🎓 Manual Startup (For Development)

### Option 1: Individual Terminals

**Terminal 1 - Backend:**
```bash
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd frontend-redesign
npm run dev
```

### Option 2: Background Processes

**Start Backend:**
```bash
nohup ./mvnw spring-boot:run > logs/backend.log 2>&1 &
echo $! > logs/backend.pid
```

**Start Frontend:**
```bash
cd frontend-redesign
nohup npm run dev > ../logs/frontend.log 2>&1 &
echo $! > ../logs/backend.pid
```

**Stop:**
```bash
kill $(cat logs/backend.pid)
kill $(cat logs/frontend.pid)
```

## 🌐 Access URLs

Once all services are running:

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:5175 | Main application UI |
| **Backend API** | http://localhost:8080 | REST API endpoints |
| **API Docs** | http://localhost:8080/swagger-ui.html | API documentation (if enabled) |

## 👤 Default Credentials

### Admin Login:
- Username: `admin`
- Password: (as configured in database)

### Test Customer:
- Username: `customer`
- Password: (as configured in database)

## 📁 Project Structure

```
bankingsystem/
├── start-all.sh          # Main startup script
├── stop-all.sh           # Stop all services
├── logs/                 # Log files directory
│   ├── backend.log       # Backend logs
│   ├── frontend.log      # Frontend logs
│   ├── backend.pid       # Backend process ID
│   └── frontend.pid      # Frontend process ID
├── frontend-redesign/    # React frontend (ACTIVE)
├── frontend/             # Old frontend (UNUSED)
├── src/                  # Backend source code
└── pom.xml              # Maven configuration
```

## 🔄 Workflow Examples

### Daily Development:
```bash
# Morning - Start everything
./start-all.sh

# Work on code...

# Evening - Stop everything
./stop-all.sh
```

### After Major Changes:
```bash
# Stop services
./stop-all.sh

# Clean start
./start-all.sh --clean
```

### Quick Restart:
```bash
./stop-all.sh && ./start-all.sh
```

## ⚠️ Important Notes

1. **MySQL Must Run First:**
   - MySQL is NOT started/stopped by these scripts
   - You must manage MySQL separately

2. **Log Files:**
   - Logs are appended, not overwritten
   - Clean old logs periodically: `rm logs/*.log`

3. **Process Cleanup:**
   - Scripts automatically clean up stale processes
   - If processes don't stop, use `kill -9` manually

4. **Frontend Port:**
   - Vite auto-selects port if 5175 is busy
   - Check script output for actual port used

5. **Background Execution:**
   - Services run in background after script exits
   - Press Ctrl+C to stop log monitoring (services continue)
   - Use `stop-all.sh` to actually stop services

## 📞 Support

### Check System Status:
```bash
# Quick status check
lsof -ti:8080 && echo "✅ Backend running" || echo "❌ Backend stopped"
lsof -ti:5175 && echo "✅ Frontend running" || echo "❌ Frontend stopped"
lsof -ti:3306 && echo "✅ MySQL running" || echo "❌ MySQL stopped"
```

### Common Commands:
```bash
# Restart backend only
lsof -ti:8080 | xargs kill -9
./mvnw spring-boot:run > logs/backend.log 2>&1 &

# Restart frontend only
lsof -ti:5175 | xargs kill -9
cd frontend-redesign && npm run dev > ../logs/frontend.log 2>&1 &

# View last 100 lines of backend log
tail -100 logs/backend.log

# Follow both logs (Ctrl+C to stop)
tail -f logs/backend.log logs/frontend.log
```

## ✅ Success Indicators

When everything is running correctly, you should see:

```
╔══════════════════════════════════════════════════════════════════╗
║                      System Status Summary                      ║
╚══════════════════════════════════════════════════════════════════╝

✅ MySQL Database: Running on port 3306
✅ Spring Boot Backend: Running on port 8080
   API: http://localhost:8080
✅ React Frontend: Running on port 5175
   URL: http://localhost:5175
```

**Happy Coding! 🚀**
