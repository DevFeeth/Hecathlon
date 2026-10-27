package com.spring.henallux.firstspringproject.services;

import com.spring.henallux.firstspringproject.model.Hobby;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HobbiesService {

    private ArrayList<Hobby> hobbies;

    public HobbiesService() {
        hobbies = new ArrayList<>();

        hobbies.add(new Hobby("Sport", "Sport"));
        hobbies.add(new Hobby("Nature", "Nature"));
        hobbies.add(new Hobby("Reading", "Reading"));
        hobbies.add(new Hobby("Music", "Music"));
    }

    public ArrayList<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(ArrayList<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public String getHobbyName(String id) {
        for (Hobby h : hobbies) {
            if (h.getId().equals(id)){
                return h.getName();
            }
        }
        return "None";
    }

}
