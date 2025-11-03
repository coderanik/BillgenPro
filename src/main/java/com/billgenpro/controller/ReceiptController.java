package com.billgenpro.controller;

import com.billgenpro.model.Receipt;
import com.billgenpro.model.ReceiptItem;
import com.billgenpro.model.Company;
import com.billgenpro.model.User;
import com.billgenpro.service.ReceiptService;
import com.billgenpro.service.PdfService;
import com.billgenpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String listReceipts(Model model) {
        User currentUser = getCurrentUser();
        model.addAttribute("receipts", receiptService.getAllReceiptsByUser(currentUser));
        return "receipts/list";
    }

    @GetMapping("/new")
    public String newReceipt(Model model) {
        User currentUser = getCurrentUser();
        Receipt receipt = new Receipt();
        receipt.setNumber(receiptService.generateReceiptNumber(currentUser));
        receipt.setDate(LocalDate.now());
        receipt.setCompany(new Company());
        
        // Add one empty item
        List<ReceiptItem> items = new ArrayList<>();
        items.add(new ReceiptItem());
        receipt.setItems(items);
        
        model.addAttribute("receipt", receipt);
        return "receipts/form";
    }

    @GetMapping("/{id}")
    public String viewReceipt(@PathVariable Long id, Model model) {
        User currentUser = getCurrentUser();
        Receipt receipt = receiptService.getReceiptByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Receipt not found or you don't have permission to view it"));
        model.addAttribute("receipt", receipt);
        return "receipts/view";
    }

    @GetMapping("/{id}/edit")
    public String editReceipt(@PathVariable Long id, Model model) {
        User currentUser = getCurrentUser();
        Receipt receipt = receiptService.getReceiptByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Receipt not found or you don't have permission to edit it"));
        model.addAttribute("receipt", receipt);
        return "receipts/form";
    }

    @PostMapping("/save")
    public String saveReceipt(@Valid @ModelAttribute Receipt receipt, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "receipts/form";
        }

        // Remove empty items
        if (receipt.getItems() != null) {
            receipt.getItems().removeIf(item -> 
                item.getName() == null || item.getName().trim().isEmpty());
        }

        User currentUser = getCurrentUser();
        receiptService.saveReceipt(receipt, currentUser);
        return "redirect:/receipts";
    }

    @GetMapping("/{id}/delete")
    public String deleteReceipt(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        receiptService.deleteReceipt(id, currentUser);
        return "redirect:/receipts";
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        Receipt receipt = receiptService.getReceiptByIdAndUser(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Receipt not found or you don't have permission to access it"));

        byte[] pdfBytes = pdfService.generateReceiptPdf(receipt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "receipt-" + receipt.getNumber() + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}