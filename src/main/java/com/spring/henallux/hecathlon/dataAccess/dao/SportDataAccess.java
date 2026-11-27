package com.spring.henallux.hecathlon.dataAccess.dao;

import com.spring.henallux.hecathlon.model.Sport;

import java.util.List;

public interface SportDataAccess {
    List<Sport> findAll();

    Sport findById(String id);
}
