package org.msh.pharmadex2.service.r2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Scheduler;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.ThingDoc;
import org.msh.pdex2.model.r2.ThingScheduler;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.repository.r2.ThingRepo;
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
	private static final Logger logger = LoggerFactory.getLogger(ResolverService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private AssemblyService assemblyServ;
	/**
	 * Resolve model to values map for using in DocxView
	 * @param model
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, Object> resolveModel(Map<String, Object> model, ResourceDTO fres) throws ObjectNotFoundException {
		for(String key :model.keySet()) {
			String[] expr = key.split("@");
			if(expr.length==2) {
				Map<String, Object> value = resolve(expr[0],fres);	//first change here in READVARIABLE
				model.put(key, valueToString(value, expr[1]));				//second there
			}else {
				throw new ObjectNotFoundException("resolveModel. Wrong expression "+key,logger);
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
		Object data = value.get(dataType.trim());
		if(data==null && dataType.startsWith("level")) {
			data=value.get("choice");
		}
		if(data!=null) {

			if(dataType.equalsIgnoreCase("number")) {
				if(data instanceof Long) {
					Long retLong= (Long) data;
					return retLong+"";
				}
			}
			if(dataType.equalsIgnoreCase("date") || dataType.equalsIgnoreCase("registered") || dataType.equalsIgnoreCase("expired")) {
				if(data instanceof LocalDate) {
					LocalDate ld = (LocalDate) data;
					String ldStr = ld.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
					return ldStr;
				}
			}
			//the rest are always strings
			if(data instanceof String) {
				ret=(String) data;
			}
			return ret;
		}else {
			if(value.size()>0) {
				logger.warn("valueToString. Wrong data type "+dataType +"/"+value.keySet(),logger);	//object found, datatype wrong
				return "";
			}else {
				logger.warn("valueToString. Object not found for datatype "+ dataType +" will return empty string", logger);
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
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		long historyId=fres.getHistoryId();
		if(expr!=null && expr.length()>0) {
			if(historyId>0) {
				List<String> urls = Arrays.asList(expr.split("/"));

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
						throw new ObjectNotFoundException("resolve. Invalid expression "+expr,logger);
					}
					//top url may belong to application or activity data, but anyway it is a thing
				}else {
					//resolve from the top concept
					Concept topConcept = topConcept(urls.get(0), historyId);
					if(topConcept!=null) {
						//search for concept
						int lastIndex=urls.size()-1;
						if(lastIndex>=1) {
							String varName=urls.get(lastIndex);
							urls=urls.subList(1, lastIndex);
							Concept var=topConcept;
							for(String v : urls) {
								var=nextConcept(v, var);
							}
							ret=readVariable(varName, var, ret);				//ADD NEW CLASSESS TO IT!
						}else {
							throw new ObjectNotFoundException("resolve. Variable is not defined. "+expr,logger);
						}
					}
				}
			}else {
				throw new ObjectNotFoundException("resolve. History ID in ThingDTO is ZERO",logger);
			}
		}else {
			throw new ObjectNotFoundException("resolve. Expression is empty",logger);
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
							var=nextConcept(v, var);
						}
						ret=readVariable(varName, var, ret);				//ADD NEW CLASSESS TO IT!
					}else {
						throw new ObjectNotFoundException("resolve. Variable is not defined. "+urls,logger);
					}
				}
			}else {
				logger.error("resolvePerson. Selection not found for selector "+personSelector);
			}
			return ret;
		}else {
			throw new ObjectNotFoundException("resolvePerson. Should be at least 4 elements, actually "+urls.size() ,logger);
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
				throw new ObjectNotFoundException("readVariableFromThing. Wrong address "+valAddr.toString(),logger);
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
		Thing varThing = boilerServ.loadThingByNode(var);
		value.clear();
		List<AssemblyDTO> literals = assemblyServ.auxLiterals(varThing.getUrl());
		List<AssemblyDTO> numbers = assemblyServ.auxNumbers(varThing.getUrl());
		List<AssemblyDTO> dates = assemblyServ.auxDates(varThing.getUrl());
		List<AssemblyDTO> addresses = assemblyServ.auxAddresses(varThing.getUrl());
		List<AssemblyDTO> dictionaries = assemblyServ.auxDictionaries(varThing.getUrl());
		List<AssemblyDTO> documents = assemblyServ.auxDocuments(varThing.getUrl());
		List<AssemblyDTO> schedulers = assemblyServ.auxSchedulers(varThing.getUrl());

		for(AssemblyDTO ad : literals) {
			if(ad.getPropertyName().equalsIgnoreCase(varName)) {
				String valStr = literalServ.readValue(varName, var);
				if(valStr.length()>0) {
					value.put("literal", valStr);
					return value;
				}else if(valStr.length() == 0) {
					value.put("literal", valStr);
					logger.warn("readVariable. Value is empty for "+varName);
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
					return value;
				}else if(valStr.length() == 0) {
					value.put("number", valStr);
					logger.warn("readVariable. Value is empty for "+varName);
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
					return value;
				}else if(valStr.length() == 0) {
					value.put("date", valStr);
					logger.warn("readVariable. Value is empty for "+varName);
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
		if(value == null) {
			logger.warn("readVariable. Value not found for "+varName,logger);
		}
		if(value.size() == 0) {
			logger.warn("readVariable. Value is empty for "+varName, logger);
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
	private Map<String, Object> scheduler(AssemblyDTO ad, String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		Thing thing = boilerServ.loadThingByNode(var);
		for(ThingScheduler ts : thing.getSchedulers()) {
			if(ts.getVarName().equalsIgnoreCase(varName)) {
				Scheduler sched = boilerServ.loadSchedulerByNode(ts.getConcept());
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
	 * В таблице с документами их может быть много
	 * добавим все значения обьединив их "/"
	 * так же добавим поле "chose" где будет список названий файлов через ","
	 */
	private Map<String, Object> document(AssemblyDTO ad, String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		Thing thing = boilerServ.loadThingByNode(var);
		List<String> filenames = new ArrayList<String>();
		List<String> preflabels = new ArrayList<String>();
		List<String> descr = new ArrayList<String>();
		for(ThingDoc td:thing.getDocuments()) {
			if(td.getVarName().equalsIgnoreCase(varName)) {
				Concept c = td.getConcept();
				String fname = c.getLabel();
				if(fname.isEmpty()) {
					logger.warn("document. Value filename is empty for "+varName);
				}
				filenames.add(fname);
					//value.put("filename", fname);
				
				Concept dict = td.getDictNode();//closureServ.loadConceptById(td.getDictNode().getID());
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
		Thing thing = boilerServ.loadThingByNode(var);
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
		if(ad.isMult()) {
			List<String> choices= new ArrayList<String>();
			for(Concept dictNode : selected) {
				choices.add(literalServ.readPrefLabel(dictNode));
			}
			value.put("choice", String.join(", ", choices));
		}else {
			if(selected.size()==1) {
				List<String> choices= new ArrayList<String>();
				List<Concept> all = closureServ.loadParents(selected.get(0));
				for(int i=1; i<all.size();i++) {
					String item = literalServ.readPrefLabel(all.get(i));
					value.put("level"+i, item);
					choices.add(item);
				}
				value.put("choice", String.join(", ", choices));
			}
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
		Thing thing = boilerServ.loadThingByNode(var);
		for(ThingThing tt : thing.getThings()) {
			if(tt.getVarname().equalsIgnoreCase(varName)) {
				Concept addr = tt.getConcept();
				value.put("gis", addr.getLabel());
				Thing addrThing = boilerServ.loadThingByNode(addr);
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept nextConcept(String varName, Concept var) throws ObjectNotFoundException {
		Thing thing = boilerServ.loadThingByNode(var);
		for(ThingThing tt : thing.getThings()) {
			if(tt.getVarname().equalsIgnoreCase(varName)) {
				return tt.getConcept();
			}
		}
		logger.warn("nextConcept. Variable not found. Name is "+varName);
		return var;
	}
	/**
	 * Search for top concept in data, then in history
	 * @param url top url
	 * @param historyId - id of the current history
	 * @return exception if not found
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept topConcept(String url, long historyId) throws ObjectNotFoundException {
		History his = boilerServ.historyById(historyId);
		//try in application data
		List<Concept> all = closureServ.loadParents(his.getApplicationData());
		if(all.get(0).getIdentifier().equalsIgnoreCase(url)) {
			return his.getApplicationData();
		}
		//try in activity data
		if(his.getApplication()!=null) {
			List<History> allHis = boilerServ.historyAll(his.getApplication());
			for(History history :allHis) {
				if(history.getDataUrl()!=null && history.getDataUrl().equalsIgnoreCase(url) && history.getActivityData()!=null) {
					return history.getActivityData();
				}
			}
		}
		throw new ObjectNotFoundException("topConcept.Top concept not found. url/historyId="+url+"/"+historyId);
	}



}
