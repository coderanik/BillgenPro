# 🧾 Billgen Pro - Professional Billing Made Simple

A powerful Java Spring Boot invoice and receipt generator application for professional billing needs.

## 📱 About

Billgen Pro is a comprehensive server-side billing application built with Spring Boot and Thymeleaf. It provides professional invoice and receipt generation with modern web application architecture and robust data persistence.

## ⚡️ Key Features

- **User Authentication:** Secure user registration and login with Spring Security
  - Email-based authentication with password validation
  - BCrypt password hashing for secure password storage
  - User-specific data isolation (users can only access their own invoices and receipts)
  - Protected routes requiring authentication
- **Invoice Generation:** Create and customize professional invoices with company info, items, taxes, and notes
- **Receipt Generation:** Generate receipts with multiple template options
- **PDF Export:** Export invoices and receipts as professional PDFs using iText
- **Data Persistence:** Store invoices and receipts in a database with full CRUD operations
- **Tax Calculations:** Automatic tax calculations with configurable tax rates
- **Responsive Design:** Bootstrap-based responsive web interface
- **Item Management:** Dynamic item addition/removal with real-time total calculations

## 🛠 Tech Stack

- **Backend:** Spring Boot 3.2.0
- **Security:** Spring Security with form-based authentication
- **Database:** H2 (in-memory for development)
- **Template Engine:** Thymeleaf
- **PDF Generation:** iText 7
- **Frontend:** HTML5, CSS, Bootstrap 5
- **Build Tool:** Maven
- **Java Version:** 17+

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Installation & Running

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd billgen-pro
   ```

2. **Build the application:**
   ```bash
   mvn clean compile
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application:**
   - Main application: http://localhost:8080
   - Login page: http://localhost:8080/login
   - Registration page: http://localhost:8080/register
   - H2 Database console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:billgenpro`
     - Username: `sa`
     - Password: (leave empty)

### First-Time Setup

1. **Register a new account:**
   - Navigate to http://localhost:8080/register
   - Enter your name, email, and password (minimum 6 characters)
   - Click "Register" to create your account

2. **Login:**
   - Use your registered email and password to login at http://localhost:8080/login
   - After successful login, you'll be redirected to the home page

## 📋 Usage

### Authentication

**Registration:**
1. Navigate to `/register` or click "Register here" on the login page
2. Fill in your name, email, and password
3. Password must be at least 6 characters long
4. Email must be unique (not already registered)
5. Upon successful registration, you'll be redirected to the login page

**Login:**
1. Navigate to `/login` or access any protected route
2. Enter your registered email and password
3. After successful authentication, you'll be redirected to the home page
4. All invoices and receipts are user-specific - you'll only see your own data

**Logout:**
- Click the logout button in the navigation menu
- You'll be logged out and redirected to the login page

**Security Features:**
- Passwords are securely hashed using BCrypt before storage
- All application routes except `/register`, `/login`, and static resources require authentication
- Session-based authentication maintains your login state
- User data isolation ensures each user only accesses their own invoices and receipts

### Creating Invoices

1. **Login to your account** (required)
2. Navigate to the home page or click "Create Invoice"
3. Fill in company information, invoice details, and items
4. The invoice will be automatically associated with your user account
5. Save the invoice
6. Download as PDF or view/edit later

### Creating Receipts

1. **Login to your account** (required)
2. Click "Create Receipt" or go to `/receipts/new`
3. Enter company details, receipt information, and items
4. Add notes and footer message
5. The receipt will be automatically associated with your user account
6. Save and download as PDF

### Managing Data

- **View your invoices:** `/invoices` - Shows only invoices created by the logged-in user
- **View your receipts:** `/receipts` - Shows only receipts created by the logged-in user
- Edit existing documents by clicking the edit button
- Delete documents with confirmation
- Download PDFs directly from the list view
- All data is isolated per user account

## 🏗 Project Structure

```text
src/
├── main/
│   ├── java/com/billgenpro/
│   │   ├── BillgenProApplication.java    # Main application class
│   │   ├── controller/                   # Web controllers
│   │   │   ├── AuthController.java       # Authentication endpoints
│   │   │   ├── HomeController.java
│   │   │   ├── InvoiceController.java
│   │   │   └── ReceiptController.java
│   │   ├── config/                       # Configuration classes
│   │   │   ├── SecurityConfiguration.java # Spring Security config
│   │   │   └── CustomUserDetailsService.java # User authentication service
│   │   ├── model/                        # JPA entities
│   │   │   ├── User.java                 # User entity
│   │   │   ├── Invoice.java
│   │   │   ├── InvoiceItem.java
│   │   │   ├── Receipt.java
│   │   │   ├── ReceiptItem.java
│   │   │   ├── Company.java
│   │   │   └── BillTo.java
│   │   ├── repository/                   # Data repositories
│   │   │   ├── UserRepository.java       # User data access
│   │   │   ├── InvoiceRepository.java
│   │   │   └── ReceiptRepository.java
│   │   └── service/                      # Business logic
│   │       ├── UserService.java          # User registration and management
│   │       ├── InvoiceService.java
│   │       ├── ReceiptService.java
│   │       └── PdfService.java
│   └── resources/
│       ├── templates/                    # Thymeleaf templates
│       │   ├── index.html
│       │   ├── login.html                # Login page
│       │   ├── register.html             # Registration page
│       │   ├── invoices/
│       │   └── receipts/
│       └── application.properties        # Configuration
```

## 🔧 Configuration

The application uses H2 in-memory database by default. To use a persistent database:

1. Add the appropriate database dependency to `pom.xml`
2. Update `application.properties` with your database configuration:

```properties
# Example for MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/billgenpro
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

## 📊 API Endpoints

### Authentication Routes (Public)
- `GET /login` - Display login page
- `POST /login` - Process login form submission
- `GET /register` - Display registration page
- `POST /register` - Process user registration
- `POST /logout` - Logout user (redirects to login)

### Protected Routes (Require Authentication)
- `GET /` - Home page (dashboard)
- `GET /invoices` - List all invoices for logged-in user
- `GET /invoices/new` - Create new invoice form
- `POST /invoices/save` - Save invoice (associated with current user)
- `GET /invoices/{id}` - View invoice (only if owned by user)
- `GET /invoices/{id}/edit` - Edit invoice form (only if owned by user)
- `GET /invoices/{id}/pdf` - Download invoice PDF (only if owned by user)
- `GET /invoices/{id}/delete` - Delete invoice (only if owned by user)
- `GET /receipts` - List all receipts for logged-in user
- `GET /receipts/new` - Create new receipt form
- `POST /receipts/save` - Save receipt (associated with current user)
- `GET /receipts/{id}` - View receipt (only if owned by user)
- `GET /receipts/{id}/edit` - Edit receipt form (only if owned by user)
- `GET /receipts/{id}/pdf` - Download receipt PDF (only if owned by user)
- `GET /receipts/{id}/delete` - Delete receipt (only if owned by user)

## 🎨 Core Features

| Feature | Status |
|---------|--------|
| User Authentication & Registration | ✅ |
| Secure Password Hashing (BCrypt) | ✅ |
| User Data Isolation | ✅ |
| Invoice Generation | ✅ |
| Receipt Generation | ✅ |
| PDF Export | ✅ |
| Multiple Templates | ✅ |
| Database Persistence | ✅ |
| Real-time Calculations | ✅ |
| Responsive Design | ✅ |
| Professional UI | ✅ |

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🔮 Future Enhancements

- [ ] Multiple PDF template designs
- [ ] Password reset functionality
- [ ] Email verification for new registrations
- [ ] REST API for mobile apps
- [ ] Email integration for sending invoices
- [ ] Payment tracking and reminders
- [ ] Advanced reporting and analytics
- [ ] Import/Export functionality
- [ ] Custom branding and themes
- [ ] Role-based access control (Admin/User roles)

## 📞 Support

For issues and questions:
1. Check the existing issues in the repository
2. Create a new issue with detailed description
3. Include steps to reproduce any bugs

---

Built with ❤️ using Spring Boot and Java