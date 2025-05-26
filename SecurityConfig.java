package com.dreamydayplanner.config;

import com.dreamydayplanner.model.AdminUser;
import com.dreamydayplanner.model.User;
import com.dreamydayplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/user/register", "/user/login").permitAll()
                        .requestMatchers("/vendor/add", "/vendor/edit/**", "/vendor/delete/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/user/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/user/login?logout") // Custom logout page
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                System.out.println("Attempting to load user with email: " + email);
                User user = userService.findByEmail(email);
                if (user == null) {
                    System.out.println("User not found with email: " + email);
                    throw new UsernameNotFoundException("User not found with email: " + email);
                }
                if (user instanceof AdminUser && !((AdminUser) user).isActive()) {
                    System.out.println("User account is not active: " + email);
                    throw new UsernameNotFoundException("User account is not active: " + email);
                }
                String role = (user instanceof AdminUser) ? "ADMIN" : "USER";
                System.out.println("User loaded successfully: " + email + ", Role: " + role + ", Password: " + user.getPassword());
                return new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                );
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println("Authentication successful for: " + authentication.getName());
            User user = userService.findByEmail(authentication.getName());
            if (user instanceof AdminUser) {
                System.out.println("Redirecting to admin dashboard");
                response.sendRedirect("/admin/dashboard");
            } else {
                System.out.println("Redirecting to user profile");
                response.sendRedirect("/user/profile");
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            System.out.println("Authentication failed: " + exception.getMessage());
            response.sendRedirect("/user/login?error");
        };
    }
}