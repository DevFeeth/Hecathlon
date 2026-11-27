package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.dataAccess.dao.UserDataAccess;
import com.spring.henallux.hecathlon.model.AddressForm;
import com.spring.henallux.hecathlon.model.PasswordForm;
import com.spring.henallux.hecathlon.model.RegistrationForm;
import com.spring.henallux.hecathlon.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);

    @Mock
    private UserDataAccess userDataAccess;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(userDataAccess, encoder);
    }

    private User storedUser() {
        User user = new User();
        user.setEmail("alex@example.com");
        user.setPassword(encoder.encode("Ancien123"));
        return user;
    }

    @Test
    void registerHashesThePasswordAndKeepsTheAddress() {
        RegistrationForm form = new RegistrationForm();
        form.setFirstName(" Alex ");
        form.setLastName("Martin");
        form.setEmail(" Alex@Example.com ");
        form.setPassword("Motdepasse1");
        form.setPhone("0470 12 34 56");
        form.setAddress("Rue de Fer 12");
        form.setAddressComplement(" ");
        form.setPostalCode("5000");
        form.setCity("Namur");
        form.setCountry("Belgique");
        when(userDataAccess.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.register(form);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userDataAccess).save(saved.capture());
        User user = saved.getValue();
        assertEquals("alex@example.com", user.getEmail());
        assertNotEquals("Motdepasse1", user.getPassword());
        assertTrue(encoder.matches("Motdepasse1", user.getPassword()));
        assertEquals("Alex", user.getFirstName());
        assertEquals("0470123456", user.getPhone());
        assertNull(user.getAddressComplement());
        assertEquals(LocalDate.now(), user.getCreatedAt());
    }

    @Test
    void emailIsTakenWhenAUserExists() {
        when(userDataAccess.findByEmail("alex@example.com")).thenReturn(storedUser());

        assertTrue(accountService.isEmailTaken("alex@example.com"));
        assertFalse(accountService.isEmailTaken("autre@example.com"));
    }

    @Test
    void passwordChangeNeedsTheCurrentPassword() {
        User user = storedUser();
        when(userDataAccess.findByEmail("alex@example.com")).thenReturn(user);
        PasswordForm form = new PasswordForm();
        form.setCurrentPassword("Mauvais123");
        form.setNewPassword("Nouveau123");

        assertFalse(accountService.changePassword("alex@example.com", form));
        verify(userDataAccess, never()).save(any());

        form.setCurrentPassword("Ancien123");
        assertTrue(accountService.changePassword("alex@example.com", form));
        assertTrue(encoder.matches("Nouveau123", user.getPassword()));
        verify(userDataAccess).save(user);
    }

    @Test
    void addressIsSaved() {
        User user = storedUser();
        when(userDataAccess.findByEmail("alex@example.com")).thenReturn(user);
        when(userDataAccess.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AddressForm form = new AddressForm();
        form.setPhone("0470.12.34.56");
        form.setAddress(" Rue de Fer 12 ");
        form.setPostalCode("5000");
        form.setCity("Namur");
        form.setCountry("Belgique");

        accountService.updateAddress("alex@example.com", form);

        assertEquals("0470123456", user.getPhone());
        assertEquals("Rue de Fer 12", user.getAddress());
        assertTrue(user.hasAddress());
    }

    @Test
    void unknownLoggedInUserIsAnError() {
        when(userDataAccess.findByEmail("fantome@example.com")).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> accountService.getUser("fantome@example.com"));
    }
}
