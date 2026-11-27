package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.dataAccess.dao.UserDataAccess;
import com.spring.henallux.hecathlon.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final UserDataAccess userDataAccess;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InscriptionController(UserDataAccess userDataAccess, PasswordEncoder passwordEncoder) {
        this.userDataAccess = userDataAccess;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("title", "Hecathlon");
        model.addAttribute("userForm", new User());
        return "inscription";
    }

    @PostMapping
    public String send(Model model, @Valid @ModelAttribute("userForm") User userForm, BindingResult errors) {
        // email is the primary key: saving an existing one would overwrite that account
        if (!errors.hasFieldErrors("email") && userDataAccess.findByEmail(userForm.getEmail()) != null) {
            errors.rejectValue("email", "email.taken");
        }
        if (errors.hasErrors()) {
            model.addAttribute("title", "Hecathlon");
            return "inscription";
        }

        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));
        userDataAccess.save(userForm);

        return "redirect:/login?registered";
    }

}
