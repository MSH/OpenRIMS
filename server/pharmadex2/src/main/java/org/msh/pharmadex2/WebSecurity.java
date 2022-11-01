package org.msh.pharmadex2;

import java.util.HashSet;
import java.util.Set;

import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.auth.UserRoleDto;
import org.msh.pharmadex2.service.common.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * There are tree configuration for three login entry points depends on path:
 * <ul>
 * <li>"/" allowed for all, however will be redirected to login form path
 * "/form"
 * <li>"/form" here you can find form based authentication with the possibility
 * to switch to OATH2 providers
 * <li>"/oath2" use OATH2 to authentication
 * </ul>
 * Regardless of authentication type, authorization performs from the
 * application's database data
 * 
 * @author alexk
 *
 */
@Configuration
@EnableWebSecurity
public class WebSecurity {

	@Autowired
	UserService userService;
	@Autowired
	PasswordEncoder encoder;

	/**
	 * Configure form based authentication
	 * 
	 * @author alexk
	 *
	 */
	@Configuration
	@Order(1)
	public static class FormAuthEntryPoint extends WebSecurityConfigurerAdapter{
		/**
		 * Configure HTTP security
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/form/**")
			.cors().disable()
			.headers().frameOptions().disable() // allow the iFrame, however the same domain
			.and()
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/form/login").permitAll()
			.antMatchers("/oauth_login").permitAll()
			.antMatchers("/").permitAll()
			.antMatchers("/img/**").permitAll()
			.antMatchers("/js/**").permitAll()
			.antMatchers("/favicon.ico").permitAll()
			.antMatchers("/api/public/**").permitAll()
			.antMatchers("/landing").permitAll()
			.antMatchers("/api/landing/report/**").permitAll()
			.antMatchers("/form/login").permitAll()
			.antMatchers("/oauth_login").permitAll()
			.antMatchers("/api/common/**").authenticated()
			.antMatchers("/api/guest/**").hasAuthority("ROLE_GUEST")
			.antMatchers("/guest/**").hasAuthority("ROLE_GUEST")
			.antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/shablon/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/moderator/**").hasAuthority("ROLE_MODERATOR")
			.antMatchers("/api/moderator/**").hasAuthority("ROLE_MODERATOR")
			.antMatchers("/screener/**").hasAuthority("ROLE_SCREENER")
			.antMatchers("/api/screener/**").hasAuthority("ROLE_SCREENER")
			.antMatchers("/reviewer/**").hasAuthority("ROLE_REVIEWER")
			.antMatchers("/api/reviewer/**").hasAuthority("ROLE_REVIEWER")
			.antMatchers("/accountant/**").hasAuthority("ROLE_ACCOUNTANT")
			.antMatchers("/api/accountant/**").hasAuthority("ROLE_ACCOUNTANT")
			.antMatchers("/inspector/**").hasAuthority("ROLE_INSPECTOR")
			.antMatchers("/api/inspector/**").hasAuthority("ROLE_INSPECTOR")
			.antMatchers("/secretary/**").hasAuthority("ROLE_SECRETARY")
			.antMatchers("/api/secretary/**").hasAuthority("ROLE_SECRETARY")
			.antMatchers("/actuator").hasAuthority("ROLE_ADMIN")
			.antMatchers("/actuator/**").hasAuthority("ROLE_ADMIN")
			.anyRequest().denyAll()
			.and()
			.formLogin()
			.loginPage("/form/login")
			.failureUrl("/form/login")
			//.successForwardUrl("/")
			.and()
			.logout().deleteCookies("PDX2_SESSION","remember-me")
			.logoutSuccessUrl("/")
			.and()
			.rememberMe().key("арозаупаланалапуазора")
			.and()			
			.exceptionHandling().accessDeniedPage("/");
		}
	}

	/**
	 * Configure oath2 based authentication and authentication rules
	 * 
	 * @author alexk
	 *
	 */
	@Configuration
	@Order(3)
	public static class Oath2EntryPoint extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
			.cors().disable()
			.headers().frameOptions().disable() // allow the iFrame, however the same domain
			.and()
			.csrf().disable()
			.authorizeRequests()
			.antMatchers("/").permitAll()
			.antMatchers("/img/**").permitAll()
			.antMatchers("/js/**").permitAll()
			.antMatchers("/favicon.ico").permitAll()
			.antMatchers("/api/public/**").permitAll()
			.antMatchers("/landing").permitAll()
			.antMatchers("/api/landing/report/**").permitAll()
			.antMatchers("/form/login").permitAll()
			.antMatchers("/oauth_login").permitAll()
			.antMatchers("/api/common/**").authenticated()
			.antMatchers("/api/guest/**").hasAuthority("ROLE_GUEST")
			.antMatchers("/guest/**").hasAuthority("ROLE_GUEST")
			.antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/shablon/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
			.antMatchers("/moderator/**").hasAuthority("ROLE_MODERATOR")
			.antMatchers("/api/moderator/**").hasAuthority("ROLE_MODERATOR")
			.antMatchers("/screener/**").hasAuthority("ROLE_SCREENER")
			.antMatchers("/api/screener/**").hasAuthority("ROLE_SCREENER")
			.antMatchers("/reviewer/**").hasAuthority("ROLE_REVIEWER")
			.antMatchers("/api/reviewer/**").hasAuthority("ROLE_REVIEWER")
			.antMatchers("/accountant/**").hasAuthority("ROLE_ACCOUNTANT")
			.antMatchers("/api/accountant/**").hasAuthority("ROLE_ACCOUNTANT")
			.antMatchers("/inspector/**").hasAuthority("ROLE_INSPECTOR")
			.antMatchers("/api/inspector/**").hasAuthority("ROLE_INSPECTOR")
			.antMatchers("/secretary/**").hasAuthority("ROLE_SECRETARY")
			.antMatchers("/api/secretary/**").hasAuthority("ROLE_SECRETARY")
			.antMatchers("/actuator").hasAuthority("ROLE_ADMIN")
			.antMatchers("/actuator/**").hasAuthority("ROLE_ADMIN")
			.anyRequest().denyAll()
			.and()
			.oauth2Login().loginPage("/oauth_login")///oauth_login
			//.failureUrl("/form/login")
			//.oauth2Login().failureUrl("/form/login")
			.and()
			.logout().deleteCookies("PDX2_SESSION","remember-me")
			.logoutSuccessUrl("/")
			.and()			
			.exceptionHandling().accessDeniedPage("/");
		}
	}



	/**
	 * Add user roles from the database. We can't find ones in Google or Facebook,
	 * or Twitter
	 * 
	 * @return
	 */
	@Bean
	public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		final OidcUserService delegate = new OidcUserService();
		return (userRequest) -> {
			OidcUser oidcUser = delegate.loadUser(userRequest);
			Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
			UserDetailsDTO userDetails = userService.loadByEmail(oidcUser.getEmail());
			if (userDetails != null) {
				if(userDetails.isLocked()) {
					return oidcUser;						//disable locked users. It works for unknown reason!
				}
				mappedAuthorities.addAll(userDetails.getGranted());
			}else {
				UserRoleDto urd = new UserRoleDto();
				urd.setActive(true);
				urd.setAuthority("ROLE_GUEST");
				mappedAuthorities.add(urd);
			}
			OidcUser oidcUser1 = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
			return oidcUser1;
		};
	}

	/**
	 * Authentication should be from the database
	 * 
	 * @return
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(encoder);
		return authProvider;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return userService;
	}

}
