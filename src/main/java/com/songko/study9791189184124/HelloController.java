package com.songko.study9791189184124;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RequestMapping("/api/v1")
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/samples/list")
    public ResponseEntity<?> list() {
        log.info("list........ ");
        String[] arr = {"AAA", "BBB", "CCC"};
        return ResponseEntity.ok(arr);
    }
}
