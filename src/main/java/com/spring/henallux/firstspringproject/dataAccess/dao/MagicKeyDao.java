package com.spring.henallux.firstspringproject.dataAccess.dao;

import com.spring.henallux.firstspringproject.dataAccess.entity.MagicKeyEntity;
import com.spring.henallux.firstspringproject.dataAccess.repository.MagicKeyRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MagicKeyDao implements MagicKeyDataAccess{

    private final MagicKeyRepository magicKeyRepository;

    @Autowired
    public MagicKeyDao(MagicKeyRepository magicKeyRepository) {
        this.magicKeyRepository = magicKeyRepository;
    }

    @Override
    public ArrayList<String> getMagicKeys() {
        List<MagicKeyEntity> entities = magicKeyRepository.findAll();
        ArrayList<String> keys = new ArrayList<>();
        for (MagicKeyEntity entity : entities) {
            keys.add(entity.getMagicValue());
        }
        return keys;
    }

}
