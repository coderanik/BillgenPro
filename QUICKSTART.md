# ⚡ Quick Start Deployment Guide

Get your Billgen Pro application up and running in minutes!

## 🐳 Docker Compose (Easiest Method)

### Prerequisites
- Docker and Docker Compose installed
- Git

### Steps

1. **Clone and navigate:**
   ```bash
   git clone <your-repo-url>
   cd billgen-pro-main
   ```

2. **Run the deployment script:**
   ```bash
   ./deploy.sh start
   ```
   
   Or manually:
   ```bash
   docker-compose up -d
   ```

3. **Access your application:**
   - Open http://localhost:3000 in your browser (default port)
   - Register a new account
   - Start creating invoices and receipts!
   
   **Note:** If port 3000 is in use, set `HOST_PORT` environment variable:
   ```bash
   HOST_PORT=8080 docker-compose up -d
   ```

### Useful Commands

```bash
# View logs
./deploy.sh logs
# or
docker-compose logs -f app

# Stop application
./deploy.sh stop

# Restart application
./deploy.sh restart

# Check status
./deploy.sh status

# Update application
./deploy.sh update
```

## ☁️ Cloud Deployment (Railway - Recommended for Beginners)

1. **Sign up at [Railway.app](https://railway.app)**
2. **Click "New Project" → "Deploy from GitHub repo"**
3. **Select your repository**
4. **Add MySQL database:**
   - Click "New" → "Database" → "MySQL"
5. **Set environment variables:**
   - Go to your service → Variables
   - Add:
     ```
     SPRING_DATASOURCE_URL=${{MySQL.DATABASE_URL}}
     SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
     SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
     SPRING_JPA_HIBERNATE_DDL_AUTO=update
     ```
6. **Deploy!** Railway will automatically build and deploy.

## 🔵 Heroku Deployment

1. **Install Heroku CLI:**
   ```bash
   brew install heroku/brew/heroku
   ```

2. **Login and create app:**
   ```bash
   heroku login
   heroku create your-app-name
   ```

3. **Add MySQL database:**
   ```bash
   heroku addons:create jawsdb:kitefin
   ```

4. **Set config variables:**
   ```bash
   heroku config:set SPRING_JPA_HIBERNATE_DDL_AUTO=update
   heroku config:set SPRING_THYMELEAF_CACHE=true
   ```

5. **Deploy:**
   ```bash
   git push heroku main
   ```

## 🌐 Render Deployment

1. **Go to [Render.com](https://render.com)**
2. **Create new Web Service**
3. **Connect GitHub repository**
4. **Configure:**
   - Build Command: (auto-detected from Dockerfile)
   - Start Command: (auto-detected)
5. **Add MySQL database service**
6. **Set environment variables** (use database connection string)
7. **Deploy!**

## 📝 Environment Variables

Minimum required for deployment:

```bash
SPRING_DATASOURCE_URL=jdbc:mysql://host:port/database?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Optional (for email features):
```bash
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password
```

## 🆘 Troubleshooting

### Application won't start
- Check database connection string
- Verify environment variables are set
- Check logs: `docker-compose logs app`

### Database connection error
- Verify database is running
- Check username/password
- Ensure database allows remote connections

### Port already in use
- Change PORT environment variable
- Or stop the service using the port

## 📚 Full Deployment Guide

For detailed deployment instructions, see [DEPLOYMENT.md](./DEPLOYMENT.md)

---

**Need help?** Check the [DEPLOYMENT.md](./DEPLOYMENT.md) for comprehensive guides!

