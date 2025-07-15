package com.example.journalapp.controller;

import com.example.journalapp.Services.UserService;
import com.example.journalapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;



    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("id/{myid}")
    public ResponseEntity<User> getById(@PathVariable String myid) {
        User jr = userService.getByID(myid);
        if (jr != null) {
            return new ResponseEntity<>(jr, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }




    @DeleteMapping("id/{myid}")
    public boolean deleteentry(@PathVariable String myid) {

        userService.deleteById(myid);
        return true;
    }


    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody User user){

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String userName=authentication.getName();
        User userInDb=userService.findByUserName(user.getUserName());
        if(userInDb != null){
            userInDb.setUserName(user.getUserName());
            userInDb.setPassword(user.getPassword());
            userService.saveNewUser(userInDb);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
