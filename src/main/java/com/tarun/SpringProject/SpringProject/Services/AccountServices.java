package com.tarun.SpringProject.SpringProject.Services;


import com.tarun.SpringProject.SpringProject.Entity.User;
import com.tarun.SpringProject.SpringProject.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class AccountServices {

    @Autowired
    private UserRepository userRepository;

    public boolean registerUser(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            return false;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
        return true; // User created successfully
    }

    public boolean loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }

    public boolean sendmsg(String reciever, String sender, String message) {
        User user = userRepository.findByUsername(reciever);
        if (user == null) {
            return false;
        }
        if (user.getMessages().containsKey(sender)) {
            user.getMessages().get(sender).add(message);
        } else {
            user.getMessages().put(sender, new ArrayList<>(List.of(message)));
        }
        userRepository.save(user);
        return true;
    }

    public ArrayList<String> getMessages(String username, String client) {
        User  user = userRepository.findByUsername(username);
        Map<String, ArrayList<String>> temp = user.getMessages();
        if (temp.containsKey(client)) {
            ArrayList<String> res = temp.get(client);
            res.addAll(temp.get(client));
            temp.put(client, new ArrayList<>());
            user.setMessages(temp);
            userRepository.save(user);
            return res;
        }else{
            return new ArrayList<>();
        }
    }

    public HashSet<String> getChats(String username) {
        User user = userRepository.findByUsername(username);
        return new HashSet<>(user.getMessages().keySet());
    }

    public boolean deleteClient(String username, String client) {
        User user = userRepository.findByUsername(username);
        Map<String, ArrayList<String>> temp = user.getMessages();
        if (temp.containsKey(client)) {
            temp.remove(client);
            user.setMessages(temp);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }
}