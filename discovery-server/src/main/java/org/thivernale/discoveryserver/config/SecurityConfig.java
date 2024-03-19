package org.thivernale.discoveryserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${eureka.username:eureka}")
    private String username;
    @Value("${eureka.password:password}")
    private String password;
    @Value("USER")
    private String[] roles;

    @Bean
    public UserDetailsService userDetails() {
        UserDetails userDetails = User.withDefaultPasswordEncoder()
            .username(username)
            .password(password)
            .roles(roles)
            .build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public SecurityFilterChain config(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry.anyRequest()
                .authenticated())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
