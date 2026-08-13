package com.gestionInventario.security;

//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Descomentar cuando implementen seguridad
/*
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
*/

@Configuration
// @EnableWebSecurity
public class SecurityConfig {

    /*
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(login -> login.disable())
            .httpBasic(basic -> {})

            .authorizeHttpRequests(auth -> auth

                // Rutas públicas
                .requestMatchers(HttpMethod.POST, "/api/usuarios/registrar").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()

                // Productos
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                // Rutas protegidas
                .requestMatchers("/api/**").authenticated()

                .anyRequest().authenticated()
            );

        return http.build();
    }
    */

}