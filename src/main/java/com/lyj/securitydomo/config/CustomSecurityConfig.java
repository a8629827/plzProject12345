package com.lyj.securitydomo.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.disable())
                .authorizeHttpRequests(authorizeHttpRequestsConfigurer -> authorizeHttpRequestsConfigurer
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .requestMatchers("/login", "/sinup", "/user/**", "/", "/all", "/posting/**","/view/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/report/create").permitAll() // 신고하기 URL 허용
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/images/**", "/css/**", "/js/**", "/webjars/**").permitAll()// 정적 리소스 허용
                        .anyRequest().authenticated())
                .formLogin(formLoginConfigurer -> formLoginConfigurer
                        .loginPage("/user/login")
                        .loginProcessingUrl("/loginProcess")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/posting/list"))
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true))
                .build();
    }


    @Bean // password 암호화 빈
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring() // 모든 스태틱 자원 필터 제외
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
        //.requestMatchers("/static/**");
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       BCryptPasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailsService userDetailsService, AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return builder.build();
    }
}