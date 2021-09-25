package com.example.gistcompetitioncnserver.registration;


import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class RegistrationEmailValidator implements Predicate<String> {

    @Override
    public boolean test(String s) {
        // only can register gist mail
        if(s.contains("@gm.gist.ac.kr")||s.contains("@gist.ac.kr")){
            return true;
        }
        return false;
    }
}
