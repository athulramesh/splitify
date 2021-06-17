package com.splitify.splitify.security.service;

import com.splitify.splitify.security.domain.UserEntity;
import com.splitify.splitify.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = repository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Not Found: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getCredential().getPassword(), new ArrayList<>());
    }

    public int addUser(UserEntity user) {
        String encodedPass = passwordEncoder.encode(user.getCredential().getPassword());
        user.getCredential().setPassword(encodedPass);
        return repository.save(user).getUserId();
    }
}
