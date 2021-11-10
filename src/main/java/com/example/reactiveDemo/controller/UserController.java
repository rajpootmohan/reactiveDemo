//package com.example.reactiveDemo.controller;
//
//import com.example.reactiveDemo.model.User;
//import com.example.reactiveDemo.serviceImpl.UserServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//public class UserController {
//
//    @Autowired
//    private UserServiceImpl userServiceImpl;
//
//    @GetMapping("/gitHubUsers")
//    public List<User> getGitHubUsers() throws IOException {
//        List<User> users = userServiceImpl.getUsers(10, 3);
//        return users;
//    }
//
//}
