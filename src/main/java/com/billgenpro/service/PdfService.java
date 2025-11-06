package com.billgenpro.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.billgenpro.model.Invoice;
import com.billgenpro.model.InvoiceItem;
import com.billgenpro.model.Receipt;
import com.billgenpro.model.ReceiptItem;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(Invoice invoice) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header
            document.add(new Paragraph("INVOICE")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(24)
                    .setBold());

            // Company Info
            if (invoice.getCompany() != null) {
                document.add(new Paragraph(invoice.getCompany().getName())
                        .setFontSize(16)
                        .setBold());
                if (invoice.getCompany().getAddress() != null) {
                    document.add(new Paragraph(invoice.getCompany().getAddress()));
                }
                if (invoice.getCompany().getPhone() != null) {
                    document.add(new Paragraph("Phone: " + invoice.getCompany().getPhone()));
                }
                if (invoice.getCompany().getGst() != null) {
                    document.add(new Paragraph("GST: " + invoice.getCompany().getGst()));
                }
            }

            document.add(new Paragraph("\n"));

            // Invoice Details
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            infoTable.setWidth(UnitValue.createPercentValue(100));

            infoTable.addCell(new Cell().add(new Paragraph("Invoice Number: " + invoice.getNumber())));
            infoTable.addCell(new Cell().add(new Paragraph("Date: " + 
                (invoice.getDate() != null ? invoice.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""))));

            if (invoice.getPaymentDate() != null) {
                infoTable.addCell(new Cell().add(new Paragraph("Payment Date: " + 
                    invoice.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
            }

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // Bill To
            if (invoice.getBillTo() != null) {
                document.add(new Paragraph("Bill To:").setBold());
                if (invoice.getBillTo().getName() != null) {
                    document.add(new Paragraph(invoice.getBillTo().getName()));
                }
                if (invoice.getBillTo().getAddress() != null) {
                    document.add(new Paragraph(invoice.getBillTo().getAddress()));
                }
                if (invoice.getBillTo().getPhone() != null) {
                    document.add(new Paragraph("Phone: " + invoice.getBillTo().getPhone()));
                }
                document.add(new Paragraph("\n"));
            }

            // Ship To
            if (invoice.getShipTo() != null && invoice.getShipTo().getName() != null && !invoice.getShipTo().getName().trim().isEmpty()) {
                document.add(new Paragraph("Ship To:").setBold());
                if (invoice.getShipTo().getName() != null) {
                    document.add(new Paragraph(invoice.getShipTo().getName()));
                }
                if (invoice.getShipTo().getAddress() != null) {
                    document.add(new Paragraph(invoice.getShipTo().getAddress()));
                }
                if (invoice.getShipTo().getPhone() != null) {
                    document.add(new Paragraph("Phone: " + invoice.getShipTo().getPhone()));
                }
                document.add(new Paragraph("\n"));
            }

            // Items Table
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1}));
            itemsTable.setWidth(UnitValue.createPercentValue(100));

            // Headers
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Qty").setBold()));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Rate").setBold()));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Amount").setBold()));

            // Items
            for (InvoiceItem item : invoice.getItems()) {
                String description = item.getName();
                if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                    description += "\n" + item.getDescription();
                }
                itemsTable.addCell(new Cell().add(new Paragraph(description)));
                itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                itemsTable.addCell(new Cell().add(new Paragraph("₹" + item.getAmount())));
                itemsTable.addCell(new Cell().add(new Paragraph("₹" + item.getTotal())));
            }

            document.add(itemsTable);
            document.add(new Paragraph("\n"));

            // Totals
            Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
            totalsTable.setWidth(UnitValue.createPercentValue(100));

            totalsTable.addCell(new Cell().add(new Paragraph("Sub Total:")));
            totalsTable.addCell(new Cell().add(new Paragraph("₹" + invoice.getSubTotal())));

            totalsTable.addCell(new Cell().add(new Paragraph("Tax (" + invoice.getTaxPercentage() + "%):")));
            totalsTable.addCell(new Cell().add(new Paragraph("₹" + invoice.getTaxAmount())));

            totalsTable.addCell(new Cell().add(new Paragraph("Grand Total:").setBold()));
            totalsTable.addCell(new Cell().add(new Paragraph("₹" + invoice.getGrandTotal()).setBold()));

            document.add(totalsTable);

            // Notes
            if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Notes:").setBold());
                document.add(new Paragraph(invoice.getNotes()));
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public byte[] generateReceiptPdf(Receipt receipt) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header
            document.add(new Paragraph("RECEIPT")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold());

            // Company Info
            if (receipt.getCompany() != null) {
                document.add(new Paragraph(receipt.getCompany().getName())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(14)
                        .setBold());
                if (receipt.getCompany().getAddress() != null) {
                    document.add(new Paragraph(receipt.getCompany().getAddress())
                            .setTextAlignment(TextAlignment.CENTER));
                }
                if (receipt.getCompany().getPhone() != null) {
                    document.add(new Paragraph("Phone: " + receipt.getCompany().getPhone())
                            .setTextAlignment(TextAlignment.CENTER));
                }
            }

            document.add(new Paragraph("\n"));

            // Receipt Details
            document.add(new Paragraph("Receipt #: " + receipt.getNumber()));
            if (receipt.getDate() != null) {
                document.add(new Paragraph("Date: " + receipt.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            }
            if (receipt.getCashier() != null) {
                document.add(new Paragraph("Cashier: " + receipt.getCashier()));
            }
            if (receipt.getBillTo() != null) {
                document.add(new Paragraph("Customer: " + receipt.getBillTo()));
            }

            document.add(new Paragraph("\n"));

            // Items
            for (ReceiptItem item : receipt.getItems()) {
                document.add(new Paragraph(item.getName() + " x" + item.getQuantity() + " @ ₹" + item.getAmount() + " = ₹" + item.getTotal()));
            }

            document.add(new Paragraph("\n"));

            // Totals
            document.add(new Paragraph("Sub Total: ₹" + receipt.getSubTotal()));
            document.add(new Paragraph("Tax (" + receipt.getTaxPercentage() + "%): ₹" + receipt.getTaxAmount()));
            document.add(new Paragraph("Total: ₹" + receipt.getGrandTotal()).setBold());

            // Footer
            if (receipt.getFooter() != null && !receipt.getFooter().isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(receipt.getFooter())
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(10));
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}