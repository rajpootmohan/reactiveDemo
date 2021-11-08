package com.example.reactiveDemo.serviceImpl;

import com.example.reactiveDemo.configuration.GitHubServiceGenerator;
import com.example.reactiveDemo.model.User;
import com.example.reactiveDemo.service.UserServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserServiceImpl{

    @Autowired
    GitHubServiceGenerator gitHubServiceGenerator;

    public List<User> getUsers(int per_page, int page) throws IOException {
        UserServiceApi userServiceApi = gitHubServiceGenerator.getService(UserServiceApi.class); ;
        List<User> users = userServiceApi.getUsers(per_page, page).execute().body();
        return users;
    }

//    @GET("/users/{username}")
//    @Override
//    public Call<User> getUser(@Path("username") String username) {
//        Call<User> callSync = service.getUser("eugenp");
//        User user = null;
//        return callSync;
//        try {
//            Response<User> response = callSync.execute();
//            user = response.body();
//            System.out.println("user is : " + user.toString());
//            return user;
//        } catch (Exception ex) {
//            System.out.println("Exception : " + ex);
//        }
//        return user;
//    }

}
