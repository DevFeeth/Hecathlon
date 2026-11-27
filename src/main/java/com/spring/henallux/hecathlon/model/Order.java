package com.spring.henallux.hecathlon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    public static final int ESTIMATED_DELIVERY_DAYS = 3;

    private Long id;
    private String customerEmail;
    private LocalDateTime date;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private DeliveryMode deliveryMode;
    private String address;
    private String addressComplement;
    private String postalCode;
    private String city;
    private String country;
    private String paymentReference;
    private LocalDateTime paidAt;
    private OrderStatus status;
    private LocalDateTime statusDate;
    private BigDecimal subtotal;
    private BigDecimal deliveryCost;
    private BigDecimal total;
    private List<OrderLine> lines = new ArrayList<>();

    public String getNumber() { return id == null ? null : "HC%06d".formatted(id); }

    public BigDecimal getVat() { return Cart.vatIncluded(total); }

    public int getItemCount() { return lines.stream().mapToInt(OrderLine::getQuantity).sum(); }

    public LocalDateTime getExpectedDeliveryDate() { return date.plusDays(ESTIMATED_DELIVERY_DAYS); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public DeliveryMode getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(DeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAddressComplement() { return addressComplement; }
    public void setAddressComplement(String addressComplement) { this.addressComplement = addressComplement; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public LocalDateTime getStatusDate() { return statusDate; }
    public void setStatusDate(LocalDateTime statusDate) { this.statusDate = statusDate; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getDeliveryCost() { return deliveryCost; }
    public void setDeliveryCost(BigDecimal deliveryCost) { this.deliveryCost = deliveryCost; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<OrderLine> getLines() { return lines; }
    public void setLines(List<OrderLine> lines) { this.lines = lines; }
}
