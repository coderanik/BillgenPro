# 🔧 Local Development Setup Guide

This guide helps you set up the application for local development when MySQL requires a password.

## Problem
The error `Access denied for user 'root'@'localhost' (using password: NO)` means MySQL requires a password but the application isn't providing one.

## Solution Options

### Option 1: Use Docker Compose (Recommended - Easiest)

This sets up both MySQL and the application automatically:

```bash
# Start everything with Docker Compose
./deploy.sh start

# Or manually:
docker-compose up -d
```

This will:
- Start MySQL with a configured password
- Start the application with correct database credentials
- Handle all configuration automatically

**Access at:** http://localhost:5000

### Option 2: Set Environment Variables

Set MySQL password as environment variable before running:

```bash
# macOS/Linux
export SPRING_DATASOURCE_PASSWORD=your_mysql_password
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/billgenpro?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC

# Then run the application
mvn spring-boot:run
```

Or create a `.env` file (for use with tools like `dotenv`):

```bash
SPRING_DATASOURCE_PASSWORD=your_mysql_password
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/billgenpro?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
```

### Option 3: Update application.properties (Not Recommended for Production)

**⚠️ Warning:** Only for local development. Never commit passwords to git!

1. Find your MySQL root password (or reset it - see MYSQL_SETUP.md)
2. Update `src/main/resources/application.properties`:

```properties
spring.datasource.password=your_actual_mysql_password
```

### Option 4: Create MySQL User (Recommended for Security)

Instead of using root, create a dedicated user:

```bash
# Connect to MySQL
mysql -u root -p

# In MySQL prompt:
CREATE DATABASE IF NOT EXISTS billgenpro;
CREATE USER 'billgenuser'@'localhost' IDENTIFIED BY 'secure_password_here';
GRANT ALL PRIVILEGES ON billgenpro.* TO 'billgenuser'@'localhost';
FLUSH PRIVILEGES;
exit;
```

Then set environment variables:
```bash
export SPRING_DATASOURCE_USERNAME=billgenuser
export SPRING_DATASOURCE_PASSWORD=secure_password_here
```

## Finding Your MySQL Password

### If you installed MySQL via Homebrew:

1. Check if MySQL is running:
   ```bash
   brew services list | grep mysql
   ```

2. Try common default passwords:
   - Empty password (just press Enter)
   - `root`
   - Your macOS user password

3. If you forgot the password, see [MYSQL_SETUP.md](./MYSQL_SETUP.md) for reset instructions

### Test MySQL Connection:

```bash
# Try connecting
mysql -u root -p

# If it asks for a password, enter it
# If it connects without password, your MySQL has no password set
```

## Quick Start (Docker - Recommended)

```bash
# 1. Make sure Docker is running
docker --version

# 2. Start the application
./deploy.sh start

# 3. Wait for services to start (30-60 seconds)
docker-compose logs -f app

# 4. Access the application
open http://localhost:5000
```

## Troubleshooting

### MySQL not running:
```bash
# Check if MySQL is running
brew services list | grep mysql

# Start MySQL (if installed via Homebrew)
brew services start mysql

# Or start MySQL service
sudo /usr/local/mysql/support-files/mysql.server start
```

### Port 3306 already in use:
```bash
# Check what's using the port
lsof -i :3306

# If it's another MySQL instance, you can either:
# 1. Stop it and use Docker Compose MySQL
# 2. Use the existing MySQL instance (set password in env vars)
```

### Application still can't connect:

1. **Verify MySQL is accessible:**
   ```bash
   mysql -u root -p -h localhost
   ```

2. **Check database exists:**
   ```bash
   mysql -u root -p -e "SHOW DATABASES;"
   ```

3. **Verify credentials:**
   ```bash
   # Test connection with your credentials
   mysql -u root -p -e "SELECT 1;"
   ```

4. **Check application logs:**
   ```bash
   # If using Docker
   docker-compose logs app
   
   # If running locally
   # Check console output for detailed error messages
   ```

## Recommended Setup for Deployment

For production deployment, **always use environment variables**:

```bash
# Set these before deploying
export SPRING_DATASOURCE_URL=jdbc:mysql://host:port/database
export SPRING_DATASOURCE_USERNAME=username
export SPRING_DATASOURCE_PASSWORD=password
```

Or use Docker Compose which handles this automatically with the `.env` file.

## Next Steps

Once the application is running locally:

1. **Register a user:** http://localhost:5000/register
2. **Login:** http://localhost:5000/login
3. **Create invoices and receipts**

For deployment to production, see [DEPLOYMENT.md](./DEPLOYMENT.md)

