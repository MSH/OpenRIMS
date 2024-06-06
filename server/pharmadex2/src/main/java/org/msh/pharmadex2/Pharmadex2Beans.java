package org.msh.pharmadex2;

import java.util.Locale;

import org.msh.pdex2.i18n.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableAsync
/**
 * 
 * @author 
 *
 */

public class Pharmadex2Beans implements WebMvcConfigurer{
	@Autowired
	Messages messages;
	
	public static final String DATA_IMPORT = "DataImport-";
		
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
		Locale def = messages.setDefaultLocaleToLCH();
		CookieLocaleResolver clr = new CookieLocaleResolver();
	    clr.setDefaultLocale(def);
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
	
	/**
	 * Thread pool for data imports
	 * @return
	 */
	@Bean(name = "taskExecutorDataImport")
	 public ThreadPoolTaskExecutor taskExecutorDataImport() {
	  ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	  executor.setCorePoolSize(1);
	  executor.setMaxPoolSize(1);
	  executor.setQueueCapacity(0);
	  executor.setThreadNamePrefix(DATA_IMPORT);
	  executor.initialize();
	  return executor;
	 }
}
