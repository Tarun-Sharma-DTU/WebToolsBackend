package com.tarun.SpringProject.SpringProject.Controllers;


import com.tarun.SpringProject.SpringProject.Services.AccountServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://16.171.134.100","http://16.171.134.100:3000","http://16.171.134.100:3001","http://16.171.134.100:3002","http://16.171.134.100:3003", "http://hidenreveal.duckdns.org", "http://hidenreveal.duckdns.org:3000", "http://hidenreveal.duckdns.org:3001", "http://hidenreveal.duckdns.org:3002", "http://hidenreveal.duckdns.org:3003"})
public class Messages {

    @Autowired
    private AccountServices messagesServices;

    @PostMapping("/sendMessage")
    public ResponseEntity<Map<String, String>> sendmsg(@RequestBody Map<String, String> request) {
        try {
            if (messagesServices.sendmsg(request.get("reciever"), request.get("sender"), request.get("message"))) {
                return new ResponseEntity<>(Map.of("response", "Message sent"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("response", "Message Not sent"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("response", "Message Not sent"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getMessages")
    public ResponseEntity<Map<String, ArrayList<String>>> getMessages(@RequestBody Map<String, String> request) {
        try {

            ArrayList<String> res = messagesServices.getMessages(request.get("reciever"), request.get("client"));
            if (res.size() > 0) {
                return new ResponseEntity<>(Map.of("response", res), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("response", new ArrayList<>()), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("response", new ArrayList<>()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/getChats")
    public ResponseEntity<Map<String, HashSet<String>>> getChats(@RequestBody Map<String, String> request) {
        try {
            HashSet<String> res = messagesServices.getChats(request.get("username"));
            if (res.size() > 0) {
                return new ResponseEntity<>(Map.of("response", res), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("response", new HashSet<>()), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("response", new HashSet<>()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deleteClient")
    public ResponseEntity<Map<String, String>> deleteClient(@RequestBody Map<String, String> request) {
        try {
            if (messagesServices.deleteClient(request.get("username"), request.get("client"))) {
                return new ResponseEntity<>(Map.of("response", "Client deleted"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Map.of("response", "Client Not deleted"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("response", "Client Not deleted"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
