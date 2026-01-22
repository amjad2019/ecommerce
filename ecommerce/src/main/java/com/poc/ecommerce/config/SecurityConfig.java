package com.poc.ecommerce.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.poc.ecommerce.security.CustomUserDetailsService;
import com.poc.ecommerce.security.JwtAuthenticationEntryPoint;
import com.poc.ecommerce.security.JwtAuthenticationFilter;
import com.poc.ecommerce.security.JwtTokenProvider;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;

@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtTokenProvider tokenProvider;

    private static final String[] PUBLIC_URLS = {
            "/",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/auth/**",
            "/api/public/**",
            "/error",
            "/h2-console/**",
            "/actuator/health",
            "/actuator/info"
    };

    /* ===================== SECURITY FILTER ===================== */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll()
            );

        return http.build();
    }
    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .csrf(csrf -> csrf.disable())
//            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
//            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .headers(headers -> headers
//                    .frameOptions(frame -> frame.sameOrigin())
//                    .httpStrictTransportSecurity(hsts -> hsts
//                            .includeSubDomains(true)
//                            .maxAgeInSeconds(31536000))
//            )
//            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers(PUBLIC_URLS).permitAll()
//
//                    // Products
//                    .requestMatchers(HttpMethod.GET, "/api/products/**")
//                        .hasAnyRole("USER", "PREMIUM_USER", "ADMIN")
//                    .requestMatchers("/api/products/**")
//                        .hasRole("ADMIN")
//
//                    // Orders
//                    .requestMatchers(HttpMethod.POST, "/api/orders/**")
//                        .hasAnyRole("USER", "PREMIUM_USER", "ADMIN")
//                    .requestMatchers(HttpMethod.GET, "/api/orders/user/**")
//                        .hasAnyRole("USER", "PREMIUM_USER", "ADMIN")
//                    .requestMatchers("/api/orders/**")
//                        .hasRole("ADMIN")
//
//                    // Users
//                    .requestMatchers("/api/users/profile")
//                        .authenticated()
//                    .requestMatchers("/api/users/admin/**")
//                        .hasRole("ADMIN")
//
//                    // Categories
//                    .requestMatchers(HttpMethod.GET, "/api/categories/**")
//                        .permitAll()
//                    .requestMatchers("/api/categories/**")
//                        .hasRole("ADMIN")
//
//                    // Payments
//                    .requestMatchers("/api/payments/**")
//                        .hasAnyRole("USER", "PREMIUM_USER", "ADMIN")
//
//                    // Reviews
//                    .requestMatchers(HttpMethod.GET, "/api/reviews/**")
//                        .permitAll()
//                    .requestMatchers(HttpMethod.POST, "/api/reviews/**")
//                        .authenticated()
//
//                    // Admin
//                    .requestMatchers("/api/admin/**")
//                        .hasRole("ADMIN")
//
//                    .anyRequest().authenticated()
//            )
//            .authenticationProvider(authenticationProvider())
//            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }

    /* ===================== AUTH ===================== */

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* ===================== CORS ===================== */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:4200"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /* ===================== SWAGGER ===================== */

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce API")
                        .version("v1.0.0")
                        .description("Secure E-Commerce Backend"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
