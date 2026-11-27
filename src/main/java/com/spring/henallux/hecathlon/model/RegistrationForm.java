package com.spring.henallux.hecathlon.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public class RegistrationForm {
    @NotBlank @Size(max = 50)
    private String firstName;
    @NotBlank @Size(max = 50)
    private String lastName;
    @NotBlank @Email @Pattern(regexp = ValidationRules.EMAIL) @Size(max = 100)
    private String email;
    @NotBlank @Size(min = 8, max = 100) @Pattern(regexp = ValidationRules.PASSWORD)
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank @Pattern(regexp = ValidationRules.PHONE)
    private String phone;
    @NotBlank @Size(max = 100)
    private String address;
    @Size(max = 100)
    private String addressComplement;
    @NotBlank @Pattern(regexp = ValidationRules.POSTAL_CODE)
    private String postalCode;
    @NotBlank @Size(max = 60)
    private String city;
    @NotBlank
    private String country = ValidationRules.COUNTRIES.getFirst();
    private boolean newsletter;
    @AssertTrue
    private boolean acceptTerms;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = ValidationRules.withoutSeparators(phone); }
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
    public boolean isNewsletter() { return newsletter; }
    public void setNewsletter(boolean newsletter) { this.newsletter = newsletter; }
    public boolean isAcceptTerms() { return acceptTerms; }
    public void setAcceptTerms(boolean acceptTerms) { this.acceptTerms = acceptTerms; }
}
