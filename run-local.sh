#!/bin/bash

# Local Development Runner Script
# This script helps run the application locally with MySQL

set -e

echo "🔧 Billgen Pro Local Development Setup"
echo "======================================"
echo ""

# Check if MySQL is running
if ! mysqladmin ping -h localhost --silent 2>/dev/null; then
    echo "⚠️  MySQL doesn't seem to be running or accessible."
    echo "   Please start MySQL first:"
    echo "   brew services start mysql"
    exit 1
fi

echo "✅ MySQL is running"
echo ""
echo "🔑 MySQL Configuration"
echo "---------------------"
echo "You need to provide your MySQL root password."
echo "If you don't know it, see MYSQL_SETUP.md for reset instructions."
echo ""

# Try to get password from environment or prompt
if [ -z "$MYSQL_PASSWORD" ]; then
    read -sp "Enter MySQL root password (press Enter if no password): " MYSQL_PASSWORD
    echo ""
    export MYSQL_PASSWORD
fi

# Test connection and check/create database
echo "🔌 Testing MySQL connection..."
if [ -z "$MYSQL_PASSWORD" ]; then
    # No password provided
    if mysql -u root -e "SELECT 1;" &>/dev/null 2>&1; then
        echo "✅ MySQL connection successful (no password)"
        MYSQL_CMD="mysql -u root"
        export SPRING_DATASOURCE_PASSWORD=""
    else
        echo "❌ MySQL connection failed. Your MySQL requires a password."
        echo ""
        echo "Options:"
        echo "1. Use Docker Compose: ./deploy.sh start (easiest)"
        echo "2. Reset MySQL password (see MYSQL_SETUP.md)"
        echo "3. Set MYSQL_PASSWORD environment variable: export MYSQL_PASSWORD=your_password"
        exit 1
    fi
else
    # Password provided
    if mysql -u root -p"$MYSQL_PASSWORD" -e "SELECT 1;" &>/dev/null 2>&1; then
        echo "✅ MySQL connection successful!"
        MYSQL_CMD="mysql -u root -p$MYSQL_PASSWORD"
        export SPRING_DATASOURCE_PASSWORD="$MYSQL_PASSWORD"
    else
        echo "❌ MySQL connection failed. Please check your password."
        echo ""
        echo "Options:"
        echo "1. Use Docker Compose: ./deploy.sh start (easiest)"
        echo "2. Reset MySQL password (see MYSQL_SETUP.md)"
        echo "3. Try again with correct password"
        exit 1
    fi
fi

# Check if database exists, if not create it
echo "📊 Checking database..."
if $MYSQL_CMD -e "USE billgenpro;" &>/dev/null 2>&1; then
    echo "✅ Database 'billgenpro' exists"
else
    echo "⚠️  Database 'billgenpro' not found. Creating it..."
    if $MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS billgenpro;" &>/dev/null 2>&1; then
        echo "✅ Database 'billgenpro' created"
    else
        echo "❌ Failed to create database. Please create it manually:"
        echo "   $MYSQL_CMD"
        echo "   CREATE DATABASE billgenpro;"
        exit 1
    fi
fi

# Set environment variables
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/billgenpro?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_JPA_HIBERNATE_DDL_AUTO="update"
export SPRING_THYMELEAF_CACHE="false"
export LOGGING_LEVEL_COM_BILLGENPRO="DEBUG"

echo ""
echo "🚀 Starting application..."
echo "   Access at: http://localhost:5000"
echo "   Press Ctrl+C to stop"
echo ""

# Run the application
mvn spring-boot:run

