package com.billgenpro.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.billgenpro.model.Receipt;
import com.billgenpro.model.ReceiptItem;
import com.billgenpro.model.User;
import com.billgenpro.repository.ReceiptRepository;

@Service
@Transactional
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAllOrderByDateDesc();
    }

    public List<Receipt> getAllReceiptsByUser(User user) {
        return receiptRepository.findByUserOrderByDateDesc(user);
    }

    public Optional<Receipt> getReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    public Optional<Receipt> getReceiptByIdAndUser(Long id, User user) {
        return receiptRepository.findByIdAndUserWithItems(id, user);
    }

    public Receipt saveReceipt(Receipt receipt, User user) {
        // Associate receipt with user
        receipt.setUser(user);
        
        // If updating an existing receipt, load it first to properly manage items
        if (receipt.getId() != null) {
            Receipt existingReceipt = receiptRepository.findByIdAndUserWithItems(receipt.getId(), user)
                    .orElseThrow(() -> new RuntimeException("Receipt not found or you don't have permission to access it"));
            // Clear existing items - orphanRemoval will handle deletion
            existingReceipt.getItems().clear();
            // Set receipt reference for all new items
            if (receipt.getItems() != null) {
                for (ReceiptItem item : receipt.getItems()) {
                    item.setReceipt(existingReceipt);
                    existingReceipt.getItems().add(item);
                }
            }
            // Copy other fields
            existingReceipt.setNumber(receipt.getNumber());
            existingReceipt.setDate(receipt.getDate());
            existingReceipt.setCompany(receipt.getCompany());
            existingReceipt.setBillTo(receipt.getBillTo());
            existingReceipt.setCashier(receipt.getCashier());
            existingReceipt.setTaxPercentage(receipt.getTaxPercentage());
            existingReceipt.setNotes(receipt.getNotes());
            existingReceipt.setFooter(receipt.getFooter());
            existingReceipt.setTemplateNumber(receipt.getTemplateNumber());
            // Ensure user is set (security check)
            existingReceipt.setUser(user);
            return receiptRepository.save(existingReceipt);
        } else {
            // New receipt - just set receipt reference for all items
            if (receipt.getItems() != null) {
                for (ReceiptItem item : receipt.getItems()) {
                    item.setReceipt(receipt);
                }
            }
            return receiptRepository.save(receipt);
        }
    }

    public void deleteReceipt(Long id, User user) {
        Receipt receipt = receiptRepository.findByIdAndUserWithItems(id, user)
                .orElseThrow(() -> new RuntimeException("Receipt not found or you don't have permission to delete it"));
        receiptRepository.delete(receipt);
    }

    public List<Receipt> searchReceipts(String query) {
        return receiptRepository.findByNumberContainingIgnoreCase(query);
    }

    public List<Receipt> searchReceiptsByUser(String query, User user) {
        return receiptRepository.findByUserAndNumberContainingIgnoreCase(user, query);
    }

    public String generateReceiptNumber() {
        String number;
        do {
            number = generateRandomReceiptNumber();
        } while (receiptRepository.existsByNumber(number));
        return number;
    }

    public String generateReceiptNumber(User user) {
        String number;
        do {
            number = generateRandomReceiptNumber();
        } while (receiptRepository.existsByNumberAndUser(number, user));
        return number;
    }

    private String generateRandomReceiptNumber() {
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