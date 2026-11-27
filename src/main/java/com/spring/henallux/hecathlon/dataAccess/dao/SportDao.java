package com.spring.henallux.hecathlon.dataAccess.dao;

import com.spring.henallux.hecathlon.dataAccess.repository.SportRepository;
import com.spring.henallux.hecathlon.dataAccess.util.CatalogMapper;
import com.spring.henallux.hecathlon.model.Sport;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SportDao implements SportDataAccess {
    private final SportRepository sportRepository;
    private final CatalogMapper catalogMapper;

    @Autowired
    public SportDao(SportRepository sportRepository, CatalogMapper catalogMapper) {
        this.sportRepository = sportRepository;
        this.catalogMapper = catalogMapper;
    }

    @Override
    public List<Sport> findAll() {
        return sportRepository.findAllByOrderByPosition().stream()
                .map(catalogMapper::sportEntityToSportModel)
                .toList();
    }

    @Override
    public Sport findById(String id) {
        return sportRepository.findById(id).map(catalogMapper::sportEntityToSportModel).orElse(null);
    }
}
