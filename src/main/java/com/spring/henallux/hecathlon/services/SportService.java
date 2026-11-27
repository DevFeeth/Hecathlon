package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.model.Sport;
import org.springframework.stereotype.Service;

import java.util.List;

// shortcut: hardcoded list (photos: see static/images/CREDITS.md), move to an entity/repository/DAO when sports are stored in the database
@Service
public class SportService {

    private final List<Sport> sports = List.of(
            new Sport("running", "/images/sports/running.jpg"),
            new Sport("football", "/images/sports/football.jpg"),
            new Sport("cycling", "/images/sports/cycling.jpg")
    );

    public List<Sport> getSports() {
        return sports;
    }

}
