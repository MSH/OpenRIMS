package org.msh.pharmadex2.service.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.enums.YesNoNA;
import org.msh.pdex2.model.r2.User;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Checklistr2;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.LocationDTO;
import org.msh.pharmadex2.dto.QuestionDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A collection of methods to create DTOs
 * The creation is mainly from entities
 * @author alexk
 *
 */
@Service
public class DtoService {
	private static final Logger logger = LoggerFactory.getLogger(DtoService.class);
	@Autowired
	ClosureService closureServ;
	@Autowired
	LiteralService literalServ;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	Messages messages;
	
	/**
	 * Create optionDTO in the current language from the list of dictionary nodes
	 * Any dictionary node may contain:
	 * <ul>
	 * 	<li> prefLabel
	 * <li> description
	 * <li> other nodes
	 * </ul>
	 * @param nodes - list of nodes
	 * @param optionDTO 
	 * @return OptionDTO with list of possible values derived from nodes
	 * @throws ObjectNotFoundException 
	 */
	public OptionDTO createOptionDTO(List<Concept> nodes, OptionDTO optionDTO) throws ObjectNotFoundException {
		OptionDTO ret = optionDTO;
		ret.getOptions().clear();
		for(Concept node :nodes) {
			OptionDTO opt = new OptionDTO();
			String prefLabel = literalServ.readValue("prefLabel", node);
			String description = literalServ.readValue("description", node);
			opt.setCode(prefLabel);
			opt.setDescription(description);
			opt.setId(node.getID());
			ret.getOptions().add(opt);
		}
		return ret;
	}
	/**
	 * Crate OptionDT from users list
	 * @param userList
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public OptionDTO userList(List<User> userList) throws ObjectNotFoundException {
		OptionDTO ret = new OptionDTO();
		ret.getOptions().add(OptionDTO.of(0, "-", ""));
		if(userList != null) {
			for(User user : userList) {
				ret.getOptions().add(optionFromUser(user));
			}
		}
		return ret;
	}
	/**
	 * Create a single option from the user record
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public OptionDTO optionFromUser(User user) throws ObjectNotFoundException {
		OptionDTO ret = new OptionDTO();
		if(user.getConcept() != null) {
			ret.setCode(literalServ.readValue("prefLabel", user.getConcept()));
			ret.setDescription(literalServ.readValue("description", user.getConcept()));
		}
		if(ret.getCode().length()==0) {
			ret.setCode(user.getName());
		}
		if(ret.getDescription().length()==0) {
			ret.setDescription(user.getEmail());	
		}
		ret.setId(user.getUserId());
		return ret;
	}



	/**
	 * There are a lot of enums here
	 * Convert any enum to OptionDTO
	 * @param gender
	 * @return
	 */
	public <T> OptionDTO enumToOptionDTO( T value, T[] values) {
		OptionDTO ret = new OptionDTO();
		int selected=-1;
		for(int i=0; i<values.length;i++) {
			T val=values[i];
			String key = val.getClass().getSimpleName() + "."+  val.toString();
			OptionDTO opt = new OptionDTO();
			Long ord = new Long(i+1); //ATTENTION 0 is reserved to empty value!!!!!
			opt.setId(ord);
			opt.setCode(messages.get(key));
			if(val.equals(value)) {
				selected=i;
			}
			ret.getOptions().add(opt);
		}
		if(selected>-1) {
			ret.setCode(ret.getOptions().get(selected).getCode());
			ret.setId(ret.getOptions().get(selected).getId());
		}else {
			ret.setId(0l);
		}
		return ret;
	}

	/**
	 * Enum to form fields
	 * @return
	 */
	public <T> FormFieldDTO<OptionDTO> enumToField(T value, T[] values){
		OptionDTO opt = enumToOptionDTO(value, values);
		FormFieldDTO<OptionDTO> ret = new FormFieldDTO<OptionDTO>(opt);
		return ret;
	}

	/**
	 * Convert boolean value to yes/no
	 * @param value
	 * @return
	 */
	public OptionDTO booleanToYesNo(boolean value) {
		YesNoNA val = YesNoNA.NO;
		if(value) {
			val=YesNoNA.YES;
		}
		OptionDTO ret = enumToOptionDTO(val,YesNoNA.values());
		return ret;
	}

	/**
	 * Create OptionDTO from a dictionary item
	 * @param selected selected item
	 * @param selections possible selection
	 * @return
	 */
	public OptionDTO optionFromDict(DictNodeDTO selected, ArrayList<DictNodeDTO> selections) {
		OptionDTO ret = new OptionDTO();
		if(selected != null) {
			ret.setActive(true);
			ret.setCode((selected.fetchPrefLabel().getValue()));
			ret.setDescription(selected.fetchDescription().getValue());
			ret.setId(selected.getNodeId());
			ret.setOriginalCode(selected.fetchPrefLabel().getValue());
			ret.setOriginalDescription(selected.fetchDescription().getValue());
			ret.getOptions().clear();
			if(selections != null) {
				for(DictNodeDTO dict : selections) {
					ret.getOptions().add(optionFromDict(dict, null));
				}
			}
		}
		return ret;
	}

	/**
	 * Convert OptionDTO to Enum
	 * example:<br>
	 * Nationality nat = optionToEnum(Nationality.values(), data.getPatient().getNationality())
	 * @param values array of all Enum's values
	 * @param opt OptionDTO to convert
	 * @return Enum value
	 */
	public <T> T optionToEnum(T[] values, OptionDTO opt) {
		if(opt != null) {
			if(values.length>=opt.getId()) {
				Long ordl = new Long(opt.getId()); 
				if(ordl>0) {
					return values[ordl.intValue()-1];
				}else {
					return null;
				}
			}else {
				return null;
			}
		}else {
			return null;
		}
	}

	/**
	 * может быть 2 варианта варианта записи в БД
	 * 1) -15,098470; 48,798399 - более старый
	 * 2) -15.098470, 48.798399 - новый
	 * @param loc
	 * @return
	 */
	public LocationDTO createLocationDTO(String loc) {
		LocationDTO dto = new LocationDTO();
		if(loc != null && loc.length() > 0) {
			String[] mas = loc.split(";");
			if(mas.length == 2) {
				Double l = new Double(mas[0].trim());
				dto.setLat(l);
				l = new Double(mas[1].trim());
				dto.setLng(l);
			}else {
				mas = loc.split(",");
				if(mas.length == 2) {
					Double l = new Double(mas[0].trim());
					dto.setLat(l);
					l = new Double(mas[1].trim());
					dto.setLng(l);
				}
			}
		}
		return dto;
	}
	/**
	 * Convert Checklistr2 to QuestionDTO 
	 * @param ch - database
	 * @param dict - record from the dictionary
	 * @return
	 */
	@Transactional
	public QuestionDTO question(Checklistr2 ch, OptionDTO dict) {
		QuestionDTO ret = new QuestionDTO();
		String val = ch.getComment();
		if(val==null) {
			val="";
		}
		ret.setComment(FormFieldDTO.of(val));
		ret.setDescription(dict.getDescription());
		ret.setDictId(ch.getDictItem().getID());
		ret.setHead(!dict.isActive());
		ret.setId(ch.getID());
		ret.setQuestion(ch.getQuestion());
		ret.setAnswer(ch.getAnswer());
		return ret;
	}

	/**
	 * Create empty fields from literal descriptions
	 * @param data
	 * @param literals
	 * @return 
	 * @return
	 */
	public <T extends ThingDTO> T createLiterals(T data, List<AssemblyDTO> literals, boolean thisApplicant) {
		data.getLiterals().clear();
		if(literals != null) {
			for(AssemblyDTO ad : literals) {
				FormFieldDTO<String> fld = FormFieldDTO.of("", ad.isReadOnly(), ad.isTextArea(),ad.getAssistant());
				data.getLiterals().put(ad.getPropertyName(), fld);
			}
		}
		return data;
	}
	/**
	 * Create empty strings
	 * @param data
	 * @param strings
	 * @return
	 */
	public  <T extends ThingDTO> T createStrings(T data, List<AssemblyDTO> strings, boolean thisApplicant) {
		data.getStrings().clear();
		if(strings != null) {
			for(AssemblyDTO ad : strings) {
				FormFieldDTO<String> fld = FormFieldDTO.of("", ad.isReadOnly(), ad.isTextArea(), ad.getAssistant());
				data.getStrings().put(ad.getPropertyName(), fld);
			}
		}
		return data;
	}
	
	
	/**
	 * Create dates fields
	 * @param data
	 * @param dates
	 * @return
	 */
	public ThingDTO createDates(ThingDTO data, List<AssemblyDTO> dates, boolean thisApplicant) {
		data.getDates().clear();
		if(dates != null) {
			for(AssemblyDTO date :dates) {
				FormFieldDTO<LocalDate> fld = FormFieldDTO.of(LocalDate.now());
				fld.setReadOnly(date.isReadOnly());
				fld.setDetail(getCalendarType(date));
				data.getDates().put(date.getPropertyName(), fld);
			}
		}
		return data;
	}
	
	/**
	 * 06122022 khomenska
	 * for the calendar type depending on the configuration settings
	 * @return "month", "year", "decade" or "century"
	 */
	private String getCalendarType(AssemblyDTO assDate) {
		String detail = "month";
		LocalDate minDate = LocalDate.now().plusMonths(assDate.getMin().intValue());
		LocalDate maxDate = LocalDate.now().plusMonths(assDate.getMax().intValue());
		int century=LocalDate.now().getYear()-2000;
		int yCount = Period.between(minDate, maxDate).getYears();
		int mCount = Period.between(minDate, maxDate).getMonths();
		
		if(yCount>century) {
			detail = "century";
		}else if(yCount >= 10) {
			detail = "decade";
		}else if(yCount == 1) {
			detail = "year";
		}else if(mCount>1 && mCount<=12) {
			detail = "year";
		}else if(mCount == 1) {
			detail = "month";
		}
		
		return detail;
	}
	
	/**
	 * Create numbers from descriptions
	 * @param data
	 * @param numbers
	 * @return
	 */
	public ThingDTO createNumbers(ThingDTO data, List<AssemblyDTO> numbers) {
		data.getNumbers().clear();
		if(numbers != null) {
			for(AssemblyDTO num :numbers) {
				FormFieldDTO<Long> fld = FormFieldDTO.of(0l);
				fld.setReadOnly(num.isReadOnly());
				data.getNumbers().put(num.getPropertyName(), fld);
			}
		}
		return data;
	}
	/**
	 * Create logicals from descriptions
	 * Enum YesNoNa is in use. The value by default is NA
	 * @param data
	 * @param logicals
	 * @return
	 */
	public ThingDTO createLogicals(ThingDTO data, List<AssemblyDTO> logicals) {
		data.getLogical().clear();
		if(logicals != null) {
			for(AssemblyDTO alog : logicals) {
				OptionDTO opt = enumToOptionDTO(YesNoNA.NA, YesNoNA.values());
				FormFieldDTO<OptionDTO> fld = FormFieldDTO.of(opt);
				fld.setReadOnly(alog.isReadOnly());
				data.getLogical().put(alog.getPropertyName(),fld);
			}
		}
		return data;
	}
	
	
	/**
	 * Load values for all literals from the database
	 * Add, if necessary new configured for url literals
	 * @param url to get list of new configured literals
	 * @param node parent node for literals
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, FormFieldDTO<String>> readAllLiterals(Map<String, FormFieldDTO<String>> literals, Concept node) throws ObjectNotFoundException {
		for(String key :literals.keySet()) {
			String val = literalServ.readValue(key,node);
			literals.get(key).setValue(val);
		}
		return literals;
	}
	/**
	 * Read all strings
	 * @param strings
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public Map<String, FormFieldDTO<String>> readAllStrings(Map<String, FormFieldDTO<String>> strings, Concept node) throws ObjectNotFoundException {
		for(String key :strings.keySet()) {
			String val = literalServ.readValue(key,node);
			strings.get(key).setValue(val);
		}
		return strings;
	}
	
	/**
	 * get dates from literals given
	 * @param dateFlds
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, FormFieldDTO<LocalDate>> readAllDates(Map<String, FormFieldDTO<LocalDate>> dateFlds,
			Concept node) throws ObjectNotFoundException {
		for(String key :dateFlds.keySet()) {
			LocalDate ld = readDate(node, key);
			dateFlds.get(key).setValue(ld);
		}
		return dateFlds;
	}
	/**
	 * Read a date from the node
	 * @param node
	 * @param key
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public LocalDate readDate(Concept node, String key) throws ObjectNotFoundException {
		String val = literalServ.readValue(key,node);
		LocalDate ld = LocalDate.now();
		try {
			ld = LocalDate.parse(val);
		} catch (Exception e) {
			ld = LocalDate.now();
		}
		return ld;
	}
	/**
	 * Read all numbers from literals
	 * @param numbers
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public Map<String, FormFieldDTO<Long>> readAllNumbers(Map<String, FormFieldDTO<Long>> numbers,
			Concept node) throws ObjectNotFoundException {
		for(String key : numbers.keySet()) {
			String valStr = literalServ.readValue(key,node);
			Long valNum = 0l;
			try {
				valNum=new Long(valStr);
			} catch (Exception e) {
				//nothing to do
			}
			numbers.get(key).setValue(valNum);
		}
		return numbers;
	}
	/**
	 * load logical variables from literals
	 * @param logical
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public Map<String, FormFieldDTO<OptionDTO>> readAllLogical(Map<String, FormFieldDTO<OptionDTO>> logical,
			Concept node) throws ObjectNotFoundException {
		for(String key : logical.keySet()) {
			String valStr = literalServ.readValue(key, node);
			Integer index=2;
			try {
				index = Integer.valueOf(valStr);
			} catch (NumberFormatException e) {
				//nothing to do
			}
			YesNoNA val = YesNoNA.NA;
			if(index>0) {
				if(valStr.length()>0) {
					val = YesNoNA.values()[index-1];
				}
			}
			OptionDTO opt = enumToOptionDTO(val, YesNoNA.values());
			FormFieldDTO< OptionDTO> fld = logical.get(key);
			fld.setValue(opt);
		}
		return logical;
	}
	/**
	 * Read logical literal directly to YesNoNa enum
	 * @param varName
	 * @param actConf
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public YesNoNA readLogicalLiteral(String varName, Concept actConf) throws ObjectNotFoundException {
		YesNoNA ret = YesNoNA.NA;
		Map<String, FormFieldDTO<OptionDTO>> logical = new HashMap<String, FormFieldDTO<OptionDTO>>();
		logical.put(varName, FormFieldDTO.of(new OptionDTO()));
		logical = readAllLogical(logical, actConf);
		ret = optionToEnum(YesNoNA.values(), logical.get(varName).getValue());
		return ret;
	}
	/**
	 * Make an assembly DTO from the database
	 * Suppose, logical and options are initialized
	 * @param assm
	 * @param varNode 
	 * @param varNode2 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DataVariableDTO assembly(Assembly assm, Concept node, Concept varNode, DataVariableDTO data) throws ObjectNotFoundException {
		//concept
		data.getDescription().setValue(literalServ.readPrefLabel(varNode));
		data.setNodeId(node.getID());
		data.getVarName().setValue(stringVal(varNode.getIdentifier()));
		//data.getVarName().setAssistant(AssistantEnum.VARIABLE); temporarily TO DO
		data.getVarNameExt().setValue(stringVal(varNode.getLabel()));
		//data.getVarNameExt().setAssistant(AssistantEnum.VARIABLE); temporarily TO DO
		data.setVarNodeId(varNode.getID());
		//Assembly
		data=assemblyToDataVariableDto(assm, data);
		return data;
	}
	
	/**
	 * Add data from the Assembly to the DTO
	 * @param assm
	 * @param data
	 */
	public DataVariableDTO assemblyToDataVariableDto(Assembly assm, DataVariableDTO data) {
		data=initializeLogical(data);
		data=initializeClazz(data);
		data.getPublicavailable().setValue(logicalOpt(assm.getPublicavailable(), data.getPublicavailable().getValue()));
		data.getHidefromapplicant().setValue(logicalOpt(assm.getHidefromapplicant(), data.getHidefromapplicant().getValue()));
		data.getClazz().setValue(optionCodeVal(assm.getClazz(), data.getClazz().getValue()));
		data.getCol().setValue(new Long(assm.getCol()));
		data.getDictUrl().setValue(stringVal(assm.getDictUrl()));
		data.getFileTypes().setValue(stringVal(assm.getFileTypes()));
		data.getMaxLen().setValue(new Long(assm.getMax()));
		data.getMinLen().setValue(new Long(assm.getMin()));
		data.getMult().setValue(logicalOpt(assm.getMult(),data.getMult().getValue()));
		data.getUnique().setValue(logicalOpt(assm.getUnique(), data.getUnique().getValue()));
		data.getPrefLabel().setValue(logicalOpt(assm.getPrefLabel(), data.getPrefLabel().getValue()));
		data.getOrd().setValue(new Long(assm.getOrd()));
		data.getReadOnly().setValue(logicalOpt(assm.getReadOnly(),data.getReadOnly().getValue()));
		data.getRequired().setValue(logicalOpt(assm.getRequired(),data.getRequired().getValue()));
		data.getRow().setValue(new Long(assm.getRow()));
		data.getUrl().setValue(stringVal(assm.getUrl()));
		data.getAuxUrl().setValue(assm.getAuxDataUrl());
		return data;
	}
	/**
	 * Initialize logical values
	 * 
	 * @param data
	 * @return
	 */
	public DataVariableDTO initializeLogical(DataVariableDTO data) {
		data.getPublicavailable().setValue(enumToOptionDTO(YesNoNA.NA, YesNoNA.values()));
		data.getHidefromapplicant().setValue(enumToOptionDTO(YesNoNA.NA, YesNoNA.values()));
		data.getMult().setValue(enumToOptionDTO(YesNoNA.NA, YesNoNA.values()));
		data.getUnique().setValue(enumToOptionDTO(YesNoNA.NA, YesNoNA.values()));
		data.getPrefLabel().setValue(enumToOptionDTO(YesNoNA.NA, YesNoNA.values()));
		data.getRequired().setValue(enumToOptionDTO(YesNoNA.NA, YesNoNA.values()));
		data.getReadOnly().setValue(enumToOptionDTO(YesNoNA.NA, YesNoNA.values()));
		return data;
	}
	/**
	 * Add all possible classes of a variable. Default is Literal.
	 * 
	 * @param data
	 * @return
	 */
	public DataVariableDTO initializeClazz(DataVariableDTO data) {
		List<String> possible = ThingDTO.thingClazzesNames();
		if (possible.size() > 0) {
			OptionDTO optVal = data.getClazz().getValue();
			optVal.getOptions().clear();
			optVal.setId(1);
			optVal.setCode(possible.get(0));
			int i = 1;
			for (String nm : possible) {
				OptionDTO opt = new OptionDTO();
				opt.setId(i);
				opt.setCode(nm);
				optVal.getOptions().add(opt);
				i++;
			}
			Collections.sort(optVal.getOptions(), new Comparator<OptionDTO>() {

				@Override
				public int compare(OptionDTO o1, OptionDTO o2) {
					return o1.getCode().compareTo(o2.getCode());
				}

			});
			data.getClazz().setValue(optVal);
		}
		return data;
	}
	/**
	 * Set OptionDTO create from YesNoNA enumeration to yes or No
	 * @param data 
	 * @param mult
	 * @return
	 */
	private OptionDTO logicalOpt(boolean val, OptionDTO data) {
		if(val) {
			data.setId(data.getOptions().get(0).getId());
			data.setCode(data.getOptions().get(0).getCode());
			data.setDescription(data.getOptions().get(0).getDescription());
		}else {
			data.setId(data.getOptions().get(1).getId());
			data.setCode(data.getOptions().get(1).getCode());
			data.setDescription(data.getOptions().get(1).getDescription());
		}
		return data;
	}
	/**
	 * Set value of OptionDTO by a code given
	 * @param code
	 * @param data 
	 * @return same as was if not found
	 */
	private OptionDTO optionCodeVal(String code, OptionDTO data) {
		for(OptionDTO opt: data.getOptions()) {
			if(opt.getCode().equals(code)) {
				data.setId(opt.getId());
				data.setCode(opt.getCode());
				data.setDescription(opt.getDescription());
				return data;
			}
		}
		return data;
	}
	private String stringVal(String str) {
		String ret="";
		if(str!=null) {
			ret=str;
		}
		return ret;
	}
	/**
	 * AssemblyDTO from assembly record
	 * @param assm
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public AssemblyDTO assemblyDto(Assembly assm) throws ObjectNotFoundException {
		AssemblyDTO ret = new AssemblyDTO();
		ret.setClazz(assm.getClazz());
		ret.setPublicAvailable(assm.getPublicavailable());
		ret.setHideFromApplicant(assm.getHidefromapplicant());
		ret.setDictUrl(stringVal(assm.getDictUrl()));
		ret.setFileTypes(stringVal(assm.getFileTypes()));
		ret.setMax(new BigDecimal(assm.getMax()));
		ret.setMin(new BigDecimal(assm.getMin()));
		ret.setMult(assm.getMult());
		ret.setUnique(assm.getUnique());
		ret.setPrefLabel(assm.getPrefLabel());
		ret.setPropertyName(assm.getPropertyName().getIdentifier());
		String description = literalServ.readDescription(assm.getPropertyName());
		ret.setDescription(description);
		ret.setReadOnly(assm.getReadOnly());
		ret.setRequired(assm.getRequired());
		ret.setTextArea(assm.getMult());
		ret.setUrl(assm.getUrl());
		ret.setAuxDataUrl(assm.getAuxDataUrl());
		return ret;
	}
}
