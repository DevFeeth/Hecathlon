package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.model.Sport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// shortcut: hardcoded list (photos: see static/images/CREDITS.md), move to an entity/repository/DAO when sports are stored in the database
@Service
public class SportService {

    private final List<Sport> sports = List.of(
            new Sport("running", "/images/sports/running.jpg", List.of("shoes", "clothing", "accessories")),
            new Sport("football", "/images/sports/football.jpg", List.of("shoes", "balls", "clothing")),
            new Sport("cycling", "/images/sports/cycling.jpg", List.of("bikes", "helmets", "gear"))
    );

    public List<Sport> getSports() {
        return sports;
    }

    public Optional<Sport> findById(String id) {
        return sports.stream().filter(sport -> sport.getId().equals(id)).findFirst();
    }

}
