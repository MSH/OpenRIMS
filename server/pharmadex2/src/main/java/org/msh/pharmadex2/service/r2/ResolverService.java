package org.msh.pharmadex2.service.r2;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Register;
import org.msh.pdex2.model.r2.Scheduler;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.ThingDoc;
import org.msh.pdex2.model.r2.ThingPerson;
import org.msh.pdex2.model.r2.ThingRegister;
import org.msh.pdex2.model.r2.ThingScheduler;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AddressValuesDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DictValuesDTO;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.SchedulerDTO;
import org.msh.pharmadex2.dto.ThingValuesDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible to resolve url expressions like  pharmacy.site/address@level1
 * Applicable for docx document generator
 * @author alexk
 *
 */
@Service
public class ResolverService {
	private static final String ERROR_TAG = "_ERROR_";
	private static final Logger logger = LoggerFactory.getLogger(ResolverService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private AssemblyService assemblyServ;
	@Autowired
	private AmendmentService amendServ;


	/**
	 * Resolve model to values map for using in DocxView
	 * @param model
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, Object> resolveModel(Map<String, Object> model, ResourceDTO fres) throws ObjectNotFoundException {
		Map<String, Object> errors = new LinkedHashMap<String, Object>();
		for(String key :model.keySet()) {
			String[] expr = key.split("@");
			if(expr.length==2) {
				Map<String, Object> value = resolve(expr[0],fres);	//first change here in READVARIABLE
				model.put(key, valueToString(value, expr[1]));				//second there
				errors = resolveError(value, errors);
			}else {
				if(!key.equalsIgnoreCase(ERROR_TAG)) {
					model=error("resolveModel. Wrong expression "+key, key, model);
				}
			}
		}
		if(errors.keySet().size()>0) {		//actually one
			model.putAll(errors);
		}
		return model;
	}
	/**
	 * Add info to the _ERROR_  tag, if one
	 * @param value
	 * @param model
	 * @return
	 */
	private Map<String, Object> resolveError(Map<String, Object> value, Map<String, Object> model) {
		Object valueErr = value.get(ERROR_TAG);
		if(valueErr != null) {
			String valueErrStr = (String) valueErr;
			Object modelErr = model.get(ERROR_TAG);
			if(modelErr != null && modelErr instanceof String) {
				String modelErrStr = (String) modelErr;
				model.put(ERROR_TAG, modelErrStr+"; " + valueErrStr);
			}else {
				model.put(ERROR_TAG, valueErrStr);
			}
		}
		return model;
	}
	/**
	 * Convert resolved value to string representation
	 * @param value
	 * @param string
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private Object valueToString(Map<String, Object> value, String dataType) throws ObjectNotFoundException {
		String ret="";
		if(dataType.equalsIgnoreCase(ERROR_TAG)) {
			return value.get(ERROR_TAG);
		}
		Object data = value.get(dataType.trim());
		if(data==null && dataType.startsWith("level")) {
			data="";
		}
		if(data!=null) {
			if(dataType.equalsIgnoreCase("number")) {
				if(data instanceof Long) {
					Long retLong= (Long) data;
					String str = String.format("%,d", retLong);
					return str;
				}
			}
			//Nepali number
			if(dataType.equalsIgnoreCase("numberBS")) {
				if(data instanceof Long) {
					Long retLong= (Long) data;
					Locale locale = new Locale("ne","NP'");
					String str = String.format(locale,"%,d", retLong);
					str=boilerServ.numberToNepali(str);
					return str;
				}
			}
			
			
			if(dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("registered") || dataType.equalsIgnoreCase("expired")) {
				if(data instanceof LocalDate) {
					LocalDate ld = (LocalDate) data;
					String ldStr = ld.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
					return ldStr;
				}
			}

			if(dataType.equalsIgnoreCase("dateBS")) {
				if(data instanceof LocalDate) {
					LocalDate ld = (LocalDate) data;
					String ldStr = boilerServ.localDateToNepali(ld, true);
					return ldStr;
				}
			}

			if(dataType.equalsIgnoreCase("dateBS1")) {
				if(data instanceof LocalDate) {
					LocalDate ld = (LocalDate) data;
					String ldStr = boilerServ.localDateToNepali(ld, false);
					return ldStr;
				}
			}
			// full Gregorian years from date to the current date
			if(dataType.equalsIgnoreCase("years")) {
				if(data instanceof LocalDate) {
					LocalDate ld = (LocalDate) data;
					int years = fullYears(ld);
					String str = String.format("%,d", new Long(years));
					return str;
				}
			}
			
			// full Gregorian years from date to the current date, but Nepali numbers
			if(dataType.equalsIgnoreCase("yearsBS")) {
				if(data instanceof LocalDate) {
					LocalDate ld = (LocalDate) data;
					int years = fullYears(ld);
					Locale locale = new Locale("ne","NP'");
					String str = String.format(locale,"%,d", new Long(years));
					str=boilerServ.numberToNepali(str);
					return str;
				}
			}
			
			// full Nepali years from date to the current date
			if(dataType.equalsIgnoreCase("yearsBS1")) {
				if(data instanceof LocalDate) {
					LocalDate ld = (LocalDate) data;
					int years = boilerServ.fullYearsNepali(ld);
					Locale locale = new Locale("ne","NP'");
					String str = String.format(locale,"%,d", new Long(years));
					str=boilerServ.numberToNepali(str);
					return str;
				}
			}

			//the rest are always strings
			if(data instanceof String) {
				ret=(String) data;
			}
			return ret;
		}else {
			if(value.size()>0) {
				value = error(dataType, "valueToString. Wrong data type "+dataType +"/"+value.keySet(),value);	//object found, datatype wrong
				return "";
			}else {
				value = error(dataType, "valueToString. Object not found for datatype "+ dataType +" will return empty string", value);
				return "";
			}

		}

	}

	/**
	 * Resolve expression to map key is data type (literal, date, number, dictionary, address, image) value 
	 * depends on the data type
	 * @param expr
	 * @param rootNodeId
	 * @param fres - the template with EL expressions
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, Object> resolve(String expr,  ResourceDTO fres) throws ObjectNotFoundException{
		//System.out.println(new Date());
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		long historyId=fres.getHistoryId();
		if(expr!=null && expr.length()>0) {
			if(historyId>0) {
				List<String> urls = Arrays.asList(expr.split("/"));

				if(urls.get(0).equalsIgnoreCase("character")) {
					return resolveCharacter(urls,fres,ret);
				}

				if(urls.get(0).equalsIgnoreCase("person")) {
					//resolve from person node
					return resolvePerson(urls, fres, ret);
				}
				if(urls.get(0).equalsIgnoreCase("this")) {
					//resolve from the current Thing
					if(urls.size()==2) {
						ret=readVariableFromThing(urls.get(1),fres.getData(),ret);
						return ret;
					}else {
						ret = error(expr, "resolve. Invalid expression "+expr,ret);
					}
					//top url may belong to application or activity data, but anyway it is a thing
				}else {
					//resolve from the top concept
					long nodeId = fres.getData().getNodeId();
					if(nodeId==0) {
						nodeId=fres.getData().getParentId();
					}
					Concept topConcept = topConcept(urls.get(0), nodeId, historyId,ret);
					if(topConcept!=null) {
						if(urls.size()>=1) {
							ret = plainVariable(ret, urls, topConcept);
						}else {
							ret = error("resolve. Variable is not defined. "+expr, expr, ret);
							return ret;
						}
					}
				}
			}else {
				ret = error(expr, "resolve. History ID in ThingDTO is ZERO - wrong software codes, call tech support!",ret);
				return ret;
			}
		}else {
			ret = error(expr, "resolve. Expression is empty",ret);
			return ret;
		}
		//System.out.println(new Date());
		return ret;
	}

	/**
	 * Add an error message to the map
	 * @param message
	 * @param expr string for which error occurs
	 * @param ret map of the resolved variables
	 * @return
	 */
	private Map<String, Object> error(String expr, String message, Map<String, Object> ret) {
		String errMess = "["+ expr + ": " +message + "]";
		Object err = ret.get(ERROR_TAG);
		if(err!=null && err instanceof String) {
			String errStr=(String) err;
			errMess = errStr+", "+errMess;
		}else {
			ret.put(ERROR_TAG, errMess);
		}
		return ret;
	}
	/**
	 * Get a plain variable from the top concept
	 * @param ret map with the variables 
	 * @param urls path to the variable
	 * @param topConcept concept from which the path starts
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Map<String, Object> plainVariable(Map<String, Object> ret, List<String> urls, Concept topConcept)
			throws ObjectNotFoundException {
		Concept var=topConcept;
		List<String> varNameList=urls.subList(1, urls.size());
		if(urls.size()>2) {
			var=nextConcept(urls.get(1),var,ret);
			varNameList=urls.subList(2, urls.size());
		}

		//
		String varName = String.join("/",varNameList);
		ret=readVariable(varName, var, ret);				//ADD NEW CLASSESS TO IT!
		return ret;
	}

	/**
	 * Resolve PersonSpecial assignment
	 * @param urls
	 * @param fres
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Map<String, Object> resolveCharacter(List<String> urls, ResourceDTO fres, Map<String, Object> ret) throws ObjectNotFoundException {
		long historyId=fres.getHistoryId();
		if(urls.size()>2) {		//character/pharmacist/prefLabel@literal
			if(historyId>0) {
				History his = boilerServ.historyById(historyId);
				Thing thing = boilerServ.thingByNode(his.getApplicationData());
				long characterId = 0;
				for(ThingThing tt : thing.getThings()) {
					if(tt.getVarname().equalsIgnoreCase(urls.get(1))) {
						String idStr = tt.getConcept().getLabel();
						if(idStr != null) {
							try {
								characterId= new Long(idStr);
							} catch (NumberFormatException e) {
								//nothing to do
							}
						}
					}
				}
				if(characterId>0) {
					Concept topConcept=closureServ.loadConceptById(characterId);
					urls=urls.subList(1,urls.size());		//character
					//search for concept
					int lastIndex=urls.size()-1;
					if(lastIndex>=1) {
						String varName=urls.get(lastIndex);
						urls=urls.subList(1, lastIndex);
						Concept var=topConcept;
						for(String v : urls) {
							var=nextConcept(v, var,ret);
						}
						ret=readVariable(varName, var, ret);
					}else {
						ret = error(urls.toString(), "resolveCharacter. Variable is not defined. "+urls, ret);
						return ret;
					}
				}else {
					ret = error(urls.get(1), "resolveCharacter. character not found. Variable is " + urls.get(1), ret);
				}
			}else {
				ret = error("Call Tech Support!","resolveCharacter. HistoryID is zero",ret);
			}
		}else {
			ret = error(urls.toString(),"reslveCharacter. Path should contain at least 3 componetns. Actual is " + urls.size(), ret);
		}
		return ret;
	}
	/**
	 * Resolve person data
	 * person/selector/url/variable@convertor
	 * @param urls
	 * @param fres
	 * @param ret 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Map<String, Object> resolvePerson(List<String> urls, ResourceDTO fres, Map<String, Object> ret) throws ObjectNotFoundException {
		if(urls.size()>=4) {
			String personSelector = urls.get(1);
			Set<String> keys = fres.getData().getPersonselection().keySet();
			long persNodeId=0l;
			for(String key : keys) {
				if(key.equalsIgnoreCase(personSelector)) {
					persNodeId=fres.getData().getPersonselection().get(key);
					break;
				}
			}
			if(persNodeId>0) {
				Concept topConcept=closureServ.loadConceptById(persNodeId);
				if(topConcept!=null) {
					urls=urls.subList(2,urls.size());		//person + selector name
					//search for concept
					int lastIndex=urls.size()-1;
					if(lastIndex>=1) {
						String varName=urls.get(lastIndex);
						urls=urls.subList(1, lastIndex);
						Concept var=topConcept;
						for(String v : urls) {
							var=nextConcept(v, var,ret);
						}
						ret=readVariable(varName, var, ret);				//ADD NEW CLASSESS TO IT!
					}else {
						ret = error(urls.toString(),"resolve. Variable is not defined. "+urls,ret);
					}
				}
			}else {
				ret = error("Select a person","resolvePerson. Selection not found for selector "+personSelector, ret);
			}
			return ret;
		}else {
			ret = error(urls.toString(),"resolvePerson. Should be at least 4 elements, actually "+urls.size() ,ret);
			return ret;
		}
	}
	/**
	 * Read values from the current thing passed in FileResourseDTO.data
	 * @param varName 
	 * @param data
	 * @param ret 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Map<String, Object> readVariableFromThing(String varName, ThingValuesDTO data, Map<String, Object> value) throws ObjectNotFoundException {
		value.clear();
		//literals
		String valStr = data.getLiterals().get(varName.toUpperCase());
		if(valStr!=null) {
			value.put("literal", valStr);
			return value; 
		}
		String valStr1 = data.getStrings().get(varName.toUpperCase());
		if(valStr1!=null) {
			value.put("literal", valStr1);
			return value; 
		}
		//numbers
		Long valLong = data.getNumbers().get(varName.toUpperCase());
		if(valLong != null) {
			value.put("number", valLong);
			return value;
		}
		//dates
		LocalDate valLd = data.getDates().get(varName.toUpperCase());
		if(valLd != null) {
			value.put("date", valLd);
			return value;
		}
		//dictionaries
		DictValuesDTO valDict = data.getDictionaries().get(varName.toUpperCase());
		if(valDict != null) {
			List<AssemblyDTO> adList = assemblyServ.auxDictionaries(data.getUrl());
			for(AssemblyDTO ad : adList) {
				List<Concept> selected = new ArrayList<Concept>();
				for(Long id : valDict.getSelected()) {
					selected.add(closureServ.loadConceptById(id));
				}
				value=dictionaryValues(ad, value, selected);
			}
			return value;
		}
		//addresses
		AddressValuesDTO valAddr = data.getAddresses().get(varName.toUpperCase());
		if(valAddr != null) {
			value.put("gis", valAddr.getGisCoordinates());
			List<Long> selected = valAddr.getAdminUnits().getSelected();
			if(selected.size()==1) {
				Concept au = closureServ.loadConceptById(selected.get(0));
				value = addressLevels(value, au);
				return value;
			}else {
				value = error(valAddr.toString(),"readVariableFromThing. Wrong address "+valAddr.toString(),value);
			}	
		}
		//schedulers
		SchedulerDTO valSched = data.getSchedulers().get(varName.toUpperCase());
		if(valSched!=null) {
			value.put("date",valSched.getSchedule().getValue());
			return value;
		}
		//registers
		RegisterDTO valReg=data.getRegisters().get(varName.toUpperCase());
		if(valReg!=null) {
			value.put("literal",valReg.getReg_number().getValue());
			value.put("registered",valReg.getRegistration_date().getValue());
			value.put("expired",valReg.getExpiry_date().getValue());
			value.put("registeredBS",boilerServ.localDateToNepali(valReg.getRegistration_date().getValue(),true));
			value.put("expiredBS",boilerServ.localDateToNepali(valReg.getExpiry_date().getValue(),true));
			value.put("registeredBS1",boilerServ.localDateToNepali(valReg.getRegistration_date().getValue(),false));
			value.put("expiredBS1",boilerServ.localDateToNepali(valReg.getExpiry_date().getValue(),false));
		}

		return value;
	}
	/**
	 * Read a value of the variable 
	 * Do not forget do the same for readVariableFromThing
	 * @param varName
	 * @param var
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, Object> readVariable(String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		value.clear();
		//init configurations
		Thing varThing = boilerServ.thingByNode(var);
		List<AssemblyDTO> strings = assemblyServ.auxStrings(varThing.getUrl());
		List<AssemblyDTO> literals = assemblyServ.auxLiterals(varThing.getUrl());
		List<AssemblyDTO> numbers = assemblyServ.auxNumbers(varThing.getUrl());
		List<AssemblyDTO> dates = assemblyServ.auxDates(varThing.getUrl());
		List<AssemblyDTO> addresses = assemblyServ.auxAddresses(varThing.getUrl());
		List<AssemblyDTO> dictionaries = assemblyServ.auxDictionaries(varThing.getUrl());
		List<AssemblyDTO> documents = assemblyServ.auxDocuments(varThing.getUrl());
		List<AssemblyDTO> schedulers = assemblyServ.auxSchedulers(varThing.getUrl());
		List<AssemblyDTO> persons = assemblyServ.auxPersons(varThing.getUrl());
		List<AssemblyDTO> registers = assemblyServ.auxRegisters(varThing.getUrl());

		for(AssemblyDTO ad : strings) {											//strings are literals
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				String valStr = literalServ.readValue(varName, var);
				if(valStr.length()>0) {
					value.put("literal", valStr);
					return value;
				}else if(valStr.length() == 0) {
					value.put("literal", valStr);
					value = error(varName, "readVariable. Value is empty for "+varName, value);
					return value;
				}
			}
		}

		for(AssemblyDTO ad : literals) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				String valStr = literalServ.readValue(varName, var);
				if(valStr.length()>0) {
					value.put("literal", valStr);
					return value;
				}else if(valStr.length() == 0) {
					value.put("literal", valStr);
					value = error(varName, "readVariable. Value is empty for "+varName, value);
					return value;
				}
			}
		}

		for(AssemblyDTO ad : numbers) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				String valStr = literalServ.readValue(varName, var);
				if(valStr.length()>0) {
					Long num = boilerServ.longParse(valStr);
					value.put("number", num);
					value.put("numberBS", num);
					return value;
				}else if(valStr.length() == 0) {
					value.put("number", valStr);
					value.put("numberBS",valStr);
					value = error(varName, "readVariable. Value is empty for "+varName, value);
					return value;
				}
			}
		}
		for(AssemblyDTO ad : dates) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				String valStr = literalServ.readValue(varName, var);
				if(valStr.length()>0) {
					LocalDate dt = boilerServ.localDateParse(valStr);
					value.put("date", dt);
					value.put("dateBS", boilerServ.localDateToNepali(dt,false));
					value.put("dateBS1", boilerServ.localDateToNepali(dt,true));
					value.put("years", dt);
					value.put("yearsBS", dt);
					value.put("yearsBS1", dt);
					return value;
				}else if(valStr.length() == 0) {
					value.put("date", valStr);
					value.put("dateBS", valStr);
					value.put("dateBS1", valStr);
					value.put("years",valStr);
					value.put("yearsBS", valStr);
					value.put("yearsBS1", valStr);
					value = error(varName, "readVariable. Value is empty for "+varName, value);
					return value;
				}
			}
		}
		for(AssemblyDTO ad : addresses) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				value=address(varName, var,value);
				return value;
			}
		}
		for(AssemblyDTO ad : dictionaries) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				value=dictionary(ad, varName, var,value);
				return value;
			}
		}
		for(AssemblyDTO ad:documents) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				value=document(ad, varName, var,value);
				return value;
			}
		}
		for(AssemblyDTO ad :schedulers) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				value=scheduler(ad,varName,var,value);
			}
		}

		for(AssemblyDTO ad :persons) {
			if(varName.toUpperCase().startsWith(ad.getPropertyName().toUpperCase())) {	//following path will be after variable
				value=persons(ad,varName,var,value);
			}
		}

		for(AssemblyDTO ad :registers) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				value=register(ad,varName,var,value);
			}
		}

		if(value == null) {
			value=error(varName,"readVariable. Value not found",value);
		}
		if(value.size() == 0) {
			value = error(varName,"readVariable. Value is empty", value);
		}
		return value;
	}
	/**
	 * Full years between he current data and dt
	 * @param dt
	 * @return
	 */
	private int fullYears(LocalDate dt) {
		Period period = Period.between(dt, LocalDate.now().plusDays(1));
		return period.getYears();
	}
	/**
	 * Data from a register
	 * @param ad
	 * @param varName
	 * @param var
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private Map<String, Object> register(AssemblyDTO ad, String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		Thing thing = boilerServ.thingByNode(var);
		for(ThingRegister tr : thing.getRegisters()) {
			Register reg = boilerServ.registerByConcept(tr.getConcept());
			LocalDate regDate = boilerServ.convertToLocalDateViaMilisecond(reg.getRegisteredAt());
			LocalDate expDate = boilerServ.convertToLocalDateViaMilisecond(reg.getValidTo());
			value.put("literal",reg.getRegister());
			value.put("registered",regDate);				//to locale string!!!!
			value.put("registeredBS", boilerServ.localDateToNepali(regDate, false));
			value.put("registeredBS1",boilerServ.localDateToNepali(regDate, true));
			value.put("expired", expDate);
			value.put("expiredBS", boilerServ.localDateToNepali(expDate, false));
			value.put("expiredBS1",boilerServ.localDateToNepali(expDate, true));
			
			/**
			value.put("registered",valReg.getRegistration_date().getValue());
			value.put("expired",valReg.getExpiry_date().getValue());
			value.put("registeredBS",boilerServ.localDateToNepali(valReg.getRegistration_date().getValue(),true));
			value.put("expiredBS",boilerServ.localDateToNepali(valReg.getExpiry_date().getValue(),true));
			value.put("registeredBS1",boilerServ.localDateToNepali(valReg.getRegistration_date().getValue(),false));
			value.put("expiredBS1",boilerServ.localDateToNepali(valReg.getExpiry_date().getValue(),false));*/
		}
		return value;
	}
	/**
	 * Create a variable from persons
	 * @param ad
	 * @param varName
	 * @param var
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Map<String, Object> persons(AssemblyDTO ad, String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		Thing thing = boilerServ.thingByNode(var);
		List<Concept> pers = new ArrayList<Concept>();
		for(ThingPerson tp : thing.getPersons()) {
			pers.add(tp.getConcept());
		}
		//varName should has following structure varName/index/path_to_value
		//example is owners/1/prefLabel means prefLAbel of the first person in the list of owners
		//thus, parse it and make a recursive call
		List<String> urls = Arrays.asList(varName.split("/"));
		if(urls.size()>2) {
			try {
				Integer index=Integer.valueOf(urls.get(1));
				if(pers.size()>index) {
					List<String> path=urls.subList(1, urls.size());
					value = plainVariable(value,path,pers.get(index));
				}else {
					value.put(urls.get(urls.size()-1), "");
					value=error(varName, " Index to a person data is wrong " +index +" allowed up to "+(pers.size()-1),value);
				}
			} catch (NumberFormatException e) {
				value.put(urls.get(urls.size()-1), "");
				value = error(varName, "Path to a person data is wrong ",value);
			}
		}else {
			value.put(urls.get(urls.size()-1), "");
			value = error(varName, "Path to a person data is wrong ",value);
		}
		return value;
	}
	/**
	 * Scheduler with converter "date"
	 * @param ad
	 * @param varName
	 * @param var
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Map<String, Object> scheduler(AssemblyDTO ad, String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		Thing thing = boilerServ.thingByNode(var);
		for(ThingScheduler ts : thing.getSchedulers()) {
			if(ts.getVarName().equalsIgnoreCase(varName)) {
				Scheduler sched = boilerServ.schedulerByNode(ts.getConcept());
				if(sched.getScheduled() != null) {
					LocalDate ld = boilerServ.convertToLocalDateViaMilisecond(sched.getScheduled());
					value.put("date",ld);
					break;
				}
			}
		}
		return value;
	}
	/**
	 * The list of files, if needed
	 */
	private Map<String, Object> document(AssemblyDTO ad, String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		Thing thing = boilerServ.thingByNode(var);
		List<String> filenames = new ArrayList<String>();
		List<String> preflabels = new ArrayList<String>();
		List<String> descr = new ArrayList<String>();
		for(ThingDoc td:thing.getDocuments()) {
			if(td.getVarName().equalsIgnoreCase(varName)) {
				Concept c = td.getConcept();
				String fname = c.getLabel();
				if(fname.isEmpty()) {
					value = error(varName, "document. Value filename is empty", value);
				}
				filenames.add(fname);
				Concept dict = td.getDictNode();
				String preflabel = literalServ.readPrefLabel(dict);
				preflabels.add(preflabel);
				//value.put("name", name);
				String desc = literalServ.readDescription(dict);
				if(desc.isEmpty())
					desc = " ";
				descr.add(desc);
				//value.put("description", desc);
			}
		}
		if(filenames.size() > 0) {
			String fname = String.join("/", filenames);
			String choice = String.join(", ", filenames);
			String preflabel = String.join("/", preflabels);
			String desc = String.join("/", descr);

			value.put("filename", fname);
			value.put("preflabel", preflabel);
			value.put("description", desc);
			value.put("choice", choice);
		}

		return value;
	}
	/**
	 * Read selected dictionary values
	 * @param ad 
	 * @param varName
	 * @param var
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private Map<String, Object> dictionary(AssemblyDTO ad, String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		value.clear();
		Thing thing = boilerServ.thingByNode(var);
		List<Concept> selected = new ArrayList<Concept>();
		for(ThingDict td :thing.getDictionaries()) {
			if(td.getVarname().equalsIgnoreCase(varName)) {
				selected.add(td.getConcept());
			}
		}
		dictionaryValues(ad, value, selected);
		return value;
	}
	/**
	 * Create values from the dictionary
	 * For single choice - all levels
	 * For mult choice a String that represents a list
	 * @param ad
	 * @param value
	 * @param selected
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public Map<String, Object> dictionaryValues(AssemblyDTO ad, Map<String, Object> value, List<Concept> selected)
			throws ObjectNotFoundException {
		List<String> choices= new ArrayList<String>();
		if(selected.size()>0) {
			List<Concept> all = closureServ.loadParents(selected.get(0));
			for(int i=1; i<all.size()-1;i++) {
				String item = literalServ.readPrefLabel(all.get(i));
				value.put("level"+i, item);
				choices.add(item);
			}
			if(selected.size()==1) {
				String item = literalServ.readPrefLabel(selected.get(0));
				value.put("level"+(all.size()-1), item);
				choices.add(item);
			}else {
				List<String> mChoice = new ArrayList<String>();
				for(Concept dictNode : selected) {
					mChoice.add(literalServ.readPrefLabel(dictNode));
				}
				String item = String.join(",", mChoice);
				value.put("level"+(all.size()-1), item);
				choices.add("/"+item);
			}
			value.put("choice", String.join(", ", choices));		
		}
		return value;
	}
	/**
	 * Read an address and represent it as a map
	 * @param varName 
	 * @param var
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private Map<String, Object> address(String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		value.clear();
		Thing thing = boilerServ.thingByNode(var);
		for(ThingThing tt : thing.getThings()) {
			if(tt.getVarname().equalsIgnoreCase(varName)) {
				Concept addr = tt.getConcept();
				value.put("gis", addr.getLabel());
				Thing addrThing = boilerServ.thingByNode(addr);
				Concept au = new Concept();
				for(ThingDict td : addrThing.getDictionaries()) {
					au=td.getConcept();
				}
				if(au.getID()>0) {
					value=addressLevels(value, au);
				}
			}
		}
		return value;
	}
	/**
	 * create address levels map from admin unit dict node 
	 * @param value map
	 * @param au admin unit dict node 
	 * @throws ObjectNotFoundException
	 */
	public Map<String, Object> addressLevels(Map<String, Object> value, Concept au) throws ObjectNotFoundException {
		List<Concept> all = closureServ.loadParents(au);
		List<String> choices= new ArrayList<String>();
		for(int i=1; i<all.size();i++) {
			String item = literalServ.readPrefLabel(all.get(i));
			value.put("level"+i, item);
			choices.add(item);
		}
		value.put("choice", String.join(", ", choices));
		//for some reason it will be nice to reserve up to 10 levels
		for(int i=all.size();i<10;i++) {
			value.put("level"+i, "");
		}
		return value;
	}
	/**
	 * get next concept
	 * @param varName
	 * @param var
	 * @param ret 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept nextConcept(String varName, Concept var, Map<String, Object> ret) throws ObjectNotFoundException {
		Thing thing = boilerServ.thingByNode(var);
		for(ThingThing tt : thing.getThings()) {
			if(tt.getVarname().equalsIgnoreCase(varName.trim())) {
				return tt.getConcept();
			}
		}
		ret = error(varName,"nextConcept. Variable not found.", ret);
		return var;
	}
	/**
	 * Search for top concept in data, then in history
	 * @param url top url
	 * @param historyId - id of the current history
	 * @param nodeId 
	 * @return null if not found
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept topConcept(String url,  long nodeId, long historyId, Map<String, Object> ret) throws ObjectNotFoundException {
		History his = boilerServ.historyById(historyId);
		//try in application data from the root of one
		List<Concept> all = closureServ.loadParents(his.getApplicationData());
		if(url.length()==0 || all.get(0).getIdentifier().equalsIgnoreCase(url)) {
			return amendServ.initialApplicationData(his.getApplicationData());
			/*Concept amended = amendServ.amendedConcept(his.getApplicationData());
			if(amended.getID()>0) {
				return amendServ.amendedApplication(amended);
			}else {
				return his.getApplicationData();
			}*/
		}
		if(url.equals("~")) {					//home node of this object. Assume two level hierarchy. Any ThingThing point to it
			if(nodeId>0) {
				Concept node = closureServ.loadConceptById(nodeId);
				Concept home = boilerServ.homeNode(node);
				return home;
			}else {
				ret=error("topConcept ~", "Node is not saved yet",ret);
			}
		}
		//try in activity data
		if(his.getApplication()!=null) {
			List<History> allHis = boilerServ.historyAllByApplication(his.getApplication());
			for(History history :allHis) {
				if(history.getDataUrl()!=null
						&& (url.length()==0 || history.getDataUrl().equalsIgnoreCase(url))
						&& history.getActivityData()!=null) {
					return history.getActivityData();
				}
			}
		}
		ret = error("url/historyId is " +url+"/"+historyId, "topConcept.Top concept not found", ret);
		return null;
	}



}
