package com.billgenpro.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.billgenpro.model.Invoice;
import com.billgenpro.model.InvoiceItem;
import com.billgenpro.model.InvoiceStatus;
import com.billgenpro.model.User;
import com.billgenpro.repository.InvoiceRepository;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAllOrderByDateDesc();
    }

    public List<Invoice> getAllInvoicesByUser(User user) {
        return invoiceRepository.findByUserOrderByDateDesc(user);
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> getInvoiceByIdAndUser(Long id, User user) {
        return invoiceRepository.findByIdAndUserWithItems(id, user);
    }

    public Invoice saveInvoice(Invoice invoice, User user) {
        // Associate invoice with user
        invoice.setUser(user);
        
        // If updating an existing invoice, load it first to properly manage items
        if (invoice.getId() != null) {
            Invoice existingInvoice = invoiceRepository.findByIdAndUserWithItems(invoice.getId(), user)
                    .orElseThrow(() -> new RuntimeException("Invoice not found or you don't have permission to access it"));
            // Clear existing items - orphanRemoval will handle deletion
            existingInvoice.getItems().clear();
            // Set invoice reference for all new items
            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    item.setInvoice(existingInvoice);
                    existingInvoice.getItems().add(item);
                }
            }
            // Copy other fields
            existingInvoice.setNumber(invoice.getNumber());
            existingInvoice.setDate(invoice.getDate());
            existingInvoice.setPaymentDate(invoice.getPaymentDate());
            existingInvoice.setCompany(invoice.getCompany());
            existingInvoice.setBillTo(invoice.getBillTo());
            existingInvoice.setShipTo(invoice.getShipTo());
            existingInvoice.setTaxPercentage(invoice.getTaxPercentage());
            existingInvoice.setNotes(invoice.getNotes());
            existingInvoice.setTemplateNumber(invoice.getTemplateNumber());
            existingInvoice.setLogoUrl(invoice.getLogoUrl());
            existingInvoice.setPrimaryColor(invoice.getPrimaryColor());
            existingInvoice.setSecondaryColor(invoice.getSecondaryColor());
            // Auto-update status based on payment date (only if payment date is set)
            if (invoice.getPaymentDate() != null) {
                existingInvoice.setStatus(InvoiceStatus.PAID);
            } else if (invoice.getDate() != null && invoice.getDate().isBefore(java.time.LocalDate.now().minusDays(30))) {
                existingInvoice.setStatus(InvoiceStatus.OVERDUE);
            } else if (existingInvoice.getStatus() == null) {
                // Only auto-calculate if status is null, otherwise preserve existing status
                existingInvoice.setStatus(InvoiceStatus.PENDING);
            }
            // Ensure user is set (security check)
            existingInvoice.setUser(user);
            return invoiceRepository.save(existingInvoice);
        } else {
            // New invoice - just set invoice reference for all items
            if (invoice.getItems() != null) {
                for (InvoiceItem item : invoice.getItems()) {
                    item.setInvoice(invoice);
                }
            }
            // For new invoices, only auto-calculate status if payment date is set
            // Otherwise, preserve the status that was set (PENDING by default)
            if (invoice.getPaymentDate() != null) {
                invoice.setStatus(InvoiceStatus.PAID);
            } else if (invoice.getDate() != null && invoice.getDate().isBefore(java.time.LocalDate.now().minusDays(30))) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
            } else if (invoice.getStatus() == null) {
                invoice.setStatus(InvoiceStatus.PENDING);
            }
            return invoiceRepository.save(invoice);
        }
    }

    public void deleteInvoice(Long id, User user) {
        Invoice invoice = invoiceRepository.findByIdAndUserWithItems(id, user)
                .orElseThrow(() -> new RuntimeException("Invoice not found or you don't have permission to delete it"));
        invoiceRepository.delete(invoice);
    }

    public List<Invoice> searchInvoices(String query) {
        return invoiceRepository.findByNumberContainingIgnoreCase(query);
    }

    public List<Invoice> searchInvoicesByUser(String query, User user) {
        return invoiceRepository.findByUserAndNumberContainingIgnoreCase(user, query);
    }

    public String generateInvoiceNumber() {
        String number;
        do {
            number = generateRandomInvoiceNumber();
        } while (invoiceRepository.existsByNumber(number));
        return number;
    }

    public String generateInvoiceNumber(User user) {
        String number;
        do {
            number = generateRandomInvoiceNumber();
        } while (invoiceRepository.existsByNumberAndUser(number, user));
        return number;
    }

    private String generateRandomInvoiceNumber() {
        Random random = new Random();
        int length = random.nextInt(6) + 3; // 3-8 characters
        int alphabetCount = Math.min(random.nextInt(4), length);
        StringBuilder result = new StringBuilder();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";

        // Add alphabet characters
        for (int i = 0; i < alphabetCount; i++) {
            result.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        // Add number characters
        for (int i = alphabetCount; i < length; i++) {
            result.append(numbers.charAt(random.nextInt(numbers.length())));
        }

        return result.toString();
    }

    // Filter methods
    public List<Invoice> filterInvoicesByUser(User user, LocalDate startDate, LocalDate endDate, 
                                             String clientName, InvoiceStatus status) {
        return invoiceRepository.findByUserWithFilters(user, startDate, endDate, clientName, status);
    }

    public BigDecimal getTotalRevenueByUser(User user) {
        List<Invoice> paidInvoices = invoiceRepository.findPaidInvoicesByUserWithItems(user);
        return paidInvoices.stream()
                .map(Invoice::getGrandTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getInvoiceCountByUser(User user) {
        return invoiceRepository.countByUser(user);
    }

    public Long getInvoiceCountByUserAndStatus(User user, InvoiceStatus status) {
        return invoiceRepository.countByUserAndStatus(user, status);
    }

    public void updateInvoiceStatus(Long id, User user, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findByIdAndUserWithItems(id, user)
                .orElseThrow(() -> new RuntimeException("Invoice not found or you don't have permission to update it"));
        invoice.setStatus(status);
        invoiceRepository.save(invoice);
    }

    public BigDecimal getUnpaidRevenueByUser(User user) {
        List<Invoice> allInvoices = invoiceRepository.findByUserOrderByDateDesc(user);
        return allInvoices.stream()
                .filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID)
                .map(invoice -> {
                    // Load items if needed for calculation
                    if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
                        invoice = invoiceRepository.findByIdAndUserWithItems(invoice.getId(), user)
                                .orElse(invoice);
                    }
                    return invoice.getGrandTotal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDailyRevenueByUser(User user, LocalDate date) {
        List<Invoice> allInvoices = invoiceRepository.findByUserOrderByDateDesc(user);
        return allInvoices.stream()
                .filter(invoice -> invoice.getDate() != null && invoice.getDate().equals(date))
                .filter(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
                .map(invoice -> {
                    // Load items if needed for calculation
                    if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
                        invoice = invoiceRepository.findByIdAndUserWithItems(invoice.getId(), user)
                                .orElse(invoice);
                    }
                    return invoice.getGrandTotal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}