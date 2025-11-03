package com.billgenpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receipts")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Receipt number is required")
    private String number;

    private LocalDate date;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "company_name")),
        @AttributeOverride(name = "address", column = @Column(name = "company_address")),
        @AttributeOverride(name = "phone", column = @Column(name = "company_phone")),
        @AttributeOverride(name = "gst", column = @Column(name = "company_gst"))
    })
    private Company company;

    private String billTo;
    private String cashier;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReceiptItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PositiveOrZero(message = "Tax percentage must be positive")
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String footer;

    private Integer templateNumber = 1;

    // Constructors
    public Receipt() {}

    // Calculated fields
    public BigDecimal getSubTotal() {
        return items.stream()
                .map(ReceiptItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTaxAmount() {
        BigDecimal subTotal = getSubTotal();
        return subTotal.multiply(taxPercentage).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal getGrandTotal() {
        return getSubTotal().add(getTaxAmount());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getBillTo() { return billTo; }
    public void setBillTo(String billTo) { this.billTo = billTo; }

    public String getCashier() { return cashier; }
    public void setCashier(String cashier) { this.cashier = cashier; }

    public List<ReceiptItem> getItems() { return items; }
    public void setItems(List<ReceiptItem> items) { 
        this.items = items;
        if (items != null) {
            items.forEach(item -> item.setReceipt(this));
        }
    }

    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal taxPercentage) { this.taxPercentage = taxPercentage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getFooter() { return footer; }
    public void setFooter(String footer) { this.footer = footer; }

    public Integer getTemplateNumber() { return templateNumber; }
    public void setTemplateNumber(Integer templateNumber) { this.templateNumber = templateNumber; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}