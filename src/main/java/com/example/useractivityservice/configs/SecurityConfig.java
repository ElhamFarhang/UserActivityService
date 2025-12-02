package com.example.useractivityservice.configs;

import com.example.useractivityservice.converters.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//--------------------- Elham - SecurityConfig --------------
@Configuration
public class SecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;

    @Autowired
    public SecurityConfig(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/activity/playmusic/**").hasRole("edufy_User")
                                .requestMatchers("/activity/playpodcast/**").hasRole("edufy_User")
                                .requestMatchers("/activity/playvideo/**").hasRole("edufy_User")
                                .requestMatchers("/activity/addactivity/**").hasRole("edufy_User")
                                .requestMatchers("/activity/activities/**").hasRole("edufy_Admin")
//                                .requestMatchers("/activity/mostplayed/**").hasRole("edufy_Admin")
                                .requestMatchers("/activity/stats/getRecommendations/{mediaType}").hasRole("edufy_User")
                                .requestMatchers("/activity/stats/getHistory/{mediaType}").hasRole("edufy_User")
                                .requestMatchers("/activity/stats/getAllHistoryBetween").hasRole("edufy_User")
                                .requestMatchers("/activity/stats/allusersmostplayed/{mediaType}").hasAnyRole("edufy_User", "edufy_Admin")
                                .requestMatchers("/activity/stats/usermostplayed/{mediaType}").hasRole("edufy_User")
                                .requestMatchers("/activity/stats/usermostplayedAllTypes").hasRole("edufy_User")
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2
                                .jwt(jwt->jwt.jwtAuthenticationConverter(jwtAuthConverter))
                );
        return http.build();
    }



}
