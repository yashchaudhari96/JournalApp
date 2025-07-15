package com.example.journalapp.Services;

import com.example.journalapp.Repository.UserRepository;
import com.example.journalapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userrepo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user=userrepo.findByUserName(userName);
        if(user!=null){
            UserDetails userdetails= org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUserName())
                    .password(user.getPassword())
                    .roles(user.getRoles().toArray(new String[0]))
                    .build();
            return userdetails;
        }
        throw new UsernameNotFoundException("User not found "+userName);

    }
}
