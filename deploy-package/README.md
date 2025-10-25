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

