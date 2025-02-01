package com.example.demo.config;
import com.example.demo.security.jwt.JWTUtil;
import com.example.demo.security.jwt.JwtAuthenticationFilter;
import com.example.demo.security.oauth.OAuthLoginFailureHandler;
import com.example.demo.security.oauth.OAuthLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;

    // 소셜 로그인
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                            .requestMatchers("/api/permit/**", "/login/**", "/nickname/**", "/home/**").permitAll()
                            .requestMatchers("/favicon.ico", "/static/**", "/api/member/**").permitAll() // 인증 없이 허용
                            .requestMatchers("/payment/**", "/payment/create/**", "payment/approve/**", "payment/redirect/**", "/index.html", "/hello.html/**").permitAll() // 인증 없이 허용 - yoon 테스트
                            .anyRequest().authenticated();
                })
                .oauth2Login(oauth -> {
                    oauth
                            // 여기서 Spring Security가 DefaultOAuth2UserSerivce 사용해 자동으로 사용자 정보 처리.
                            // 처리한 정보는 SecurityContext에 OAuth2User로 기록되어있음
                            .successHandler(oAuthLoginSuccessHandler) // 로그인 성공시 수행
                            .failureHandler(oAuthLoginFailureHandler); // 로그인 실패시 수행
                });
        return httpSecurity.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }
}