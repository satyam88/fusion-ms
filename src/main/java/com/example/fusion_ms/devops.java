package com.example.fusion_ms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class devops {
    @GetMapping("/devops")
    public String getData() {return  "Welcome to Devops Class" ; }
}

