package org.msh.pharmadex2.service.r2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.AssemblyRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.LayoutCellDTO;
import org.msh.pharmadex2.dto.LayoutRowDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for entity assembly issues such are dictionaries bound, root concept URLs, etc 
 * @author alexk
 *
 */
@Service
public class AssemblyService {

	public static final String CONCURRENTURL = "concurrenturl";

	public static final String HIDECHECKLIST_FLD = "hidechecklist";
	public static final String DATAIMPORT_ADDRESS = "dataimport_address";
	public static final String DATAIMPORT_RESULT = "dataimport_result";
	public static final String DATAIMPORT_SYSTEM_RESULT = "dataimport_system_result";
	public static final String DATAIMPORT_DATA = "dataimport";
	public static final String DATAIMPORT_DATA_ERROR = "dataimporterror";
	public static final String SYSTEM_IMPORT_ADMINUNITS="system.import.adminunits";
	public static final String SYSTEM_IMPORT_ADMINUNITS_RELOAD="system.import.adminunits_reload";
	public static final String ACTIVITY_EXECUTIVES = "executives";
	public static final String ACTIVITY_CONFIG_FINALIZE = "finalize";
	public static final String ACTIVITY_CONFIGURATION = "activity.configuration";
	private static final String OBJECT_SITE_CLASSIFIERS = "object.site.classifiers";
	private static final Logger logger = LoggerFactory.getLogger(AssemblyService.class);
	public static final String SYSTEM_IMPORT_LEGACY_DATA = "system.import.legacy.data";

	public static final String SYSTEM_CHANGEPASS_ADMIN="system.changepassadmin";
	public static final String CHANGEPASS_NEWPASS = "changepass";
	public static final String CHANGEPASS_NEWPASS_REPEAT = "changepass_repeat";

	public static final String SYSTEM_IMPORT_ATCCODES = "system.import.atccodes";
	public static final String SYSTEM_IMPORT_ATCCODES_RELOAD = SYSTEM_IMPORT_ATCCODES + "_reload";
	public static final String DATAIMPORT_CODES = "dataimport_codes";
	//dictionary for file import
	public static final String SYSTEM_IMPORT_DICTIONARY="system.import.dictionary";
	//electronic form for data configuration import
	public static final String SYSTEM_IMPORT_DATA_CONFIGURATION="system.import.data.configuration";
	//electronic form for data workflows import
	public static final String SYSTEM_IMPORT_DATA_WORKFLOW="system.import.data.workflow";

	public static final String SYSTEM_IMPORT_LOCALES="system.import.locales";

	@Autowired
	private ClosureService closureServ;
	@Autowired
	Messages messages;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private AssemblyRepo assmRepo;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private AccessControlService accessServ;

	/**
	 * Create a list of assemblyDTO from stored list of  AssemblyDTO for class clazz
	 * @param dataConfigStored
	 * @param clazz
	 * @param ret
	 * @return
	 */
	private List<AssemblyDTO> auxFromDataConfigStored(List<AssemblyDTO> dataConfigStored, String clazz,
			List<AssemblyDTO> ret) {
		for(AssemblyDTO dto : dataConfigStored) {
			if(dto.getClazz().equalsIgnoreCase(clazz)) {
				ret.add(dto);
			}
		}
		return ret;
	}
	/**
	 * Create a list of assemblyDTO from assembly entities for class clazz
	 * @param assms
	 * @param clazz
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public List<AssemblyDTO> auxFromAssembly(List<Assembly> assms, String clazz, List<AssemblyDTO> ret) throws ObjectNotFoundException {
		for(Assembly assm : assms) {
			if(assm.getClazz().equalsIgnoreCase(clazz)) {
				ret.add(dtoServ.assemblyDto(assm));
			}
		}
		return ret;
	}

	/**
	 * Auxiliary literals for a user (MOCK!!!)
	 * @return
	 */
	public List<AssemblyDTO> auxUserLiterals() {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		AssemblyDTO fld = new AssemblyDTO();
		fld.setRequired(true);
		fld.setPropertyName("salutation");
		ret.add(fld);
		return ret;
	}
	/**
	 * Load headings defined
	 * @param url
	 * @param assms 
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxHeadings(String url, List<Assembly> assms, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.toUpperCase().startsWith(ACTIVITY_CONFIGURATION.toUpperCase())) {
			AssemblyDTO doc1 = new AssemblyDTO(); 
			doc1.setPropertyName("workflowguide");
			doc1.setUrl("/api/admin/manual/workflow"); 
			ret.add(doc1); 
		}
		// irka
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS)) { 
			AssemblyDTO doc1 = new AssemblyDTO(); 
			doc1.setPropertyName("templateupload");
			doc1.setUrl("/shablon/TemplateExample.xlsx"); 
			ret.add(doc1); 
			AssemblyDTO doc2 = new AssemblyDTO(); 
			doc2.setPropertyName("readmefirst");
			doc2.setUrl("/shablon/AdminUnitsImportGuide.pdf"); 
			ret.add(doc2); 
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ATCCODES)) { 
			AssemblyDTO doc1 = new AssemblyDTO(); 
			doc1.setPropertyName("templateupload");
			doc1.setUrl("/shablon/ATC_DDD_2021_WHO.xlsx"); 
			ret.add(doc1); 
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_LEGACY_DATA)) {
			AssemblyDTO doc1 = new AssemblyDTO(); 
			doc1.setPropertyName("templateupload");
			doc1.setUrl("/shablon/LegacyImportTemplate.xlsx"); 
			ret.add(doc1); 
		}
		//*********************************** read from configuration ***************************************************
		if(ret.size()==0) {
			//List<Assembly> assms =loadDataConfiguration(url);
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("heading")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assms, "heading", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "heading", ret);
			}
		}
		return ret;
	}
	/**
	 * Create auxiliary strings, unlike literals strings are without language separation
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxStrings(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
	
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_DATA_CONFIGURATION)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setMin(BigDecimal.ZERO);
			fld.setMax(BigDecimal.ZERO);
			fld.setReadOnly(true);
			fld.setTextArea(true);
			fld.setPropertyName("description");
			ret.add(fld);	
		}
		if(url.equalsIgnoreCase(ACTIVITY_CONFIGURATION)) {
			BigDecimal min = BigDecimal.valueOf(3l);
			BigDecimal max= BigDecimal.valueOf(80l);
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setMin(min);
				fld.setMax(max);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setPropertyName("activityurl");
				fld.setAssistant(AssistantEnum.URL_ACTIVITY);
				ret.add(fld);	
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setPropertyName("checklisturl");
				fld.setAssistant(AssistantEnum.URL_DICTIONARY_ALL);
				ret.add(fld);	
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setPropertyName("dataurl");
				fld.setAssistant(AssistantEnum.URL_DATA_ANY);
				ret.add(fld);	
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setPropertyName("addressurl");
				ret.add(fld);	
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setAssistant(AssistantEnum.URL_HOST);
				fld.setPropertyName(CONCURRENTURL);
				ret.add(fld);	
			}
			{// notes to mail 26.11.2022
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(true);
				fld.setPropertyName("attnote");
				ret.add(fld);
			}
		}
		// stored or uploaded from the database
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "strings", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored,"strings", ret);
			}
		}
		return ret;
	}

	/**
	 * Create empty auxiliary literals for the url given
	 * @param url url given
	 * @param dataConfigStored 
	 * @param activityName 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxLiterals(String url, List<Assembly> assms, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.toUpperCase().startsWith("DICTIONARY.")) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(true);
				fld.setTextArea(false);
				fld.setPropertyName("URL");
				fld.setAssistant(AssistantEnum.URL_ANY);
				ret.add(fld);	
			}
		}

		// Configure a activity constructor. Not a mock!
		if(url.toUpperCase().startsWith(ACTIVITY_CONFIGURATION.toUpperCase())) {
			BigDecimal min = BigDecimal.valueOf(3l);
			BigDecimal max= BigDecimal.valueOf(80l);
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setMin(min);
				fld.setMax(max);
				fld.setPropertyName("prefLabel");
				ret.add(fld);	
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setReadOnly(false);
				fld.setTextArea(true);
				fld.setPropertyName("description");
				ret.add(fld);	
			}

		}

		if(url.equalsIgnoreCase(SystemService.DICTIONARY_HOST_APPLICATIONS)){
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setReadOnly(true);
				fld.setPropertyName("applicationurl");
				fld.setAssistant(AssistantEnum.URL_APPLICATION_ALL);
				ret.add(fld);
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(true);
				fld.setPropertyName("dataurl");
				fld.setAssistant(AssistantEnum.URL_DATA_ANY);
				ret.add(fld);
			}
		}

		if(url.equalsIgnoreCase(SystemService.DICTIONARY_SHUTDOWN_APPLICATIONS)) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setReadOnly(true);
				fld.setPropertyName("applicationurl");
				fld.setAssistant(AssistantEnum.URL_APPLICATION_ALL);
				ret.add(fld);
			}
		}

		/**
		 * Applications implemented
		 */
		if(url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_APPLICATIONS)
				|| url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_AMENDMENTS)
				|| url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_DEREGISTRATION)
				|| url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_INSPECTIONS)
				) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setReadOnly(true);
				fld.setPropertyName("applicationurl");
				fld.setAssistant(AssistantEnum.URL_APPLICATION_ALL);
				ret.add(fld);
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setReadOnly(true);
				fld.setPropertyName("dataurl");
				fld.setAssistant(AssistantEnum.URL_DATA_ANY);
				ret.add(fld);
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setReadOnly(true);
				fld.setTextArea(false);
				fld.setAssistant(AssistantEnum.URL_DICTIONARY_ALL);
				fld.setPropertyName("checklisturl");
				ret.add(fld);	
			}
		}


		// Tiles
		if(url.equalsIgnoreCase(SystemService.ROOT_SYSTEM_TILES)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setPropertyName(LiteralService.ICON_URL); // url by icon Tile
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setPropertyName(LiteralService.MORE_URL); // url by click Join Us
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setPropertyName(LiteralService.MORE_LBL); // text "more.." , "Join Us"
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setPropertyName(LiteralService.LAYOUT); // the place in the 3xN greed
			ret.add(fld);
		}

		//Address related
		if(url.equalsIgnoreCase(SystemService.DICTIONARY_ADMIN_UNITS)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setPropertyName(LiteralService.GIS_LOCATION); 
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setPropertyName(LiteralService.ZOMM); 
			ret.add(fld);
		}

		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS) || url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS_RELOAD)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setPropertyName(DATAIMPORT_RESULT);
			fld.setReadOnly(true);
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setPropertyName(DATAIMPORT_SYSTEM_RESULT);
			fld.setReadOnly(true);
			ret.add(fld);
		}
		if(url.equalsIgnoreCase(SYSTEM_CHANGEPASS_ADMIN)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setPropertyName(CHANGEPASS_NEWPASS);
			fld.setReadOnly(false);
			fld.setMult(false);
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setPropertyName(CHANGEPASS_NEWPASS_REPEAT);
			fld.setReadOnly(false);
			fld.setMult(false);
			ret.add(fld);
		}

		if(url.equalsIgnoreCase(SYSTEM_IMPORT_LEGACY_DATA)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setPropertyName(DATAIMPORT_RESULT);
			fld.setReadOnly(true);
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setPropertyName(DATAIMPORT_SYSTEM_RESULT);
			fld.setReadOnly(true);
			ret.add(fld);
		}

		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ATCCODES) || url.equalsIgnoreCase(SYSTEM_IMPORT_ATCCODES_RELOAD)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setPropertyName(DATAIMPORT_RESULT);
			fld.setReadOnly(true);
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setPropertyName(DATAIMPORT_SYSTEM_RESULT);
			fld.setReadOnly(true);
			ret.add(fld);
		}

		// stored or uploaded from the database
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assms, "literals", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored,"literals", ret);
			}
		}
		return ret;
	}

	/**
	 * Load class configuration for the url given
	 * @param url
	 * @param clazz
	 * @return only configured definitions, without defaults!!!!
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> aux(String url, String clazz) throws ObjectNotFoundException{
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		List<Assembly> assms = loadDataConfiguration(url);
		for(Assembly assm : assms) {
			if(assm.getClazz().equalsIgnoreCase(clazz)) {
				ret.add(dtoServ.assemblyDto(assm));
			}
		}
		return ret;
	}
	/**
	 * Addresses to include
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @param objectUrl
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxAddresses(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// irka
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS)){ 
			AssemblyDTO addr = new AssemblyDTO(); 
			addr.setPropertyName(DATAIMPORT_ADDRESS);
			addr.setUrl("address.tests"); 
			ret.add(addr); 
		}
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "addresses", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "addresses", ret);
			}
		}
		return ret;
	}

	/**
	 * Dates for screen forms
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @param activity
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxDates(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//... possible hardcoded
		if(ret.isEmpty()) {
			// stored or uploaded from the database
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "dates", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored,"dates", ret);
			}
		}
		return ret;
	}
	/**
	 * Decimal number fields
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxNumbers(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_APPLICATIONS)) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setPropertyName("expireinmonths");
				ret.add(fld);	
			}
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_DATA_WORKFLOW)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setReadOnly(true);
			fld.setTextArea(false);
			fld.setPropertyName("dataID");
			ret.add(fld);	
		}
		if(ret.size()==0) {
			// stored or uploaded from the database
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "numbers", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored,"numbers", ret);
			}
		}
		return ret;
	}

	/**
	 * Logical fields
	 * @param url
	 * @param assemblies
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public List<AssemblyDTO> auxLogicals(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.equalsIgnoreCase(ACTIVITY_CONFIGURATION)) {
			{
				AssemblyDTO assm = new AssemblyDTO();
				assm.setPropertyName("background");
				ret.add(assm);
			}
			{
				AssemblyDTO assm = new AssemblyDTO();
				assm.setPropertyName("attention");
				ret.add(assm);
			}
			{
				AssemblyDTO assm = new AssemblyDTO();
				assm.setPropertyName(HIDECHECKLIST_FLD);
				ret.add(assm);
			}
		}
		// stored or uploaded from the database
		if(dataConfigStored.isEmpty()) {
			ret = auxFromAssembly(assemblies, "logical", ret);
		}else {
			ret=auxFromDataConfigStored(dataConfigStored,"logical", ret);
		}
		return ret;
	}
	/**
	 * Files that should be uploaded/accessible for the activity of an application defined by the  url
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @return map with 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxDocuments(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException{
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS) 
				|| url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS_RELOAD)) {
			AssemblyDTO assm = new AssemblyDTO();
			assm.setPropertyName(DATAIMPORT_DATA);
			assm.setUrl("data.import");
			assm.setDescription(messages.get("pleaseuploadimportdata"));
			assm.setRequired(false);
			assm.setFileTypes(".xlsx");
			assm.setDictUrl(SystemService.DICTIONARY_SYSTEM_IMPORT_DATA);
			ret.add(assm);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_LEGACY_DATA)) {
			AssemblyDTO assm = new AssemblyDTO();
			assm.setDictUrl(SystemService.DICTIONARY_SYSTEM_IMPORT_DATA);
			assm.setFileTypes(".xlsx");
			assm.setMax(new BigDecimal(0));
			assm.setMin(new BigDecimal(0));
			assm.setMult(false);
			assm.setUnique(false);
			assm.setPropertyName(DATAIMPORT_DATA);
			assm.setDescription(messages.get("pleaseuploadimportdata"));
			assm.setReadOnly(false);
			assm.setRequired(false);
			assm.setTextArea(false);
			assm.setUrl("data.import");
			assm.setAuxDataUrl("");
			ret.add(assm);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ATCCODES) || url.equalsIgnoreCase(SYSTEM_IMPORT_ATCCODES_RELOAD)) {
			AssemblyDTO assm = new AssemblyDTO();
			assm.setPropertyName(DATAIMPORT_DATA);
			assm.setUrl("data.import");
			assm.setDescription(messages.get("pleaseuploadimportdata"));
			assm.setRequired(true);
			assm.setFileTypes(".xlsx");
			assm.setDictUrl(SystemService.DICTIONARY_SYSTEM_IMPORT_DATA);
			ret.add(assm);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_DICTIONARY)) {
			AssemblyDTO assm = new AssemblyDTO();
			assm.setPropertyName(DATAIMPORT_DATA);
			assm.setUrl("data.import");
			assm.setDescription(messages.get("pleaseuploadimportdata"));
			assm.setRequired(true);
			assm.setFileTypes(".xlsx");
			assm.setDictUrl("dictionary.system.import");
			ret.add(assm);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_DATA_CONFIGURATION) || url.equalsIgnoreCase(SYSTEM_IMPORT_DATA_WORKFLOW)) {
			AssemblyDTO assm = new AssemblyDTO();
			assm.setPropertyName("import_electronic_form");
			assm.setUrl("data.import");
			assm.setDescription(messages.get("pleaseuploadimportdata"));
			assm.setRequired(false);
			assm.setFileTypes(".xlsx");
			assm.setDictUrl(SystemService.DICTIONARY_SYSTEM_IMPORT_DATA);
			ret.add(assm);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_LOCALES)) {
			AssemblyDTO assm = new AssemblyDTO();
			assm.setPropertyName("files");
			assm.setUrl("data.import.files");
			assm.setDescription(messages.get("pleaseuploadimportdata"));
			assm.setFileTypes(".xlsx");
			assm.setRequired(false);
			//assm.setMult(true);
			assm.setDictUrl(SystemService.DICTIONARY_SYSTEM_LOCALES);
			ret.add(assm);

			AssemblyDTO assm1 = new AssemblyDTO();
			assm1.setPropertyName("flags");
			assm1.setUrl("data.import.flags");
			assm1.setDescription(messages.get("pleaseuploadimportdata"));
			assm1.setRequired(false);
			assm1.setFileTypes(".svg");
			assm1.setDictUrl(SystemService.DICTIONARY_SYSTEM_LOCALES);
			ret.add(assm1);
		}

		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "documents", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "documents", ret);
			}
		}
		return ret;
	}
	/**
	 * File resources - references and templates
	 * @param url
	 * @param assemblies 
	 * @param storedConfig 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxResources(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//possible hardcoded
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "resources", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "resources", ret);
			}
		}
		return ret;
	}
	/**
	 * Additional things to fill out
	 * @param url application URL
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxThings(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// possible hardcoded
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "things", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "things", ret);
			}
		}
		return ret;
	}

	/**
	 * Get urls of all configured dictionaries
	 * @return
	 */
	@Transactional
	public List<String> allDictUrls() {
		List<String> ret = new ArrayList<String>();
		List<Concept> allRoots = closureServ.allRoots();
		if(allRoots != null) {
			for(Concept conc : allRoots) {
				if(conc.getIdentifier().toUpperCase().startsWith("DICTIONARY")) {
					ret.add(conc.getIdentifier());
				}
			}

		}
		return ret;
	}


	/**
	 * Labels for fields "prefLabel" and "description" are depends on URL of an entity  
	 * @param url
	 * @return
	 */
	public Map<String, String> mainLabelsByUrl(String url) {
		Map<String,String> ret = new HashMap<String, String>();

		if(url.equalsIgnoreCase(SystemService.ROOT_SYSTEM_TILES)) {
			ret.put("prefLabel", messages.get("rolename"));
			//ret.put("description", messages.get("businessdetails"));
		}

		return ret;
	}

	/**
	 * 
	 * @param url
	 * @param activityName 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<LayoutRowDTO> formLayout(String url) throws ObjectNotFoundException {
		List<LayoutRowDTO> ret = new ArrayList<LayoutRowDTO>();
		if(url.equalsIgnoreCase(ACTIVITY_CONFIGURATION)) {
			{
				LayoutRowDTO row= new LayoutRowDTO();
				LayoutCellDTO cell1 = new LayoutCellDTO();
				cell1.getVariables().add("workflowguide");
				cell1.getVariables().add("prefLabel");
				cell1.getVariables().add("description");
				cell1.getVariables().add("activityurl");
				cell1.getVariables().add("checklisturl");
				cell1.getVariables().add(HIDECHECKLIST_FLD);	//2023-02-13
				cell1.getVariables().add("dataurl");
				cell1.getVariables().add("addressurl");
				cell1.getVariables().add(CONCURRENTURL);			//2023-05-04
				cell1.getVariables().add("attention");
				cell1.getVariables().add("attnote");// notes to mail 26.11.2022
				row.getCells().add(cell1);

				LayoutCellDTO cell2 = new LayoutCellDTO();
				cell2.getVariables().add(ACTIVITY_EXECUTIVES);
				cell2.getVariables().add("background");
				cell2.getVariables().add(ACTIVITY_CONFIG_FINALIZE);
				row.getCells().add(cell2);

				ret.add(row);
			}
		}
		if(url.equalsIgnoreCase(OBJECT_SITE_CLASSIFIERS)) {
			{
				LayoutRowDTO row= new LayoutRowDTO();
				LayoutCellDTO cell1 = new LayoutCellDTO();
				cell1.getVariables().add("pharmacytype");
				cell1.getVariables().add("documents");
				row.getCells().add(cell1);
				LayoutCellDTO cell2 = new LayoutCellDTO();
				cell2.getVariables().add("pharmacybusiness");
				row.getCells().add(cell2);
				ret.add(row);
			}
		}

		if(url.equalsIgnoreCase(SystemService.ROOT_SYSTEM_TILES)) {
			LayoutRowDTO row_1 = new LayoutRowDTO();
			LayoutCellDTO cell_11 = new LayoutCellDTO();
			cell_11.getVariables().add("tile0");
			LayoutCellDTO cell_12 = new LayoutCellDTO();
			cell_12.getVariables().add("tile1");
			row_1.getCells().add(cell_11);
			row_1.getCells().add(cell_12);
			ret.add(row_1);

			LayoutRowDTO row_2 = new LayoutRowDTO();
			LayoutCellDTO cell_21 = new LayoutCellDTO();
			cell_21.getVariables().add("tile2");
			LayoutCellDTO cell_22 = new LayoutCellDTO();
			cell_22.getVariables().add("tile3");
			LayoutCellDTO cell_23 = new LayoutCellDTO();
			cell_23.getVariables().add("tile4");
			row_2.getCells().add(cell_21);
			row_2.getCells().add(cell_22);
			row_2.getCells().add(cell_23);
			ret.add(row_2);
		}

		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS)) {
			//file uploader and r/o field for messages output
			LayoutRowDTO row= new LayoutRowDTO();
			LayoutCellDTO cell1 = new LayoutCellDTO();
			cell1.getVariables().add("readmefirst");
			cell1.getVariables().add("templateupload");
			cell1.getVariables().add(DATAIMPORT_DATA);
			cell1.getVariables().add(DATAIMPORT_RESULT);
			row.getCells().add(cell1);
			//address
			LayoutCellDTO cell2 = new LayoutCellDTO();
			cell2.getVariables().add(DATAIMPORT_ADDRESS);
			row.getCells().add(cell2);
			ret.add(row);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS_RELOAD)) {
			//file uploader and r/o field for messages output
			LayoutRowDTO row= new LayoutRowDTO();
			LayoutCellDTO cell1 = new LayoutCellDTO();
			cell1.getVariables().add(DATAIMPORT_DATA);
			cell1.getVariables().add(DATAIMPORT_RESULT);
			row.getCells().add(cell1);
			//address
			ret.add(row);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_LEGACY_DATA)) {
			//file uploader and r/o field for messages output
			LayoutRowDTO row= new LayoutRowDTO();
			LayoutCellDTO cell1 = new LayoutCellDTO();
			cell1.getVariables().add("templateupload");
			cell1.getVariables().add(DATAIMPORT_DATA);
			row.getCells().add(cell1);
			LayoutCellDTO cell2 = new LayoutCellDTO();
			cell2.getVariables().add(DATAIMPORT_RESULT);
			row.getCells().add(cell2);
			ret.add(row);
		}

		if(url.equalsIgnoreCase(SYSTEM_CHANGEPASS_ADMIN)) {
			//file uploader and r/o field for messages output
			LayoutRowDTO row = new LayoutRowDTO();
			LayoutCellDTO cell = new LayoutCellDTO();
			cell.getVariables().add(CHANGEPASS_NEWPASS);
			row.getCells().add(cell);
			ret.add(row);

			row = new LayoutRowDTO();
			cell = new LayoutCellDTO();
			cell.getVariables().add(CHANGEPASS_NEWPASS_REPEAT);
			row.getCells().add(cell);
			ret.add(row);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ATCCODES)) {
			//file uploader and r/o field for messages output
			LayoutRowDTO row= new LayoutRowDTO();
			LayoutCellDTO cell1 = new LayoutCellDTO();
			cell1.getVariables().add("templateupload");
			cell1.getVariables().add(DATAIMPORT_DATA);
			cell1.getVariables().add(DATAIMPORT_RESULT);
			row.getCells().add(cell1);
			//address
			LayoutCellDTO cell2 = new LayoutCellDTO();
			cell2.getVariables().add(DATAIMPORT_CODES);
			row.getCells().add(cell2);
			ret.add(row);
		} 
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ATCCODES_RELOAD)) {
			//file uploader and r/o field for messages output
			LayoutRowDTO row= new LayoutRowDTO();
			LayoutCellDTO cell1 = new LayoutCellDTO();
			cell1.getVariables().add(DATAIMPORT_DATA);
			cell1.getVariables().add(DATAIMPORT_RESULT);
			row.getCells().add(cell1);

			ret.add(row);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_DICTIONARY)) {
			//file uploader and r/o field for messages output
			LayoutRowDTO row= new LayoutRowDTO();
			LayoutCellDTO cell1 = new LayoutCellDTO();
			cell1.getVariables().add(DATAIMPORT_DATA);
			row.getCells().add(cell1);
			ret.add(row);
		}
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_DATA_CONFIGURATION)|| url.equalsIgnoreCase(SYSTEM_IMPORT_DATA_WORKFLOW) ) {
			LayoutRowDTO row= new LayoutRowDTO();
			LayoutCellDTO cell1 = new LayoutCellDTO();
			cell1.getVariables().add("import_electronic_form");	//file uploader
			cell1.getVariables().add("description");						//import data diagnostic
			cell1.getVariables().add("dataID");
			row.getCells().add(cell1);
			ret.add(row);
		}
		if(url.equals(SYSTEM_IMPORT_LOCALES)) {
			LayoutRowDTO row = new LayoutRowDTO();
			LayoutCellDTO cell1 = new LayoutCellDTO();
			cell1.getVariables().add("templatedownload");
			cell1.getVariables().add("locales");
			cell1.getVariables().add("flags");
			cell1.getVariables().add("files");
			row.getCells().add(cell1);

			ret.add(row);
		}
		/*************************************** LOAD FROM CONFIGURATION ******************************************************************/
		if(ret.size()==0) {
			List<Assembly> as = loadDataConfiguration(url);	//right sort order is assumed!
			LayoutCalculator layout = new LayoutCalculator();
			for(Assembly a : as) {
				if(!a.getClazz().equalsIgnoreCase("things") && a.getPropertyName().getActive()) {
					layout.add(a.getRow(),a.getCol(),a.getPropertyName().getIdentifier());
				}
			}
			ret.addAll(layout.getRows());
		}
		return ret;
	}
	/**
	 * Get dictionaries by root URL
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @param init 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxDictionaries(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();

		if(url.equalsIgnoreCase(ACTIVITY_CONFIGURATION)) {
			{
				AssemblyDTO assm = new AssemblyDTO();
				assm.setMult(false);
				assm.setPropertyName(ACTIVITY_EXECUTIVES);
				assm.setRequired(true);
				assm.setUrl(SystemService.DICTIONARY_SYSTEM_ROLES);
				ret.add(assm);
			}
			{
				AssemblyDTO assm = new AssemblyDTO();
				assm.setMult(false);
				assm.setPropertyName(ACTIVITY_CONFIG_FINALIZE);
				assm.setRequired(true);
				assm.setUrl(SystemService. DICTIONARY_SYSTEM_FINALIZATION );
				ret.add(assm);
			}
		}


		if(url.equalsIgnoreCase(SystemService.ROOT_SYSTEM_TILES)) {
			for(int i=0; i < 6; i++) {
				AssemblyDTO assm = new AssemblyDTO();
				assm.setMult(false);
				assm.setPropertyName("tile" + i);
				assm.setRequired(false);
				assm.setUrl(SystemService.ROOT_SYSTEM_TILES);
				ret.add(assm);
			}
		}
		if(url.equals(SYSTEM_IMPORT_LOCALES)) {
			AssemblyDTO assm = new AssemblyDTO();
			assm.setMult(false);
			assm.setPropertyName("locales");
			assm.setRequired(true);
			assm.setUrl(SystemService.DICTIONARY_SYSTEM_LOCALES);
			ret.add(assm);
		}

		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "dictionaries", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "dictionaries", ret);
			}
		}
		return ret;
	}

	/**
	 * Auxiliarry persons definitions
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxPersons(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//possible hardcoded definitions
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "persons", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "persons", ret);
			}
		}
		return ret;
	}

	/**
	 * Person selector is using mainly by resources to determine variables
	 * @param url
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxPersonSelector(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//TODO hardcoded definitions
		if(ret.size()==0) {
			List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("personselector")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * Special persons
	 * @param url
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxPersonSpecials(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//TODO hardcoded definitions
		if(ret.size()==0) {
			List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("personspec")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * Auxiliary schedulers
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxSchedulers(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//possible hardcoded definitions
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "schedulers", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "schedulers", ret);
			}
		}
		return ret;
	}
	/**
	 * Read registers definitions
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public List<AssemblyDTO> auxRegisters(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// possible hardcoded definitions...
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "registers", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "registers", ret);
			}
		}
		return ret;
	}
	/**
	 * Read configurations for amendment definition
	 * @param url
	 * @param  
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxAmendments(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// TODO hardcoded definitions...
		if(ret.size()==0) {
			List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("amendments")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * ATC codes
	 * @param url
	 * @param assemblies 
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxAtc(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();

		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ATCCODES)){ 
			AssemblyDTO addr = new AssemblyDTO(); 
			addr.setPropertyName(DATAIMPORT_CODES);
			addr.setDictUrl(SystemService.PRODUCTCLASSIFICATION_ATC_HUMAN); 
			ret.add(addr); 
		}

		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "atc", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "atc", ret);
			}
		}
		return ret;
	}

	/**
	 * legacy data references
	 * @param assms
	 * @param dataConfigStored 
	 * @param exts 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<AssemblyDTO> auxLegacyData(String url, List<Assembly> assms, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//... possible hardcoded
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assms, "legacy", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "legacy", ret);
			}
		}
		return ret;
	}


	/**
	 * droplist data references
	 * @param assms
	 * @param dataConfigStored 
	 * @param exts 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<AssemblyDTO> auxDropListData(String url, List<Assembly> assms, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// possible hardcoded definitions...
		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assms, "droplist", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "droplist", ret);
			}
		}
		return ret;
	}

	/**
	 * Intervals data references
	 * @param assms
	 * @param storedConfig 
	 * @param exts 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<AssemblyDTO> auxIntervals(String url, List<Assembly> assms, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// TODO hardcoded definitions...
		if(ret.size()==0) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assms, "intervals", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "intervals", ret);
			}
		}
		return ret;
	}

	/**
	 * Dates intervals
	 * !!!! attantion, temporary only for the first iteration!
	 * @param data
	 * @param dataConfigStored 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxIntervals(ThingDTO data, String clazzName, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(dataConfigStored.isEmpty()) {	//take from the database
			List<String> exts = boilerServ.variablesExtensions(data);
			jdbcRepo.assembly_variables(data.getUrl());
			Headers heads= new Headers();
			heads.getHeaders().add(TableHeader.instanceOf("ext", TableHeader.COLUMN_STRING));
			heads.getHeaders().add(TableHeader.instanceOf("varname", TableHeader.COLUMN_STRING));
			heads.getHeaders().add(TableHeader.instanceOf("Clazz", TableHeader.COLUMN_STRING));
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from assembly_variables", "", "", heads);
			Map<String, Long> vars = new HashMap<String, Long>();	//varname, ID
			Map<String,String> extended = new HashMap<String, String>();	//extension, varname
			Map<String, Long> extVars = new HashMap<String, Long>();			//varname+extension, ID
			//prepare maps
			for(TableRow row : rows) {
				String ext = row.getRow().get(0).getValue();
				String varname = row.getRow().get(1).getValue();
				String clazz = row.getRow().get(2).getValue();
				if(clazz.equalsIgnoreCase(clazzName)) {
					if(ext==null || ext.length()==0) {
						vars.put(varname, row.getDbID());
					}else {
						varname=varname.replace(ext, "");
						extended.put(ext, varname);
						extVars.put(varname+ext, row.getDbID());
					}
				}
			}
			//prepare DTOs
			for(String varname : vars.keySet()) {
				Assembly assm = calcAssembly(vars.get(varname), varname, extended, exts, extVars);
				ret.add(dtoServ.assemblyDto(assm));
			}
		}else {		//take from the stored configuration
			ret=auxFromDataConfigStored(dataConfigStored, "intervals", ret);
		}
		return ret;
	}

	public List<AssemblyDTO> auxLinks(String url, List<Assembly> assemblies, List<AssemblyDTO> dataConfigStored) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// possible hardcoded definitions...

		if(ret.isEmpty()) {
			if(dataConfigStored.isEmpty()) {
				ret = auxFromAssembly(assemblies, "links", ret);
			}else {
				ret=auxFromDataConfigStored(dataConfigStored, "links", ret);
			}
		}
		return ret;
	}
	/**
	 * Get right assembly
	 * @param assmId
	 * @param varname
	 * @param extended
	 * @param exts
	 * @param extVars 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Assembly calcAssembly(Long assmId, String varname, Map<String, String> extended, List<String> exts, Map<String, Long> extVars) throws ObjectNotFoundException {
		//search for extension
		Long id = assmId;
		for(String ext : extended.keySet()) {
			if(exts.contains(ext)) {
				if(extended.get(ext).equalsIgnoreCase(varname)) {
					id=extVars.get(varname+ext);
					break;
				}
			}
		}
		Optional<Assembly> assmo = assmRepo.findById(id);
		if(assmo.isPresent()) {
			assmo.get().getPropertyName().setIdentifier(varname);
		}else {
			throw new ObjectNotFoundException("calcAssembly. Assembly not found. ID is "+assmId,logger);
		}
		return assmo.get();
	}
	/**
	 * URL of business types dictionary
	 * @return
	 */
	public String businessesDictUrl() {
		return "dictionary.business.structure";
	}
	/**
	 * Label for new created 
	 * @param url
	 * @return null if not defined
	 */
	public String initLabel(String url) {
		return "INIT";
	}

	public String adminUnitsDict() {
		return "dictionary.admin.units";
	}

	/**
	 * get url for activity data
	 * @param identifier url of the process
	 * @param actName
	 * @return
	 */
	public String activityUrl(String identifier, String actName) {
		return actName.toLowerCase()+"."+identifier.toLowerCase();
	}

	/**
	 * Get default value of aux data URL for auxPathVar given
	 * @param url common data url
	 * @param auxPathVar
	 * @return empty string if not found
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String auxDataUrl(String url, String auxPathVar) throws ObjectNotFoundException {
		List<Assembly> assms = loadDataConfiguration(url);
		for(Assembly assm : assms) {
			if(assm.getPropertyName().getIdentifier().equalsIgnoreCase(auxPathVar)) {
				return assm.getAuxDataUrl();
			}
		}
		return "";
	}

	/**
	 * Get the current 
	 * @param data 
	 * @param user 
	 * @param storedConfig
	 * @param varName
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public AssemblyDTO auxPathConfig(ThingDTO data, UserDetailsDTO user, List<AssemblyDTO> storedConfig, String varName) throws ObjectNotFoundException {
		AssemblyDTO ret = new AssemblyDTO();
		//configuration is in a variable configuration
		if(data.getParentId()==0) {
			data.setParentId(data.getNodeId());
		}
		Concept parent = closureServ.loadConceptById(data.getParentId());
		Thing parentThing = boilerServ.thingByNode(parent);
		List<AssemblyDTO> assms = new ArrayList<AssemblyDTO>();
		if(storedConfig.isEmpty()) {
			List<Assembly> asmList = loadDataConfiguration(parentThing.getUrl(),user);
			for(Assembly asm : asmList) {
				assms.add(dtoServ.assemblyDto(asm));
			}
		}else {
			assms.addAll(storedConfig);
		}
		for(AssemblyDTO assm : assms) {
			if(assm.getPropertyName().equalsIgnoreCase(varName)){
				String urlStr=assm.getAuxDataUrl();
				if(urlStr.length()>0) {
					ret.setUrl(urlStr);
				}else {
					ret.addError(messages.get("emptyauxurl"));
				}
			}
		}
		return ret;
	}


	/**
	 * Get a url from a node selected in a dictionary linked to a report configuration
	 * @param node
	 * @param variable
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private String urlFromDictionary(Concept node, String variable) throws ObjectNotFoundException {
		String ret="";
		Thing thing = new Thing();
		thing = boilerServ.thingByNode(node,thing); 
		if(thing.getID()>0) {
			Concept dictNode=new Concept();
			for(ThingDict td : thing.getDictionaries()) {
				if(td.getVarname().equalsIgnoreCase(variable)) {
					dictNode=td.getConcept();
					break;
				}
			}
			if(dictNode.getID()>0) {
				ret=literalServ.readValue("url", dictNode);
			}
		}
		return ret;
	}
	/**
	 * Get an executor's role of the activity from the configuration
	 * @param actConfig
	 * @return executor's role concept or null
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept activityExecutorRole(Concept actConfig) throws ObjectNotFoundException {
		Concept ret = null;
		if(actConfig !=null) {
			Thing thing = boilerServ.thingByNode(actConfig);
			for(ThingDict td : thing.getDictionaries()) {
				if(td.getVarname().equalsIgnoreCase(ACTIVITY_EXECUTIVES)) {
					ret=td.getConcept();
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Load data configuration from assembly table
	 * Sorted by row, col, ord
	 * @param url
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<Assembly> loadDataConfiguration(String url) throws ObjectNotFoundException {
		List<Assembly> ret = new ArrayList<Assembly>();
		List<Concept> datas = loadAllDataConfigurations();
		List<Concept> vars = new ArrayList<Concept>();
		for(Concept dat : datas) {
			if(dat.getIdentifier().equalsIgnoreCase(url) && dat.getActive()) {
				List<Concept> varsAll = literalServ.loadOnlyChilds(dat);
				for(Concept var : varsAll) {
					if(var.getActive() && (var.getLabel()==null || var.getLabel().length()==0)) {
						vars.add(var);
					}
				}
			}
		}
		if(vars.size()>0) {
			ret=assmRepo.findAllByPropertyNameIn(vars, Sort.by(Sort.Direction.ASC,"row", "col", "ord"));
		}
		return ret;
	}
	/**
	 * Load data configurations as DTOs
	 * @param url
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> loadDataConfigurationAsDtos(String url) throws ObjectNotFoundException{
		List<Assembly> assms=loadDataConfiguration(url);
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		for(Assembly assm :assms) {
			ret.add(dtoServ.assemblyDto(assm));
		}
		return ret;
	}

	@Transactional
	public List<Assembly> loadDataConfiguration(String url, String clazz) throws ObjectNotFoundException {
		List<Assembly> ret = new ArrayList<Assembly>();
		List<Concept> datas = loadAllDataConfigurations();
		List<Concept> vars = new ArrayList<Concept>();
		for(Concept dat : datas) {
			if(dat.getIdentifier().equalsIgnoreCase(url) && dat.getActive()) {
				List<Concept> varsAll = literalServ.loadOnlyChilds(dat);
				for(Concept var : varsAll) {
					if(var.getActive() && (var.getLabel()==null || var.getLabel().length()==0)) {
						vars.add(var);
					}
				}
			}
		}
		if(vars.size()>0) {
			ret=assmRepo.findAllByPropertyNameInAndClazz(vars, Sort.by(Sort.Direction.ASC,"row", "col", "ord"), clazz);
		}
		return ret;
	}

	/**
	 * Load a list of variables that configured under the URL
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<Concept> loadAllDataConfigurations() throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
		List<Concept> datas = literalServ.loadOnlyChilds(root);
		return datas;
	}

	@Transactional
	public Concept loadDataConfigurationsAdmUnits() throws ObjectNotFoundException {
		Concept concept = null;
		List<Concept> datas = loadAllDataConfigurations();
		for(Concept c:datas) {
			if(c.getIdentifier().equals(SystemService.CONFIGURATION_ADMIN_UNITS)) {
				concept = c;
				break;
			}
		}
		return concept;
	}
	/**
	 * All auxs by URL
	 * @param url
	 * @param assemblies 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, List<AssemblyDTO>> auxAll(String url, Map<String, List<AssemblyDTO>> assemblies) throws ObjectNotFoundException {
		boolean found=false;
		for(String key : assemblies.keySet()) {
			found=key.startsWith(url+".");
			if(found) {
				break;
			}
		}
		if(!found) {
			logger.trace("aus for url "+url);
			List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assms) {
				String key=url+"."+assm.getClazz();
				List<AssemblyDTO> alist= assemblies.get(key);
				if(alist==null) {
					alist=new ArrayList<AssemblyDTO>();
					assemblies.put(key,alist);
				}
				alist.add(dtoServ.assemblyDto(assm));
			}
		}else {
			logger.trace("aux for url hit");
		}
		return assemblies;
	}

	public List<AssemblyDTO> auxByClazz(String url, String clazz) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		List<Assembly> assemblies = loadDataConfiguration(url);
		switch (clazz) {
		case "legacy":{
			ret = auxLegacyData(url, assemblies, new ArrayList<AssemblyDTO>());
			break;
		}
		case "dictionaries":{
			ret = auxDictionaries(url, assemblies, new ArrayList<AssemblyDTO>());
			break;
		}
		}

		return ret;
	}

	public AssemblyDTO auxAssemblyDTOByClazz(List<AssemblyDTO> ret, String key) throws ObjectNotFoundException {
		AssemblyDTO dto = null;
		if(ret != null && ret.size() > 0) {
			for(AssemblyDTO ass:ret) {
				if(key.equals(ass.getPropertyName())) {
					dto = ass;
					break;
				}
			}
		}
		return dto;
	}
	/**
	 * Create layout for a thing created from literals
	 * @param literals
	 * @return
	 */
	public List<LayoutRowDTO> literalsLayout(Map<String, String> literals) {
		List<LayoutRowDTO> ret = new ArrayList<LayoutRowDTO>();
		LayoutRowDTO row = new LayoutRowDTO();
		LayoutCellDTO cell = new LayoutCellDTO();
		row.getCells().add(cell);
		for(String key :literals.keySet()) {
			cell.getVariables().add(key);
		}
		ret.add(row);
		return ret;
	}
	/**
	 * Load data configurations and then apply user restrictions on it
	 * @param url
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<Assembly> loadDataConfiguration(String url, UserDetailsDTO user) throws ObjectNotFoundException {
		List<Assembly> assms = loadDataConfiguration(url);
		List<Assembly> ret=new ArrayList<Assembly>();
		for(Assembly assm :assms) {
			//AssemblyDTO assmDTO = dtoServ.assemblyDto(assm);
			if(accessServ.allowAssembly(assm, user)) {
				ret.add(assm);
			}
		}
		return ret;
	}


}

