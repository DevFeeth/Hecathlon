package com.spring.henallux.hecathlon.controller;

import com.spring.henallux.hecathlon.model.Sport;
import com.spring.henallux.hecathlon.services.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

/** Data needed by the layout (header menu, footer) on every page. */
@ControllerAdvice
public class GlobalModelAttributes {

    private final SportService sportService;

    @Autowired
    public GlobalModelAttributes(SportService sportService) {
        this.sportService = sportService;
    }

    @ModelAttribute("sports")
    public List<Sport> sports() {
        return sportService.getSports();
    }

}
