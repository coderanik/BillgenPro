package com.billgenpro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Invoice number is required")
    private String number;

    private LocalDate date;
    private LocalDate paymentDate;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "company_name")),
        @AttributeOverride(name = "address", column = @Column(name = "company_address")),
        @AttributeOverride(name = "phone", column = @Column(name = "company_phone")),
        @AttributeOverride(name = "gst", column = @Column(name = "company_gst"))
    })
    private Company company;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "bill_to_name")),
        @AttributeOverride(name = "address", column = @Column(name = "bill_to_address")),
        @AttributeOverride(name = "phone", column = @Column(name = "bill_to_phone"))
    })
    private BillTo billTo;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "ship_to_name")),
        @AttributeOverride(name = "address", column = @Column(name = "ship_to_address")),
        @AttributeOverride(name = "phone", column = @Column(name = "ship_to_phone"))
    })
    private BillTo shipTo;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PositiveOrZero(message = "Tax percentage must be positive")
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Integer templateNumber = 1;

    // Constructors
    public Invoice() {}

    // Calculated fields
    public BigDecimal getSubTotal() {
        return items.stream()
                .map(InvoiceItem::getTotal)
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

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public BillTo getBillTo() { return billTo; }
    public void setBillTo(BillTo billTo) { this.billTo = billTo; }

    public BillTo getShipTo() { return shipTo; }
    public void setShipTo(BillTo shipTo) { this.shipTo = shipTo; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { 
        this.items = items;
        // Set the invoice reference for each item
        if (items != null) {
            items.forEach(item -> item.setInvoice(this));
        }
    }

    public BigDecimal getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(BigDecimal taxPercentage) { this.taxPercentage = taxPercentage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getTemplateNumber() { return templateNumber; }
    public void setTemplateNumber(Integer templateNumber) { this.templateNumber = templateNumber; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}