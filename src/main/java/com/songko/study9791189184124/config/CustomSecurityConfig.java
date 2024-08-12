package com.songko.study9791189184124.config;

import com.songko.study9791189184124.member.security.filter.JWTCheckFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Log4j2
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class CustomSecurityConfig {

    private JWTCheckFilter jwtCheckFilter;

    @Autowired
    private void setJwtCheckFilter(JWTCheckFilter jwtCheckFilter) {
        this.jwtCheckFilter = jwtCheckFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("filter chain..................");

        // 로그인 페이지를 제공하지 않도록
        httpSecurity.formLogin(formLogin -> formLogin.disable());

        // 로그아웃 사용 안함
        httpSecurity.logout(logout -> logout.disable());

        // CSRF 토큰은 세션 단위로 관리되므로 Rest API 서버에서는 무의미
        httpSecurity.csrf(csrf -> csrf.disable());

        // 무상태로 유지하도록 구성하고 세션을 생성하지 않도록 구성
        httpSecurity.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.NEVER)
        );

        // jwtCheckFilter를 UsernamePasswordAuthenticationFilter 앞에서 동작하도록 설정
        httpSecurity.addFilterBefore(jwtCheckFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}