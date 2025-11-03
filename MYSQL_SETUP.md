# MySQL Setup Instructions

Your MySQL server requires a password. Follow these steps to configure it:

## Option 1: Reset MySQL Root Password (if you forgot it)

```bash
# Stop MySQL
brew services stop mysql

# Start MySQL in safe mode
mysqld_safe --skip-grant-tables &

# Connect without password
mysql -u root

# In MySQL prompt, run:
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'yourpassword';
FLUSH PRIVILEGES;
exit;

# Restart MySQL normally
brew services restart mysql
```

## Option 2: Create a New MySQL User (Recommended)

```bash
# Connect to MySQL (you'll need your root password)
mysql -u root -p

# In MySQL prompt, create a new user and database:
CREATE DATABASE IF NOT EXISTS billgenpro;
CREATE USER 'billgenuser'@'localhost' IDENTIFIED BY 'billgenpass123';
GRANT ALL PRIVILEGES ON billgenpro.* TO 'billgenuser'@'localhost';
FLUSH PRIVILEGES;
exit;
```

Then update `application.properties`:
```properties
spring.datasource.username=billgenuser
spring.datasource.password=billgenpass123
```

## Option 3: Find Your Current MySQL Password

If you have MySQL installed via Homebrew and set a password during installation, use that password.

Update `src/main/resources/application.properties`:
```properties
spring.datasource.password=your_actual_mysql_password
```

## After Configuration

Once you've set the password, update `src/main/resources/application.properties` with the correct password and restart the application.

