package com.billgenpro.service;

import com.billgenpro.model.Invoice;
import com.billgenpro.model.Receipt;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generateReceiptsExcel(List<Receipt> receipts) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Receipts");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Receipt #", "Date", "Customer", "Cashier", "Subtotal", "Tax %", "Tax Amount", "Grand Total"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Receipt receipt : receipts) {
                Row row = sheet.createRow(rowNum++);
                
                int colNum = 0;
                createCell(row, colNum++, receipt.getNumber(), dataStyle);
                createCell(row, colNum++, 
                    receipt.getDate() != null ? receipt.getDate().format(DATE_FORMATTER) : "", 
                    dataStyle);
                createCell(row, colNum++, receipt.getBillTo() != null ? receipt.getBillTo() : "", dataStyle);
                createCell(row, colNum++, receipt.getCashier() != null ? receipt.getCashier() : "", dataStyle);
                createCell(row, colNum++, receipt.getSubTotal(), currencyStyle);
                createCell(row, colNum++, 
                    receipt.getTaxPercentage() != null ? receipt.getTaxPercentage().doubleValue() : 0.0, 
                    dataStyle);
                createCell(row, colNum++, receipt.getTaxAmount(), currencyStyle);
                createCell(row, colNum++, receipt.getGrandTotal(), currencyStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] generateInvoicesExcel(List<Invoice> invoices) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Invoices");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Invoice #", "Date", "Payment Date", "Bill To", "Status", "Subtotal", "Tax %", "Tax Amount", "Grand Total"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            int rowNum = 1;
            for (Invoice invoice : invoices) {
                Row row = sheet.createRow(rowNum++);
                
                int colNum = 0;
                createCell(row, colNum++, invoice.getNumber(), dataStyle);
                createCell(row, colNum++, 
                    invoice.getDate() != null ? invoice.getDate().format(DATE_FORMATTER) : "", 
                    dataStyle);
                createCell(row, colNum++, 
                    invoice.getPaymentDate() != null ? invoice.getPaymentDate().format(DATE_FORMATTER) : "", 
                    dataStyle);
                createCell(row, colNum++, 
                    invoice.getBillTo() != null && invoice.getBillTo().getName() != null 
                        ? invoice.getBillTo().getName() : "", 
                    dataStyle);
                createCell(row, colNum++, 
                    invoice.getStatus() != null ? invoice.getStatus().name() : "", 
                    dataStyle);
                createCell(row, colNum++, invoice.getSubTotal(), currencyStyle);
                createCell(row, colNum++, 
                    invoice.getTaxPercentage() != null ? invoice.getTaxPercentage().doubleValue() : 0.0, 
                    dataStyle);
                createCell(row, colNum++, invoice.getTaxAmount(), currencyStyle);
                createCell(row, colNum++, invoice.getGrandTotal(), currencyStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("₹#,##0.00"));
        return style;
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int column, java.math.BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value.doubleValue() : 0.0);
        cell.setCellStyle(style);
    }
}

