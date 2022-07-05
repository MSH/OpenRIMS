package org.msh.pharmadex2.service.r2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.msh.pharmadex2.dto.ReportConfigDTO;
import org.msh.pharmadex2.dto.ThingDTO;
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

	public static final String DATAIMPORT_ADDRESS = "dataimport_address";
	public static final String DATAIMPORT_RESULT = "dataimport_result";
	public static final String DATAIMPORT_DATA = "dataimport";
	public static final String DATAIMPORT_DATA_ERROR = "dataimporterror";
	public static final String SYSTEM_IMPORT_ADMINUNITS="system.import.adminunits";
	public static final String SYSTEM_IMPORT_ADMINUNITS_RELOAD="system.import.adminunits_reload";
	private static final String ACTIVITY_EXECUTIVES = "executives";
	public static final String ACTIVITY_CONFIG_FINALIZE = "finalize";
	private static final String ACTIVITY_CONFIGURATION = "activity.configuration";
	private static final String OBJECT_SITE_CLASSIFIERS = "object.site.classifiers";
	private static final Logger logger = LoggerFactory.getLogger(AssemblyService.class);
	public static final String SYSTEM_IMPORT_LEGACY_DATA = "system.import.legacy.data";

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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxHeadings(String url, List<Assembly> assms) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//TODO defined for the system
		//*********************************** read from configuration ***************************************************
		if(ret.size()==0) {
			//List<Assembly> assms =loadDataConfiguration(url);
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("heading")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * Create auxiliary strings, unlike literals strings are without language separation
	 * @param url
	 * @param assemblies 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxStrings(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
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
				ret.add(fld);	
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setPropertyName("checklisturl");
				ret.add(fld);	
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setPropertyName("dataurl");
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
		}
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("strings")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}

	/**
	 * Create empty auxiliary literals for the url given
	 * @param url url given
	 * @param activityName 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxLiterals(String url) throws ObjectNotFoundException {
		List<Assembly> assms = loadDataConfiguration(url);
		List<AssemblyDTO> ret = auxLiterals(url, assms);
		return ret;
	}
	/**
	 * Create empty auxiliary literals for the url given
	 * @param url url given
	 * @param activityName 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxLiterals(String url, List<Assembly> assms) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.toUpperCase().startsWith("DICTIONARY.")) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(false);
				fld.setPropertyName("URL");
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

		if(url.equalsIgnoreCase(SystemService.DICTIONARY_HOST_APPLICATIONS)) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setPropertyName("applicationurl");
				ret.add(fld);
			}
		}

		if(url.equalsIgnoreCase(SystemService.DICTIONARY_SHUTDOWN_APPLICATIONS)) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setPropertyName("applicationurl");
				ret.add(fld);
			}
		}

		/**
		 * Applications implemented
		 */
		if(url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_APPLICATIONS)
				|| url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_AMENDMENTS)
				|| url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_RENEWAL)
				|| url.equalsIgnoreCase(SystemService.DICTIONARY_GUEST_DEREGISTRATION)
				) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setPropertyName("applicationurl");
				ret.add(fld);
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setPropertyName("dataurl");
				ret.add(fld);
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setReadOnly(false);
				fld.setTextArea(false);
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
			fld.setMult(true);
			ret.add(fld);
		}
		
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_LEGACY_DATA)) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setPropertyName(DATAIMPORT_RESULT);
			fld.setReadOnly(true);
			fld.setMult(true);
			ret.add(fld);
		}

		//*********************************** read from configuration ***************************************************
		if(ret.size()==0) {
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("literals")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
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
	 * @param objectUrl
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxAddresses(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// irka
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS) /*|| url.equalsIgnoreCase(SYSTEM_IMPORT_LEGACY_DATA)*/) { 
			AssemblyDTO addr = new AssemblyDTO(); 
			addr.setPropertyName(DATAIMPORT_ADDRESS);
			addr.setUrl("address.tests"); 
			ret.add(addr); 
		}

		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("addresses")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}

	/**
	 * Dates for screen forms
	 * @param url
	 * @param assemblies 
	 * @param activity
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxDates(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//permanent system's settings
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("dates")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * Decimal number fields
	 * @param url
	 * @param assemblies 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxNumbers(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
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
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("numbers")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}


	public List<AssemblyDTO> auxLogicals(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.equalsIgnoreCase(ACTIVITY_CONFIGURATION)) {
			{
				AssemblyDTO assm = new AssemblyDTO();
				assm.setPropertyName("background");
				ret.add(assm);
			}
		}
		//*********************************** read from configuration ***************************************************
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("logical")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * Files that should be uploaded/accessible for the activity of an application defined by the  url
	 * @param url
	 * @param assemblies 
	 * @return map with 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxDocuments(String url, List<Assembly> assemblies) throws ObjectNotFoundException{
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS) || url.equalsIgnoreCase(SYSTEM_IMPORT_ADMINUNITS_RELOAD)) {
			AssemblyDTO assm = new AssemblyDTO();
			assm.setPropertyName(DATAIMPORT_DATA);
			assm.setUrl("data.import");
			assm.setDescription(messages.get("pleaseuploadimportdata"));
			assm.setRequired(true);
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
			assm.setRequired(true);
			assm.setTextArea(false);
			assm.setUrl("data.import");
			assm.setAuxDataUrl("");
			ret.add(assm);
		}
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("documents")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * File resources - references and templates
	 * @param url
	 * @param assemblies 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxResources(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//TODO system defined?
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("resources")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * Additional things to fill out
	 * @param url application URL
	 * @param assemblies 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxThings(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();

		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("things")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
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
				cell1.getVariables().add("prefLabel");
				cell1.getVariables().add("description");
				cell1.getVariables().add("activityurl");
				cell1.getVariables().add("checklisturl");
				cell1.getVariables().add("dataurl");
				cell1.getVariables().add("addressurl");
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
			cell1.getVariables().add(DATAIMPORT_DATA);
			cell1.getVariables().add(DATAIMPORT_RESULT);
			row.getCells().add(cell1);
			//address
			LayoutCellDTO cell2 = new LayoutCellDTO();
			cell2.getVariables().add(DATAIMPORT_ADDRESS);
			row.getCells().add(cell2);
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
	 * @param init 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxDictionaries(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
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
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("dictionaries")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}

	/**
	 * Auxiliarry persons definitions
	 * @param url
	 * @param assemblies 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxPersons(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//TODO system persons, maybe
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("persons")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxSchedulers(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//TODO hardcoded definitions
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("schedulers")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * Read registers definitions
	 * @param url
	 * @param assemblies 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public List<AssemblyDTO> auxRegisters(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// TODO hardcoded definitions...
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("registers")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxAtc(String url, List<Assembly> assemblies) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// TODO hardcoded definitions...
		if(ret.size()==0) {
			//List<Assembly> assms = loadDataConfiguration(url);
			for(Assembly assm : assemblies) {
				if(assm.getClazz().equalsIgnoreCase("atc")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}

	/**
	 * legacy data references
	 * @param assms
	 * @param exts 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<AssemblyDTO> auxLegacyData(String url, List<Assembly> assms) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		// TODO hardcoded definitions...
		if(ret.size()==0) {
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("legacy")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}

	/**
	 * Dates intervals
	 * !!!! attantion, temporary only for the first iteration!
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxIntervals(ThingDTO data, String clazzName) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
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
	 * @param dictNodeId
	 * @param varName
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public AssemblyDTO auxPathConfig(ThingDTO data, long dictNodeId, String varName) throws ObjectNotFoundException {
		AssemblyDTO ret = new AssemblyDTO();
		if(dictNodeId>0) {
			//configuration is in a dictionary
			Concept dictNode = closureServ.loadConceptById(dictNodeId);
			String urlStr=literalServ.readValue("URL", dictNode);
			if(urlStr.length()>0) {
				ret.setUrl(urlStr);
			}else {
				throw new ObjectNotFoundException("auxPathConfig. Bad dictionary choice. URL is undefined ", logger);
			}
		}else {
			//configuration is in a variable configuration
			if(data.getParentId()==0) {
				throw new ObjectNotFoundException("auxPathConfig. Parent ID is ZERO", logger);
			}
			Concept parent = closureServ.loadConceptById(data.getParentId());
			Thing parentThing = boilerServ.thingByNode(parent);
			List<Assembly> assms = loadDataConfiguration(parentThing.getUrl());
			for(Assembly assm : assms) {
				if(assm.getPropertyName().getIdentifier().equalsIgnoreCase(varName)){
					String urlStr=assm.getAuxDataUrl();
					if(urlStr.length()>0) {
						ret.setUrl(urlStr);
					}else {
						throw new ObjectNotFoundException("auxPathConfig. Please, configure auxDataUrl for "+data+"/"+varName, logger);
					}
				}
			}
		}
		return ret;
	}
	/**
	 * Mock to load reports configurations
	 * @deprecated
	 * @return
	 */
	public Map<String, ReportConfigDTO> reportConfigLoad(){
		Map<String, ReportConfigDTO> ret = new LinkedHashMap<String, ReportConfigDTO>();
		{
			ReportConfigDTO dto = new ReportConfigDTO();
			dto.setDataUrl("pharmacy.site");
			dto.setDictStageUrl("dictionary.host.applications");
			dto.setAddressUrl("pharamcy.site.address");
			dto.setOwnerUrl("pharmacy.site.owners");
			dto.setInspectAppUrl("application.pharmacy.inspection");
			dto.setRenewAppUrl("application.pharmacy.renew");
			dto.setRegisterAppUrl("pharmacy.site.certificate");
			dto.setRegistered(true);
			ret.put(dto.getDataUrl(), dto);
		}
		{
			ReportConfigDTO dto = new ReportConfigDTO();
			dto.setDataUrl("retail.site.owned.persons");
			dto.setDictStageUrl("dictionary.host.applications");
			dto.setAddressUrl("nepal.address");
			dto.setOwnerUrl("site.owners.persons");
			dto.setInspectAppUrl("application.pharmacy.inspection");
			dto.setRenewAppUrl("application.pharmacy.renew");
			dto.setRegisterAppUrl("pharmacy.site.certificate");
			dto.setRegistered(true);
			ret.put(dto.getDataUrl(), dto);
		}
		{
			ReportConfigDTO dto = new ReportConfigDTO();
			dto.setDataUrl("importer.site");
			dto.setDictStageUrl("dictionary.host.applications");
			dto.setAddressUrl("importer.site.address");
			dto.setOwnerUrl("importer.site.owners");
			dto.setInspectAppUrl("application.importer.inspection");
			dto.setRenewAppUrl("application.importer.renew");
			dto.setRegisterAppUrl("importer.site.certificate");
			dto.setRegistered(true);
			ret.put(dto.getDataUrl(), dto);
		}

		{
			ReportConfigDTO dto = new ReportConfigDTO();
			dto.setDataUrl("ws.site");
			dto.setDictStageUrl("dictionary.host.applications");
			dto.setAddressUrl("ws.site.address");
			dto.setOwnerUrl("ws.site.owners");
			dto.setInspectAppUrl("application.ws.inspection");
			dto.setRenewAppUrl("application.ws.renew");
			dto.setRegisterAppUrl("ws.site.certificate");
			dto.setRegistered(true);
			ret.put(dto.getDataUrl(), dto);
		}
		{
			ReportConfigDTO dto = new ReportConfigDTO();
			dto.setDataUrl("import.permit");
			dto.setRegisterAppUrl("register.import.permit");
			dto.setRegistered(true);
			dto.setApplicantRestriction(true);
			ret.put(dto.getDataUrl(), dto);
		}
		return ret;
	}
	/**
	 * Get report configuration by configuration ID
	 * @param url
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ReportConfigDTO reportConfig(ReportConfigDTO reportConfigDTO) throws ObjectNotFoundException {
		ReportConfigDTO ret = reportConfigDTO;
		if(ret.getNodeId() >0) {
			Concept node=closureServ.loadConceptById(ret.getNodeId());
			ret.setTitle(literalServ.readPrefLabel(node));
			ret.setAddressUrl(literalServ.readValue("address_url", node));
			ret.setApplicantRestriction(false); //TODO
			ret.setDataUrl(urlFromDictionary(node, "application"));
			ret.setDictStageUrl(urlFromDictionary(node, "stage"));
			ret.setDeregistered(ret.getDictStageUrl().equalsIgnoreCase(SystemService.DICTIONARY_GUEST_DEREGISTRATION));
			ret.setApplicantUrl(literalServ.readValue("applicanturl", node));
			ret.setInspectAppUrl(literalServ.readValue("inspection_url", node));
			ret.setOwnerUrl(literalServ.readValue("owners_url", node));
			ret.setRegisterAppUrl(literalServ.readValue("certificate_url", node));
			ret.setRegistered(true);
			ret.setRenewAppUrl(literalServ.readValue("renewal_url", node));
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





}
