#!/bin/bash

# Billgen Pro Deployment Script
# This script helps deploy the application using Docker Compose

set -e

echo "🚀 Billgen Pro Deployment Script"
echo "================================"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if .env file exists
if [ ! -f .env ]; then
    echo "⚠️  .env file not found. Creating from template..."
    cat > .env << EOF
# Database Configuration
MYSQL_ROOT_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-25)
MYSQL_DATABASE=billgenpro
MYSQL_USER=billgenuser
MYSQL_PASSWORD=$(openssl rand -base64 32 | tr -d "=+/" | cut -c1-25)

# Server Port
PORT=5000

# JPA Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false

# Thymeleaf Cache
SPRING_THYMELEAF_CACHE=true

# Logging
LOGGING_LEVEL_COM_BILLGENPRO=INFO
EOF
    echo "✅ .env file created with random passwords."
    echo "⚠️  Please update .env file with your mail configuration if needed."
    echo ""
fi

# Function to start services
start_services() {
    echo "📦 Building and starting services..."
    docker-compose up -d --build
    echo "✅ Services started!"
    echo ""
    echo "🌐 Application should be available at http://localhost:5000"
    echo "📊 View logs with: docker-compose logs -f app"
}

# Function to stop services
stop_services() {
    echo "🛑 Stopping services..."
    docker-compose down
    echo "✅ Services stopped!"
}

# Function to view logs
view_logs() {
    echo "📋 Showing application logs (Ctrl+C to exit)..."
    docker-compose logs -f app
}

# Function to restart services
restart_services() {
    echo "🔄 Restarting services..."
    docker-compose restart
    echo "✅ Services restarted!"
}

# Function to show status
show_status() {
    echo "📊 Service Status:"
    docker-compose ps
}

# Main menu
case "${1:-start}" in
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    restart)
        restart_services
        ;;
    logs)
        view_logs
        ;;
    status)
        show_status
        ;;
    update)
        echo "🔄 Updating application..."
        git pull
        docker-compose up -d --build
        echo "✅ Application updated!"
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|logs|status|update}"
        echo ""
        echo "Commands:"
        echo "  start   - Build and start services (default)"
        echo "  stop    - Stop all services"
        echo "  restart - Restart all services"
        echo "  logs    - View application logs"
        echo "  status  - Show service status"
        echo "  update  - Pull latest code and rebuild"
        exit 1
        ;;
esac

