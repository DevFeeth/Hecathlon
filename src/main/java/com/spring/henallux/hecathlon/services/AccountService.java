package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.dataAccess.dao.UserDataAccess;
import com.spring.henallux.hecathlon.model.AddressForm;
import com.spring.henallux.hecathlon.model.PasswordForm;
import com.spring.henallux.hecathlon.model.ProfileForm;
import com.spring.henallux.hecathlon.model.RegistrationForm;
import com.spring.henallux.hecathlon.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class AccountService {
    private final UserDataAccess userDataAccess;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(UserDataAccess userDataAccess, PasswordEncoder passwordEncoder) {
        this.userDataAccess = userDataAccess;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(String email) {
        User user = userDataAccess.findByEmail(email);
        if (user == null) {
            throw new IllegalStateException("Logged-in user not found: " + email);
        }
        return user;
    }

    public boolean isEmailTaken(String email) {
        return userDataAccess.findByEmail(email) != null;
    }

    public User register(RegistrationForm form) {
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setFirstName(form.getFirstName().trim());
        user.setLastName(form.getLastName().trim());
        user.setPhone(form.getPhone());
        user.setAddress(form.getAddress().trim());
        user.setAddressComplement(StringUtils.hasText(form.getAddressComplement()) ? form.getAddressComplement().trim() : null);
        user.setPostalCode(form.getPostalCode());
        user.setCity(form.getCity().trim());
        user.setCountry(form.getCountry());
        user.setNewsletter(form.isNewsletter());
        user.setCreatedAt(LocalDate.now());
        return userDataAccess.save(user);
    }

    public User updateProfile(String email, ProfileForm form) {
        User user = getUser(email);
        user.setFirstName(form.getFirstName().trim());
        user.setLastName(form.getLastName().trim());
        user.setNewsletter(form.isNewsletter());
        return userDataAccess.save(user);
    }

    public boolean changePassword(String email, PasswordForm form) {
        User user = getUser(email);
        if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userDataAccess.save(user);
        return true;
    }

    public User updateAddress(String email, AddressForm form) {
        User user = getUser(email);
        user.setPhone(form.getPhone().trim());
        user.setAddress(form.getAddress().trim());
        user.setAddressComplement(StringUtils.hasText(form.getAddressComplement()) ? form.getAddressComplement().trim() : null);
        user.setPostalCode(form.getPostalCode().trim());
        user.setCity(form.getCity().trim());
        user.setCountry(form.getCountry());
        return userDataAccess.save(user);
    }

}
