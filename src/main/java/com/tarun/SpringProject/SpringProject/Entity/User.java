package com.tarun.SpringProject.SpringProject.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String username;
    private String password;

    private Map<String, ArrayList<String>> messages = new HashMap<>();

    public Map<String, ArrayList<String>> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, ArrayList<String>> messages) {
        this.messages = messages;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}