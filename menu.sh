#!/bin/bash

# Quick Start - Visual Guide
clear

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                                                               ║"
echo "║     🏦  MoneyMesh Banking System - Quick Start Guide  🏦     ║"
echo "║                                                               ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo "Choose an option:"
echo ""
echo "  1️⃣  START ALL SERVICES (Database + Backend + Frontend)"
echo "  2️⃣  STOP ALL SERVICES"
echo "  3️⃣  START BACKEND ONLY"
echo "  4️⃣  START FRONTEND ONLY"
echo "  5️⃣  VIEW BACKEND LOGS"
echo "  6️⃣  VIEW FRONTEND LOGS"
echo "  7️⃣  CHECK STATUS"
echo "  8️⃣  EXIT"
echo ""
read -p "Enter your choice (1-8): " choice

case $choice in
    1)
        echo ""
        echo "🚀 Starting all services..."
        ./start-all.sh
        ;;
    2)
        echo ""
        echo "🛑 Stopping all services..."
        ./stop-all.sh
        ;;
    3)
        echo ""
        echo "⚙️  Starting backend only..."
        ./mvnw spring-boot:run
        ;;
    4)
        echo ""
        echo "💻 Starting frontend only..."
        cd frontend-redesign && npm run dev
        ;;
    5)
        echo ""
        echo "📋 Backend logs (Ctrl+C to exit):"
        tail -f backend.log
        ;;
    6)
        echo ""
        echo "📋 Frontend logs (Ctrl+C to exit):"
        tail -f frontend.log
        ;;
    7)
        echo ""
        echo "📊 Checking service status..."
        echo ""
        echo "MySQL (port 3306):"
        if lsof -i :3306 > /dev/null 2>&1; then
            echo "  ✅ Running"
        else
            echo "  ❌ Not running"
        fi
        echo ""
        echo "Backend (port 8080):"
        if lsof -i :8080 > /dev/null 2>&1; then
            echo "  ✅ Running - http://localhost:8080"
        else
            echo "  ❌ Not running"
        fi
        echo ""
        echo "Frontend (port 5173):"
        if lsof -i :5173 > /dev/null 2>&1; then
            echo "  ✅ Running - http://localhost:5173"
        else
            echo "  ❌ Not running"
        fi
        echo ""
        ;;
    8)
        echo ""
        echo "👋 Goodbye!"
        exit 0
        ;;
    *)
        echo ""
        echo "❌ Invalid choice. Please run the script again."
        exit 1
        ;;
esac
