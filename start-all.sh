#!/bin/bash

# MoneyMesh Banking System - Complete Startup Script

set -e

# Set TERM if not set (for VS Code Code Runner compatibility)
export TERM=${TERM:-xterm}

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Configuration
MYSQL_PORT=3306
BACKEND_PORT=8080
FRONTEND_PORT=5175
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
FRONTEND_DIR="$PROJECT_DIR/frontend-redesign"
LOG_DIR="$PROJECT_DIR/logs"

mkdir -p "$LOG_DIR"

print_header() {
    # Check if TERM is set before clearing (VS Code Code Runner doesn't set TERM)
    if [ -n "$TERM" ] && [ "$TERM" != "dumb" ]; then
        clear
    fi
    echo -e "${GREEN}================================================${NC}"
    echo -e "${GREEN}  MoneyMesh Banking System - Startup Manager  ${NC}"
    echo -e "${GREEN}================================================${NC}"
    echo ""
}

print_info() {
    echo -e "${BLUE}[INFO] $1${NC}"
}

print_success() {
    echo -e "${GREEN}[SUCCESS] $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}[WARNING] $1${NC}"
}

print_error() {
    echo -e "${RED}[ERROR] $1${NC}"
}

print_step() {
    echo -e "${CYAN}>>> $1${NC}"
}

check_port() {
    lsof -ti:$1 > /dev/null 2>&1
    return $?
}

kill_port() {
    local port=$1
    local name=$2
    if check_port $port; then
        print_warning "$name is running on port $port. Stopping it..."
        lsof -ti:$port | xargs kill -9 2>/dev/null || true
        sleep 2
        if check_port $port; then
            print_error "Failed to stop $name on port $port"
            return 1
        fi
        print_success "$name stopped"
    fi
    return 0
}

check_mysql() {
    print_step "Checking MySQL database..."
    
    if ! check_port $MYSQL_PORT; then
        print_error "MySQL is not running on port $MYSQL_PORT"
        print_info "Please start MySQL: brew services start mysql"
        return 1
    fi
    
    if mysql -u root -e "SELECT 1;" > /dev/null 2>&1; then
        print_success "MySQL is running and accessible"
        return 0
    else
        print_error "MySQL is running but not accessible"
        return 1
    fi
}

start_backend() {
    print_step "Starting Spring Boot Backend..."
    
    cd "$PROJECT_DIR"
    
    if [ "$1" == "clean" ]; then
        print_info "Cleaning previous build..."
        ./mvnw clean > /dev/null 2>&1
    fi
    
    print_info "Starting backend on port $BACKEND_PORT..."
    nohup ./mvnw spring-boot:run > "$LOG_DIR/backend.log" 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > "$LOG_DIR/backend.pid"
    
    print_info "Backend starting (PID: $BACKEND_PID)..."
    print_info "Waiting for backend to be ready..."
    
    local count=0
    local max_wait=60
    while [ $count -lt $max_wait ]; do
        if check_port $BACKEND_PORT; then
            print_success "Backend is ready on port $BACKEND_PORT"
            print_info "Backend logs: $LOG_DIR/backend.log"
            return 0
        fi
        
        if ! ps -p $BACKEND_PID > /dev/null 2>&1; then
            print_error "Backend process died. Check logs: $LOG_DIR/backend.log"
            tail -50 "$LOG_DIR/backend.log"
            return 1
        fi
        
        sleep 2
        count=$((count + 2))
        echo -n "."
    done
    
    echo ""
    print_warning "Backend may still be starting. Check logs: $LOG_DIR/backend.log"
    return 0
}

start_frontend() {
    print_step "Starting Vite/React Frontend..."
    
    if [ ! -d "$FRONTEND_DIR" ]; then
        print_error "Frontend directory not found: $FRONTEND_DIR"
        return 1
    fi
    
    cd "$FRONTEND_DIR"
    
    if [ ! -d "node_modules" ]; then
        print_info "Installing frontend dependencies..."
        npm install
    fi
    
    print_info "Starting frontend on port $FRONTEND_PORT..."
    nohup npm run dev > "$LOG_DIR/frontend.log" 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > "$LOG_DIR/frontend.pid"
    
    print_info "Frontend starting (PID: $FRONTEND_PID)..."
    print_info "Waiting for frontend to be ready..."
    
    local count=0
    local max_wait=30
    local actual_port=""
    
    while [ $count -lt $max_wait ]; do
        for port in 5175 5174 5173 5176; do
            if check_port $port; then
                if curl -s http://localhost:$port > /dev/null 2>&1; then
                    actual_port=$port
                    break
                fi
            fi
        done
        
        if [ -n "$actual_port" ]; then
            print_success "Frontend is ready on port $actual_port"
            print_info "Frontend logs: $LOG_DIR/frontend.log"
            print_info "Access at: http://localhost:$actual_port"
            return 0
        fi
        
        if ! ps -p $FRONTEND_PID > /dev/null 2>&1; then
            print_error "Frontend process died. Check logs: $LOG_DIR/frontend.log"
            tail -50 "$LOG_DIR/frontend.log"
            return 1
        fi
        
        sleep 2
        count=$((count + 2))
        echo -n "."
    done
    
    echo ""
    print_warning "Frontend may still be starting. Check logs: $LOG_DIR/frontend.log"
    return 0
}

show_status() {
    echo ""
    echo -e "${GREEN}================================================${NC}"
    echo -e "${GREEN}         System Status Summary                 ${NC}"
    echo -e "${GREEN}================================================${NC}"
    echo ""
    
    if check_port $MYSQL_PORT; then
        echo -e "${GREEN}[OK] MySQL Database: Running on port $MYSQL_PORT${NC}"
    else
        echo -e "${RED}[FAIL] MySQL Database: Not running${NC}"
    fi
    
    if check_port $BACKEND_PORT; then
        echo -e "${GREEN}[OK] Spring Boot Backend: Running on port $BACKEND_PORT${NC}"
        echo -e "     API: http://localhost:$BACKEND_PORT"
    else
        echo -e "${RED}[FAIL] Spring Boot Backend: Not running${NC}"
    fi
    
    local frontend_port=""
    for port in 5175 5174 5173 5176; do
        if check_port $port; then
            frontend_port=$port
            break
        fi
    done
    
    if [ -n "$frontend_port" ]; then
        echo -e "${GREEN}[OK] React Frontend: Running on port $frontend_port${NC}"
        echo -e "     URL: http://localhost:$frontend_port"
    else
        echo -e "${RED}[FAIL] React Frontend: Not running${NC}"
    fi
    
    echo ""
    echo -e "${CYAN}Log Files:${NC}"
    echo -e "  Backend:  $LOG_DIR/backend.log"
    echo -e "  Frontend: $LOG_DIR/frontend.log"
    echo ""
    echo -e "${YELLOW}Useful Commands:${NC}"
    echo -e "  View backend logs:  tail -f $LOG_DIR/backend.log"
    echo -e "  View frontend logs: tail -f $LOG_DIR/frontend.log"
    echo -e "  Stop all services:  ./stop-all.sh"
    echo ""
}

main() {
    print_header
    
    CLEAN_BUILD=false
    if [ "$1" == "--clean" ] || [ "$1" == "-c" ]; then
        CLEAN_BUILD=true
        print_info "Clean build requested"
    fi
    
    if ! check_mysql; then
        print_error "MySQL check failed. Exiting."
        exit 1
    fi
    
    print_step "Checking for existing services..."
    kill_port $BACKEND_PORT "Backend" || true
    kill_port $FRONTEND_PORT "Frontend" || true
    kill_port 5174 "Frontend (5174)" || true
    kill_port 5173 "Frontend (5173)" || true
    
    if [ "$CLEAN_BUILD" = true ]; then
        start_backend "clean"
    else
        start_backend
    fi
    
    if [ $? -ne 0 ]; then
        print_error "Failed to start backend. Check logs: $LOG_DIR/backend.log"
        exit 1
    fi
    
    start_frontend
    
    if [ $? -ne 0 ]; then
        print_error "Failed to start frontend. Check logs: $LOG_DIR/frontend.log"
        exit 1
    fi
    
    sleep 2
    show_status
    
    print_success "All services started successfully!"
    print_info "Press Ctrl+C to stop monitoring (services will continue running)"
    echo ""
    
    echo -e "${CYAN}=== Live Logs (Ctrl+C to exit) ===${NC}"
    echo ""
    tail -f "$LOG_DIR/backend.log" "$LOG_DIR/frontend.log"
}

trap 'echo ""; print_info "Monitoring stopped. Services are still running."; exit 0' INT

main "$@"
