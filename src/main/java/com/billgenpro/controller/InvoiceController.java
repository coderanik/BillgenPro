package com.billgenpro.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.billgenpro.model.BillTo;
import com.billgenpro.model.Company;
import com.billgenpro.model.Invoice;
import com.billgenpro.model.InvoiceItem;
import com.billgenpro.model.User;
import com.billgenpro.model.InvoiceStatus;
import com.billgenpro.service.EmailService;
import com.billgenpro.service.InvoiceService;
import com.billgenpro.service.PdfService;
import com.billgenpro.service.UserService;
import com.billgenpro.service.ExcelService;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private UserService userService;

    @Autowired(required = false)
    private EmailService emailService;

    @Autowired
    private ExcelService excelService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String listInvoices(Model model,
                               @org.springframework.web.bind.annotation.RequestParam(required = false) String startDate,
                               @org.springframework.web.bind.annotation.RequestParam(required = false) String endDate,
                               @org.springframework.web.bind.annotation.RequestParam(required = false) String clientName,
                               @org.springframework.web.bind.annotation.RequestParam(required = false) String status) {
        User currentUser = getCurrentUser();
        
        LocalDate start = null;
        LocalDate end = null;
        InvoiceStatus statusEnum = null;
        
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
            }
            if (status != null && !status.isEmpty()) {
                statusEnum = InvoiceStatus.valueOf(status.toUpperCase());
            }
        } catch (DateTimeParseException | IllegalArgumentException e) {
            // Invalid date or status, ignore filters
        }
        
        List<Invoice> invoices;
        if (start != null || end != null || (clientName != null && !clientName.isEmpty()) || statusEnum != null) {
            invoices = invoiceService.filterInvoicesByUser(currentUser, start, end, clientName, statusEnum);
        } else {
            invoices = invoiceService.getAllInvoicesByUser(currentUser);
        }
        
        model.addAttribute("invoices", invoices);
        model.addAttribute("statuses", InvoiceStatus.values());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("clientName", clientName);
        model.addAttribute("selectedStatus", status);
        return "invoices/list";
    }

    @GetMapping("/new")
    public String newInvoice(Model model) {
        User currentUser = getCurrentUser();
        Invoice invoice = new Invoice();
        invoice.setNumber(invoiceService.generateInvoiceNumber(currentUser));
        invoice.setDate(LocalDate.now());
        invoice.setStatus(InvoiceStatus.PENDING); // Set initial status to PENDING (Unpaid)
        invoice.setCompany(new Company());
        invoice.setBillTo(new BillTo());
        invoice.setShipTo(new BillTo());
        
        // Add one empty item
        List<InvoiceItem> items = new ArrayList<>();
        items.add(new InvoiceItem());
        invoice.setItems(items);
        
        model.addAttribute("invoice", invoice);
        return "invoices/form";
    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        User currentUser = getCurrentUser();
        Invoice invoice = invoiceService.getInvoiceByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Invoice not found or you don't have permission to view it"));
        model.addAttribute("invoice", invoice);
        return "invoices/view";
    }

    @GetMapping("/{id}/edit")
    public String editInvoice(@PathVariable Long id, Model model) {
        User currentUser = getCurrentUser();
        Invoice invoice = invoiceService.getInvoiceByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Invoice not found or you don't have permission to edit it"));
        model.addAttribute("invoice", invoice);
        return "invoices/form";
    }

    @PostMapping("/save")
    public String saveInvoice(@Valid @ModelAttribute Invoice invoice, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "invoices/form";
        }

        // Remove empty items
        if (invoice.getItems() != null) {
            invoice.getItems().removeIf(item -> 
                item.getName() == null || item.getName().trim().isEmpty());
        }

        User currentUser = getCurrentUser();
        invoiceService.saveInvoice(invoice, currentUser);
        return "redirect:/invoices";
    }

    @GetMapping("/{id}/delete")
    public String deleteInvoice(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        invoiceService.deleteInvoice(id, currentUser);
        return "redirect:/invoices";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Invoice invoice = invoiceService.getInvoiceByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Invoice not found or you don't have permission to access it"));

        byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice-" + invoice.getNumber() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/add-item")
    @ResponseBody
    public String addItem() {
        return """
            <div class="item-row mb-3">
                <div class="row">
                    <div class="col-md-3">
                        <input type="text" class="form-control" name="items[].name" placeholder="Item Name" required>
                    </div>
                    <div class="col-md-3">
                        <input type="text" class="form-control" name="items[].description" placeholder="Description">
                    </div>
                    <div class="col-md-2">
                        <input type="number" class="form-control quantity" name="items[].quantity" placeholder="Qty" min="0" step="1" value="0">
                    </div>
                    <div class="col-md-2">
                        <input type="number" class="form-control amount" name="items[].amount" placeholder="Amount" min="0" step="0.01" value="0">
                    </div>
                    <div class="col-md-1">
                        <input type="number" class="form-control total" name="items[].total" placeholder="Total" readonly>
                    </div>
                    <div class="col-md-1">
                        <button type="button" class="btn btn-danger btn-sm remove-item">Remove</button>
                    </div>
                </div>
            </div>
            """;
    }

    @PostMapping("/{id}/send-email")
    public String sendInvoiceEmail(@PathVariable Long id, 
                                   @org.springframework.web.bind.annotation.RequestParam String recipientEmail) {
        User currentUser = getCurrentUser();
        Invoice invoice = invoiceService.getInvoiceByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Invoice not found or you don't have permission to access it"));
        
        if (emailService == null) {
            return "redirect:/invoices/" + id + "?emailError=Email service is not configured. Please configure mail properties in application.properties";
        }
        
        try {
            emailService.sendInvoiceEmail(invoice, recipientEmail);
            return "redirect:/invoices/" + id + "?emailSent=true";
        } catch (IllegalStateException e) {
            return "redirect:/invoices/" + id + "?emailError=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "redirect:/invoices/" + id + "?emailError=" + java.net.URLEncoder.encode("Failed to send email: " + e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{id}/update-status")
    public String updateInvoiceStatus(@PathVariable Long id, 
                                      @org.springframework.web.bind.annotation.RequestParam String status) {
        User currentUser = getCurrentUser();
        
        try {
            InvoiceStatus newStatus = InvoiceStatus.valueOf(status.toUpperCase());
            invoiceService.updateInvoiceStatus(id, currentUser, newStatus);
            return "redirect:/invoices?statusUpdated=true";
        } catch (IllegalArgumentException e) {
            return "redirect:/invoices?statusError=true";
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportInvoices(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String startDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String endDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String clientName,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String status) {
        try {
            User currentUser = getCurrentUser();
            
            LocalDate start = null;
            LocalDate end = null;
            InvoiceStatus statusEnum = null;
            
            try {
                if (startDate != null && !startDate.isEmpty()) {
                    start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
                }
                if (endDate != null && !endDate.isEmpty()) {
                    end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
                }
                if (status != null && !status.isEmpty()) {
                    statusEnum = InvoiceStatus.valueOf(status.toUpperCase());
                }
            } catch (DateTimeParseException | IllegalArgumentException e) {
                // Invalid date or status, ignore filters
            }
            
            List<Invoice> invoices;
            if (start != null || end != null || (clientName != null && !clientName.isEmpty()) || statusEnum != null) {
                invoices = invoiceService.filterInvoicesByUser(currentUser, start, end, clientName, statusEnum);
            } else {
                invoices = invoiceService.getAllInvoicesByUser(currentUser);
            }
            
            byte[] excelBytes = excelService.generateInvoicesExcel(invoices);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "invoices-export.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export invoices: " + e.getMessage(), e);
        }
    }
}