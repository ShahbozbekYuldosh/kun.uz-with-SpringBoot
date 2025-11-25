package dasturlash.uz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationProvider authenticationProvider() {

        String rawPassword = UUID.randomUUID().toString();
        System.out.println("Password: " + rawPassword);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode(rawPassword))
                .roles("USER")
                .build();

        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager(user);

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setUserDetailsPasswordService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);

        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // authorization - Foydalanuvchining tizimdagi huquqlarini tekshirish.
        // Ya'ni foydalanuvchi murojat qilayotgan API-larni ishlatishga ruxsati bor yoki yo'qligini tekshirishdir.
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
            authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/auth/**").permitAll()
                    .anyRequest()
                    .authenticated();
        });

        http.csrf(AbstractHttpConfigurer::disable); // csrf o'chirilgan
        http.cors(httpSecurityCorsConfigurer -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(Arrays.asList("*"));
            configuration.setAllowedMethods(Arrays.asList("*"));
            configuration.setAllowedHeaders(Arrays.asList("*"));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            httpSecurityCorsConfigurer.configurationSource(source);
        });   // cors yoqilgan

        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

