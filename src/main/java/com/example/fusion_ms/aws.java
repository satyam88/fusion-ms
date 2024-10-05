package com.example.fusion_ms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class aws {
    @GetMapping("/aws")
    public String getData() {return  "Welcome to aws Class" ; }
}
