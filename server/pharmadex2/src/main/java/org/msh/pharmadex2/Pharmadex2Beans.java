package org.msh.pharmadex2;

import java.util.Locale;

import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.exception.AsyncUncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskDecorator;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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

public class Pharmadex2Beans implements WebMvcConfigurer, AsyncConfigurer{
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
	LocaleResolver localeResolver() {
		Locale def = messages.setDefaultLocaleToLCH();
		CookieLocaleResolver clr = new CookieLocaleResolver();
		clr.setDefaultLocale(def);
		clr.setCookieName("lang");
		clr.setCookieMaxAge(365*24*60*60); //year
		return clr;
	}

	@Bean
	LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Bean
	ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
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
	ThreadPoolTaskExecutor taskExecutorDataImport() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(1);
		executor.setMaxPoolSize(1);
		executor.setQueueCapacity(0);
		executor.setThreadNamePrefix(DATA_IMPORT);
		executor.setTaskDecorator(new TaskDecorator() {
			private final Logger logger = LoggerFactory.getLogger(Pharmadex2Beans.class); 
			@Override
			public Runnable decorate(Runnable runnable) {
				logger.info("decorator is inheriting the current locale "+LocaleContextHolder.getLocale());
				LocaleContextHolder.setLocale(LocaleContextHolder.getLocale(),true);	//inherit locale to the thread
				return ()->{
					runnable.run();
				};
			}
		});
		executor.initialize();
		return executor;
	}
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncheckedException();
	}
}
