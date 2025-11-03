package com.billgenpro.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.billgenpro.model.Invoice;
import com.billgenpro.model.InvoiceItem;
import com.billgenpro.model.User;
import com.billgenpro.repository.InvoiceRepository;

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
}