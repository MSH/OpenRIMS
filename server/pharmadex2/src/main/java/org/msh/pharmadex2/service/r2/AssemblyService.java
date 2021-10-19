package org.msh.pharmadex2.service.r2;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pharmadex2.dto.ActionBarDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.LayoutCellDTO;
import org.msh.pharmadex2.dto.LayoutRowDTO;
import org.msh.pharmadex2.dto.ReportConfigDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is responsible for entity assembly issues such are dictionaries bound, root concept URLs, etc 
 * @author alexk
 *
 */
@Service
public class AssemblyService {


	private static final String ACTIVITY_CONFIGURATION = "activity.configuration";
	private static final String OBJECT_SITE_CLASSIFIERS = "object.site.classifiers";
	private static final String SAVE_ACTION = "save_action";
	private static final Logger logger = LoggerFactory.getLogger(AssemblyService.class);
	private static final String DICTIONARY_WORKFLOW_ACTIVITIES = "dictionary.workflow.activities";
	
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
	/**
	 * List of dictionaries for public organization
	 * @TODO load this configuration from the database
	 * @return
	 */
	public List<AssemblyDTO> pubOrgDict(){
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		AssemblyDTO dto= new AssemblyDTO();
		dto.setUrl("dictionary.responsibilities.organizations.public");
		dto.setPropertyName("responsibility");
		dto.setMult(true);
		dto.setRequired(true);
		ret.add(dto);
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxHeadings(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//TODO defined for the system
		//*********************************** read from configuration ***************************************************
		if(ret.size()==0) {
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxStrings(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		List<Assembly> assms = boilerServ.loadDataConfiguration(url);
		for(Assembly assm : assms) {
			if(assm.getClazz().equalsIgnoreCase("strings")) {
				ret.add(dtoServ.assemblyDto(assm));
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

		/**
		 * Activities implemented
		 */
		if(url.equalsIgnoreCase(DICTIONARY_WORKFLOW_ACTIVITIES)) {
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(true);
				fld.setPropertyName("activityurl");
				ret.add(fld);
			}
			{
				AssemblyDTO fld = new AssemblyDTO();
				fld.setRequired(false);
				fld.setPropertyName("activitydataurl");
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
		if(url.equalsIgnoreCase("dictionary.admin.units")) {
			AssemblyDTO fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setPropertyName(LiteralService.GIS_LOCATION); 
			ret.add(fld);

			fld = new AssemblyDTO();
			fld.setRequired(false);
			fld.setPropertyName(LiteralService.ZOMM); 
			ret.add(fld);
		}

		//*********************************** read from configuration ***************************************************
		if(ret.size()==0) {
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("literals")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}

	/**
	 * Addresses to include
	 * @param objectUrl
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxAddresses(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		
		if(ret.size()==0) {
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
			for(Assembly assm : assms) {
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
	 * @param activity
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxDates(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//permanent system's settings
		if(ret.size()==0) {
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
			for(Assembly assm : assms) {
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxNumbers(String url) throws ObjectNotFoundException {
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
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("numbers")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}


	public List<AssemblyDTO> auxLogicals(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		if(url.equalsIgnoreCase(ACTIVITY_CONFIGURATION)) {
			{
				AssemblyDTO assm = new AssemblyDTO();
				assm.setPropertyName("background");
				ret.add(assm);
			}
			{
				AssemblyDTO assm = new AssemblyDTO();
				assm.setPropertyName("finalize");
				ret.add(assm);
			}
		}
		//*********************************** read from configuration ***************************************************
		if(ret.size()==0) {
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
			for(Assembly assm : assms) {
				if(assm.getClazz().equalsIgnoreCase("logicals")) {
					ret.add(dtoServ.assemblyDto(assm));
				}
			}
		}
		return ret;
	}
	/**
	 * Files that should be uploaded/accessible for the activity of an application defined by the  url
	 * @param url
	 * @return map with 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxDocuments(String url) throws ObjectNotFoundException{
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();

		if(ret.size()==0) {
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
			for(Assembly assm : assms) {
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<AssemblyDTO> auxResources(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
		//TODO system defined?
		if(ret.size()==0) {
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
			for(Assembly assm : assms) {
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<AssemblyDTO> auxThings(String url) throws ObjectNotFoundException {
		List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();

		if(ret.size()==0) {
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
			for(Assembly assm : assms) {
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
				cell2.getVariables().add("executives");
				cell2.getVariables().add("background");
				cell2.getVariables().add("finalize");
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

		/*************************************** LOAD FROM CONFIGURATION ******************************************************************/
		if(ret.size()==0) {
			List<Assembly> as = boilerServ.loadDataConfiguration(url);	//right sort order is assumed!
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
		 * @param init 
		 * @return
		 * @throws ObjectNotFoundException 
		 */
		public List<AssemblyDTO> auxDictionaries(String url) throws ObjectNotFoundException {
			List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();

			if(url.equalsIgnoreCase(ACTIVITY_CONFIGURATION)) {
				{
					AssemblyDTO assm = new AssemblyDTO();
					assm.setMult(false);
					assm.setPropertyName("executives");
					assm.setRequired(true);
					assm.setUrl("dictionary.system.roles");
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
				List<Assembly> assms = boilerServ.loadDataConfiguration(url);
				for(Assembly assm : assms) {
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
		 * @return
		 * @throws ObjectNotFoundException 
		 */
		public List<AssemblyDTO> auxPersons(String url) throws ObjectNotFoundException {
			List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
			//TODO system persons, maybe
			if(ret.size()==0) {
				List<Assembly> assms = boilerServ.loadDataConfiguration(url);
				for(Assembly assm : assms) {
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
				List<Assembly> assms = boilerServ.loadDataConfiguration(url);
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
				List<Assembly> assms = boilerServ.loadDataConfiguration(url);
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
		 * @return
		 * @throws ObjectNotFoundException 
		 */
		public List<AssemblyDTO> auxSchedulers(String url) throws ObjectNotFoundException {
			List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
			//TODO hardcoded definitions
			if(ret.size()==0) {
				List<Assembly> assms = boilerServ.loadDataConfiguration(url);
				for(Assembly assm : assms) {
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
		 * @return
		 * @throws ObjectNotFoundException
		 */
		public List<AssemblyDTO> auxRegisters(String url) throws ObjectNotFoundException {
			List<AssemblyDTO> ret = new ArrayList<AssemblyDTO>();
			// TODO hardcoded definitions...
			if(ret.size()==0) {
				List<Assembly> assms = boilerServ.loadDataConfiguration(url);
				for(Assembly assm : assms) {
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
				List<Assembly> assms = boilerServ.loadDataConfiguration(url);
				for(Assembly assm : assms) {
					if(assm.getClazz().equalsIgnoreCase("amendments")) {
						ret.add(dtoServ.assemblyDto(assm));
					}
				}
			}
			return ret;
		}
		
		/**
		 * Mock form actions
		 * @param url
		 * @param activity
		 * @return
		 */
		public ActionBarDTO formActions(String url) {
			ActionBarDTO ret = new ActionBarDTO();
			ret.getActions().add(SAVE_ACTION);
			return ret;
		}


		/**
		 * Email of executor of an activity of the next activity from the configuration
		 * @param url
		 * @param activity
		 * @return
		 * @throws ObjectNotFoundException 
		 */
		public String executorEmail(String url, String activity) throws ObjectNotFoundException {
			//TODO MOCK!!!!!
			return "alex.kurasoff@gmail.com";
		}

		/**
		 * Get a name of the start activity
		 * @param applicationURL
		 * @return
		 */
		public String startActivityName(String applicationURL) {
			return "INIT";
		}
		/**
		 * Files under this dictionary url are shared for all applications/activities
		 * @param dictUrl
		 * @return
		 */
		public boolean sharedFiles(String dictUrl) {
			if(dictUrl.equalsIgnoreCase("dictionary.pharmacy.registration.business.documents")) {
				return true;
			}
			return false;
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
			List<Assembly> assms = boilerServ.loadDataConfiguration(url);
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
				Thing parentThing = boilerServ.loadThingByNode(parent);
				List<Assembly> assms = boilerServ.loadDataConfiguration(parentThing.getUrl());
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
		 * Get report configuration by report url
		 * @param url
		 * @return
		 */
		public ReportConfigDTO reportConfig(String url) {
			Map<String, ReportConfigDTO> data = reportConfigLoad();
			if(url.equalsIgnoreCase("report.pharmacy.registered")) {
				return data.get("pharmacy.site");
			}
			if(url.equalsIgnoreCase("report.importer.registered")) {
				return data.get("importer.site");
			}
			if(url.equalsIgnoreCase("report.ws.registered")) {
				return data.get("ws.site");
			}
			if(url.equalsIgnoreCase("report.import.permit.valid")) {
				return data.get("import.permit");
			}
			return new ReportConfigDTO();
		}



	
	}
