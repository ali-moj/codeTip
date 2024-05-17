package com.jvpars.codetip.security;

import com.jvpars.codetip.domain.AppUser;
import com.jvpars.codetip.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private AppUserRepository repository;

    public CustomUserDetailsService(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = this.repository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("Username: " + username + " not found");
        return user;

    }
}