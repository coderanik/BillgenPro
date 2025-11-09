# 🚀 Deployment Guide for Billgen Pro

This guide covers multiple deployment options for your Spring Boot application.

## 📋 Prerequisites

- Java 17+ installed (for local builds)
- Maven 3.6+ (for local builds)
- Docker and Docker Compose (for containerized deployment)
- MySQL database (cloud or managed service)

## 🔧 Environment Variables

Before deploying, you'll need to configure these environment variables:

### Required Variables

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://host:port/database?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password

# Server Port (usually set by platform, but can override)
PORT=5000
```

### Optional Variables

```bash
# Mail Configuration (for email invoice sending)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=465
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_app_password  # Use App Password for Gmail

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

# Thymeleaf Cache (set to true for production)
SPRING_THYMELEAF_CACHE=true

# Logging
LOGGING_LEVEL_COM_BILLGENPRO=INFO
```

## 🐳 Option 1: Docker Compose Deployment (Recommended for VPS/Self-Hosted)

This is the easiest way to deploy on your own server or VPS.

### Steps:

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd billgen-pro-main
   ```

2. **Create a `.env` file:**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Set environment variables in `.env`:**
   ```bash
   MYSQL_ROOT_PASSWORD=your_secure_root_password
   MYSQL_DATABASE=billgenpro
   MYSQL_USER=root
   MYSQL_PASSWORD=Anik@2005
   PORT=5000
   
   # Optional: Mail configuration
   SPRING_MAIL_USERNAME=your_email@gmail.com
   SPRING_MAIL_PASSWORD=your_app_password
   ```

4. **Build and start the containers:**
   ```bash
   docker-compose up -d
   ```

5. **Check logs:**
   ```bash
   docker-compose logs -f app
   ```

6. **Access your application:**
   - Application: http://localhost:3000 (default, configurable via HOST_PORT)
   - MySQL: localhost:3306
   
   **Note:** To change the host port, set `HOST_PORT` environment variable:
   ```bash
   HOST_PORT=8080 docker-compose up -d
   ```

### Stopping the application:
```bash
docker-compose down
```

### Updating the application:
```bash
git pull
docker-compose up -d --build
```

## ☁️ Option 2: Railway Deployment

[Railway](https://railway.app) provides easy deployment with managed MySQL.

### Steps:

1. **Install Railway CLI:**
   ```bash
   npm i -g @railway/cli
   ```

2. **Login to Railway:**
   ```bash
   railway login
   ```

3. **Initialize Railway project:**
   ```bash
   railway init
   ```

4. **Add MySQL database:**
   ```bash
   railway add mysql
   ```

5. **Set environment variables:**
   ```bash
   railway variables set SPRING_DATASOURCE_URL=${{MySQL.DATABASE_URL}}
   railway variables set SPRING_DATASOURCE_USERNAME=${{MySQL.MYSQLUSER}}
   railway variables set SPRING_DATASOURCE_PASSWORD=${{MySQL.MYSQLPASSWORD}}
   railway variables set SPRING_JPA_HIBERNATE_DDL_AUTO=update
   railway variables set SPRING_THYMELEAF_CACHE=true
   ```

6. **Deploy:**
   ```bash
   railway up
   ```

Alternatively, connect your GitHub repo to Railway and it will auto-deploy on push.

## 🌐 Option 3: Render Deployment

[Render](https://render.com) offers free tier with managed PostgreSQL (you'll need to adapt for MySQL or use PostgreSQL).

### Steps:

1. **Create a `render.yaml` file** (see render.yaml in repo)

2. **Go to Render Dashboard:**
   - Create a new Web Service
   - Connect your GitHub repository
   - Select the repository and branch
   - Render will detect the Dockerfile automatically

3. **Configure Environment Variables:**
   - Add all required environment variables in Render dashboard
   - For database, create a MySQL database service in Render

4. **Deploy:**
   - Render will automatically build and deploy

## 🔵 Option 4: Heroku Deployment

### Steps:

1. **Install Heroku CLI:**
   ```bash
   # macOS
   brew tap heroku/brew && brew install heroku
   ```

2. **Login to Heroku:**
   ```bash
   heroku login
   ```

3. **Create a Heroku app:**
   ```bash
   heroku create your-app-name
   ```

4. **Add MySQL addon (JawsDB or ClearDB):**
   ```bash
   heroku addons:create jawsdb:kitefin
   # or
   heroku addons:create cleardb:ignite
   ```

5. **Set environment variables:**
   ```bash
   heroku config:set SPRING_JPA_HIBERNATE_DDL_AUTO=update
   heroku config:set SPRING_THYMELEAF_CACHE=true
   heroku config:set LOGGING_LEVEL_COM_BILLGENPRO=INFO
   ```

6. **Database URL will be automatically set by addon:**
   ```bash
   heroku config:get DATABASE_URL
   ```

7. **Create a `Procfile`:**
   ```
   web: java -jar target/billgen-pro-1.0.0.jar
   ```

8. **Deploy:**
   ```bash
   git push heroku main
   ```

## 🖥️ Option 5: AWS Elastic Beanstalk

### Steps:

1. **Install AWS CLI and EB CLI:**
   ```bash
   pip install awsebcli
   ```

2. **Initialize Elastic Beanstalk:**
   ```bash
   eb init -p "Java 17 running on 64bit Amazon Linux 2" billgen-pro
   ```

3. **Create environment:**
   ```bash
   eb create billgen-pro-env
   ```

4. **Configure environment variables:**
   ```bash
   eb setenv SPRING_DATASOURCE_URL=your_db_url
   eb setenv SPRING_DATASOURCE_USERNAME=your_username
   eb setenv SPRING_DATASOURCE_PASSWORD=your_password
   ```

5. **Deploy:**
   ```bash
   mvn clean package
   eb deploy
   ```

## ☁️ Option 6: DigitalOcean App Platform

### Steps:

1. **Create a DigitalOcean account**

2. **Create App from GitHub:**
   - Go to DigitalOcean App Platform
   - Connect your GitHub repository
   - Select the repository

3. **Configure:**
   - Build Command: `mvn clean package -DskipTests`
   - Run Command: `java -jar target/billgen-pro-1.0.0.jar`
   - Environment: Java
   - Port: 5000

4. **Add MySQL Database:**
   - Add a MySQL database component
   - Link it to your app

5. **Set Environment Variables:**
   - Add all required environment variables
   - Database URL will be provided by the database component

6. **Deploy:**
   - DigitalOcean will build and deploy automatically

## 🏢 Option 7: Google Cloud Run

### Steps:

1. **Install Google Cloud SDK:**
   ```bash
   # macOS
   brew install google-cloud-sdk
   ```

2. **Login and set project:**
   ```bash
   gcloud auth login
   gcloud config set project YOUR_PROJECT_ID
   ```

3. **Build and push Docker image:**
   ```bash
   gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/billgen-pro
   ```

4. **Deploy to Cloud Run:**
   ```bash
   gcloud run deploy billgen-pro \
     --image gcr.io/YOUR_PROJECT_ID/billgen-pro \
     --platform managed \
     --region us-central1 \
     --allow-unauthenticated \
     --set-env-vars SPRING_DATASOURCE_URL=your_db_url \
     --set-env-vars SPRING_DATASOURCE_USERNAME=your_username \
     --set-env-vars SPRING_DATASOURCE_PASSWORD=your_password
   ```

## 🐋 Option 8: Docker on VPS (Manual)

### Steps:

1. **SSH into your VPS:**
   ```bash
   ssh user@your-server.com
   ```

2. **Install Docker and Docker Compose:**
   ```bash
   # Ubuntu/Debian
   curl -fsSL https://get.docker.com -o get-docker.sh
   sh get-docker.sh
   sudo usermod -aG docker $USER
   ```

3. **Clone repository:**
   ```bash
   git clone <your-repo-url>
   cd billgen-pro-main
   ```

4. **Create `.env` file and configure**

5. **Start with Docker Compose:**
   ```bash
   docker-compose up -d
   ```

6. **Set up reverse proxy (Nginx):**
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;

       location / {
           proxy_pass http://localhost:5000;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```

7. **Set up SSL with Let's Encrypt:**
   ```bash
   sudo apt install certbot python3-certbot-nginx
   sudo certbot --nginx -d your-domain.com
   ```

## 📝 Database Setup

### Using Managed MySQL Services:

- **Railway:** Automatically provides MySQL connection string
- **Render:** Offers PostgreSQL (requires code changes) or external MySQL
- **Heroku:** Use JawsDB or ClearDB addon
- **AWS RDS:** Create MySQL RDS instance
- **DigitalOcean:** Managed MySQL database
- **Google Cloud SQL:** Managed MySQL service

### Connection String Format:
```
jdbc:mysql://host:port/database?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

## 🔒 Security Checklist

- [ ] Use strong database passwords
- [ ] Set `SPRING_THYMELEAF_CACHE=true` in production
- [ ] Set `SPRING_JPA_SHOW_SQL=false` in production
- [ ] Use environment variables, never commit secrets
- [ ] Enable SSL/TLS for database connections in production
- [ ] Use App Passwords for Gmail (not regular password)
- [ ] Set up firewall rules to restrict database access
- [ ] Regularly update dependencies
- [ ] Use HTTPS for your application (Let's Encrypt)

## 🔍 Troubleshooting

### Application won't start:
- Check database connection string
- Verify all environment variables are set
- Check application logs: `docker-compose logs app`
- Ensure database is accessible from application

### Database connection issues:
- Verify database host, port, username, and password
- Check firewall rules
- Ensure database allows connections from your app's IP
- Test connection with MySQL client

### Port already in use:
- Change PORT environment variable
- Check what's using the port: `lsof -i :5000`
- Kill the process or use a different port

## 📚 Additional Resources

- [Spring Boot Deployment](https://spring.io/guides/gs/spring-boot-for-azure/)
- [Docker Documentation](https://docs.docker.com/)
- [Railway Documentation](https://docs.railway.app/)
- [Render Documentation](https://render.com/docs)

## 🆘 Need Help?

If you encounter issues:
1. Check the application logs
2. Verify all environment variables
3. Ensure database is running and accessible
4. Check firewall and network settings

---

**Happy Deploying! 🚀**
