package com.billgenpro.controller;

import com.billgenpro.model.InvoiceStatus;
import com.billgenpro.model.User;
import com.billgenpro.service.InvoiceService;
import com.billgenpro.service.ReceiptService;
import com.billgenpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class HomeController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // Redirect authenticated users to dashboard
            return "redirect:/dashboard";
        }
        // Show index.html for unauthenticated users
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userService.findByEmail(email);

            // Invoice statistics
            BigDecimal totalRevenuePaid = invoiceService.getTotalRevenueByUser(user);
            BigDecimal totalRevenueUnpaid = invoiceService.getUnpaidRevenueByUser(user);
            Long totalInvoices = invoiceService.getInvoiceCountByUser(user);
            Long paidCount = invoiceService.getInvoiceCountByUserAndStatus(user, InvoiceStatus.PAID);
            Long pendingCount = invoiceService.getInvoiceCountByUserAndStatus(user, InvoiceStatus.PENDING);
            Long overdueCount = invoiceService.getInvoiceCountByUserAndStatus(user, InvoiceStatus.OVERDUE);

            // Receipt statistics
            Long totalReceipts = receiptService.getReceiptCountByUser(user);

            // Daily revenue (from both invoices and receipts)
            LocalDate today = LocalDate.now();
            BigDecimal dailyInvoiceRevenue = invoiceService.getDailyRevenueByUser(user, today);
            BigDecimal dailyReceiptRevenue = receiptService.getDailyRevenueByUser(user, today);
            BigDecimal totalDailyRevenue = dailyInvoiceRevenue.add(dailyReceiptRevenue);

            model.addAttribute("totalRevenuePaid", totalRevenuePaid);
            model.addAttribute("totalRevenueUnpaid", totalRevenueUnpaid);
            model.addAttribute("totalInvoices", totalInvoices);
            model.addAttribute("paidCount", paidCount);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("overdueCount", overdueCount);
            model.addAttribute("totalReceipts", totalReceipts);
            model.addAttribute("totalDailyRevenue", totalDailyRevenue);
            model.addAttribute("username", user.getName());
        }
        return "dashboard";
    }
}