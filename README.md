# 🧾 Billgen Pro - Professional Billing Made Simple

A powerful Java Spring Boot invoice and receipt generator application for professional billing needs.

## 📱 About

Billgen Pro is a comprehensive server-side billing application built with Spring Boot and Thymeleaf. It provides professional invoice and receipt generation with modern web application architecture and robust data persistence.

## ⚡️ Key Features

- **Authentication:** Authenticate users using credentials
- **Invoice Generation:** Create and customize professional invoices with company info, items, taxes, and notes
- **Receipt Generation:** Generate receipts with multiple template options
- **PDF Export:** Export invoices and receipts as professional PDFs using iText
- **Data Persistence:** Store invoices and receipts in a database with full CRUD operations
- **Tax Calculations:** Automatic tax calculations with configurable tax rates
- **Responsive Design:** Bootstrap-based responsive web interface
- **Item Management:** Dynamic item addition/removal with real-time total calculations

## 🛠 Tech Stack

- **Backend:** Spring Boot 3.2.0
- **Database:** H2 (in-memory for development)
- **Template Engine:** Thymeleaf
- **PDF Generation:** iText 7
- **Frontend:** HTML5, CSS
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
   - H2 Database console: http://localhost:8080/h2-console
     - JDBC URL: `jdbc:h2:mem:billgenpro`
     - Username: `sa`
     - Password: (leave empty)

## 📋 Usage

### Creating Invoices

1. Navigate to the home page
2. Click "Create Invoice" or go to `/invoices/new`
3. Fill in company information, invoice details, and items
4. Save the invoice
5. Download as PDF or view/edit later

### Creating Receipts

1. Click "Create Receipt" or go to `/receipts/new`
2. Enter company details, receipt information, and items
3. Add notes and footer message
4. Save and download as PDF

### Managing Data

- View all invoices: `/invoices`
- View all receipts: `/receipts`
- Edit existing documents by clicking the edit button
- Delete documents with confirmation
- Download PDFs directly from the list view

## 🏗 Project Structure

```
src/
├── main/
│   ├── java/com/billgenpro/
│   │   ├── BillgenProApplication.java    # Main application class
│   │   ├── controller/                   # Web controllers
│   │   │   ├── HomeController.java
│   │   │   ├── InvoiceController.java
│   │   │   └── ReceiptController.java
│   │   ├── model/                        # JPA entities
│   │   │   ├── Invoice.java
│   │   │   ├── InvoiceItem.java
│   │   │   ├── Receipt.java
│   │   │   ├── ReceiptItem.java
│   │   │   ├── Company.java
│   │   │   └── BillTo.java
│   │   ├── repository/                   # Data repositories
│   │   │   ├── InvoiceRepository.java
│   │   │   └── ReceiptRepository.java
│   │   └── service/                      # Business logic
│   │       ├── InvoiceService.java
│   │       ├── ReceiptService.java
│   │       └── PdfService.java
│   └── resources/
│       ├── templates/                    # Thymeleaf templates
│       │   ├── index.html
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

### Web Routes
- `GET /` - Home page
- `GET /invoices` - List all invoices
- `GET /invoices/new` - Create new invoice form
- `POST /invoices/save` - Save invoice
- `GET /invoices/{id}` - View invoice
- `GET /invoices/{id}/edit` - Edit invoice form
- `GET /invoices/{id}/pdf` - Download invoice PDF
- `GET /invoices/{id}/delete` - Delete invoice

### Receipt Routes
- `GET /receipts` - List all receipts
- `GET /receipts/new` - Create new receipt form
- `POST /receipts/save` - Save receipt
- `GET /receipts/{id}` - View receipt
- `GET /receipts/{id}/edit` - Edit receipt form
- `GET /receipts/{id}/pdf` - Download receipt PDF
- `GET /receipts/{id}/delete` - Delete receipt

## 🎨 Core Features

| Feature | Status |
|---------|--------|
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
- [ ] User authentication and multi-tenancy
- [ ] REST API for mobile apps
- [ ] Email integration for sending invoices
- [ ] Payment tracking and reminders
- [ ] Advanced reporting and analytics
- [ ] Import/Export functionality
- [ ] Custom branding and themes

## 📞 Support

For issues and questions:
1. Check the existing issues in the repository
2. Create a new issue with detailed description
3. Include steps to reproduce any bugs

---

Built with ❤️ using Spring Boot and Java