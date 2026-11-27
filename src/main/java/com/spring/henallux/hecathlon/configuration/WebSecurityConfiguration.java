package com.spring.henallux.hecathlon.configuration;

import org.springframework.beans.factory.annotation.Value;
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
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
												   @Value("${hecathlon.remember-me-key}") String rememberMeKey) throws Exception {
		http
				.authorizeHttpRequests(requests -> requests
						.requestMatchers("/", "/css/**", "/images/**", "/a-propos", "/sports", "/sports/**", "/produits/**", "/panier", "/panier/**", "/inscription", "/error", LOGIN_PAGE_URL).permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage(LOGIN_PAGE_URL)
						.usernameParameter("email")
						.defaultSuccessUrl("/")
						.permitAll()
				)
				.rememberMe(remember -> remember
						.key(rememberMeKey)
						.tokenValiditySeconds(14 * 24 * 3600)
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
