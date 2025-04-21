package com.lzy.mall.tiny.config;

import com.lzy.mall.tiny.component.JwtAuthenticationTokenFilter;
import com.lzy.mall.tiny.component.RestAuthenticationEntryPoint;
import com.lzy.mall.tiny.component.RestfulAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig 类本身是在应用启动时执行，用于构建和配置 Spring Security 的安全机制，
 * 而它配置出来的 SecurityFilterChain 才是在后续每个 HTTP 请求到来时被执行。
 * 具体到链中的各个过滤器（包括您自定义的 JwtAuthenticationTokenFilter），它们会在请求处理的不同阶段按顺序执行。
 */

@Configuration
@EnableMethodSecurity  // 启用方法级别安全注解，如@PreAuthorize
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        // 禁用CSRF，因为使用JWT，不受csrf攻击
        httpSecurity.csrf(csrf -> csrf.disable())
                // 基于token，所以不需要session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置拦截规则
                .authorizeHttpRequests(authorize -> authorize
                        // 允许匿名访问的路径（静态资源、登录注册等）
                        .requestMatchers(HttpMethod.GET,
                                "/",
                                "/*.html",
                                "/favicon.ico",
                                "/**/*.html",
                                "/**/*.css",
                                "/**/*.js",
                                "/swagger-resources/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()
                        .requestMatchers("/admin/login", "/admin/register") // 对登录注册允许匿名访问
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS) // 跨域请求会先进行一次options请求
                        .permitAll()
                        .anyRequest().authenticated()  // 除上面外的所有请求全部需要鉴权认证
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                );

        // 将JWT认证过滤器添加到Spring Security的过滤器链中
        // 放置在UsernamePasswordAuthenticationFilter之前，确保在处理用户名密码之前先检查JWT
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return httpSecurity.build();
    }

    /**
     * JWT认证过滤器 Bean
     */
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    /**
     * 暴露AuthenticationManager Bean，用于在需要的地方进行认证
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}