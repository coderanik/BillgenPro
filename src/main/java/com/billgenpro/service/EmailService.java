package com.billgenpro.service;

import com.billgenpro.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private PdfService pdfService;

    @Autowired(required = false)
    private Environment environment;

    public void sendInvoiceEmail(Invoice invoice, String recipientEmail) throws MessagingException {
        if (mailSender == null) {
            throw new IllegalStateException("Email service is not configured. Please uncomment and configure mail properties in application.properties. For Gmail: 1) Enable 2-Step Verification, 2) Generate App Password at https://myaccount.google.com/apppasswords, 3) Update application.properties with your email and app password.");
        }
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setTo(recipientEmail);
        String companyName = invoice.getCompany() != null && invoice.getCompany().getName() != null 
            ? invoice.getCompany().getName() 
            : "Billgen Pro";
        helper.setSubject("Invoice #" + invoice.getNumber() + " from " + companyName);
        
        // Gmail requires the "from" address to match the authenticated email
        String fromEmail = (environment != null && environment.getProperty("spring.mail.username") != null)
            ? environment.getProperty("spring.mail.username")
            : "noreply@billgenpro.com";
        helper.setFrom(fromEmail);

        // Create HTML email body
        String htmlBody = createEmailTemplate(invoice);
        helper.setText(htmlBody, true);

        // Attach PDF
        byte[] pdfBytes = pdfService.generateInvoicePdf(invoice);
        helper.addAttachment("invoice-" + invoice.getNumber() + ".pdf", () -> {
            return new ByteArrayInputStream(pdfBytes);
        });

        mailSender.send(message);
    }

    private String createEmailTemplate(Invoice invoice) {
        String primaryColor = invoice.getPrimaryColor() != null ? invoice.getPrimaryColor() : "#6366f1";
        String companyName = invoice.getCompany() != null ? invoice.getCompany().getName() : "Company";
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: linear-gradient(135deg, " + primaryColor + " 0%, #0ea5e9 100%); color: white; padding: 30px; border-radius: 10px 10px 0 0; }" +
                ".content { background: #f9fafb; padding: 30px; border-radius: 0 0 10px 10px; }" +
                ".invoice-details { background: white; padding: 20px; border-radius: 8px; margin: 20px 0; }" +
                ".button { display: inline-block; padding: 12px 24px; background: " + primaryColor + "; color: white; text-decoration: none; border-radius: 6px; margin-top: 20px; }" +
                ".footer { text-align: center; color: #666; font-size: 12px; margin-top: 30px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1 style='margin: 0;'>Invoice #" + invoice.getNumber() + "</h1>" +
                "<p style='margin: 10px 0 0 0;'>" + companyName + "</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Dear " + (invoice.getBillTo() != null && invoice.getBillTo().getName() != null ? invoice.getBillTo().getName() : "Customer") + ",</p>" +
                "<p>Please find attached your invoice #" + invoice.getNumber() + " for the amount of ₹" + 
                String.format("%.2f", invoice.getGrandTotal()) + ".</p>" +
                "<div class='invoice-details'>" +
                "<p><strong>Invoice Date:</strong> " + invoice.getDate() + "</p>" +
                "<p><strong>Total Amount:</strong> ₹" + String.format("%.2f", invoice.getGrandTotal()) + "</p>" +
                "<p><strong>Status:</strong> " + (invoice.getStatus() != null ? invoice.getStatus().getDisplayName() : "Pending") + "</p>" +
                "</div>" +
                "<p>If you have any questions about this invoice, please don't hesitate to contact us.</p>" +
                "<p>Thank you for your business!</p>" +
                "<p>Best regards,<br>" + companyName + "</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>This is an automated email from Billgen Pro. Please do not reply to this email.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

