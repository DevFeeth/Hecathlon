package com.spring.henallux.hecathlon.model;

import java.util.List;

public final class ValidationRules {
    public static final String PHONE = "^[0-9]{9,15}$";
    public static final String POSTAL_CODE = "^[0-9]{4,5}$";
    public static final String EMAIL = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    public static final String PASSWORD = "^(?=.*[A-Z])(?=.*[0-9]).*$";
    public static final List<String> COUNTRIES = List.of("Belgique", "France", "Luxembourg");

    private ValidationRules() {}

    public static String withoutSeparators(String phone) {
        return phone == null ? null : phone.replaceAll("[\\s./()-]", "");
    }
}
