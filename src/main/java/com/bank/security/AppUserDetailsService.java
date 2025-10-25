package com.bank.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bank.model.AppUser;
import com.bank.repository.AppUserRepository;

@Service
public class AppUserDetailsService
implements UserDetailsService {
    @Autowired
    private AppUserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser u = this.repo.findByUsername(username).orElse(null);
        if (u == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String role = u.getRole();
        String authority = role != null && role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return new User(u.getUsername(), u.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(authority)));
    }
}
