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

    /** 
     * To return UserDetails for Spring Security 
     * Note that the method takes only an ID.
     * The UserDetails interface has methods to get the password.
     * 
     * @param id The ID of the user to be retrieved.
     * @return The user's details.
     * @throws UserNotFoundException If user with the given ID is not found.
    */
    public UserDetails loadUsersByID(Long id) throws UserNotFoundException {
        return users.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User '" + id + "' not found"));
    }

    /** 
     * To return UserDetails for Spring Security
     * Note that the method takes only a username.
     * The UserDetails interface has methods to get the password.
     * 
     * @param username The username of the user to be retrieved.
     * @return The user's details
     * @throws UserNotFoundException If user with the given username is not found.
     */
    @Override 
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException{
        return users.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
    
}