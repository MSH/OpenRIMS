package org.msh.pharmadex2;

import java.util.Locale;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.service.common.ContextServices;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.AmendmentService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootApplication(scanBasePackages= "org.msh")
@EnableAsync
public class Pharmadex2Application implements WebMvcConfigurer  {

	public static void main(String[] args) {
		SpringApplication.run(Pharmadex2Application.class, args);
	}

	@Autowired
	UserService userService;
	@Autowired
	SystemService systemService;
	@Autowired
	Messages messages;
	@Autowired
	AmendmentService amendServ;
	@Value( "${spring.web.locale:en_US}" )
	String defaultLocaleName;

	 /**
	  * Cleans up the context, initialize i18n messages, and, try to set default passwords for users that have not the  password yet
	  * @return
	  */
	@Bean
	public ApplicationListener<ContextRefreshedEvent> applicationListener(){
		return new ApplicationListener<ContextRefreshedEvent>(){
			@Autowired
			ContextServices contextServ;
			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				messages.loadLanguages();
				contextServ.removeAllContexts();
				userService.assignDefaultPasswords();
				try {
					amendServ.rewriteAmendedData();
				} catch (ObjectNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}				//TODO temporary!!!!
				try {
					systemService.checkDictionaries();
				} catch (ObjectNotFoundException e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	/**
	 * It is convenient to get the Rest template from one place. At least it ensures that we use the same RestTemplate for the all cases
	 * @param builder
	 * @return
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	/**
	 * Locale should be stored to the cookie. Default locale should became from:
	 * <ul> 
	 * <li>the workspace if no logged in user (login form for example)
	 * <li>individual for a logged in user from the user database record
	 * </ul>
	 * @return
	 */
	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver clr = new CookieLocaleResolver();
	    clr.setDefaultLocale(new Locale(defaultLocaleName));
	    clr.setCookieName("lang");
        clr.setCookieMaxAge(365*24*60*60); //year
	    return clr;
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	    lci.setParamName("lang");
	    return lci;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(localeChangeInterceptor());
	}
	
	@Bean
	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper ret = new ObjectMapper();
		ret.registerModule(new JavaTimeModule());
		ret.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return ret;
	}
	
}
