package com.edumanager.application.config;

import com.edumanager.shared.security.JwtAccessDeniedHandler;
import com.edumanager.shared.security.JwtAuthenticationEntryPoint;
import com.edumanager.shared.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity          //Spring Security 활성화
@EnableMethodSecurity       //@PreAuthorize, @PostAuthorize 등 메서드 레벨 보안 활성화
@RequiredArgsConstructor    // final 필드 자동 생성자 주입
public class SecurityConfig {

    //JWT 인증 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 401 처리를 위한 EntryPoint
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // 403 처리를 위한 Handler
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //    Spring Security 필터 체인 설정
//    모든 보안 관련 설정을 여기서 정의
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)          // JWT사용하므로 불필요
                // CORS 설정 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션 사용하지 않음(JWT는 Stateless) -> 이거 더 알아보기
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 예외 처리 설정
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)//401처리
                                .accessDeniedHandler(jwtAccessDeniedHandler)          //403처리
                )
                // URL 별 접근 권한 설정
                .authorizeHttpRequests(auth ->
                        auth
                                // 인증 없이 접근 가능한 경로
                                .requestMatchers(
                                        "/api/auth/login",
                                        "/api/auth/signup",
                                        "/api/auth/refresh",
                                        "/api/health",
                                        "/error",
                                        "/swagger-ui/**",   //Swagger UI
                                        "/v3/api-docs/**"   //Swagger 문서

                                ).permitAll()
                                // 관리자 전용 API
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                // 강사, 관리자 권한 필요
                                .requestMatchers("/api/teacher/**").hasAnyRole("ADMIN","TEACHER")
                                // 학부모 API
                                .requestMatchers("/api/parent/**").hasAnyRole("ADMIN","TEACHER","PARENT")
                                // 학생 API
                                .requestMatchers("/api/student/**").hasAnyRole("ADMIN","TEACHER","STUDENT")

                                // 그 외 모든 요청은 인증 필요
                                .anyRequest().authenticated()

                );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();


    }


//    CORS 설정
//    프론트엔드와의 통신을 위해 필요

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //허용할 Origin
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173"

        ));

        // 허용할 HTTP 메소드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        //허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));

        //인증 정보 포함 허용(쿠키, 헤더 등)
        configuration.setAllowCredentials(true);

        //preflight 요청 캐시 시간
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

}
