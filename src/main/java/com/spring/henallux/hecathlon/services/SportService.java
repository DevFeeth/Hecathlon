package com.spring.henallux.hecathlon.services;

import com.spring.henallux.hecathlon.dataAccess.dao.SportDataAccess;
import com.spring.henallux.hecathlon.model.Sport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SportService {
    private final SportDataAccess sportDataAccess;

    @Autowired
    public SportService(SportDataAccess sportDataAccess) {
        this.sportDataAccess = sportDataAccess;
    }

    public List<Sport> getSports() {
        return sportDataAccess.findAll();
    }

    public Optional<Sport> findById(String id) {
        return Optional.ofNullable(sportDataAccess.findById(id));
    }
}
