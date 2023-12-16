package org.msh.pharmadex2.controller.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.msh.pdex2.dto.i18n.Language;
import org.msh.pdex2.dto.i18n.Languages;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.dto.AboutDTO;
import org.msh.pharmadex2.dto.AskForPass;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.ExchangeConfigDTO;
import org.msh.pharmadex2.dto.PublicPermitDTO;
import org.msh.pharmadex2.dto.SystemImageDTO;
import org.msh.pharmadex2.dto.UserFormDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.ContextServices;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.msh.pharmadex2.service.r2.ExchangeConfigMainService;
import org.msh.pharmadex2.service.r2.ReportService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * This controller responsible for public domain  REST API
 * @author Alex Kurasoff
 *
 */
@RestController
public class PublicAPI{

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(PublicAPI.class);

	@Autowired
	private Messages messages;

	@Autowired
	private UserService userService;

	@Autowired
	private ContentService contentService;
	@Autowired
	private ResourceService resourceServ;
	@Autowired
	private ReportService reportServ;
	@Autowired
	ExchangeConfigMainService exchangeMainServ;
	@Autowired
	private ValidationService validService;

	@Value("${app.buildTime}")
	private String buildTime;
	@Value("${app.release}")
	private String release;
	@Value("${pharmadex.allow.process.import}")
	private boolean allowProcessImport;

	/**
	 * Create a context cookie
	 * @param contextId
	 */
	public static Cookie createContextCookie(String contextId){
		Cookie ret = new Cookie(ContextServices.PDEX_CONTEXT, contextId);
		ret.setPath("/");
		return ret;
	}

	/**
	 * Living or died?
	 * @return
	 */
	@RequestMapping(value = "/api/public/ping",  method = {RequestMethod.POST,RequestMethod.GET})
	public String ping(){
		return "OK";
	}
	
	/**
	 * Ping main server (by import proccess)
	 * and verif param pharmadex.allow.process.import
	 * @return
	 */
	@RequestMapping(value = "/api/public/pingbyimport",  method = {RequestMethod.POST,RequestMethod.GET})
	public String pingbyimport(){
		if(allowProcessImport)
			return "OK";
		return "NOACCESS";
	}


	/**
	 * Provide labels for keys defined in the parameter
	 * For label "locale" always returns a name of the current locale
	 * @param keys
	 * @return
	 */
	@RequestMapping(value="/api/public/provideLabels", method = RequestMethod.POST)
	public Map<String,String> localeProvideLabels(@RequestBody List<String> keys){
		Map<String,String> ret = new HashMap<String, String>();
		for(String key :keys) {
			ret.put(key,messages.get(key));
		}
		ret.put("locale", messages.getCurrentLocaleStr());
		return ret;
	}

	/**
	 * Load the emblem (national)
	 * @return
	 */
	@RequestMapping(value="api/public/emblem.svg", method = RequestMethod.GET)
	public ResponseEntity<Resource> emblem() {
		Resource res = new ByteArrayResource(messages.loadEmblem().getBytes());
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("image/svg+xml"))
				.header("filename","emblem.svg")
				.body(res);
	}

	/**
	 * Load the NMRA logo
	 * @return
	 * @throws DataNotFoundException 
	 */
	@RequestMapping(value="api/public/headerlogo", method = RequestMethod.GET)
	public ResponseEntity<Resource> nmraLogo() throws DataNotFoundException {
		try {
			SystemImageDTO dto = resourceServ.logo();
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(dto.getMediatype()))
					.header("filename", dto.getFilename())
					.body(dto.getResource());
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}


	/**
	 * Load the NMRA footer
	 * @return
	 * @throws DataNotFoundException 
	 */
	@RequestMapping(value="api/public/footerlogo", method = RequestMethod.GET)
	public ResponseEntity<Resource> nmraFooter() throws DataNotFoundException {
		try {
			SystemImageDTO dto = resourceServ.footer();
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(dto.getMediatype()))
					.header("filename", dto.getFilename())
					.body(dto.getResource());
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * About data for the footer etc
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@RequestMapping(value="api/public/about", method = RequestMethod.POST)
	public AboutDTO about(@RequestBody AboutDTO data) throws DataNotFoundException {
		data.setBuildTime(buildTime);
		data.setRelease(release);
		return data;
	}



	/**
	 * Get an SVG flag to switch the language
	 * @return
	 */
	@RequestMapping(value="/api/public/flag", method = RequestMethod.GET)
	public ResponseEntity<Resource> flag(@RequestParam String localeStr	){
		Resource res = new ByteArrayResource("".getBytes());
		for(Language lang : messages.getLanguages().getLangs()) {
			if(lang.getLocaleAsString().equalsIgnoreCase(localeStr)) {
				res=new ByteArrayResource(lang.getFlagSVG().getBytes());
			}
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("image/svg+xml"))
				.header("filename","emblem.svg")
				.body(res);
	}

	/**
	 * Get user's details for just authenticated user. Not for edit/display
	 * @param user
	 * @return
	 */	
	@RequestMapping(value= {"/api/public/userdata"}, method = RequestMethod.POST)
	public UserDetailsDTO userData(Authentication auth) {
		UserDetailsDTO ret = new UserDetailsDTO();
		ret = userService.userData(auth, ret);
		return ret;
	}

	/**
	 * Get user's details for just authenticated user. For edit/display
	 * @param user
	 * @return
	 * @throws DataNotFoundException 
	 */	
	@RequestMapping(value= {"/api/public/userdata/form"}, method = RequestMethod.POST)
	public UserFormDTO userDataForm(Authentication auth) throws DataNotFoundException {
		UserFormDTO ret;
		try {
			ret = userService.createFields(userData(auth));
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return ret;
	}

	/**
	 * Get a list of available languages
	 * @param List<FlagDTO>
	 * @return
	 */	
	@RequestMapping(value= {"/api/public/languages"}, method = RequestMethod.POST)
	public Languages languages(@RequestBody Languages data) {
		data=messages.getLanguages();
		return data;
	}

	/**
	 * Tiles for landing page
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/public/landing/content")
	public ContentDTO landingContent(@RequestBody ContentDTO data) throws DataNotFoundException {
		try {
			data=contentService.loadContent(data, "landing");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@RequestMapping(value="api/public/tileicon", method = RequestMethod.GET)
	public ResponseEntity<Resource> loadTileIcon(@RequestParam String iconurl) throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			if(iconurl != null && iconurl.length() > 0) {
				if(iconurl.startsWith("img/") || iconurl.startsWith("/img/")) {
					res = resourceServ.createImageResource(iconurl);
				}else {
					res = resourceServ.loadTileIconByUrl(iconurl);
				}
			}else {
				res = resourceServ.createEmptyResource();
			}


			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/*
	 * @PostMapping("/api/public/report/loadlink") public String
	 * reportLoadlink(Authentication auth, @RequestBody String data) { return
	 * reportServ.getLinkReport(); }
	 */

	/* 15.11.2022 khomenska */
	@RequestMapping(value="api/public/terms", method = RequestMethod.GET)
	public ResponseEntity<Resource> loadTerms() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.downloadTerms();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/* 15.11.2022 khomenska */
	@RequestMapping(value="api/public/privacy", method = RequestMethod.GET)
	public ResponseEntity<Resource> loadPrivacy() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.downloadPrivacy();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * ASk for a temporary password
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@PostMapping(value="/api/public/temporary/password")
	public AskForPass temporaryPassword(@RequestBody AskForPass data){
		data=userService.temporaryPassword(data);
		return data;
	}
	
	/**
	 * ASk for a temporary password
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@PostMapping(value="/api/public/permit/data")
	public PublicPermitDTO permitData(Authentication auth, @RequestBody PublicPermitDTO data) throws DataNotFoundException{
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			if(!validService.IsNotSubmittedGuest(data.getPermitDataID())) {
				data=reportServ.permitData(user, data);
			}else {
				
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/processes/load")
	public ExchangeConfigDTO loadProcesses(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.loadProcesses(data);
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/processes/geturl")
	public ExchangeConfigDTO geturlProcesses(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.getUrlProcesses(data);
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/checklists/load")
	public ExchangeConfigDTO loadChecklistDictionaries(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.loadChecklistDictionaries(data);
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/dictionaries/load")
	public ExchangeConfigDTO loadDictionaries(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.loadDictionaries(data);
		return data;
	}
	@PostMapping(value="/api/public/exchange/resources/load")
	public ExchangeConfigDTO loadResources(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.loadResources(data);
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/dataconfig/load")
	public ExchangeConfigDTO loadDataConfigs(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.loadDataConfigs(data);
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/workflows/load")
	public ExchangeConfigDTO loadWorkflows(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.loadWorkflows(data);
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/dictionary/import")
	public ExchangeConfigDTO importDictionary(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.importDictionary(data);
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/resource/import")
	public ExchangeConfigDTO importResource(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.importResource(data);
		return data;
	}
	
	@PostMapping(value="/api/public/exchange/dataconfig/import")
	public ExchangeConfigDTO importDataConfig(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.importDataConfig(data);
		return data;
	}
	@PostMapping(value="/api/public/exchange/workflows/importall")
	public ExchangeConfigDTO importWorkflows(Authentication auth, @RequestBody ExchangeConfigDTO data) throws ObjectNotFoundException{
		data = exchangeMainServ.importAllWorkflows(data);
		return data;
	}
}