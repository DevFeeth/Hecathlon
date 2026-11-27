package com.spring.henallux.hecathlon.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

	public static final String LOGIN_PAGE_URL = "/login";

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(requests -> requests
						.requestMatchers("/", "/css/**", "/images/**", "/sports", "/sports/**", "/inscription", "/error", LOGIN_PAGE_URL).permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage(LOGIN_PAGE_URL)
						.usernameParameter("email")
						.defaultSuccessUrl("/", true)
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll()
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
