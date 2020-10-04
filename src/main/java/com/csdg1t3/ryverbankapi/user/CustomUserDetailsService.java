package com.csdg1t3.ryverbankapi.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository users;
    
    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    /** To return a UserDetails for Spring Security 
     *  Note that the method takes only a username.
        The UserDetails interface has methods to get the password.
    */
    public UserDetails loadUsersByID(Long id)  throws UserNotFoundException {
        return users.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User '" + id + "' not found"));
    }

    @Override 
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException{
        return users.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User '" + username + "' not found"));
    }
    
}