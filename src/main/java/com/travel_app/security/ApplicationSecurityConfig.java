package com.travel_app.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

import static com.travel_app.security.ApplicationUserRole.ADMIN;
import static com.travel_app.security.ApplicationUserRole.USER;


@Configuration
@EnableWebSecurity // adnotacja, która określa, że klasa będzie będzie zawierała konfiguracje dla "Security"
//@EnableGlobalMethodSecurity(prePostEnabled = true) // dzięki temu działają adnotacje nad endpointami
@EnableMethodSecurity(securedEnabled = true)
public class ApplicationSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "management/alltrips").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN") // tutaj filtrowanie
                        .requestMatchers(HttpMethod.POST, "management/addtrip").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "management/buytrip/{id}").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "management/deletetrips/{id}").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "management/updatetrips/{id}").hasAuthority("ROLE_ADMIN")
                        .anyRequest().permitAll()) // wpuszczamy wszystkich pozostałych
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .defaultSuccessUrl("/trips", true)
                        .passwordParameter("kochamywycieczki")
                        .usernameParameter("username"))// jeśli chcemy użyć innej nazwy niz pliku html
                .rememberMe(rememberMeConfigurer -> rememberMeConfigurer // domyślnie działa przez 30 minut braku aktywności
                        .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                        .key("jakiskluczdoszyfrowania") // klucz do szyfrowania przez MD5 dla zawartości, czyli 'username', 'expirationDate'
                        .rememberMeParameter("remember-me"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .logoutSuccessUrl("/login"));

        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsManager() {
        return new InMemoryUserDetailsManager();
    }

    @Bean
    protected InitializingBean initializingBean() {
        return () -> {
            UserDetails adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("kochamywycieczki"))
                    .authorities(ADMIN.getGrantedAuthorities())
                    .build();
            userDetailsManager().createUser(adminUser);
            UserDetails customerUser = User.builder()
                    .username("customer")
                    .password(passwordEncoder.encode("bardzochcejechaćnawakacje"))
                    .authorities(USER.getGrantedAuthorities())
                    .build();
            userDetailsManager().createUser(customerUser);
        };
    }
}
