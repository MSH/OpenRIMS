package org.msh.pharmadex2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Define password encoder bean.
 * The didicated class is necessary to avoid circular references
 * @author alexk
 *
 */
@Configuration
public class WebPasswordEncoder {
	/**
	 * new and modern password encoder
	 * 
	 * @return
	 */
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder() {
			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				boolean ret = super.matches(rawPassword, encodedPassword);
				return ret;
			}
		};
	}
}
