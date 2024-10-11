package com.example.fusion_ms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class mlops {
    @GetMapping("/mlops")
    public String getData() {return  "Welcome to mlops learn aws sagemaker kubeflow mlflow tensorflow  Class" ; }
}