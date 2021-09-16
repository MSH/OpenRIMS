package org.msh.pdex2.i18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.msh.pdex2.dto.i18n.Language;
import org.msh.pdex2.dto.i18n.Languages;
import org.msh.pdex2.model.i18n.ResourceBundle;
import org.msh.pdex2.model.i18n.ResourceMessage;
import org.msh.pdex2.repository.i18n.ResourceBundleRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * This class is responsible for all i18N things, include::
 * <ul>
 *  <li>messages store and all common operations on it
 *  <li>list of all possible locale
 *  <li>country specific data like the national emblem etc from the workspace
 *  </ul>
 * Should be a @Singleton (https://www.baeldung.com/spring-bean-scopes) to avoid unnecessary database operations 
 * The better place for it is locale change intercepter
 * @author alexk
 *
 */
@Component
@Scope("singleton")
public class Messages {

	@Value( "${spring.web.locale:en_US}" )
	String defaultLocaleName;
	
	public static final String KEY_UNKNOWN = "unknown";
	private final Logger logger = LoggerFactory.getLogger(Messages.class); 
	/**
	 * key is locale name in uppercase, value is all messages for this locale in key,value map. Keys are non case sensitive as well
	 */
	Map<String, Map<String,String>> messages = new HashMap<String, Map<String,String>>();
	Locale currentLocale = Locale.US;					//by default

	public Languages languages = new Languages();
	@Autowired
	private ResourceBundleRepo bundleRepo;

	/**
	 * Localized strings for labels, messages, etc
	 * @return
	 */
	public Map<String, Map<String,String>> getMessages() {
		return messages;
	}
	
	public void setMessages(Map<String, Map<String,String>> messages) {
		this.messages = messages;
	}

	public Locale getCurrentLocale() {
		return currentLocale;
	}

	public void setCurrentLocale(Locale currentLocale) {
		this.currentLocale = currentLocale;
	}

	/**
	 * Get all possible locale
	 * @return
	 */
	public Languages getLanguages() {
		String locStr = getCurrentLocaleStr();
		this.languages.setSelected(new Language());
		for(Language lang :this.languages.getLangs()) {
			if(lang.getLocaleAsString().equalsIgnoreCase(locStr)) {
				this.languages.setSelected(lang);
			}
		}
		return languages;
	}
	/**
	 * GEt current locale as string like en-US
	 * @return
	 */
	public String getCurrentLocaleStr() {
		String localeStr = LocaleContextHolder.getLocale().toString(); 
		return localeStr;
	}


	/**
	 * Convenient method to get a message on proper language
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String localeStr = LocaleContextHolder.getLocale().toString();
		Map<String,String> mess = getMessages().get(localeStr.toUpperCase());
		if(mess != null && key!=null) {
			String ret = mess.get(key.toUpperCase());
			if(ret != null) {
				return ret;
			}else {
				return key;
			}
		}
		return key;
	}

	/**
	 * Load all possible languages from the database  
	 */
	@Transactional
	public void loadLanguages() {
		List<ResourceBundle> bundles = bundleRepo.findAllByOrderBySortOrder();
		if(bundles != null) {
			ResourceBundle currentBundle=null;
			getLanguages().getLangs().clear();
			String localeStr=LocaleContextHolder.getLocale().toString();
			for(ResourceBundle bundle : bundles) {
				messagesFromBundle(bundle);
				if(bundle.getLocale().equalsIgnoreCase(localeStr.toUpperCase())) {
					currentBundle=bundle;
					getLanguages().setSelected(resourceBundleToLanguage(bundle));
				}else {
					getLanguages().getLangs().add(resourceBundleToLanguage(bundle));

				}
			}
			if(currentBundle != null) {
				getLanguages().getLangs().add(0, resourceBundleToLanguage(currentBundle));
			}else {
				currentBundle=bundles.get(0);
				Locale curLocale = new Locale(currentBundle.getLocale());
				LocaleContextHolder.setLocale(curLocale);
			}
		}else {
			logger.error("Can't find any language bundle");
		}
	}

	/**
	 * Convert resource bundle to DTO
	 * @param bundle
	 * @return
	 */
	@Transactional
	public Language resourceBundleToLanguage(ResourceBundle bundle) {
		Language ret = new Language();
		ret.setDisplayName(bundle.getDisplayName());
		ret.setFlag64("");
		ret.setFlagSVG(bundle.getSvgFlag());
		ret.setLocaleAsString(bundle.getLocale());
		ret.setNmraLogo(bundle.getNmraLogo());
		return ret;
	}
	/**
	 * Create messages from a bundle given
	 * @param bundle
	 */
	@Transactional
	private void messagesFromBundle(ResourceBundle bundle) {
		Map<String,String> values = new HashMap<String, String>();
		if(bundle.getMessages()!=null) {
			for(ResourceMessage rm :  bundle.getMessages()) {
				values.put(rm.getMessage_key().toUpperCase(), rm.getMessage_value());
			}
		}
		getMessages().put(bundle.getLocale().toUpperCase(), values);
	}

	/**
	 * Utility to parse string to locale to string
	 * @param locStr - string like en-US, ru-RU etc
	 * @return parsed or default locale
	 */
	public static Locale parseLocaleString(String locStr){
		Locale ret = Locale.US;
		if(locStr != null) {
			String[] lo = locStr.split("-");
			if(lo.length == 2){
				ret = new Locale(lo[0], lo[1]);
			}
		}
		return ret;
	}


	/**
	 * get an emblem in SVG format
	 * @return
	 */
	public String loadEmblem() {
		String ret = getLanguages().getSelected().getFlagSVG();
		return ret;
	}
	/**
	 * Create a map [lang_tag,language_name] from all languages available
	 * For usage in not-ReactJS applications, e.g., login window
	 * @param excludeLocale 
	 * @return
	 */
	public Map<String, String> getLanguagesMap(String excludeLocale) {
		Map<String,String> ret = new HashMap<String, String>();
		Languages langs = getLanguages();
		for(Language lang :langs.getLangs()) {
			if(!lang.getLocaleAsString().equalsIgnoreCase(excludeLocale)) {
				ret.put(lang.getLocaleAsString(),lang.getDisplayName());
			}
		}
		return ret;
	}
	/**
	 * GEt the NMRA logo
	 * @return
	 */
	public String loadNmraLogo() {
		String ret = getLanguages().getSelected().getNmraLogo();
		return ret;
	}

	/**
	 * Get the current bundle
	 * @return
	 */
	@Transactional
	public ResourceBundle getCurrentBundle() {
		Iterable<ResourceBundle> bundles = bundleRepo.findAll();
		for(ResourceBundle bundle : bundles) {
			if(bundle.getLocale().toUpperCase().equals(LocaleContextHolder.getLocale().toString().toUpperCase())) {
				return bundle;
			}
		}
		/**
		 * very rare case...
		 */
		ResourceBundle ret = bundles.iterator().next();
		for(ResourceBundle bundle : bundles) {
			if(bundle.getLocale().toUpperCase().equals(defaultLocaleName.toUpperCase())) {
				ret= bundle;
				break;
			}
		}
		return ret;
	}
	/**
	 * Get all locales as string configured for this installation
	 * @return list of en_US, ru_RU, pt, etc
	 */
	public List<String> getAllUsed() {
		List<String> ret = new ArrayList<String>();
		Languages langs = getLanguages();
		for(Language lang : langs.getLangs()) {
			ret.add(lang.getLocaleAsString());
		}
		return ret;
	}
	/**
	 * GEt all used, but not case sens
	 * @return
	 */
	public List<String> getAllUsedUpperCase() {
		List<String> ret = new ArrayList<String>();
		Languages langs = getLanguages();
		for(Language lang : langs.getLangs()) {
			ret.add(lang.getLocaleAsString().toUpperCase());
		}
		return ret;
	}
	/**
	 * Remove dots and apply
	 * @param url
	 * @return
	 */
	public String getUrl(String url) {
		String key = url.replace(".", "");
		return get(key);
	}

	public String getDefaultLocaleName() {
		return defaultLocaleName;
	}

	
}
