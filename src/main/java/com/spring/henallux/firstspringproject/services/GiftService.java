package com.spring.henallux.firstspringproject.services;

import org.springframework.stereotype.Service;

@Service
public class GiftService {

    public String chooseGift(String hobby, Integer age){
        if (age < 5) {
            return "a puzzle about " + hobby;
        } else if (age <= 10) {
            return "a DVD about " + hobby;
        } else {
            return "a book about " + hobby;
        }
    }

}
