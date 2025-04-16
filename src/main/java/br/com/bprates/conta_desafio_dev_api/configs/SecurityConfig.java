package br.com.bprates.conta_desafio_dev_api.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**").permitAll() // Libera acesso ao actuator
                        .anyRequest().authenticated()
                )
                .csrf().disable(); // Desativa CSRF para simplificar chamadas via Prometheus

        return http.build();
    }
}
