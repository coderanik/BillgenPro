package com.billgenpro.controller;

import com.billgenpro.model.InvoiceStatus;
import com.billgenpro.model.User;
import com.billgenpro.service.InvoiceService;
import com.billgenpro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class HomeController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            User user = userService.findByEmail(email);

            BigDecimal totalRevenue = invoiceService.getTotalRevenueByUser(user);
            Long totalInvoices = invoiceService.getInvoiceCountByUser(user);
            Long paidCount = invoiceService.getInvoiceCountByUserAndStatus(user, InvoiceStatus.PAID);
            Long pendingCount = invoiceService.getInvoiceCountByUserAndStatus(user, InvoiceStatus.PENDING);
            Long overdueCount = invoiceService.getInvoiceCountByUserAndStatus(user, InvoiceStatus.OVERDUE);

            model.addAttribute("dashboard_totalRevenue", totalRevenue);
            model.addAttribute("dashboard_totalInvoices", totalInvoices);
            model.addAttribute("dashboard_paidCount", paidCount);
            model.addAttribute("dashboard_pendingCount", pendingCount);
            model.addAttribute("dashboard_overdueCount", overdueCount);
        }
        return "index";
    }
}