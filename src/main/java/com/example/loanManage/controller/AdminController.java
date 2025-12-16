package com.example.loanManage.controller;


import com.example.loanManage.dto.CreateOfficerRequest;
import com.example.loanManage.entity.User;
import com.example.loanManage.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // for frontend fetch from file or another port
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }


    // POST /api/admin/officers
    @PostMapping("/officers")
    public ResponseEntity<User> createOfficer(@RequestBody CreateOfficerRequest request) {
        User created = userService.createOfficer(request);
        // usually you return a DTO, not the entity; but simple version is fine
        created.setPassword(null); // don't expose password back
        return ResponseEntity.ok(created);
    }
}
