package org.msh.pharmadex2.controller.common;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.old.Context;
import org.msh.pharmadex2.service.common.ContextServices;
import org.msh.pharmadex2.service.common.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Responsible for navigation
 * @author alexk
 *
 */
@RestController
public class WebApp {
	private static final Logger logger = LoggerFactory.getLogger(WebApp.class);
	@Autowired
	private HttpServletRequest request;

	@Value("${server.servlet.context-path:}")
	private String contextPath;
	@Value("${pharmadex.google.analytic4.id:}")
	private String ga4ID;
	@Autowired
	ContextServices contextServ;
	@Autowired
	Messages messages;
	@Autowired
	UserService userServ;
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	private static String authorizationRequestBaseUri = "oauth2/authorization";

	/**
	 * We've created a login form manually, because of security consideraton
	 * @return
	 */
	@GetMapping({"/form/login"})
	public ModelAndView login(@CookieValue(name = "username") Optional<String> emailcookie){
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
		ModelAndView ret = new ModelAndView("login");
		ret.addObject("emailplaceholder", messages.get("valid_email"));
		if(emailcookie.isPresent()) {
			ret.addObject("useremail", emailcookie.get());
		}else {
			ret.addObject("useremail", "");
		}
		ret.addObject("get_password", messages.get("sendPasswordByEmail"));
		ret.addObject("pswdplaceholder", messages.get("please_password"));
		ret.addObject("login", messages.get("clicktologin"));
		ret.addObject("lblor", "><");
		ret.addObject("google", messages.get("Google"));

		Iterable<ClientRegistration> clientRegistrations = null;
		ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
				.as(Iterable.class);
		if (type != ResolvableType.NONE && 
				ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
			clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
		}
		String googlelink = "/"+authorizationRequestBaseUri + "/" + clientRegistrations.iterator().next().getRegistrationId();
		ret.addObject("googlelink", googlelink);

		return ret;
	}

	/**
	 * redirect to a tabset depends on auth data
	 * @return
	 * @throws JsonProcessingException 
	 */
	@GetMapping({"/"})
	public RedirectView redirectHome(Authentication auth,
			@CookieValue(ContextServices.PDEX_CONTEXT) Optional<String> contextId, 
			HttpServletResponse response) throws JsonProcessingException {
		Context context = contextServ.loadContext(contextId);
		response.addCookie(createContextCookie(Long.toString(context.getID())));
		
		if(auth == null) {
			return new RedirectView("landing",true);
		}else {
			if((auth.getAuthorities() != null && !auth.getAuthorities().isEmpty())) {
				String role = auth.getAuthorities().iterator().next().getAuthority().toLowerCase();
				if(role.contains("guest")) {
					return new RedirectView("/guest",true);
				}
				if(role.contains("admin")) {
					return new RedirectView("/admin",true);
				}
				if(role.contains("moder")) {
					return new RedirectView("/moderator",true);
				}
				if(role.contains("screener")) {
					return new RedirectView("/screener",true);
				}
				if(role.contains("inspector")) {
					return new RedirectView("/inspector",true);
				}
				if(role.contains("accountant")) {
					return new RedirectView("/accountant",true);
				}
				if(role.contains("reviewer")) {
					return new RedirectView("/reviewer",true);
				}
				if(role.contains("secretary")) {
					return new RedirectView("/secretary",true);
				}
				if(role.contains("applicant") || role.contains("user")) {
					return new RedirectView("/logout");
				}
				return new RedirectView("landing",true);		//default
			}else {
				return new RedirectView("landing",true);
			}
		}
	}
	
	@GetMapping({"/landing","/admin","/moderator","/guest","/screener","/inspector","/accountant","/reviewer", "/secretary", "/public"})
	public ModelAndView landing() {
		return createWithBundles("application");
	}

	/**
	 * Crerate a context cookie
	 * @param contextId
	 */
	public static Cookie createContextCookie(String contextId){
		Cookie ret = new Cookie(ContextServices.PDEX_CONTEXT, contextId);
		ret.setPath("/");
		return ret;
	}

	/**
	 * Create ModelAndView and resolve js bundle names. Currently we use only Application template
	 * @param entryPoint 
	 * @return
	 */
	private ModelAndView createWithBundles(String entryPoint) {
		ModelAndView mv = new ModelAndView("page");
		mv.addObject("title", messages.get("application"));
		mv.addObject("ga4",ga4ID);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			Resource[] bundles = resolver.getResources("classpath:static/js/*.js");
			if(bundles.length>0){
				for(Resource child : bundles){
					String name = child.getFilename().toUpperCase();
					if(name.toUpperCase().contains(entryPoint.toUpperCase())){
						if(!child.isFile()){
							mv.addObject("scriptBundle", "/js/"+ child.getFilename());  //production mode from jar
						}else{
							mv.addObject("scriptBundle", "/js/"+ entryPoint+"Bundle.js"); //development mode from Eclipse
						}
						break;
					}
				}
			}else {
				logger.error("Java script files cannot be found. Please, build the project");
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		mv.addObject("contextPath", contextPath);
		return mv;
	}
}
