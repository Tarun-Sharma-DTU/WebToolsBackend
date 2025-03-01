package com.tarun.SpringProject.SpringProject.Controllers;

import com.tarun.SpringProject.SpringProject.Services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost", "https://hidenrevealapp.netlify.app", "https://iamscrappy.netlify.app/"})
public class Account {

    @Autowired
    private AccountServices accountServices;

    @PostMapping("/createUser")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody Map<String, String> request) throws IOException {
        try {
            if (accountServices.registerUser(request.get("username"), request.get("password"))) {
                return new ResponseEntity<>(Map.of("response", "User created"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("response", "User Already Exist"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("response", "User Not created"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) throws IOException {
        try {
            if (accountServices.loginUser(request.get("username"), request.get("password"))) {
                return new ResponseEntity<>(Map.of("response", "User logged in"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("response", "User Not logged in"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("response", "User Not logged in"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}