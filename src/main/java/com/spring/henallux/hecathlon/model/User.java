package com.spring.henallux.hecathlon.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class User implements UserDetails {

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    private List<Authority> authorities = List.of();
    private Boolean accountNonExpired = true;
    private Boolean accountNonLocked = true;
    private Boolean credentialsNonExpired = true;
    private Boolean enabled = true;

    public User() {}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    // Spring Security's "username" is the email address
    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return accountNonExpired; }

    @Override
    public boolean isAccountNonLocked() { return accountNonLocked; }

    @Override
    public boolean isCredentialsNonExpired() { return credentialsNonExpired; }

    @Override
    public boolean isEnabled() { return enabled; }

    public void setPassword(String password) { this.password = password; }
    public void setAuthorities(List<Authority> authorities) { this.authorities = authorities; }
    public void setAccountNonExpired(Boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }
    public void setAccountNonLocked(Boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
    public void setCredentialsNonExpired(Boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    // stored trimmed and lower-case so "Alex@Mail.com" and "alex@mail.com" are the same account
    public void setEmail(String email) { this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT); }
}
