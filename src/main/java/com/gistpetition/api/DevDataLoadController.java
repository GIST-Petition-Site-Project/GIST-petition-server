package com.gistpetition.api;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RequiredArgsConstructor
@RestController
public class DevDataLoadController {
    private final DataLoader dataLoader;

    @GetMapping("/data/setting")
    public ResponseEntity<Void> dataSetting() {
        dataLoader.loadData();
        return ResponseEntity.ok().build();
    }

}
