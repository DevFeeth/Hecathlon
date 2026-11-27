package com.spring.henallux.hecathlon.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class DeliveryForm {
    public static final List<String> COUNTRIES = ValidationRules.COUNTRIES;

    @NotBlank @Size(max = 50)
    private String firstName;
    @NotBlank @Size(max = 50)
    private String lastName;
    @NotBlank @Email @Pattern(regexp = ValidationRules.EMAIL) @Size(max = 100)
    private String email;
    @NotBlank @Pattern(regexp = ValidationRules.PHONE)
    private String phone;
    @NotNull
    private DeliveryMode deliveryMode = DeliveryMode.HOME;
    @Size(max = 100)
    private String address;
    @Size(max = 100)
    private String addressComplement;
    @Pattern(regexp = "^$|" + ValidationRules.POSTAL_CODE)
    private String postalCode;
    @Size(max = 60)
    private String city;
    private String country = COUNTRIES.getFirst();

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = ValidationRules.withoutSeparators(phone); }
    public DeliveryMode getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(DeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAddressComplement() { return addressComplement; }
    public void setAddressComplement(String addressComplement) { this.addressComplement = addressComplement; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode == null ? null : postalCode.trim(); }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
