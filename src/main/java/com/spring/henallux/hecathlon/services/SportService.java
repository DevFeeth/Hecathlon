package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.model.Sport;
import org.springframework.stereotype.Service;

import java.util.List;

// shortcut: hardcoded list, move to an entity/repository/DAO when sports are stored in the database
@Service
public class SportService {

    private final List<Sport> sports = List.of(
            new Sport("running", null),
            new Sport("football", null),
            new Sport("cycling", null)
    );

    public List<Sport> getSports() {
        return sports;
    }

}
