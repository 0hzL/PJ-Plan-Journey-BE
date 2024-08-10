package com.pj.planjourney.global.jwt;

import com.pj.planjourney.domain.user.entity.User;
import com.pj.planjourney.domain.user.repository.UserRepository;
import com.pj.planjourney.global.auth.service.UserDetailsImpl;
import com.pj.planjourney.global.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {
    @Resource(name="userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationProvider(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = token.getName();
        String password = (String) token.getCredentials();
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);


        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException(userDetails.getUsername() + "Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
