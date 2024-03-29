package org.msh.pharmadex2.service.r2;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.URLAssistantDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service is responsible for all data input assistance, like URL
 * @author alexk
 *
 */
@Service
public class AssistanceService {
	private static final Logger logger = LoggerFactory.getLogger(AssistanceService.class);
	private static final List<String> FORBIDDEN_DOMAINS =  
			Collections.unmodifiableList(Arrays.asList(
					"dictionary"
					,"configuration.resources"
					,"report.configuration"
					,"activity.configuration"
					,"log"
					,"system"
					));
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private Messages messages;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private DictService dictService;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private ResourceService resourceServ;



	/**
	 * Assist to select URL in URL fields
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public URLAssistantDTO assistanceMain(URLAssistantDTO data) throws ObjectNotFoundException {
		if(data.getAssistant() != AssistantEnum.NO) {
			data.setTitle(messages.get(data.getAssistant().toString()));
			data=domainTable(data);
			data=subDomainTable(data);
			data=urlTable(data);
			data=urlFieldSet(data);
		}
		data.setOldValue("");	//old value plays one time
		return data;
	}
	/**
	 * URLs table
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public URLAssistantDTO urlTable(URLAssistantDTO data) throws ObjectNotFoundException {
		data=headersUrlTable(data);
		List<TableRow> rows = new ArrayList<TableRow>();
		switch(data.getAssistant()) {
		case URL_ANY:								// Any existing or not existing URL that is suit URL syntax
		case URL_NEW:								//URL should suit the syntax, however does not exist
			rows= urlAnyRows(data);
			break;	
		case URL_DICTIONARY_NEW:			// URL that is suit dictionary URL syntax, however does not exist yet
		case URL_DICTIONARY_ALL:			//Any existing or not existing dictionary
			rows= urlDictRows(data);
			break;
		case URL_APPLICATION_ALL:			//Any existing or not existing URL for applications
			rows= urlApplRows(data);
			break;
		case URL_DATA_ANY:						//Any existing or not existing data configurations
		case URL_DATA_NEW:						//data URL should not be existing
			rows= urlDataRows(data);
			break;
		case URL_RESOURCE_NEW:				//A file resource should be new new
			rows= urlResourceRows(data);
			break;
		default:
			throw new ObjectNotFoundException("Wrong assistant "+data.getAssistant(), logger);
		}
		prepareTable(data.getUrls(),data.getSelectedUrl() ,rows);
		urlFieldSet(data);
		return data;
	}
	/**
	 * URLs for resources
	 * @param data
	 * @return
	 */
	private List<TableRow> urlResourceRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = urlFilter(data);
		//URL make sense only if sub domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT distinct\r\n" + 
					"r.url as 'url',\r\n" + 
					"r.description as 'prefLabel',\r\n" + 
					"r.Lang as 'Lang'\r\n" + 
					"FROM resources r\r\n" + 
					") t";
			String where="Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
			where = where +" and url like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getUrls().getHeaders());
		}else {
			cleanUpUrl(data);
		}
		return ret;
	}
	/**
	 * Rows for data configuration URLs
	 * @param data
	 * @return
	 */
	private List<TableRow> urlDataRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = urlFilter(data);
		//URL make sense only if sub domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT  distinct\r\n" + 
					"du.url as 'url',\r\n" + 
					"du.prefLabel as 'prefLabel',\r\n" + 
					"du.Lang as 'Lang'\r\n" + 
					"FROM data_urls du\r\n" + 
					") t";
			String where="Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
			where = where +" and url like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getUrls().getHeaders());
		}else {
			cleanUpUrl(data);
		}
		return ret;
	}
	/**
	 * Application's URLs
	 * @param data
	 * @return
	 */
	private List<TableRow> urlApplRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = urlFilter(data);
		//URL make sense only if sub domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT  \r\n" + 
					"au.url as 'url',\r\n" + 
					"au.prefLabel as 'prefLabel',\r\n" + 
					"au.Lang as 'Lang'\r\n" + 
					"FROM application_urls au\r\n" + 
					") t";
			String where="Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
			where = where +" and url like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getUrls().getHeaders());
		}else {
			cleanUpUrl(data);
		}
		return ret;
	}
	/**
	 * Load URls table rows for dictionaries
	 * @param data
	 * @return
	 */
	private List<TableRow> urlDictRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = urlFilter(data);
		//URL make sense only if sub domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT distinct\r\n" + 
					"du.url as 'url',\r\n" + 
					"du.prefLabel as 'prefLabel',\r\n" + 
					"du.Lang as 'Lang'\r\n" +
					"FROM dict_urls du\r\n" + 
					")t";
			String where="Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
			where = where +" and url like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getUrls().getHeaders());
		}else {
			cleanUpUrl(data);
		}
		return ret;
	}
	/**
	 * Clean up URL table
	 * @param data
	 */
	public void cleanUpUrl(URLAssistantDTO data) {
		//cleanup it if no sub domain selected
		data.getUrls().getRows().clear();
		data.getUrls().getHeaders().getHeaders().clear();
		data.setSelectedUrl("");
	}

	/**
	 * filter for URL
	 * @param data
	 * @return
	 */
	public String urlFilter(URLAssistantDTO data) {
		String filter="";
		if(!data.getOldValue().isEmpty()) {
			filter=data.getOldValue();
			data.setSelectedUrl(filter);
		}else {
			if(!data.getSelectedSubDomain().isEmpty()) {
				filter=data.getSelectedSubDomain();
			}
		}
		return filter;
	}


	/**
	 * Load URls table rows for all urls
	 * @param data
	 * @return
	 */
	private List<TableRow> urlAnyRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = urlFilter(data);
		//URL make sense only if sub domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT distinct\r\n" + 
					"alu.url as 'url',\r\n" + 
					"alu.prefLabel as 'prefLabel',\r\n" + 
					"alu.Lang as 'Lang'  \r\n" + 
					"FROM all_urls alu\r\n" + 
					") t";
			String where="Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
			where = where +" and url like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getUrls().getHeaders());
		}else {
			cleanUpUrl(data);
		}
		return ret;
	}
	/**
	 * build subdomains table
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public URLAssistantDTO subDomainTable(URLAssistantDTO data) throws ObjectNotFoundException {
		data=headersSubDomainTable(data);
		List<TableRow> rows = new ArrayList<TableRow>();
		switch(data.getAssistant()) {
		case URL_ANY:								// Any existing or not existing URL that is suit URL syntax
		case URL_NEW:								//URL should suit the syntax, however does not exist
			rows= subDomainAnyRows(data);
			break;	
		case URL_DICTIONARY_NEW:			// URL that is suit dictionary URL syntax, however does not exist yet
		case URL_DICTIONARY_ALL:			//Any existing or not existing dictionary
			rows= subDomainDictRows(data);
			break;
		case URL_APPLICATION_ALL:			//Any existing or not existing URL for applications
			rows= subDomainApplRows(data);
			break;
		case URL_DATA_ANY:						//Any existing or not existing data configurations
		case URL_DATA_NEW:						//data URL should not be existing
			rows= subDomainDataRows(data);
			break;
		case URL_RESOURCE_NEW:				//A file resource should be new new
			rows= subDomainResourceRows(data);
			break;
		default:
			throw new ObjectNotFoundException("Wrong assistant "+data.getAssistant(), logger);
		}
		prepareTable(data.getSubDomain(),data.getSelectedSubDomain() ,rows);
		urlFieldSet(data);
		return data;
	}

	/**
	 * Sub domains for resources configuration
	 * @param data
	 * @return
	 */
	private List<TableRow> subDomainResourceRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = subDomainFilter(data,2);
		//sub domain make sense only if domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT distinct\r\n" + 
					"SUBSTRING_INDEX(r.url,'.',2) as 'subdomain'\r\n" + 
					"FROM resources r\r\n" + 
					") t";
			String where="subdomain like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getSubDomain().getHeaders());
		}else {
			cleanupSubdomain(data);
		}
		return ret;
	}
	/**
	 * Sub domains for data configurations
	 * @param data
	 * @return
	 */
	private List<TableRow> subDomainDataRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = subDomainFilter(data,2);
		//sub domain make sense only if domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT distinct\r\n" + 
					"	SUBSTRING_INDEX(daurl.url,'.',2) as 'subdomain'\r\n" + 
					"FROM data_urls daurl\r\n" + 
					") t";
			String where="subdomain like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getSubDomain().getHeaders());
		}else {
			cleanupSubdomain(data);
		}
		return ret;
	}
	/**
	 * Appication's subdomains
	 * @param data
	 * @return
	 */
	private List<TableRow> subDomainApplRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = subDomainFilter(data,2);
		//sub domain make sense only if domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT distinct\r\n" + 
					"SUBSTRING_INDEX(apu.url,'.',2) as 'subdomain'\r\n" + 
					"FROM application_urls apu\r\n" + 
					")t";
			String where="subdomain like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getSubDomain().getHeaders());
		}else {
			cleanupSubdomain(data);
		}
		return ret;
	}
	/**
	 * Sub domain for dictionaries
	 * @param data
	 * @return
	 */
	private List<TableRow> subDomainDictRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter=subDomainFilter(data, 3);
		//sub domain make sense only if domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT distinct\r\n" + 
					"SUBSTRING_INDEX(du.url,'.',3) as 'subdomain'\r\n" + 
					"FROM dict_urls du\r\n" + 
					") t";
			String where="subdomain like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getSubDomain().getHeaders());
		}else {
			cleanupSubdomain(data);
		}
		return ret;
	}
	/**
	 * Sub domains for any url
	 * @param data
	 * @return
	 */
	private List<TableRow> subDomainAnyRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		String filter = subDomainFilter(data,2);
		//sub domain make sense only if domain has been selected
		if(!filter.isEmpty()) {
			String select="select * from (\r\n" + 
					"SELECT distinct\r\n" + 
					"SUBSTRING_INDEX(alu.url,'.',2) as 'subdomain' \r\n" + 
					"FROM all_urls alu\r\n" + 
					") t";
			String where="subdomain like '"+filter+"%'";
			ret = jdbcRepo.qtbGroupReport(select, "", where, data.getSubDomain().getHeaders());
		}else {
			//cleanup it if no domain selected
			data=cleanupSubdomain(data);
		}
		return ret;
	}
	/**
	 * Cleanup subdomain data
	 * @param data
	 * @return
	 */
	public URLAssistantDTO cleanupSubdomain(URLAssistantDTO data) {
		data.getSubDomain().getRows().clear();
		data.getSubDomain().getHeaders().getHeaders().clear();
		data.setSelectedSubDomain("");
		return data;
	}
	/**
	 * Subdomain filter
	 * @param data
	 * @param level - for dictionary subdomain 3 for the rest - 2
	 * @return
	 */
	public String subDomainFilter(URLAssistantDTO data, int level) {
		String filter="";
		if(!data.getOldValue().isEmpty()) {
			filter=parseUrl(data.getOldValue(), 2);
			data.setSelectedSubDomain(filter);
		}else {
			if(!data.getSelectedDomain().isEmpty()) {
				filter=data.getSelectedDomain();
			}
		}
		return filter;
	}
	/**
	 * Domains assistance
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public URLAssistantDTO domainTable(URLAssistantDTO data) throws ObjectNotFoundException {
		data=headersDomainTable(data);
		List<TableRow> rows = new ArrayList<TableRow>();
		switch(data.getAssistant()) {
		case URL_ANY:								// Any existing or not existing URL that is suit URL syntax
		case URL_NEW:								//URL should suit the syntax, however does not exist
			rows= domainAnyRows(data);
			break;	
		case URL_DICTIONARY_NEW:			// URL that is suit dictionary URL syntax, however does not exist yet
		case URL_DICTIONARY_ALL:			//Any existing or not existing dictionary
			rows= domainDictRows(data);
			break;
		case URL_APPLICATION_ALL:			//Any existing or not existing URL for applications
			rows= domainApplRows(data);
			break;
		case URL_DATA_ANY:						//Any existing or not existing data configurations
		case URL_DATA_NEW:						//data URL should not be existing
			rows= domainDataRows(data);
			break;
		case URL_RESOURCE_NEW:				//A file resource should be new new 
			rows= domainResourceRows(data);
			break;
		default:
			throw new ObjectNotFoundException("Wrong assistant "+data.getAssistant(), logger);
		}
		prepareTable(data.getDomain(),data.getSelectedDomain() ,rows);
		urlFieldSet(data);
		return data;
	}
	/**
	 * Existing file uploading resources
	 * @param data
	 * @return
	 */
	private List<TableRow> domainResourceRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		if(!data.getOldValue().isEmpty()) {
			String filter=parseUrl(data.getOldValue(), 1);
			for(TableHeader header : data.getDomain().getHeaders().getHeaders()) {
				header.setGeneralCondition(filter);
			}
			data.setSelectedDomain(filter);
		}
		String select="select * from (\r\n" + 
				"SELECT distinct\r\n" + 
				"SUBSTRING_INDEX(r.url,'.',1) as 'domain',\r\n" + 
				"r.Lang as 'Lang'\r\n" + 
				"FROM resources r\r\n" + 
				") t";
		String where = "Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
		ret = jdbcRepo.qtbGroupReport(select, "", where, data.getDomain().getHeaders());
		return ret;
	}

	/**
	 * Existing active data configurations
	 * @param data
	 * @return
	 */
	private List<TableRow> domainDataRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		if(!data.getOldValue().isEmpty()) {
			String filter=parseUrl(data.getOldValue(), 1);
			for(TableHeader header : data.getDomain().getHeaders().getHeaders()) {
				header.setGeneralCondition(filter);
			}
			data.setSelectedDomain(filter);
		}
		String select="select * from (\r\n" + 
				"SELECT distinct\r\n" + 
				"	SUBSTRING_INDEX(daurl.url,'.',1) as 'domain',\r\n" + 
				"    daurl.Lang as 'Lang'\r\n" + 
				"FROM data_urls daurl\r\n" + 
				") t";
		String where = "Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
		ret = jdbcRepo.qtbGroupReport(select, "", where, data.getDomain().getHeaders());
		return ret;
	}

	/**
	 * Application configurations existing
	 * @param data
	 * @return
	 */
	private List<TableRow> domainApplRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		if(!data.getOldValue().isEmpty()) {
			String filter=parseUrl(data.getOldValue(), 1);
			for(TableHeader header : data.getDomain().getHeaders().getHeaders()) {
				header.setGeneralCondition(filter);
			}
			data.setSelectedDomain(filter);
		}
		String select="select * from (\r\n" + 
				"SELECT distinct\r\n" + 
				"SUBSTRING_INDEX(apu.url,'.',1) as 'domain',\r\n" + 
				"apu.Lang as 'Lang'\r\n" + 
				"FROM application_urls apu\r\n" + 
				")t";
		String where = "Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
		ret = jdbcRepo.qtbGroupReport(select, "", where, data.getDomain().getHeaders());
		return ret;
	}

	/**
	 * select list for all dictionaries
	 * @param data
	 * @return
	 */
	private List<TableRow> domainDictRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		if(!data.getOldValue().isEmpty()) {
			String filter=parseUrl(data.getOldValue(), 2);
			for(TableHeader header : data.getDomain().getHeaders().getHeaders()) {
				header.setGeneralCondition(filter);
			}
			data.setSelectedDomain(filter);
		}
		String select="select * from (\r\n" + 
				"SELECT distinct\r\n" + 
				"SUBSTRING_INDEX(du.url,'.',2) as 'domain'\r\n" + 
				"FROM dict_urls du\r\n" + 
				")t";
		String where = "";
		ret = jdbcRepo.qtbGroupReport(select, "", where, data.getDomain().getHeaders());
		return ret;
	}

	/**
	 * Select rows for all domains
	 * @param data
	 * @param elements - how many elements should be taken
	 * @param where limit by the previous choice - domain, sub domain if one
	 * @return
	 */
	private List<TableRow> domainAnyRows(URLAssistantDTO data) {
		List<TableRow> ret = new ArrayList<TableRow>();
		if(!data.getOldValue().isEmpty()) {
			String filter=parseUrl(data.getOldValue(), 1);
			for(TableHeader header : data.getDomain().getHeaders().getHeaders()) {
				header.setGeneralCondition(filter);
			}
			data.setSelectedDomain(filter);
		}
		String select="select * from (\r\n" + 
				"SELECT distinct\r\n" + 
				"SUBSTRING_INDEX(alu.url,'.',1) as 'domain' \r\n" + 
				"FROM all_urls alu\r\n" + 
				") t";
		ret = jdbcRepo.qtbGroupReport(select, "", "", data.getDomain().getHeaders());
		return ret;
	}

	/**
	 * Create headers for domain table and put 
	 * @param data
	 * @return
	 */
	private URLAssistantDTO headersDomainTable(URLAssistantDTO data) {
		List<TableHeader> headers = data.getDomain().getHeaders().getHeaders();
		if(headers.isEmpty()) {
			headers.add(TableHeader.instanceOf(
					"domain",
					messages.get("domain"),
					true,
					true,
					true,
					TableHeader.COLUMN_STRING,
					0));
			headers.get(0).setSortValue(TableHeader.SORT_ASC);
		}
		return data;
	}


	/**
	 * Provide a hint for editable URL field if this field is empty
	 * @param data
	 * @return
	 */
	private URLAssistantDTO urlFieldSet(URLAssistantDTO data) {
		//first time for old URL, if one
		if(!data.getOldValue().isEmpty()) {
			data.getUrl().setValue(data.getOldValue());
		}else {
			//use the assistant
			if(!data.getSelectedDomain().isEmpty()) {
				data.getUrl().setValue(data.getSelectedDomain());
			}else {
				data.getUrl().setValue("");
			}
			if(!data.getSelectedSubDomain().isEmpty()) {
				data.getUrl().setValue(data.getSelectedSubDomain());
			}
			if(!data.getSelectedUrl().isEmpty()) {
				data.getUrl().setValue(data.getSelectedUrl());
			}
		}
		return data;
	}

	/**
	 * Headers for sub domains
	 * @param data
	 * @param oldSubDomain 
	 * @return
	 */
	public URLAssistantDTO headersSubDomainTable(URLAssistantDTO data) {
		if(data.getSubDomain().getHeaders().getHeaders().isEmpty() ) {
			data.getSubDomain().getHeaders().getHeaders().add(TableHeader.instanceOf(
					"subdomain",
					messages.get("subdomain"),
					true,
					true,
					true,
					TableHeader.COLUMN_STRING,
					0));
			data.getSubDomain().getHeaders().getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		}
		return data;
	}

	/**
	 * Headers for sub domains
	 * @param data
	 * @param oldSubDomain 
	 * @return
	 */
	public URLAssistantDTO headersUrlTable(URLAssistantDTO data) {
		if(data.getUrls().getHeaders().getHeaders().isEmpty() ) {
			data.getUrls().getHeaders().getHeaders().add(TableHeader.instanceOf(
					"url",
					messages.get("url"),
					true,
					true,
					true,
					TableHeader.COLUMN_STRING,
					0));
			data.getUrls().getHeaders().getHeaders().add(TableHeader.instanceOf(
					"prefLabel",
					messages.get("prefLabel"),
					true,
					true,
					true,
					TableHeader.COLUMN_STRING,
					0));
			data.getUrls().getHeaders().getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		}
		return data;
	}

	/**
	 * Parse old URL value to get first search criteria
	 * @param data
	 * @param size
	 * @return
	 */
	public String parseUrl(String url, int size) {
		String ret="";
		if(url != null) {
			if(!url.isEmpty()) {
				List<String> parsed=Arrays.asList(url.split("\\."));
				if(parsed.size()>=size) {
					ret=String.join(".",parsed.subList(0, size));
				}else {
					ret=url;
				}
			}
		}
		return ret;
	}

	/**
	 * Prepare rows for domain/subdomain tables
	 * @param data
	 * @param rows
	 */
	public void prepareTable(TableQtb table, String selectedUrl, List<TableRow> rows) {
		if(!selectedUrl.isEmpty()) {
			for(TableRow row : rows) {
				if(row.getRow().get(0).getValue().equalsIgnoreCase(selectedUrl)) {
					row.setSelected(true);
				}
			}
		}else {
			for(TableRow row : rows) {
				row.setSelected(false);
			}
		}
		TableQtb.tablePage(rows, table);
	}
	/**
	 * Validate URL selected using assistant 
	 * empty URL is allowed always
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public URLAssistantDTO validate(URLAssistantDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data.getUrl().clearValidation();
		String url=data.getUrl().getValue();
		if(!url.isEmpty()) {
			if(isValidURL(url) && (!isForbiddenUrl(url) || isDictionaryAssistant(data.getAssistant()))) {
				switch(data.getAssistant()) {
				case URL_DICTIONARY_NEW:
					if(!isNewURL(url)) {
						data.getUrl().invalidate(messages.get("url_exists"));
						break;
					}
					if(!isDictionaryUrl(url)) {
						data.getUrl().invalidate(messages.get("url_dictionary_error"));
						break;
					}
					break;
				case URL_DICTIONARY_ALL:
					if(!isDictionaryUrl(url)) {
						data.getUrl().invalidate(messages.get("url_dictionary_error"));
						break;
					}
					if(!isRootUrl(url)) {
						data.getUrl().invalidate(messages.get("notexisting_dictionary_error"));
						break;
					}
					break;
				case URL_DATA_NEW:
				case URL_RESOURCE_NEW:
					if(!isNewURL(url)) {
						data.getUrl().invalidate(messages.get("url_exists"));
						break;
					}
					break;
				case URL_DATA_ANY:
					if(!isDataConfigurationUrl(url)) {
						data.getUrl().invalidate(messages.get("notdataurl"));
					}
					break;
				default:
					//nothing to do yet
				}
			}else {
				data.getUrl().invalidate(messages.get("error_url"));
				if(isForbiddenUrl(url) && !isDictionaryAssistant(data.getAssistant())) {
					data.getUrl().invalidate(url+" "+messages.get("forbiddendomain") + " ("+FORBIDDEN_DOMAINS + ")");
				}
			}
		}
		data.propagateValidation();
		return data;
	}
	/**
	 * Is it dictionary related assistant?
	 * @param assistant
	 * @return
	 */
	private boolean isDictionaryAssistant(AssistantEnum assistant) {
		return assistant.equals(AssistantEnum.URL_DICTIONARY_ALL)
				|| assistant.equals(AssistantEnum.URL_DICTIONARY_NEW);
	}
	/**
	 * Is this URL belongs to resources
	 * @param url
	 * @return
	 */
	private boolean isResourceUrl(String url) {
		if(url != null) {
			List<Concept> confList = closureServ.loadAllConceptsByIdentifier(url);
			for(Concept conf : confList) {
				Concept root = closureServ.getParent(conf);
				if(root != null) {
					if(root.getIdentifier().equalsIgnoreCase(SystemService.RESOURCES_COLLECTIONS_ROOT)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * Is this URL belongs to the configuration.data tree
	 * @param url
	 * @return
	 */
	public boolean isDataConfigurationUrl(String url) {
		if(url != null) {
			List<Concept> confList = closureServ.loadAllConceptsByIdentifier(url);
			for(Concept conf : confList) {
				Concept root = closureServ.getParent(conf);
				if(root != null) {
					if(root.getIdentifier().equalsIgnoreCase(SystemService.DATA_COLLECTIONS_ROOT)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * Data URLs cannot belongs to some domains
	 * @param url
	 * @return
	 */
	private boolean isForbiddenUrl(String url) {
		if(url != null) {
			//return FORBIDDEN_DOMAINS.contains(parseUrl(url,1).toLowerCase()); 2024-03-28
			List<String> forbidden = FORBIDDEN_DOMAINS.stream()
					.filter(e->{
						return url.toUpperCase().startsWith(e.toUpperCase());
					})
					.collect(Collectors.toList());
			return !forbidden.isEmpty();
		}
		return true;	//null domain is forbidden
	}

	/**
	 * Is this dictionary URL?
	 * @param url false in case url is null or url is not a dictionary url
	 * @return
	 */
	private boolean isDictionaryUrl(String url) {
		if(url != null) {
			return url.toUpperCase().startsWith("DICTIONARY.");
		}
		return false;
	}
	/**
	 * Is this URL valid?
	 * URL in terms of OpenRIMS, not  RFC 3987
	 * @param url
	 * @return
	 */
	public boolean isValidURL(String url) {
		String URL_REGEX =
				"[A-Za-z0-9_]+((\\.)?[A-Za-z0-9_]+)*";
		Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
		String value=url;
		if(url.startsWith("http")) {
			String[] parts=url.split("://");
			if(parts.length==2) {
				value=parts[1];
			}
			try {
				new URL(url).toURI();
				return true;
			} catch (MalformedURLException e) {
				return false;
			} catch (URISyntaxException e) {
				return false;
			}
		}else {
			if(!ValidationService.REGEX.matcher(value).find()) {
				Matcher urlMatcher = URL_PATTERN.matcher(value);
				return urlMatcher.matches();
			}else {
				return false;
			}
		}
	}
	/**
	 * Is this url new in the database?
	 * @param url
	 * @param tableQtb 
	 * @return
	 */
	@Transactional
	public boolean isNewURL(String url) {
		List<Concept> concepts = closureServ.loadAllConceptsByIdentifier(url);
		List<Concept> active = concepts.stream()
				.filter(e->{
					return e.getActive();
				})
				.collect(Collectors.toList());
		return active.isEmpty();
	}
	/**
	 * Preview a dictionary or a thing under the URL
	 * @param data
	 * @param user 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public URLAssistantDTO preview(URLAssistantDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//clear all previews
		data.setPreviewDict(new DictionaryDTO());
		data.setPreviewThing(new ThingDTO());
		data.setPreviewOther("");
		if(!data.getUrl().getValue().isEmpty()) {
			if(thingServ.isThingUrl(data.getUrl().getValue()) ) {
				data.getPreviewThing().setUrl(data.getUrl().getValue());
				data.setPreviewThing(thingServ.createThing(data.getPreviewThing(), user));
			}
			if(resourceServ.isResourceUrl(data.getUrl().getValue())) {
				String thingUrl = resourceServ.dataConfigUrl(data.getUrl().getValue());
				if(thingServ.isThingUrl(thingUrl) ) {
					data.getPreviewThing().setUrl(thingUrl);
					data.setPreviewThing(thingServ.createThing(data.getPreviewThing(), user));
				}else {
					data.setPreviewOther(messages.get("previewunavailable"));
				}
			}else {
				if(isDictionaryUrl(data.getUrl().getValue()) && isRootUrl(data.getUrl().getValue())) {
					data.getPreviewDict().setUrl(data.getUrl().getValue());
					data.setPreviewDict(dictService.createDictionary(data.getPreviewDict()));
				}else {
					data.setPreviewOther(messages.get("previewunavailable"));
				}
			}
		}else {
			data.setPreviewOther(messages.get("previewunavailable"));
		}
		return data;
	}

	/**
	 * is it root url?
	 * @param url
	 * @return
	 */
	@Transactional
	public boolean isRootUrl(String url) {
		boolean ret=false;
		List<Concept> concepts = closureServ.loadAllConceptsByIdentifier(url);
		for(Concept concept :concepts) {
			if(concept.getActive()) {
				Concept parent = closureServ.getParent(concept);
				return parent==null;
			}
		}
		return ret;
	}

}
