package com.spring.henallux.hecathlon.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressForm {
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
    private String country = DeliveryForm.COUNTRIES.getFirst();

    public static AddressForm of(User user) {
        AddressForm form = new AddressForm();
        form.setPhone(user.getPhone());
        form.setAddress(user.getAddress());
        form.setAddressComplement(user.getAddressComplement());
        form.setPostalCode(user.getPostalCode());
        form.setCity(user.getCity());
        if (user.getCountry() != null) {
            form.setCountry(user.getCountry());
        }
        return form;
    }

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
}
