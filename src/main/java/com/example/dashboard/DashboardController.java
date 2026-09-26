package com.example.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @GetMapping("/api/dashboard")
    public String dashboard() {
        return "Operational Dashboard is running successfully";
    }
}