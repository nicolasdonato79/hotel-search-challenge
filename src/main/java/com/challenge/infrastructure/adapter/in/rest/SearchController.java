package com.challenge.infrastructure.adapter.in.rest;

import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {

    @PostMapping("/search")
    public String create() {
        return "TODO";
    }

    @GetMapping("/count")
    public String count(@RequestParam String searchId) {
        return "TODO";
    }
}
