package com.school.homework.config;

import com.school.homework.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置类
 * 
 * <p>配置应用程序的安全策略，包括：
 * <ul>
 *   <li>URL 访问控制（哪些路径需要认证，哪些公开访问）</li>
 *   <li>表单登录配置</li>
 *   <li>登出配置</li>
 *   <li>CSRF 保护配置</li>
 *   <li>密码编码器配置</li>
 *   <li>认证提供者配置</li>
 * </ul>
 * </p>
 * 
 * <p>权限控制：
 * <ul>
 *   <li>使用 @EnableMethodSecurity 启用方法级安全</li>
 *   <li>在控制器方法上使用 @PreAuthorize 注解进行权限控制</li>
 * </ul>
 * </p>
 * 
 * @author School Homework Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /** 自定义用户详情服务 */
    private final CustomUserDetailsService userDetailsService;

    /**
     * 构造函数注入依赖
     * 
     * @param userDetailsService 自定义用户详情服务
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 配置安全过滤器链
     * 
     * <p>定义 URL 访问规则、登录/登出配置等。</p>
     * 
     * @param http HttpSecurity 对象
     * @return 配置好的 SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 配置 URL 访问控制
            .authorizeHttpRequests(auth -> auth
                // 公开访问的路径（无需认证）
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/h2-console/**", "/images/**", "/webjars/**").permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            // 配置表单登录
            .formLogin(form -> form
                .loginPage("/login")              // 登录页面路径
                .defaultSuccessUrl("/", true)      // 登录成功后重定向的URL
                .permitAll()                       // 允许所有人访问登录页面
            )
            // 配置登出
            .logout(logout -> logout
                .logoutUrl("/logout")              // 登出URL
                .logoutSuccessUrl("/login?logout") // 登出成功后重定向的URL
                .permitAll()                       // 允许所有人登出
            )
            // H2 控制台特殊配置：禁用 CSRF 保护（仅用于开发环境）
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            // 允许同源 iframe（H2 控制台需要）
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    /**
     * 配置认证提供者
     * 
     * <p>使用自定义的 UserDetailsService 和密码编码器。</p>
     * 
     * @return 配置好的 DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * 配置认证管理器
     * 
     * @param authConfig 认证配置
     * @return 认证管理器
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 配置密码编码器
     * 
     * <p>使用 BCrypt 算法加密密码。</p>
     * 
     * @return BCrypt 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

