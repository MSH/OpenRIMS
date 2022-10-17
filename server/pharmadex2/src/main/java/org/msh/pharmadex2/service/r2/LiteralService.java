package org.msh.pharmadex2.service.r2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.msh.pdex2.dto.i18n.Language;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.enums.YesNoNA;
import org.msh.pdex2.model.r2.Closure;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.r2.ClosureRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.DtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
/**
 * Languages related set of services
 * @author alexk
 *
 */
@Service
public class LiteralService {
	private static final String IDENTIFIER = "_LITERALS_";
	public static final String DESCRIPTION = "description";
	public static final String PREF_NAME = "prefLabel";

	public static final String ICON_URL = "iconurl";
	public static final String MORE_URL = "moreurl";
	public static final String MORE_LBL = "morelbl";
	public static final String LAYOUT = "layout";

	public static final String GIS_LOCATION = "gisLocation";
	public static final String ZOMM = "zoom";
	//public static final String EXPERT = "expert";
	//public static final String ASK = "ask";
	//public static final String ASK_TEXT = "askText";

	private static final Logger logger = LoggerFactory.getLogger(LiteralService.class);
	@Autowired
	ClosureService closureServ;
	@Autowired
	Messages messages;
	@PersistenceContext
	EntityManager entityManager;

	/**
	 * Create a new or update an existed literal for parent node
	 * Current language will applied
	 * @param variableName - the name of variable
	 * @param value - the value of variable in a current language
	 * @param parent parent node
	 * @return parent node
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept createUpdateLiteral(String variableName, String value, Concept parent) throws ObjectNotFoundException {
		//make parent managed
		parent=closureServ.loadConceptById(parent.getID());
		//literal
		Concept literals = loadLiterals(parent);
		//variable
		Concept variable = loadVariable(literals, variableName);
		//value
		loadValue(variable, value.trim());
		//refresh
		entityManager.refresh(parent);
		return parent;
	}
	/**
	 * Create or update literal only if value is real string
	 * @param variableName
	 * @param value
	 * @param parent
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept createUpdateLiteralRealString(String variableName, String value, Concept parent) throws ObjectNotFoundException {
		if(value!=null && value.length()>0) {
			parent= createUpdateLiteral(variableName,value,parent);
		}
		return parent;
	}
	/**
	 * Create and update string value
	 * @param variableName
	 * @param value
	 * @param parent
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept createUpdateString(String variableName, String value, Concept parent) throws ObjectNotFoundException {
		//make parent managed
		parent=closureServ.loadConceptById(parent.getID());
		//literal
		Concept literals = loadLiterals(parent);
		//variable
		Concept variable = loadVariable(literals, variableName);
		//value
		loadStringValue(variable, value.trim());
		//refresh
		entityManager.refresh(parent);
		return parent;
	}
	/**
	 * Create/update value for all languages unconditionally
	 * @param variable
	 * @param value
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Concept loadStringValue(Concept variable, String valueStr) throws ObjectNotFoundException {
		Concept value = new Concept();
		if(valueStr != null) {
			valueStr=valueStr.trim();
			List<String> languages = messages.getAllUsedUpperCase();
			//load/create for all langs
			for(String lang : languages) {
				Concept rest = new Concept();
				rest.setIdentifier(lang);
				rest.setLabel(valueStr);
				rest.setActive(true);
				rest=closureServ.saveToTree(variable, rest);
			}
		}
		return value;
	}
	/**
	 * Create a new or update an existed literal for parent node
	 * Current language will applied
	 * @param variableName - the name of variable
	 * @param value - the value of variable in a current language
	 * @param parent parent node
	 * @return parent node
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept createUpdateLiteral(String variableName, Concept parent, Map<String, String> values) throws ObjectNotFoundException {
		//make parent managed
		parent=closureServ.loadConceptById(parent.getID());
		//literal
		Concept literals = loadLiterals(parent);
		//variable
		Concept variable = loadVariable(literals, variableName);
		//value
		loadValue(variable, values);
		//refresh
		entityManager.refresh(parent);
		return parent;
	}

	/**
	 * Create, load or update the value of the variable.
	 * For the current language set value as active
	 * For the rest language fill out empty values, however declare them as not active
	 * @param variable
	 * @param valueStr
	 * @return new value.
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Concept loadValue(Concept variable, String valueStr) throws ObjectNotFoundException{
		Concept value = new Concept();
		if(valueStr != null) {
			valueStr=valueStr.trim();
			String locale = LocaleContextHolder.getLocale().toString().toUpperCase();
			List<String> languages = messages.getAllUsedUpperCase();
			//create value anyway
			value.setIdentifier(locale);
			value.setLabel(valueStr);
			value.setActive(true);
			value=closureServ.saveToTreeFast(variable, value);
			//load all existing values
			List<Concept> level = closureServ.loadLevel(variable);
			if(level.size()==1) {
				for(String lang : languages) {
					if(!lang.equalsIgnoreCase(locale)) {
						Concept rest = new Concept();
						rest.setIdentifier(lang);
						rest.setLabel(valueStr);
						rest.setActive(false);
						rest=closureServ.saveToTree(variable, rest);
					}
				}
			}else {
				//all inactive should be the same
				for(Concept langVar :level) {
					if(!langVar.getActive()) {
						langVar.setLabel(valueStr);
						langVar=closureServ.save(langVar);
					}
				}
			}
		}
		return value;
	}

	/**
	 * Create, load or update the value of the variable.
	 * For the current language set value as active
	 * For the rest language fill out empty values, however declare them as not active
	 * @param variable
	 * @param valueStr
	 * @return new value.
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private void loadValue(Concept variable, Map<String, String> values) throws ObjectNotFoundException{
		if(values != null) {
			String currentLoc = LocaleContextHolder.getLocale().toString().toUpperCase();
			Iterator<String> it = values.keySet().iterator();
			while(it.hasNext()) {
				String locale = it.next();

				Concept value = new Concept();
				value.setIdentifier(locale);
				value.setLabel(values.get(locale));
				value.setActive(locale.equalsIgnoreCase(currentLoc));
				value = closureServ.saveToTree(variable, value);
			}
		}
	}

	/**
	 * Load or create a variable
	 * @param literals
	 * @param variableName
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept loadVariable(Concept literals, String variableName) throws ObjectNotFoundException{
		Concept variable = new Concept();
		variable.setIdentifier(variableName);
		variable = closureServ.saveToTree(literals, variable);
		return variable;
	}

	/**
	 * Load or create the "root" of all variables, i.e., literal
	 * @param parent
	 * @return the literal
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept loadLiterals(Concept parent) throws ObjectNotFoundException {
		//refresh parent
		parent = closureServ.loadConceptById(parent.getID());
		//literals
		Concept literals = new Concept();
		literals.setIdentifier(IDENTIFIER);
		//logger.trace("s}");
		literals = closureServ.saveToTree(parent, literals);
		//logger.trace("}");
		return literals;
	}

	/**
	 * add or update the most using "prefLabel" and "description" variables
	 * for convenience only
	 * @param prefLabelValue
	 * @param descriptionValue
	 * @param parent
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept prefAndDescription(String prefLabelValue, String descriptionValue, Concept parent) throws ObjectNotFoundException {
		parent =  createUpdateLiteral(PREF_NAME, prefLabelValue, parent);
		parent = createUpdateLiteral(DESCRIPTION, descriptionValue, parent);
		return parent;
	}

	/**
	 * Save all fields from dictionary literals, include, but not limited "prefLabe"l and "description"
	 * @param literals
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept saveFields(Map<String, FormFieldDTO<String>> literals, Concept parent) throws ObjectNotFoundException {
		if(literals != null) {
			for(String key : literals.keySet()) {
				createUpdateLiteral(key, literals.get(key).getValue(), parent);
			}
		}
		return parent;
	}
	/**
	 * Remove a variable from the concept 
	 * @param variableName
	 * @param parent
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept removeVariable(String variableName, Concept parent) throws ObjectNotFoundException {
		//make parent managed
		parent=closureServ.loadConceptById(parent.getID());
		//literal
		Concept literals = loadLiterals(parent);
		//variable
		Concept variable = loadVariable(literals, variableName);
		//delete it!
		closureServ.removeNode(variable);

		return parent;
	}
	/**
	 * Load level under the parent without literals
	 * @param parent
	 * @return empty if none
	 */
	public List<Concept> loadOnlyChilds(Concept parent) {
		List<Concept> ret = new ArrayList<Concept>();
		if(parent!=null) {
			List<Concept> level = closureServ.loadLevel(parent);
			for(Concept conc : level) {
				if(!conc.getIdentifier().startsWith("_")){
					ret.add(conc);
				}
			}
		}
		return ret;
	}
	/**
	 * Is this concept a leaf
	 * @param concept
	 * @return
	 */
	@Transactional
	public boolean isLeaf(Concept concept) {
		List<Concept> level=loadOnlyChilds(concept);
		boolean ret = true; 
		for(Concept node : level) {
			if(node.getActive()) {
				ret=false;
				break;
			}
		}
		return ret;
	}


	/**
	 * Create mandatory literals - "prefLabel" and "description"
	 * @return
	 */
	public Map<String,FormFieldDTO<String>> mandatoryLiterals() {
		Map<String,FormFieldDTO<String>> ret = new LinkedHashMap<String, FormFieldDTO<String>>();
		ret.put(PREF_NAME, FormFieldDTO.of(""));
		ret.put(DESCRIPTION, FormFieldDTO.of(""));
		return ret;
	}


	/**
	 * Create and read literals
	 * @param literals
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String,FormFieldDTO<String>> readAllLiterals(List<AssemblyDTO> literals, Concept node) throws ObjectNotFoundException {
		Map<String, FormFieldDTO<String>> ret = new LinkedHashMap<String, FormFieldDTO<String>>();
		for(AssemblyDTO asm : literals) {
			String val = readValue(asm.getPropertyName(),node);
			ret.put(asm.getPropertyName(), FormFieldDTO.of(val,asm.isReadOnly(), asm.isTextArea()));
		}
		return ret;
	}
	/**
	 * Convenient method to read prefLabel
	 * @param node
	 * @return value of variable or empty string if variable is not defined
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String readPrefLabel(Concept node) throws ObjectNotFoundException {
		return readValue(PREF_NAME, node);
	}
	/**
	 * Convenient method to write prefLabel
	 * @param value
	 * @param node
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void createUpdatePrefLabel(String value, Concept node) throws ObjectNotFoundException {
		createUpdateLiteral(PREF_NAME, value, node);
	}

	/**
	 * Convenient method to create or update description
	 * @param value
	 * @param node
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void createUpdateDescription(String value, Concept node) throws ObjectNotFoundException {
		createUpdateLiteral(DESCRIPTION, value, node);
	}
	/**
	 * Convenient method to read description
	 * @param node
	 * @return value of variable or empty string if variable is not defined
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String readDescription(Concept node) throws ObjectNotFoundException {
		return readValue(DESCRIPTION, node);
	}
	/**
	 * Find a concept of literal variable 
	 * @param variableName
	 * @param node
	 * @return empty concept if not found
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept literalConcept(String variableName, Concept node) throws ObjectNotFoundException {
		Concept literals = loadLiterals(node);
		List<Concept> variables = closureServ.loadLevel(literals);
		for(Concept variable : variables) {
			if(variable.getIdentifier().equalsIgnoreCase(variableName)) {
				return variable;
			}
		}
		return new Concept();
	}
	
	/**
	 * load all parent's pref labels
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<String> loadAllParentPrefLabels(Concept node) throws ObjectNotFoundException {
		String locale = LocaleContextHolder.getLocale().toString();
		return loadAllParentPrefLabels(node,locale);
	}
	
	
	/**
	 * Load list of parent prefLabel
	 * @param node
	 * @param lang
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public List<String> loadAllParentPrefLabels(Concept node, String locale) throws ObjectNotFoundException {
		List<String> ret = new ArrayList<String>();
		if(node != null) {
			List<Closure> clos = closureServ.untilRoot(node);
			for(Closure clo :clos) {
				String literal = readValue("prefLabel", clo.getParent(),locale);
				ret.add(literal);
			}
		}
		return ret;
	}
	
	
	/**
	 * Read a value of variable in the current language
	 * @param variableName - name of variable
	 * @param parent related concept
	 * @return value of variable or empty string if variable is not defined
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String readValue(String variableName, Concept parent) throws ObjectNotFoundException {
		String locale = LocaleContextHolder.getLocale().toString();
		return readValue(variableName,parent,locale);
	}
	
	/**
	 * Read a value on the language given
	 * @param string
	 * @param parent
	 * @param locale
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String readValue(String variableName, Concept parent, String locale) throws ObjectNotFoundException {
		String ret="";
		if(parent!=null) {
			//literal
			Concept literals = loadLiterals(parent);
			List<Concept> variables = closureServ.loadLevel(literals);
			for(Concept variable : variables) {
				if(variable.getIdentifier().equalsIgnoreCase(variableName)) {
					//value
					List<Concept> values = closureServ.loadLevel(variable);
					for(Concept value :values) {
						if(value.getIdentifier().equalsIgnoreCase(locale)) {
							ret = value.getLabel();
							break;
						}
					}
				}
			}
		}
		return ret;
	}

	


}
