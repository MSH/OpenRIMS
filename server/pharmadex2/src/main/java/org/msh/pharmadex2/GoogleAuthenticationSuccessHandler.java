package org.msh.pharmadex2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class GoogleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		String appurl = getCookieValue(request, authentication);
		
		redirectStrategy.sendRedirect(request, response, appurl);
	}

	static public String getCookieValue(HttpServletRequest request, Authentication authentication) throws UnsupportedEncodingException {
		String appurl = "";
		
		if(request.getCookies() != null) {
			for(Cookie ck:request.getCookies()) {
				if(ck.getName().equals(WebSecurity.PDX2_URL_COOKIE)) {
					appurl = URLDecoder.decode(ck.getValue(), "UTF-8");
					break;
				}
			}
		}
		
		if(appurl.isEmpty()) {
			appurl = "/";
		}else {
			appurl = getUserPage(authentication) + appurl;
		}
		return appurl;
	}
	
	static public String getUserPage(Authentication authentication) {
		Set<GrantedAuthority> roles = new HashSet<>();
		if(authentication != null && authentication.getAuthorities() != null) {
			roles.addAll(authentication.getAuthorities());
			for(GrantedAuthority gr:roles) {
				if(gr.getAuthority().equals("ROLE_GUEST")) {
					return "/guest";
				}
				if(gr.getAuthority().equals("ROLE_ADMIN")) {
					return "/admin";
				}
				if(gr.getAuthority().equals("ROLE_MODERATOR")) {
					return "/moderator";
				}
				if(gr.getAuthority().equals("ROLE_INSPECTOR")) {
					return "/inspector";
				}
				if(gr.getAuthority().equals("ROLE_ACCOUNTANT")) {
					return "/accountant";
				}
				if(gr.getAuthority().equals("ROLE_REVIEWER")) {
					return "/reviewer";
				}
				if(gr.getAuthority().equals("ROLE_SECRETARY")) {
					return "/secretary";
				}
				if(gr.getAuthority().equals("ROLE_SCREENER")) {
					return "/screener";
				}
			}
		}
		
		return "";
	}
}
