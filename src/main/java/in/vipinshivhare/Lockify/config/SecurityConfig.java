package in.vipinshivhare.Lockify.config;

import java.util.List;
import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import in.vipinshivhare.Lockify.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Value("${app.allowed.origins:http://localhost:5173,https://lockify-client.netlify.app}")
    private String allowedOriginsProp;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()) // Enables Cross-Origin Resource Sharing (CORS), allowing requests from different domains
                .csrf(AbstractHttpConfigurer::disable)//  Disables Cross-Site Request Forgery (CSRF) protection. This is a common practice for stateless REST APIs that use tokens (like JWT)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/health", "/login", "/register", "/send-reset-otp", "/reset-password", "/logout")
                        .permitAll()
                        .anyRequest().authenticated()) //All other endpoints require authentication.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//No session will be stored. Every request must have a fresh JWT.
                .logout(AbstractHttpConfigurer::disable)//  Disables Spring Security's default logout handling, as you likely have a custom logout endpoint (/logout)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)//Adds your jwtRequestFilter before Spring’s default authentication filter, so JWT is validated early.
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint));//If the user is unauthenticated, customAuthenticationEntryPoint sends a proper error response.
            return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter(){
        return new CorsFilter(corsConfigurationSource());
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOriginsProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*") ); // Allow all headers for flexibility
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception { // help for making a secure login API
        return authenticationConfiguration.getAuthenticationManager();
    }


}
