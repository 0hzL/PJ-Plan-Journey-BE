package com.pj.planjourney.global.auth.service;

import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user by email: " + email);
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.error("User not found for email: " + email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User user = userOptional.get();
        return new UserDetailsImpl(user, List.of());
    }
}
