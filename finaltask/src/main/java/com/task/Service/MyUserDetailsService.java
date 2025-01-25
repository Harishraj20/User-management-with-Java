package com.task.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.task.Model.User;
import com.task.Model.UserPrincipal;
import com.task.Repository.UserRepository;
@Component
public class MyUserDetailsService implements UserDetailsService {

    protected static final Logger logger = LogManager.getLogger();

    private final UserRepository repo;

    @Autowired
    public MyUserDetailsService(UserRepository repo) {
        logger.info("My User Details Service is created Successfully!");
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);

        User user = repo.checkUserByEmailid(username);

        if (user == null) {
            logger.warn("User with email '{}' not found in the database.", username);
            throw new UsernameNotFoundException("User with email '" + username + "' not found");
        }

        logger.info("User found: {}", user.getEmailId());
        logger.info("Fetched user details: {}", user);

        return new UserPrincipal(user); //object of userdetails
    }

}
