package kr.or.argos.security.config;

import kr.or.argos.security.filter.JwtRequestFilter;
import kr.or.argos.security.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManagerBean(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtProvider jwtProvider)
      throws Exception {
    // @formatter:off
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session ->
            session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(requests ->
            requests
                .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers("/users/register", "/users/login").permitAll()
                .anyRequest().authenticated()
        )
        .exceptionHandling(exception ->
            exception
                .accessDeniedPage("/users/login")
        )
        .addFilterBefore(new JwtRequestFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
    // @formatter:on
    return http.build();
  }
}