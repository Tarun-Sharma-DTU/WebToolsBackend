package com.tarun.SpringProject.SpringProject.Repo;

import com.tarun.SpringProject.SpringProject.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(String username);
}