#!/bin/bash#!/bin/bash



# MoneyMesh Banking System - Stop All Services Script# MoneyMesh Banking System - Stop All Services Script



# Colorsset -e

RED='\033[0;31m'

GREEN='\033[0;32m'# Colors for output

YELLOW='\033[1;33m'RED='\033[0;31m'

BLUE='\033[0;34m'GREEN='\033[0;32m'

NC='\033[0m'YELLOW='\033[1;33m'

BLUE='\033[0;34m'

# ConfigurationNC='\033[0m' # No Color

BACKEND_PORT=8080

FRONTEND_PORTS="5175 5174 5173 5176"print_info() {

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"    echo -e "${BLUE}ℹ️  $1${NC}"

LOG_DIR="$PROJECT_DIR/logs"}



print_info() { echo -e "${BLUE}ℹ️  $1${NC}"; }print_success() {

print_success() { echo -e "${GREEN}✅ $1${NC}"; }    echo -e "${GREEN}✅ $1${NC}"

print_warning() { echo -e "${YELLOW}⚠️  $1${NC}"; }}

print_error() { echo -e "${RED}❌ $1${NC}"; }

print_warning() {

echo -e "${BLUE}"    echo -e "${YELLOW}⚠️  $1${NC}"

echo "╔══════════════════════════════════════════════════════════╗"}

echo "║    MoneyMesh Banking System - Stop All Services        ║"

echo "╚══════════════════════════════════════════════════════════╝"print_header() {

echo -e "${NC}"    echo -e "${RED}"

    echo "╔═══════════════════════════════════════════════════════════════╗"

# Function to check if port is in use    echo "║      MoneyMesh Banking System - Shutdown Manager            ║"

check_port() {    echo "╚═══════════════════════════════════════════════════════════════╝"

    lsof -ti:$1 > /dev/null 2>&1    echo -e "${NC}"

    return $?}

}

# Stop process on a specific port

# Function to kill process on portstop_port() {

kill_port() {    local port=$1

    local port=$1    local service=$2

    local name=$2    

        if lsof -i :$port > /dev/null 2>&1; then

    if check_port $port; then        print_info "Stopping $service on port $port..."

        print_info "Stopping $name on port $port..."        kill $(lsof -ti:$port) 2>/dev/null || true

        lsof -ti:$port | xargs kill -9 2>/dev/null || true        sleep 2

        sleep 1        print_success "$service stopped"

            else

        if ! check_port $port; then        print_info "$service is not running on port $port"

            print_success "$name stopped successfully"    fi

            return 0}

        else

            print_error "Failed to stop $name on port $port"print_header

            return 1

        fi# Stop frontend (port 5173)

    elsestop_port 5173 "Frontend (React)"

        print_warning "$name is not running on port $port"

        return 0# Stop backend (port 8080)

    fistop_port 8080 "Backend (Spring Boot)"

}

# Ask about MySQL

# Stop Backendecho ""

print_info "Stopping Backend..."read -p "Do you want to stop MySQL database as well? (y/n) " -n 1 -r

kill_port $BACKEND_PORT "Backend (Spring Boot)"echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then

# Kill by PID if stored    print_info "Stopping MySQL..."

if [ -f "$LOG_DIR/backend.pid" ]; then    if command -v brew &> /dev/null; then

    PID=$(cat "$LOG_DIR/backend.pid")        brew services stop mysql

    if ps -p $PID > /dev/null 2>&1; then        print_success "MySQL stopped via Homebrew"

        kill -9 $PID 2>/dev/null || true    elif command -v mysql.server &> /dev/null; then

        print_info "Killed backend process (PID: $PID)"        mysql.server stop

    fi        print_success "MySQL stopped"

    rm "$LOG_DIR/backend.pid"    else

fi        print_warning "Please stop MySQL manually"

    fi

# Stop Frontend (check multiple ports)fi

print_info "Stopping Frontend..."

for port in $FRONTEND_PORTS; doecho ""

    if check_port $port; thenprint_success "All services have been stopped!"

        kill_port $port "Frontend (Vite)"echo ""

    fi
done

# Kill by PID if stored
if [ -f "$LOG_DIR/frontend.pid" ]; then
    PID=$(cat "$LOG_DIR/frontend.pid")
    if ps -p $PID > /dev/null 2>&1; then
        kill -9 $PID 2>/dev/null || true
        print_info "Killed frontend process (PID: $PID)"
    fi
    rm "$LOG_DIR/frontend.pid"
fi

# Kill any remaining Maven/Node processes
print_info "Cleaning up any remaining processes..."
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "vite" 2>/dev/null || true

# Show final status
echo ""
echo -e "${GREEN}╔══════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                  Final Status Check                     ║${NC}"
echo -e "${GREEN}╚══════════════════════════════════════════════════════════╝${NC}"
echo ""

# Check Backend
if check_port $BACKEND_PORT; then
    echo -e "${RED}❌ Backend:${NC} Still running on port $BACKEND_PORT"
else
    echo -e "${GREEN}✅ Backend:${NC} Stopped"
fi

# Check Frontend
FRONTEND_RUNNING=false
for port in $FRONTEND_PORTS; do
    if check_port $port; then
        echo -e "${RED}❌ Frontend:${NC} Still running on port $port"
        FRONTEND_RUNNING=true
        break
    fi
done

if [ "$FRONTEND_RUNNING" = false ]; then
    echo -e "${GREEN}✅ Frontend:${NC} Stopped"
fi

echo ""
print_success "All services stopped!"
print_info "Note: MySQL database was not stopped (must be stopped manually if needed)"
echo ""
