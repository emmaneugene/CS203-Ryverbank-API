package com.csdg1t3.ryverbankapi.security;

import com.csdg1t3.ryverbankapi.user.*;

import org.springframework.stereotype.Component;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * Convenience class that performs user role authentication. It is able to retrieve the UserDetails
 * given the authorization credentials provided in HTTP requests
 */

@Component
public class UserAuthenticator {
    private UserRepository userRepo;

    public UserAuthenticator(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public UserDetails getCurrentUserDetails() {
        return (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean idMatchesAuthenticatedUser(Long id) {
        Optional<User> resultAtId = userRepo.findById(id);

        if (!resultAtId.isPresent())
            return false;

        String usernameAtId = resultAtId.get().getUsername();
        UserDetails uDetails = getCurrentUserDetails();
        return uDetails.getUsername().equals(usernameAtId);
    }
}
