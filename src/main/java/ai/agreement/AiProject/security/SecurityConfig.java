package ai.agreement.AiProject.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origin.dev}")
    private String devAllowedOrigin;

    @Value("${cors.allowed-origin.prod}")
    private String prodAllowedOrigin;

    private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;

    private final ApiKeyValidatorFilter apiKeyValidatorFilter;

    @Bean
    public SecurityFilterChain defalutSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.securityContext((contextConfigurer) -> contextConfigurer
                .requireExplicitSave(false))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(corsConfigurer -> corsConfigurer
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration config = new CorsConfiguration();
                                config.setAllowedOrigins(Arrays.asList(
                                        "http://localhost:3000",
                                        devAllowedOrigin,
                                        prodAllowedOrigin

                                ));
                                config.setAllowedMethods(Collections.singletonList("*"));
                                config.setAllowCredentials(true);
                                config.setAllowedHeaders(Collections.singletonList("*"));
                                config.setExposedHeaders(List.of(SecurityConst.AUTH_HEADER)); // 클라이언트에서 Authorization header 에 접근 가능
                                config.setMaxAge(3600L);
                                return config;
                            }
                        }))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requestMatcherRegistry) -> requestMatcherRegistry
                        .requestMatchers("/api/v1/test/**").authenticated()
                        .requestMatchers("/api/v1/chat/**").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(exceptionHandlingConfig -> exceptionHandlingConfig
                .authenticationEntryPoint(apiAuthenticationEntryPoint))
                .addFilterBefore(apiKeyValidatorFilter, BasicAuthenticationFilter.class)
                .httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer
                        .authenticationEntryPoint(apiAuthenticationEntryPoint));
        return httpSecurity.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
