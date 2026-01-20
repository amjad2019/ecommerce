//package com.poc.ecommerce.config;
//
//import org.springframework.context.annotation.*;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig1 {
//	@Bean
//	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//		
//		http
//        .csrf(csrf -> csrf.disable()) // disable CSRF for API
//        .authorizeHttpRequests(auth -> auth
//            // Swagger endpoints open
//            .requestMatchers(
//                    "/swagger-ui/**",
//                    "/swagger-ui.html",
//                    "/v3/api-docs/**"
//            ).permitAll()
//            
//            // Auth endpoints open (for login/register)
//            .requestMatchers("/api/auth/**").permitAll()
//            
//            // All other endpoints require authentication
//            .anyRequest().permitAll()
//        );
//		
////		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
////				.requestMatchers("/api/auth/**", "/swagger-ui/**").permitAll().anyRequest().authenticated());
//		return http.build();
//	}
//}
