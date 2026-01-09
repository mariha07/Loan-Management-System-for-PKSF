package com.example.loanManage.controller;


import com.example.loanManage.dto.CreateOfficerRequest;
import com.example.loanManage.entity.User;
import com.example.loanManage.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //handle http rqt,this class as a Controller where every method returns data(JSON)instead of a HTML page.
@RequestMapping("/api/admin")// Sets the base URL path for all endpoints in this class
@CrossOrigin(origins = "*") //for frontend fetch from file or another port,Allows different websites
public class AdminController {

    private final UserService userService;

    // Constructor that connects (injects) the UserService into this controller.
    public AdminController(UserService userService) {
        this.userService = userService;
    }


    // POST /api/admin/officers
    @PostMapping("/officers")
    // @RequestBody converts the incoming JSON data from the user into a Java object (request)
    public ResponseEntity<User> createOfficer(@RequestBody CreateOfficerRequest request) {
        User created = userService.createOfficer(request);
        //Removes the password from the object so it's not sent back in the response.
        created.setPassword(null); // don't expose password back
        // Returns a "200 OK" status along with the newly created user data in JSON format.
        return ResponseEntity.ok(created);
    }
}
