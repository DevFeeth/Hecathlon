package com.spring.henallux.firstspringproject.controller;

import com.spring.henallux.firstspringproject.constants.Constants;
import com.spring.henallux.firstspringproject.dataAccess.dao.UserDataAccess;
import com.spring.henallux.firstspringproject.model.Hobby;
import com.spring.henallux.firstspringproject.model.User;
import com.spring.henallux.firstspringproject.services.HobbiesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/inscription")
@SessionAttributes({Constants.CURRENT_USER})
public class InscriptionController {

    private final UserDataAccess userDataAccess;

    @ModelAttribute(Constants.CURRENT_USER)
    public User user(){
        return new User();
    }

    private HobbiesService hobbiesService;

    @Autowired
    public InscriptionController(HobbiesService hobbiesService, UserDataAccess userDataAccess) {
        this.hobbiesService = hobbiesService;
        this.userDataAccess = userDataAccess;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("hobbies", hobbiesService.getHobbies());
        return "userInscription";
    }

    @PostMapping("/send")
    public String send(
            Model model,
            @Valid @ModelAttribute(Constants.CURRENT_USER) User userForm,
            final BindingResult errors) {
        if (errors.hasErrors()) {
            model.addAttribute("hobbies", hobbiesService.getHobbies());
            return "userInscription";
        }

        userDataAccess.save(userForm);

        return "redirect:/gift";
    }

}
