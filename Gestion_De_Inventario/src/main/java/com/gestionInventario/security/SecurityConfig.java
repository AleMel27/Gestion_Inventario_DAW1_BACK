package com.gestionInventario.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMINISTRADOR = "ADMINISTRADOR";
    private static final String ALMACENERO = "ALMACENERO";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                .requestMatchers("/api/usuarios/**").hasRole(ADMINISTRADOR)
                .requestMatchers("/api/roles/**").hasRole(ADMINISTRADOR)

                .requestMatchers(HttpMethod.DELETE, "/api/proveedor/**").denyAll()
                .requestMatchers(HttpMethod.PATCH, "/api/proveedor/**").denyAll()
                .requestMatchers(HttpMethod.POST, "/api/inventario/**").denyAll()
                .requestMatchers(HttpMethod.PUT, "/api/inventario/**").denyAll()
                .requestMatchers(HttpMethod.PATCH, "/api/inventario/**").denyAll()
                .requestMatchers(HttpMethod.DELETE, "/api/inventario/**").denyAll()

                .requestMatchers(HttpMethod.DELETE, "/api/producto/**").hasRole(ADMINISTRADOR)
                .requestMatchers(HttpMethod.PATCH, "/api/producto/**").hasRole(ADMINISTRADOR)
                .requestMatchers(HttpMethod.GET, "/api/producto/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers(HttpMethod.POST, "/api/producto/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers(HttpMethod.PUT, "/api/producto/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)

                .requestMatchers(HttpMethod.GET, "/api/categorias/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers("/api/categorias/**").hasRole(ADMINISTRADOR)

                .requestMatchers(HttpMethod.GET, "/api/unidades-medida/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers("/api/unidades-medida/**").hasRole(ADMINISTRADOR)

                .requestMatchers(HttpMethod.GET, "/api/almacen/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers("/api/almacen/**").hasRole(ADMINISTRADOR)

                .requestMatchers(HttpMethod.GET, "/api/proveedor/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers(HttpMethod.POST, "/api/proveedor/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers(HttpMethod.PUT, "/api/proveedor/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)

                .requestMatchers(HttpMethod.GET, "/api/comprobante/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers("/api/comprobante/**").hasRole(ADMINISTRADOR)

                .requestMatchers("/api/compras/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)

                .requestMatchers(HttpMethod.GET, "/api/inventario/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)

                .requestMatchers(HttpMethod.GET, "/api/movimiento/**").hasAnyRole(ADMINISTRADOR, ALMACENERO)
                .requestMatchers("/api/movimiento/**").hasRole(ADMINISTRADOR)

                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
