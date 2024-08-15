package org.msh.pharmadex2;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.service.common.ContextServices;
import org.msh.pharmadex2.service.r2.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages= "org.msh")
@EnableAsync
public class Pharmadex2Application   {//implements WebMvcConfigurer

	//public static final String DATA_IMPORT = "DataImport-";
	
	public static void main(String[] args) {
		SpringApplication.run(Pharmadex2Application.class, args);
	}

	  @Autowired 
	  SystemService systemService;
	  @Autowired 
	  Messages messages;
	 
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
					systemService.assignDefaultPasswords();
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
		/*@Bean
		public RestTemplate restTemplate(RestTemplateBuilder builder) {
			return builder.build();
		}*/
		
		@Bean
		public RestTemplate restTemplate() 
		                throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		    TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
		                    .loadTrustMaterial(null, acceptingTrustStrategy)
		                    .build();

		    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

		    CloseableHttpClient httpClient = HttpClients.custom()
		                    .setSSLSocketFactory(csf)
		                    .build();

		    HttpComponentsClientHttpRequestFactory requestFactory =
		                    new HttpComponentsClientHttpRequestFactory();

		    requestFactory.setHttpClient(httpClient);
		    RestTemplate restTemplate = new RestTemplate(requestFactory);
		    return restTemplate;
		 }
	
	  /**
		 * Locale should be stored to the cookie. Default locale should became from:
		 * <ul> 
		 * <li>the workspace if no logged in user (login form for example)
		 * <li>individual for a logged in user from the user database record
		 * </ul>
		 * @return
		 
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
	*/
		
	/*MOVED TO PHARMADEX2BEANS
	 * @Bean public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
	 * ObjectMapper ret = new ObjectMapper(); ret.registerModule(new
	 * JavaTimeModule());
	 * ret.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); return ret; }
	 */
	
	/**
	 * Thread pool for data imports
	 * @return
	 */
	/*MOVED TO PHARMADEX2BEANS
	 * @Bean(name = "taskExecutorDataImport") public ThreadPoolTaskExecutor
	 * taskExecutorDataImport() { ThreadPoolTaskExecutor executor = new
	 * ThreadPoolTaskExecutor(); executor.setCorePoolSize(1);
	 * executor.setMaxPoolSize(1); executor.setQueueCapacity(0);
	 * executor.setThreadNamePrefix(DATA_IMPORT); executor.initialize(); return
	 * executor; }
	 */
}
