#!/bin/bash

echo "🔨 Building MoneyMesh for Deployment"
echo "====================================="
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Step 1: Build Backend
echo -e "${BLUE}📦 Step 1: Building Backend...${NC}"
./mvnw clean package -DskipTests

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Backend JAR created: target/moneymesh-0.0.1-SNAPSHOT.jar${NC}"
    JAR_SIZE=$(ls -lh target/*.jar | awk '{print $5}')
    echo "  Size: $JAR_SIZE"
else
    echo -e "${YELLOW}✗ Backend build failed${NC}"
    exit 1
fi

echo ""

# Step 2: Build Frontend
echo -e "${BLUE}🎨 Step 2: Building Frontend...${NC}"
cd frontend

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
fi

# Build
npm run build

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Frontend built: frontend/dist/${NC}"
    DIST_SIZE=$(du -sh dist | awk '{print $1}')
    echo "  Size: $DIST_SIZE"
else
    echo -e "${YELLOW}✗ Frontend build failed${NC}"
    exit 1
fi

cd ..

echo ""

# Step 3: Create deployment package
echo -e "${BLUE}📦 Step 3: Creating Deployment Package...${NC}"

DEPLOY_DIR="deploy-package"
rm -rf $DEPLOY_DIR
mkdir -p $DEPLOY_DIR/backend
mkdir -p $DEPLOY_DIR/frontend

# Copy backend
cp target/*.jar $DEPLOY_DIR/backend/
cp Procfile $DEPLOY_DIR/backend/
cp system.properties $DEPLOY_DIR/backend/
cp -r src/main/resources/*.properties $DEPLOY_DIR/backend/ 2>/dev/null || true

# Copy frontend
cp -r frontend/dist/* $DEPLOY_DIR/frontend/

# Copy database schema
cp mvp_schema.sql $DEPLOY_DIR/

# Create README
cat > $DEPLOY_DIR/README.md << 'EOF'
# MoneyMesh Deployment Package

## Backend (Spring Boot)
Location: `backend/`

### Run locally:
```bash
cd backend
java -jar *.jar
```

### Deploy to server:
```bash
# Upload JAR file
scp *.jar user@server:/path/to/app/

# SSH to server and run
ssh user@server
cd /path/to/app
java -jar *.jar
```

### Environment Variables:
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT tokens
- `SERVER_PORT` - Port number (default: 8080)

## Frontend (React)
Location: `frontend/`

### Deploy options:
1. **Vercel/Netlify**: Upload `frontend/` folder
2. **Static hosting**: Upload to any web server
3. **Nginx**: Copy to `/var/www/html/`

## Database
Location: `mvp_schema.sql`

### Setup:
```bash
mysql -u root -p < mvp_schema.sql
```

## Quick Start

1. Setup database (see above)
2. Start backend: `cd backend && java -jar *.jar`
3. Serve frontend from any web server
4. Access at http://your-domain.com

EOF

echo -e "${GREEN}✓ Deployment package created: $DEPLOY_DIR/${NC}"

echo ""

# Create deployment archive
echo -e "${BLUE}📦 Step 4: Creating ZIP Archive...${NC}"
tar -czf moneymesh-deploy-$(date +%Y%m%d-%H%M%S).tar.gz $DEPLOY_DIR

echo -e "${GREEN}✓ Archive created${NC}"
ls -lh moneymesh-deploy-*.tar.gz | tail -1

echo ""
echo "✅ Build Complete!"
echo ""
echo "📋 What you have now:"
echo "  1. Backend JAR: target/moneymesh-0.0.1-SNAPSHOT.jar"
echo "  2. Frontend build: frontend/dist/"
echo "  3. Deployment package: deploy-package/"
echo "  4. Archive: moneymesh-deploy-*.tar.gz"
echo ""
echo "🚀 Deployment Options:"
echo "  A. Upload archive to your server and extract"
echo "  B. Deploy backend JAR to Heroku/Railway/Render"
echo "  C. Deploy frontend to Vercel/Netlify/GitHub Pages"
echo "  D. Use Docker (if available): docker-compose up"
echo ""
echo "💡 Recommended: Deploy frontend to Vercel (free)"
echo "   cd frontend && npx vercel --prod"
echo ""
