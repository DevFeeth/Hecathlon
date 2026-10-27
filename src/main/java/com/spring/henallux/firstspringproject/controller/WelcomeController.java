package com.spring.henallux.firstspringproject.controller;

import com.spring.henallux.firstspringproject.dataAccess.dao.MagicKeyDataAccess;
import com.spring.henallux.firstspringproject.model.MagicKeyForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Controller()
@RequestMapping("/hello")
public class WelcomeController {

    private final MagicKeyDataAccess magicKeyDao;

    @Autowired
    public WelcomeController(MagicKeyDataAccess magicKeyDao) {
        this.magicKeyDao = magicKeyDao;
    }

    @GetMapping
    public String home(Model model){
        model.addAttribute("name", "toto");
        model.addAttribute("title", "Welcome page");
        model.addAttribute("magicKeyForm", new MagicKeyForm());
        return "welcome";
    }

    @PostMapping("/send")
    public String send(@RequestParam("magicKey") String magicKey, Model model){
        ArrayList<String> validKeys = magicKeyDao.getMagicKeys();

        if (validKeys.contains(magicKey)){
            return "redirect:/inscription";
        } else {
            return "keyError";
        }
    }
}
