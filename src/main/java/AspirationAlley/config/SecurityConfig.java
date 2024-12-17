package AspirationAlley.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests
                    .requestMatchers("/","/about","/contact", "/change-password","/profile","/unreg-page","/login", "/register", "/index", "/create-post", "/report-user", "/images/**","/css/**","/admin/**","/admin/edit-user/**", "/admin/save-user","/","/delete/**","/report/**","/about","/contact", 
                    		"/updateProfilePicture","/change-password",
                    		"/posts/*/like","/reportPost/**","/posts/*/comments","/delete","/report/**","/forgot-password","/forgot-password/send-otp","/forgot-password/verify","/forgot-password/reset-password")
                    .permitAll()
                    .anyRequest().authenticated();
            })
            .csrf().disable()
            .formLogin().disable()
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
