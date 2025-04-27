package com.tomato.naraclub.common.config;

import com.tomato.naraclub.application.security.JwtAuthenticationFilter;
import com.tomato.naraclub.application.security.MemberUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final MemberUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            .requestMatchers("/uploads/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors
                .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
            )
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(daoAuthenticationProvider())
            .authorizeHttpRequests(auth -> auth
                //1) 회원만 가능 리스트
                .requestMatchers(HttpMethod.POST, "/api/board/posts").authenticated()
                .requestMatchers("/api/auth/validate").authenticated()

                // 2) 로그인·리프레시는 누구나 (토큰 없어도) 허용
                .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/board/**", "/api/vote/**", "/api/videos/**",
                "/admin/**"
                ).permitAll()

                // 3) swagger, 정적 리소스 등
                .requestMatchers("/swagger-ui.html", "/v3/api-docs/**").permitAll()
                .requestMatchers(
                    "/login/**", "/main/**", "/board/**", "/components/**",
                    "/bootstrap/**", "/css/**", "/js/**", "/images/**",
                    "/favicon.ico", "/uploads/**", "/vote/**", "/original/**",
                        "/assets/**"
                ).permitAll()

                // 4) 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            // JWT 필터 (위에서 인증이 필요한 경로에는 이 필터가 동작)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            // 예외 처리
            .exceptionHandling(ex -> ex
                // 인증 실패시 401
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                // 인가 실패시 403
                .accessDeniedHandler((req, res, denied) ->
                    res.sendError(HttpStatus.FORBIDDEN.value(), "Forbidden"))
            );

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
        throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
