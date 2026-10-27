package com.spring.henallux.firstspringproject.controller;

import com.spring.henallux.firstspringproject.constants.Constants;
import com.spring.henallux.firstspringproject.model.User;
import com.spring.henallux.firstspringproject.services.GiftService;
import com.spring.henallux.firstspringproject.services.HobbiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@RequestMapping("/gift")
@SessionAttributes({Constants.CURRENT_USER})
public class GiftController {

    private GiftService giftService;
    private HobbiesService hobbiesService;

    @Autowired
    public GiftController(
            GiftService giftService,
            HobbiesService hobbiesService) {
        this.giftService = giftService;
        this.hobbiesService = hobbiesService;
    }

    @GetMapping
    public String home(
            Model model,
            @ModelAttribute(Constants.CURRENT_USER) User currentUser) {
        String gift= giftService.chooseGift(hobbiesService.getHobbyName(currentUser.getHobby()), currentUser.getAge());

        model.addAttribute("gift", gift);

        return "gift";
    }

}
