package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.model.RegistrationForm;
import com.spring.henallux.hecathlon.model.ValidationRules;
import com.spring.henallux.hecathlon.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inscription")
public class InscriptionController {
    private final AccountService accountService;

    @Autowired
    public InscriptionController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String form() {
        return "redirect:/login#inscription";
    }

    @PostMapping
    public String send(Model model, @Valid @ModelAttribute("registrationForm") RegistrationForm form, BindingResult errors) {
        if (!errors.hasFieldErrors("confirmPassword") && !form.getConfirmPassword().equals(form.getPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch");
        }
        if (!errors.hasFieldErrors("email") && accountService.isEmailTaken(form.getEmail())) {
            errors.rejectValue("email", "email.taken");
        }
        if (!ValidationRules.COUNTRIES.contains(form.getCountry())) {
            errors.rejectValue("country", "NotBlank");
        }
        if (errors.hasErrors()) {
            model.addAttribute("title", "Hecathlon");
            model.addAttribute("countries", ValidationRules.COUNTRIES);
            return "login";
        }
        accountService.register(form);
        return "redirect:/login?registered";
    }
}
