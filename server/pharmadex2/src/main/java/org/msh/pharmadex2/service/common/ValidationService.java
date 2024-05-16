package org.msh.pharmadex2.service.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.enums.YesNoNA;
import org.msh.pdex2.model.old.User;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Register;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.common.UserRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.AddressDTO;
import org.msh.pharmadex2.dto.AmendmentDTO;
import org.msh.pharmadex2.dto.AskForPass;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.AtcDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.FormatsDTO;
import org.msh.pharmadex2.dto.IntervalDTO;
import org.msh.pharmadex2.dto.LegacyDataDTO;
import org.msh.pharmadex2.dto.LinksDTO;
import org.msh.pharmadex2.dto.MessageDTO;
import org.msh.pharmadex2.dto.PersonDTO;
import org.msh.pharmadex2.dto.PublicOrgDTO;
import org.msh.pharmadex2.dto.QuestionDTO;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.RootNodeDTO;
import org.msh.pharmadex2.dto.SchedulerDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.UserElementDTO;
import org.msh.pharmadex2.dto.VerifItemDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FieldSuggest;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.r2.AccessControlService;
import org.msh.pharmadex2.service.r2.AmendmentService;
import org.msh.pharmadex2.service.r2.AssemblyService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to validate application specific DTOs
 * @author alexk
 *
 */
@Service
public class ValidationService {
	private static final Logger logger = LoggerFactory.getLogger(ValidationService.class);
	@Autowired
	Messages messages;
	@Autowired
	AssemblyService assemblyServ;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private AccessControlService accServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private AmendmentService amendmentServ;

	/**
	 * ^[a-z]{1,} - первый символ всегда буква
	 * [a-z0-9]* - далее буква или цыфра или _ или .
	 * ((\\.|\\_)[a-z0-9]{1,})* - после любого _ или . всегда буква или цыфра. Повторы тоже возможны
	 */
	private static final String regexVarName = "^[a-z]{1,}[a-z0-9]*" + "((\\.|\\_)[a-z0-9]{1,})*";
	public static Pattern REGEX = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");
	private static final Pattern pattern = Pattern.compile(regexVarName, Pattern.CASE_INSENSITIVE);
	public static final String regexEmail="^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_]"
			+ "[-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|"
			+ "[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$";
	private static final Pattern emailPattern = Pattern.compile(regexEmail, Pattern.CASE_INSENSITIVE);
	/**
	 * Validate a node
	 * @param data
	 * @param activityName 
	 * @param strict
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public DictNodeDTO node(DictNodeDTO data, String activityName, boolean strict) throws ObjectNotFoundException {
		data.clearErrors();
		FormFieldDTO<String> prefLabel =  data.getLiterals().get("prefLabel");
		if(prefLabel != null) {
			if(prefLabel.getValue().length()<2 || prefLabel.getValue().length()>Integer.MAX_VALUE) {
				suggest(prefLabel, 2,Integer.MAX_VALUE,strict);
				data.setValid(false);
			}
			if(prefLabel.getValue().contains(",")) {
				prefLabel.setSuggest(messages.get("badchar") + ": ','");
				prefLabel.setError(true);
				prefLabel.setStrict(strict);
				data.setValid(false);
			}
		}else {
			error((AllowValidation)data, messages.get("error_preflabel"),strict);
		}
		//aux
		List<Assembly> assms = assemblyServ.loadDataConfiguration(data.getUrl());
		List<AssemblyDTO> auxFlds = assemblyServ.auxLiterals(data.getUrl(), assms, new ArrayList<AssemblyDTO>());
		for(AssemblyDTO afld : auxFlds) {
			if(afld.isRequired()) {
				FormFieldDTO<String> fld=data.getLiterals().get(afld.getPropertyName());
				if(fld!=null) {
					if(fld.getValue().length()<2) {
						fld.setError(true);
						fld.setSuggest(messages.get("valueisempty"));
						fld.setStrict(strict);
					}
				}
			}	
		}
		data.propagateValidation();
		return data;
	}

	/**
	 * Suggest or error message
	 * @param prefLabel
	 * @param min
	 * @param max
	 * @param strict
	 */
	private FieldSuggest suggest(FieldSuggest prefLabel, int min, int max, boolean strict) {
		String format = "-";
		format = String.format(messages.get("atleastchars"),min);
		format= format+" "+String.format(messages.get("maxchars"),max);
		prefLabel.setSuggest(format);
		prefLabel.setStrict(strict);
		prefLabel.setError(true);
		return prefLabel;
	}
	/**
	 * Simply suggest or error message
	 * @param prefLabel
	 * @param min
	 * @param max
	 * @param strict
	 */
	private FieldSuggest suggest(FieldSuggest prefLabel, String message, boolean strict) {
		prefLabel.setSuggest(message);
		prefLabel.setStrict(strict);
		prefLabel.setError(true);
		return prefLabel;
	}
	/**
	 * Simple error message
	 * @param data
	 * @param message
	 * @param strict 
	 */
	private void error(AllowValidation data, String message, boolean strict) {
		data.setValid(false);
		data.setIdentifier((data.getIdentifier()+" " +message).trim());
	}
	/**
	 * For person the valid node is enough
	 * @param data
	 * @param strict
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public UserElementDTO person(UserElementDTO data, boolean strict) throws ObjectNotFoundException {
		data.setValid(true);
		data.setNode(node(data.getNode(),"",strict));
		String email = data.getUser_email().getValue();
		data.getUser_email().clearValidation();
		//check email
		try {
			InternetAddress ia = new InternetAddress(email,true);
			Optional<User> usero = userRepo.findByEmail(ia.getAddress());
			if(usero.isPresent()) {
				if(usero.get().getUserId()!=data.getId()) {
					data.getUser_email().setError(true);
					data.getUser_email().setStrict(true);
					data.getUser_email().setSuggest(messages.get("email_exists"));
				}
			}
		} catch (AddressException e) {
			data.getUser_email().setError(true);
			data.getUser_email().setStrict(true);
			data.getUser_email().setSuggest(messages.get("valid_email"));
		}
		//check roles, at least one
		if(data.getRoles().getPrevSelected().size()==0) {
			data.getRoles().setValid(false);
			data.getRoles().setIdentifier(messages.get("error_rolemandatory"));
		}
		//20230419 check roles, not Applicant
		for(Long id :data.getRoles().getPrevSelected()) {
			Concept item=closureServ.loadConceptById(id);
			if(item.getIdentifier().equalsIgnoreCase("APPLICANT")) {
				data.getRoles().setValid(false);
				data.getRoles().setIdentifier(messages.get("error_roleapplicant"));
				break;
			}
		}
		data.propagateValidation();
		return data;
	}
	/**
	 * eMAil should be in right format and unique
	 * @param eMail
	 * @return
	 */
	public boolean eMail(String email){
		try {
			new InternetAddress(email,true);
		} catch (AddressException e) {
			return false;
		}
		return true;
	}

	/**
	 * Validate an organization
	 * @param data
	 * @param strict
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public PublicOrgDTO organization(PublicOrgDTO data, boolean strict) throws ObjectNotFoundException {
		data.setNode(node(data.getNode(),"",strict));
		data.propagateValidation();
		return data;
	}
	/**
	 * Validate a dictionary
	 * @param dict
	 * @param description 
	 * @param strict 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private DictionaryDTO dictionary(DictionaryDTO dict, String description, boolean strict) throws ObjectNotFoundException {
		if(dict!= null) {
			dict.clearErrors();
			dict.setStrict(strict);
			if(dict.isRequired()) {
				// 22.03.2023 by issue 1561 khomka
				/*long sumIds=dict.getSelection().getValue().getId() * dict.getSelection().getValue().getOptions().size();		//value exists, list is empty
				for(Long id : dict.getPrevSelected()) {
					sumIds+=id;
				}
				if(sumIds==0 ) {
					dict.setValid(false);
					dict.setStrict(strict);
					dict.setIdentifier(messages.get("error_dictionaryempty") +". "+ description);
				}else {
					dict.clearErrors();
				}*/
				if(dict.getPrevSelected().size() > 0) {
					dict.clearErrors();
				}else{
					dict.setValid(false);
					dict.setStrict(strict);
					dict.setIdentifier(messages.get("error_dictionaryempty") +". "+ description);
				}
			}else {
				dict.clearErrors();
			}
		}
		return dict;
	}
	/**
	 * Validate a root node
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public RootNodeDTO rootNode(RootNodeDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		//validationCommon.validateDTO(data,true,true);
		if(data.getPrefLabel().getValue().length()<3 || data.getPrefLabel().getValue().length()>80) {
			suggest(data.getPrefLabel(),3,80,true);
		}
		data.propagateValidation();
		if(data.isValid()) {
			if(!data.getUrl().getValue().startsWith("dictionary.")) {
				data.getUrl().setError(true);
				data.getUrl().setStrict(true);
				data.getUrl().setSuggest(messages.get("error_dict_url"));
			}
		}
		data.propagateValidation();
		return data;
	}


	/**
	 * General thing validation in accjrdance with thing URL
	 * @param data
	 * 
	 * @param strict 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO thing(ThingDTO data,List<AssemblyDTO> dataConfigStored, boolean strict) throws ObjectNotFoundException {
		//prepare data
		data.clearErrors();
		List<Assembly> allAssms = new ArrayList<Assembly>();
		if(dataConfigStored.isEmpty()) {
			allAssms = assemblyServ.loadDataConfiguration(data.getUrl());
		}
		//validate all components
		List<AssemblyDTO> legs = assemblyServ.auxLegacyData(data.getUrl(), allAssms, dataConfigStored);
		for(AssemblyDTO l : legs) {
			if(l.isRequired()) {
				mandatoryLegacy(data, l,strict);
			}
		}

		List<AssemblyDTO> s = assemblyServ.auxStrings(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO str : s) {
			if(str.getFileTypes().length()>0) {
				data=checkPattern(data,str, data.getStrings(),  strict);
			}
			if(str.isRequired()) {
				mandatoryString(data, str, strict);
			}
		}
		List<AssemblyDTO> lits = assemblyServ.auxLiterals(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO lit : lits) {
			if(lit.getFileTypes().length()>0) {
				data=checkPattern(data,lit, data.getLiterals(),strict);
			}
			if(lit.isRequired()) {
				mandatoryLiteral(data, lit,strict);
			}
		}
		// check for prefLabel if this is the first page of the application
		if(data.isFirstPage() || data.isAuxfirstPage()) {
			lits.addAll(s);
			boolean flag=false;
			for(AssemblyDTO assm : lits) {
				if(assm.getPropertyName().equalsIgnoreCase("prefLabel")) {
					flag=true;
				}
			}
			if(!flag) {
				data.setValid(flag);
				data.setIdentifier(messages.get("configuration_error_preflabel"));
				return data;
			}
		}


		List<AssemblyDTO> dats = assemblyServ.auxDates(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO dat : dats) {
			if(dat.isRequired()) {
				mandatoryDate(data.getDates().get(dat.getPropertyName()),dat, strict);
			}
		}
		List<AssemblyDTO> nums = assemblyServ.auxNumbers(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO num : nums) {
			if(num.isRequired()) {
				mandatoryNumber(data.getNumbers().get(num.getPropertyName()),num,strict);
			}
		}
		List<AssemblyDTO> dicts = assemblyServ.auxDictionaries(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO dic : dicts) {
			if(dic.isRequired()) {
				DictionaryDTO dict = data.getDictionaries().get(dic.getPropertyName());
				dictionary(dict,dic.getDescription(),strict);
			}
		}
		List<AssemblyDTO> addrs = assemblyServ.auxAddresses(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO addr : addrs) {
			if(addr.isRequired()) {
				mandatoryAddress(data.getAddresses().get(addr.getPropertyName()),strict);
			}
		}
		List<AssemblyDTO> docs = assemblyServ.auxDocuments(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO doc: docs) {
			if(doc.isRequired()) {
				mandatoryDoc(data.getDocuments().get(doc.getPropertyName()),doc,strict);
			}
		}
		List<AssemblyDTO> schds = assemblyServ.auxSchedulers(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO sch : schds) {
			if(sch.isRequired()) {
				mandatoryScheduler(sch, data.getSchedulers().get(sch.getPropertyName()),strict);
			}
		}
		List<AssemblyDTO> regs = assemblyServ.auxRegisters(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO reg : regs) {
			if(reg.isRequired()) {
				mandatoryRegister(reg, data.getRegisters().get(reg.getPropertyName()),strict);
			}
		}
		List<AssemblyDTO> pers = assemblyServ.auxPersons(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO per : pers) {
			if(per.isRequired()) {
				mandatoryPersons(data, per.getPropertyName(),per,strict);
			}
		}

		List<AssemblyDTO> atc = assemblyServ.auxAtc(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO a : atc) {
			if(a.isRequired()) {
				mandatoryAtc(data,a.getPropertyName(),a,strict);
			}
		}
		List<AssemblyDTO> intervals = assemblyServ.auxIntervals(data, "intervals",dataConfigStored);
		for(AssemblyDTO interval : intervals) {
			mandatoryInterval(data, interval,strict);
		}
		List<AssemblyDTO> links=assemblyServ.auxLinks(data.getUrl(), allAssms,dataConfigStored);
		mandatoryLinks(data, links, strict);

		List<AssemblyDTO> droplists = assemblyServ.auxDropListData(data.getUrl(),allAssms,dataConfigStored);
		for(AssemblyDTO droplist :droplists) {
			if(droplist.isRequired()) {
				mandatoryDropList(data.getDroplist().get(droplist.getPropertyName()), strict, droplist);
			}
		}
		data.propagateValidation();
		return data;
	}
	/**
	 * Check mandatory filled droplists
	 * @param data
	 * @param strict
	 * @param droplist
	 */
	private void mandatoryDropList(FormFieldDTO<OptionDTO> data, boolean strict, AssemblyDTO droplist) {
		data.setError(false);
		if(data.getValue().getId()==0) {
			FormFieldDTO<String> str= new FormFieldDTO<String>();
			data.setError(true);
			data.setStrict(strict);
			data.setSuggest(messages.get("valuesmustbeselected"));

			str.setValue(droplist.getPropertyName());
			;
			help(str, droplist.getDescription());
		}

	}


	private void mandatoryLinks(ThingDTO data, List<AssemblyDTO> links, boolean strict) {
		for(AssemblyDTO aDto : links) {
			if(aDto.isRequired()) {
				LinksDTO dto = data.getLinks().get(aDto.getPropertyName());
				if(dto != null) {
					if(dto.getLinks().size()==0) {
						dto.setValid(false);
						dto.setIdentifier(messages.get("link_selection is empty"));
					}
				}
			}
		}

	}

	/**
	 * Check an interval of dates
	 * @param data
	 * @param interval
	 * @param strict
	 */
	private void mandatoryInterval(ThingDTO data, AssemblyDTO interval, boolean strict) {
		//take the interval
		IntervalDTO dto = data.getIntervals().get(interval.getPropertyName());
		if(dto != null) {
			LocalDate from=dto.getFrom().getValue();
			LocalDate to = dto.getTo().getValue();
			if(from==null) {
				from=LocalDate.now();
			}
			if(to==null) {
				to=LocalDate.now();
			}
			long monthsBetween = ChronoUnit.MONTHS.between(
					YearMonth.from(from), 
					YearMonth.from(to)
					);
			if(interval.isRequired()) {
				if(monthsBetween != interval.getMin().longValue()) {
					//the length of interval should be minOffset in months
					dto.setValid(false);
					dto.setStrict(strict);
					String mess = messages.get("periodshouldbe");
					dto.setIdentifier(mess +" "+ interval.getMin().longValue() +" "+messages.get("months") + ". "+interval.getDescription());
				}
				if(interval.getMax().longValue()>0 && dto.isValid()) {
					LocalDate maxDt = LocalDate.now().plusMonths(interval.getMax().longValue());
					if(!to.isBefore(maxDt)) {
						//the left border of the interval 
						dto.setValid(false);
						dto.setStrict(strict);
						String mess = messages.get("thelastdateshoulbeafter");
						dto.setIdentifier(mess +" "+ maxDt  + ". "+interval.getDescription());
					}
				}		
			}
		}
	}

	/**
	 * Legacy data should be selected
	 * @param data
	 * @param l
	 * @param strict
	 */
	private void mandatoryLegacy(ThingDTO data, AssemblyDTO l, boolean strict) {
		LegacyDataDTO dto = data.getLegacy().get(l.getPropertyName());
		FormFieldDTO<String> prefLabelDTO = data.getLiterals().get("prefLabel");
		if(prefLabelDTO==null) {
			prefLabelDTO = data.getStrings().get("prefLabel");
		}
		//if(dto!=null) {
		if(prefLabelDTO==null || prefLabelDTO.getValue().isEmpty()) {
			if(dto.getSelectedNode()==0) {
				dto.setValid(false);
				dto.setStrict(strict);
				dto.setIdentifier(messages.get("error_dictionaryempty")+". "+l.getDescription());
			}
		}
	}

	/**
	 * Check a sting or a literal against the pattern.
	 * The Java regex will be used
	 * @param data - DTO
	 * @param aDTO - the configuration. The pattern is in fileTypes
	 * @param fields - map of FommFields
	 * @param strict red or grey
	 * @return
	 */
	private ThingDTO checkPattern(ThingDTO data, AssemblyDTO aDTO, Map<String, FormFieldDTO<String>> fields,
			boolean strict) {
		if(aDTO.getFileTypes().length()>0) {
			FormFieldDTO<String> fld=fields.get(aDTO.getPropertyName());
			if(fld!=null) {
				String value= fld.getValue();
				if(value.length()>0) {
					Pattern pattern;
					try {
						pattern = Pattern.compile(aDTO.getFileTypes(), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(value);
						if(!matcher.find()) {
							fld.setError(true);
							fld.setSuggest(messages.get("invalidformat"));
							fld.setStrict(strict);
							help(fld, aDTO.getDescription());
						}
					} catch (Exception e) {
						fld.setError(true);
						fld.setSuggest(messages.get("invalidpattern"));
						fld.setStrict(strict);
						help(fld, aDTO.getDescription());
					}
				}
			}
		}
		return data;
	}
	/**
	 * RegExp pattern match
	 * @param value
	 * @param regex
	 * @return
	 */
	public boolean patternMatch(String value, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(value);
		return matcher.find();
	}

	/**
	 * RegExp pattern matches
	 * @return true if full value  \sequencematches this matcher's pattern
	 * если вся строка value подходит под шаблон
	 */
	public boolean patternMatchFull(String value) {
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

	/**
	 * This ATC component is mandatory
	 * @param data
	 * @param propertyName
	 * @param strict 
	 * @param a 
	 */
	private void mandatoryAtc(ThingDTO data, String propertyName, AssemblyDTO a, boolean strict) {
		AtcDTO dto = data.getAtc().get(propertyName);
		if(dto.getSelectedtable().getRows().size()==0) {
			dto.setValid(false);
			dto.setStrict(strict);
			dto.setIdentifier(messages.get("atc_noatc")+" "+a.getDescription());
		}else {
			dto.setValid(true);
			dto.setIdentifier("");
		}
	}


	/**
	 * Register record should...
	 * @param ar
	 * @param strict 
	 * @param registerDTO
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryRegister(AssemblyDTO ar, RegisterDTO dto, boolean strict) throws ObjectNotFoundException {
		dto.clearErrors();
		if(!dto.isReadOnly()) {
			dto=dto.ensureDates(dto);
			LocalDate regDate = dto.getRegistration_date().getValue();
			LocalDate expDate = dto.getExpiry_date().getValue();
			LocalDate createdAt = LocalDate.now();
			if(dto.getNodeID()>0) {
				Concept node = closureServ.loadConceptById(dto.getNodeID());
				Register reg = boilerServ.registerByConcept(node);
				createdAt=boilerServ.localDateFromDate(reg.getCreatedAt());
			}
			String regNum = dto.getReg_number().getValue();
			//register date should fit an interval ?????? I would remove it!!! IK 01022024
			LocalDate maxDate = createdAt;
			LocalDate minDate = createdAt.minusMonths(1);
			String errorMess = messages.get("valuerangeerror")+" " + minDate +", " + maxDate;
			if(ar.getDescription().length()>0) {
				errorMess=errorMess + " " + ar.getDescription();
			}
			if(regDate.isBefore(minDate) || regDate.isAfter(maxDate)) {
				dto.getRegistration_date().invalidate(errorMess);
				dto.setStrict(strict);
			}
			//register number should be not empty, not duplicated for url given
			if(!dto.isReadOnly()){
				if(regNum.length()>=1 && regNum.length()<=255) {
					List<Register> rr = boilerServ.registerByUrlAndNumber(regNum, dto.getUrl());
					if(rr.size()>1) {
						dto.getReg_number().invalidate(messages.get("registrationexists") + " " + ar.getDescription());
						dto.setStrict(strict);
					}else {
						if(rr.size()==1) {
							if(rr.get(0).getConcept().getID()!=dto.getNodeID()) {
								dto.getReg_number().invalidate(messages.get("registrationexists") + " "+ar.getDescription());
							}
						}
					}
				}else {
					//suggest(dto.getReg_number(), 3, 255, strict);
					dto.getReg_number().invalidate(messages.get("pressButtonAssignNumber") + " "+ar.getDescription());
					dto.setStrict(strict);
				}
			}
			//expiration date should fit an interval if defined
			if(ar.isMult()) {
				createdAt = LocalDate.now();
				maxDate = createdAt.plusMonths(ar.getMax().intValue());
				minDate = createdAt.plusMonths(1);
				//errorMess = messages.get("valuerangeerror")+" " + regDate.plusMonths(2) +", " + maxDate;
				errorMess = messages.get("valuerangeerror")+" " + createdAt +", " + maxDate;
				if(expDate.isBefore(minDate) || expDate.isAfter(maxDate)) {
					dto.getExpiry_date().invalidate(errorMess+" "+ar.getDescription());
					dto.setStrict(strict);
				}
			}
		}
		dto.propagateValidation();
	}

	/**
	 * Schedule data should fit into interval
	 * @param strict 
	 * @param sch 
	 * @param data
	 * @param propertyName
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryScheduler(AssemblyDTO ad, SchedulerDTO schDTO, boolean strict) throws ObjectNotFoundException {
		schDTO.clearErrors();
		LocalDate createdAt = schDTO.getCreatedAt();
		if(createdAt != null) {
			LocalDate minDate=createdAt.plusMonths(ad.getMin().intValue());
			LocalDate maxDate=createdAt.plusMonths(ad.getMax().intValue()+1);
			String errorMess = messages.get("valuerangeerror")+" " + minDate +", " + maxDate;
			LocalDate sched = schDTO.getSchedule().getValue();
			if(sched.isBefore(minDate) || sched.isAfter(maxDate)) {
				schDTO.getSchedule().invalidate(errorMess);
			}
			schDTO.propagateValidation();
		}else {
			throw new ObjectNotFoundException("mandatoryScheduler. CreatedAt is unknown",logger);
		}

	}


	/**
	 * Quantity of persons. Only on the strict validation 
	 * @param data
	 * @param propertyName
	 * @param strict 
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ThingDTO mandatoryPersons(ThingDTO data, String propertyName, AssemblyDTO apers, boolean strict) throws ObjectNotFoundException {
		if(data.isStrict()) {
			PersonDTO dto = data.getPersons().get(propertyName);
			if(dto != null) {
				dto.clearErrors();
				if(dto.getAmendedNodeId()==0) {
					//regular person
					if(dto.getTable().getRows().size()==0) {
						dto.setValid(false);
						dto.setStrict(strict);
						dto.setIdentifier(messages.get("mandatorypersons")+" "+apers.getDescription());
					}
				}else {
					//amendment
					int existing = dto.getRtable().getRows().size();
					int added=dto.getTable().getRows().size();
					int removed=0;
					for(TableRow row : dto.getRtable().getRows()) {
						if(row.getSelected()) {
							removed++;
						}
					}
					if(existing-removed+added<=0) {
						dto.setValid(false);
						dto.setStrict(strict);
						dto.setIdentifier(messages.get("mandatorypersons")+" "+apers.getDescription());
					}
				}
			}
		}
		return data;
	}


	/**
	 * All documents are required
	 * @param filesDTO
	 * @param strict 
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryDoc(FileDTO filesDTO, AssemblyDTO doc, boolean strict) throws ObjectNotFoundException {
		if(filesDTO==null) {
			return;
		}
		filesDTO.clearErrors();
		int sizefiles=filesDTO.getTable().getRows().size();
		filesDTO.setStrict(strict);
		//checking for non-compliance between the requested files and the downloaded ones
		if(sizefiles>filesDTO.getLinked().size()) {
			filesDTO.setValid(false); 
			filesDTO.setIdentifier(messages.get("upload_file") +" " +doc.getDescription());
		}else if(filesDTO.getLinked()==null || filesDTO.getLinked().size()==0) {
			filesDTO.setValid(false);
			filesDTO.setIdentifier(messages.get("upload_file") +" " +doc.getDescription());
		}else {
			//check for availability dictNodeId, nodeId of a file
			Set<Long> keys = filesDTO.getLinked().keySet();
			long sumlo=0l;
			for(Long key : keys) {
				//sumlo+=filesDTO.getLinked().get(key);
				sumlo=filesDTO.getLinked().get(key);
				if(sumlo==0) {
					filesDTO.setValid(false);
					filesDTO.setIdentifier(messages.get("upload_file")+" "+ doc.getDescription());
				}
			}
			/*
			 * if(sumlo==0) { filesDTO.setValid(false);
			 * filesDTO.setIdentifier(messages.get("upload_file")+" "+
			 * doc.getDescription()); }
			 */
		}
		filesDTO.propagateValidation();
		return;
	}

	/**
	 * Address should be defined
	 * @param data
	 * TODO something more advanced!!!
	 * @param strict 
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryAddress(AddressDTO data, boolean strict) throws ObjectNotFoundException {
		if(data!=null) {
			data.clearErrors();
			data.setStrict(strict);
			data.getDictionary().clearErrors();
			dictionary(data.getDictionary(),"",strict);
			if(!data.getDictionary().isValid()) {
				data.setValid(false);
				data.setStrict(strict);
				data.setIdentifier(messages.get("addressisempty"));
				return;
			}
			if(data.getMarker().isEmpty()) {
				data.setValid(false);
				data.setStrict(strict);
				data.setIdentifier(messages.get("selectgislocation"));
				return;
			}
		}
	}

	/**
	 * Check mandatory date
	 * @param formFieldDTO
	 * @param dat - rules to check date
	 * @param strict 
	 */
	private void mandatoryDate(FormFieldDTO<LocalDate> dateFld, AssemblyDTO dat, boolean strict) {
		if(dateFld != null) {
			LocalDate minDate = LocalDate.now().plusMonths(dat.getMin().intValue());
			LocalDate maxDate = LocalDate.now().plusMonths(dat.getMax().intValue());
			String errorMess = messages.get("valuerangeerror")+" " + minDate+", " + maxDate;
			if(dat.getDescription().length()>0){
				errorMess=errorMess +" "+dat.getDescription();
			}
			if(dateFld.getValue() != null) {
				LocalDate val = dateFld.getValue();
				if(val.isBefore(minDate) || val.isAfter(maxDate)) {
					dateFld.invalidate(errorMess);
					dateFld.setStrict(strict);
				}
			}else {
				dateFld.invalidate(errorMess);
				dateFld.setStrict(strict);
			}
		}
	}
	/**
	 * Check mandatory number
	 * @param numFld
	 * @param num
	 * @param strict 
	 */
	private void mandatoryNumber(FormFieldDTO<Long> numFld, AssemblyDTO num, boolean strict) {
		String errorMess = messages.get("valuerangeerror") +" "+ num.getMin()+", " + num.getMax();
		if(num.getDescription().length()>0) {
			errorMess=errorMess+" "+num.getDescription();
		}
		if(numFld != null) {
			numFld.clearValidation();
			if(numFld.getValue()!=null) {
				Long val = numFld.getValue();
				if(val.compareTo(num.getMin().longValue())<0 || val.compareTo(num.getMax().longValue())>0){
					numFld.invalidate(errorMess);
					numFld.setStrict(strict);
				}
			}
		}
	}

	/**
	 * Check mandatory literal
	 * @param thingDTO
	 * @param lit
	 * TODO more precisely, using lit
	 * @param strict 
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryLiteral(ThingDTO thingDTO, AssemblyDTO lit, boolean strict) throws ObjectNotFoundException {
		FormFieldDTO<String> data=thingDTO.getLiterals().get(lit.getPropertyName());
		if(data != null) {
			if(data.getValue().length()<lit.getMin().intValue() || data.getValue().length()>lit.getMax().intValue()) {
				suggest(data, lit.getMin().intValue(), lit.getMax().intValue(), strict);
				help(data, lit.getDescription());
			}else {
				//check unique
				if(lit.isUnique()) {
					if(!uniqueLiteral(thingDTO, lit.getPropertyName(), data.getValue())) {
						data.setError(true);
						data.setStrict(strict);
						data.setSuggest(messages.get("valueshouldbeunique"));
						help(data, lit.getDescription());
					}
				}
			}
		}
	}

	/**
	 * Check mandatory string
	 * @param thingDTO
	 * @param lit
	 * TODO more precisely, using lit
	 * @param strict 
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryString(ThingDTO thingDTO, AssemblyDTO lit, boolean strict) throws ObjectNotFoundException {
		FormFieldDTO<String> data=thingDTO.getStrings().get(lit.getPropertyName());
		if(data != null) {
			if(data.getValue().length()<lit.getMin().intValue() || data.getValue().length()>lit.getMax().intValue()) {
				suggest(data, lit.getMin().intValue(), lit.getMax().intValue(), strict);
				help(data, lit.getDescription());
			}else {
				//check unique
				if(lit.isUnique()) {
					if(!uniqueLiteral(thingDTO, lit.getPropertyName(), data.getValue())) {
						data.setError(true);
						data.setStrict(strict);
						data.setSuggest(messages.get("valuehouldbeunique"));
						help(data, lit.getDescription());
					}
				}
			}
		}
	}
	/**
	 * Add help message to the error
	 * @param data
	 * @param description
	 */
	private void help(FormFieldDTO<String> data, String description) {
		String suggest = data.getSuggest();
		if(description.length()>0) {
			data.setSuggest(suggest+" "+description);
		}		
	}

	/**
	 * Check is this literal on the current language unique for this URL, and variable name
	 * @param thingDTO
	 * @param propertyName
	 * @param value
	 * @throws ObjectNotFoundException 
	 */
	private boolean uniqueLiteral(ThingDTO thingDTO, String propertyName, String value) throws ObjectNotFoundException {
		//literal concept ID
		long litId=0;
		if(thingDTO.getNodeId()>0) {
			Concept node = closureServ.loadConceptById(thingDTO.getNodeId());
			Concept literal = literalServ.literalConcept(propertyName, node);
			if(literal.getID()>0) {
				litId=literal.getID();
			}
		}
		//all literals with a value given
		if(thingDTO.getUrl().length()>0) {
			Concept root = closureServ.loadRoot(thingDTO.getUrl());
			jdbcRepo.literal_values(root.getID(), propertyName);
			Headers headers = new Headers();
			headers.getHeaders().add(TableHeader.instanceOf("varvalue", TableHeader.COLUMN_STRING));
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from literal_values", "", "varvalue='"+value+"'", headers);
			for(TableRow row : rows) {
				if(row.getDbID()!=litId) {
					if(isInActiveObject(root.getIdentifier(), row.getDbID())) {
						return false;
					}
				}
			}
		}
		return true;
	}
	/**
	 * Is this literal in active object
	 * @param url
	 * @param literalId
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private boolean isInActiveObject(String url, long literalId) throws ObjectNotFoundException {
		Concept literal = closureServ.loadConceptById(literalId);
		List<Concept> parents = closureServ.loadParents(literal);
		if(parents.size()==5) {	//url.owner.object._literals_.varname.varvalue
			if(parents.get(0).getIdentifier().equalsIgnoreCase(url)) {
				if(parents.get(2).getActive()) {
					return isActiveParentObject(parents.get(2));
				}
			}
		}
		return false;
	}
	/**
	 * Suppose the node is active, but it is a tyhing in another node. Let's check is teh another node active
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private boolean isActiveParentObject(Concept node) throws ObjectNotFoundException {
		ThingThing tt = boilerServ.thingThing(node,false);
		if(tt == null) {
			return true; // I'am parent object
		}else {
			Thing thing = boilerServ.thingByThingThing(tt, true);
			return thing.getConcept().getActive();
		}
	}

	/**
	 * All questions should be answered
	 * @param data
	 * @throws ObjectNotFoundException 
	 */
	public CheckListDTO checklist(CheckListDTO data) throws ObjectNotFoundException {
		data.setValid(true);
		for(QuestionDTO q :data.getQuestions()) {
			if(!q.isHead()) {
				if(q.getAnswer()==0) {
					q.setValid(false);
				}else {
					q.setValid(true);
				}
			}
		}
		data.propagateValidation();
		return data;
	}
	/**
	 * Validate workflow configuration
	 * Result to CheckListDTO
	 * @param activities
	 * @param configRoot 
	 * @param data 
	 * @throws ObjectNotFoundException 
	 */
	/* 30.05.2023 khomenska move to validWorkFlowConfig
	@Transactional
	public AllowValidation workflowConfig(List<Concept> activities, Concept configRoot, AllowValidation data) throws ObjectNotFoundException {
		data.clearErrors();
		boolean hasSchedulers = false;
		//each activity definition should has prefLabel,activityurl, checklisturl
		for(Concept aco : activities) {
			String prefLabel=literalServ.readPrefLabel(aco);
			String activityUrl = literalServ.readValue("activityurl", aco);
			String checklisturl = literalServ.readValue("checklisturl",aco);
			String dataurl = literalServ.readValue("dataurl",aco);
			if(prefLabel.length()==0
					|| activityUrl.length()==0
					|| checklisturl.length()==0){
				data.setIdentifier(messages.get("badconfiguration")+"/"+messages.get("error_preflabel")+"/"+activityUrl+"/"+checklisturl);
				data.setValid(false);
				return data;
			}
			Thing thing = boilerServ.thingByNode(aco);
			if(thing.getID()>0) {
				if(thing.getDictionaries().size()==0) {
					data.setIdentifier(messages.get("badconfiguration")+" ROLES ZERO config root is " + configRoot.getID());
					data.setValid(false);
					return data;
				}
				//Schedulers 09.11.2022 khomenska
				hasSchedulers = true;			//TODO
				/*List<Assembly> assemblies = assemblyServ.loadDataConfiguration(dataurl);
				List<AssemblyDTO> schedulers = assemblyServ.auxSchedulers(dataurl, assemblies);
				if(schedulers != null && schedulers.size() > 0) {
					hasSchedulers = true;
				}*/
	/* 30.05.2023}else {
				data.setIdentifier(messages.get("badconfiguration")+"ThingID is ZERO config root is " + configRoot.getID());
				data.setValid(false);
				return data;
			}
		}
		if(!hasSchedulers) {//Schedulers 09.11.2022 khomenska
			String w = messages.get("badconfiguration")+" " + messages.get("notFoundSchedulers");
			data.setIdentifier(w);
			logger.info(w);
			data.setValid(false);
			return data;
		}
		return data;
	}*/


	/**
	 * Validate the data collection definition
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DataCollectionDTO dataCollection(DataCollectionDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		if(data.getUrl().getValue().length()<3 || data.getUrl().getValue().length()>120) {
			data.setIdentifier( messages.get("valuerangeerror") +" "+ 3+", " + 120);
		}
		if(data.getDescription().getValue().length()<10 || data.getDescription().getValue().length()>500) {
			data.setIdentifier( messages.get("valuerangeerror") +" "+ 10+", " + 500);
		}
		return data;
	}
	/**
	 * Validate a definition of variable
	 * @param data
	 * @param isImport TODO
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DataVariableDTO variable(DataVariableDTO data, boolean strict, boolean isImport) throws ObjectNotFoundException {
		data.clearErrors();
		data=variableClazz(data,strict);
		if(!isImport) {
			data=variableName(data, strict);
			data=variableExtName(data,strict);
			data=variableScreen(data,strict);
		}
		data=variableFileTypes(data);
		// special validation
		data=variableIfPrefLabel(data);
		data=variableMinMaxWhenRequired(data,strict);
		data= variableUrl(data, strict);
		data=variableDictUrl(data,strict);
		data=variableAuxUrl(data,strict);
		data=variableDocuments(data);
		data.propagateValidation();
		return data;
	}
	
	private DataVariableDTO variableIfPrefLabel(DataVariableDTO data) {
		String clazz = data.getClazz().getValue().getCode();
		if(clazz.equalsIgnoreCase("literals") || clazz.equalsIgnoreCase("strings")) {
			if(data.getVarName().getValue().equalsIgnoreCase("prefLabel")) {
				FormFieldDTO<OptionDTO> ropt = data.getRequired();
				YesNoNA required=dtoServ.optionToEnum(YesNoNA.values(), ropt.getValue());
				if(required.equals(YesNoNA.NA) || required.equals(YesNoNA.NO)) {
					data.getRequired().setValue(dtoServ.enumToOptionDTO(YesNoNA.YES, YesNoNA.values()));				
				}
				long min = boilerServ.nullIsZero(data.getMinLen().getValue());
				long max = boilerServ.nullIsZero(data.getMaxLen().getValue());
				if(min==0) {
					min=3l;
				}
				if(max==0) {
					max=255l;
				}
				if(min>max) {
					max=255l;
				}
				data.getMaxLen().setValue(max);
				data.getMinLen().setValue(min);
			}
		}
		return data;
	}
	
	/**
	 * field fileTypes should be checked and all non-printable characters should be removed:
	 * <ul>
	 * <li> literals(strings) - empty or valid Java Regular Expression pattern
	 * <li> documents - empty or value of attribute "accept" of the input file control (https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept)
	 * </ul>
	 * For now we validate only for literals
	 * @param data
	 * @return
	 */
	private DataVariableDTO variableFileTypes(DataVariableDTO data) {
		String clazz = data.getClazz().getValue().getCode();
		if(clazz.equalsIgnoreCase("literals") || clazz.equalsIgnoreCase("strings")) {
			if(data.getFileTypes()!=null) {
				String regex = boilerServ.replaceNonPrintCh(data.getFileTypes().getValue());
				regex=regex.replace(" ", "");
				data.getFileTypes().setValue(regex);
				if(regex.length()>0) {
					try {
						Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
						data.getFileTypes().clearValidation();
					} catch (Exception e) {
						data.getFileTypes().setError(true);
						data.getFileTypes().setSuggest(messages.get("invalidpattern"));
						data.setIdentifier(messages.get("invalidpattern"));
					}
				}
			}
		}
		return data;
	}

	/**
	 * Is auxUrl mandatory
	 * @param data
	 * @return
	 */
	private DataVariableDTO variableAuxUrl(DataVariableDTO data, boolean strict) {
		List<String> auxUrlReq = Arrays.asList ( 
				//"persons", 
				"schedulers", 
				"links"
				);
		if(data.getAuxUrl().getValue().length()==0) {
			if(auxUrlReq.contains(data.getClazz().getValue().getCode().toLowerCase())) {
				data.getAuxUrl().setSuggest(messages.get("requiredvalue"));
				data.getAuxUrl().setError(true);
				data.getAuxUrl().setStrict(strict);
				if(strict) {
					data.setIdentifier(messages.get("error"));
				}else {
					data.setIdentifier(messages.get("warning"));
				}
				data.setStrict(strict);
				data.setValid(false);
			}
		}
		return data;
	}

	/**
	 * Is dictUrl mandatory?
	 * @param data
	 * @return
	 */
	private DataVariableDTO variableDictUrl(DataVariableDTO data, boolean strict) {
		List<String> dictUrlReq = Arrays.asList (
				"documents", 
				"atc", 
				"legacy"
				);
		if(data.getDictUrl().getValue().length()==0) {
			if(dictUrlReq.contains(data.getClazz().getValue().getCode().toLowerCase())) {
				data.getDictUrl().setSuggest(messages.get("requiredvalue"));
				data.getDictUrl().setError(true);
				data.getDictUrl().setStrict(strict);
				if(strict) {
					data.setIdentifier(messages.get("error"));
				}else {
					data.setIdentifier(messages.get("warning"));
				}
				data.setStrict(strict);
				data.setValid(false);
			}
		}
		return data;
	}

	/**
	 * Is url mandatory?
	 * @param data
	 * @return
	 */
	private DataVariableDTO variableUrl(DataVariableDTO data, boolean strict) {
		List<String> urlReq = Arrays.asList (
				"dictionaries", 
				"addresses", 
				"documents", 
				"resources", 
				"things", 
				"persons", 
				"schedulers", 
				"registers",
				"atc", 
				"legacy", 
				"links", 
				"droplist"
				);
		if(data.getUrl().getValue().length()==0) {
			if(urlReq.contains(data.getClazz().getValue().getCode().toLowerCase())) {
				data.getUrl().setSuggest(messages.get("requiredvalue"));
				data.getUrl().setError(true);
				data.getUrl().setStrict(strict);
				if(strict) {
					data.setIdentifier(messages.get("error"));
				}else {
					data.setIdentifier(messages.get("warning"));
				}
				data.setStrict(strict);
				data.setValid(false);
			}else {
				data.getUrl().clearValidation();
			}
		}
		return data;
	}



	/**
	 * Warning message should be if size or width/hegth are zero
	 * Sometimes it is possible to restrict images by size in pixels 
	 * @param data
	 * @return
	 */
	private DataVariableDTO variableDocuments(DataVariableDTO data) {
		if(data.isValid()) {
			if(data.getClazz().getValue().getCode().equalsIgnoreCase("documents")) {
				if(data.getMinLen().getValue()==0 & data.getMaxLen().getValue()==0) {
					data.setValid(false);
					data.setStrict(false);
					String mess = messages.get("sizeisnotdefined");
					data.setIdentifier(mess);
				}
			}
		}
		return data;
	}


	/**
	 * Shouldn't be variables on the same place
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private DataVariableDTO variableScreen(DataVariableDTO data, boolean strict) throws ObjectNotFoundException {
		if(data.getVarNameExt().getValue().length()==0) {//it is not real variables
			if(data.getNodeId()>0) {
				long row=boilerServ.nullIsZero(data.getRow().getValue());
				long col=boilerServ.nullIsZero(data.getCol().getValue());
				long ord=boilerServ.nullIsZero(data.getOrd().getValue());
				//ik 28102022 ScreenParam for not things <90
				Long s1 = 0l;
				int obj1 = Long.compare(row, s1);  
				int obj2 = Long.compare(col, s1); 
				int obj3 = Long.compare(ord, s1); 
				if (!data.getClazz().getValue().getCode().equalsIgnoreCase("things")) {
					if(obj1<0) {row=0l; data.getRow().setValue(s1);}
					if(obj2<0) {col=0l; data.getCol().setValue(s1);}
					if(obj3<0) {ord=0l; data.getOrd().setValue(s1);}
				}
				s1 = 89l;
				long s2=1l;
				obj1 = Long.compare(row, s1);  
				obj2 = Long.compare(col, s2); 
				obj3 = Long.compare(ord, s1); 
				if (!data.getClazz().getValue().getCode().equalsIgnoreCase("things")) {
					if(obj1>0) {row=s1; data.getRow().setValue(s1);}
					if(obj2>0) {col=s2; data.getCol().setValue(s2);}
					if(obj3>0) {ord=s1; data.getOrd().setValue(s1);}	
				}else {
					if(obj3>0) {ord=s1; data.getOrd().setValue(s1);}
				}
				Concept root = closureServ.loadConceptById(data.getNodeId());
				List<Concept> variables = literalServ.loadOnlyChilds(root);
				//int numOrd =(int) ord;
				int numRow=0;
				ArrayList<Assembly> list=new ArrayList<Assembly>();
				for(Concept var : variables) { //F2
					String ext=var.getLabel();
					if(ext==null) {
						ext="";
					}
					if( var.getID()!=data.getVarNodeId() && var.getActive() && ext.length() ==0) {//1 
						Assembly assm = boilerServ.assemblyByVariable(var,true);
						list.add(assm);
					}//1
				}//f2	
				long maxRow=0l; 
				for(Assembly asRow:list) {
					if(!asRow.getClazz().equalsIgnoreCase("things")) {
						obj1 = Long.compare(asRow.getRow(), maxRow); 
						if(obj1>0) {maxRow=asRow.getRow();}
					}
				}
				//is the position consolidated
				boolean flag=false;
				for(Assembly asOrd:list) {
					if(asOrd.getRow()==row && asOrd.getCol()==col && asOrd.getOrd()==ord) {
						flag=true;
					}
				}
				//
				if(flag) {
					List<Assembly> newRow= new ArrayList<Assembly>();
					for(Assembly asOrd:list) {
						if(asOrd.getRow()==row && asOrd.getCol()==col && asOrd.getOrd()>=ord) {
							if(asOrd.getOrd()+1>=90) {
								newRow.add(asOrd);
							}else {
								asOrd.setOrd(asOrd.getOrd()+1);
								asOrd = boilerServ.assemblySave(asOrd);
							}
						}
					}
					if(!newRow.isEmpty()) {
						numRow= (int) maxRow;
						if(numRow>=89) {
							data.setIdentifier(messages.get("errorscreenposition"));
							data.setValid(false);
							data.setStrict(strict);
							return data;
						}else {
							numRow+=1;	
							for(Assembly asOrd:newRow) {
								asOrd.setOrd(89-asOrd.getOrd());
								asOrd.setRow(numRow);
								asOrd = boilerServ.assemblySave(asOrd);
							}
						}

					}
				}
				//
			}else {
				throw new ObjectNotFoundException("variableScreen. Data Collection node ID not found",logger);
			}
		}
		return data;
	}


	/**
	 * Class of variable should be defined
	 * If variable is mandatory, validation rules should be defined
	 * @param data
	 * @return
	 */
	private DataVariableDTO variableClazz(DataVariableDTO data, boolean strict) {
		FormFieldDTO<OptionDTO> clazz = data.getClazz();
		clazz.clearValidation();
		if(clazz.getValue().getId()==0) {
			suggest(clazz,3,100,strict);
		}
		return data;
	}
	/**
	 * For some variables like literals 
	 * @param data
	 * @return
	 */
	public DataVariableDTO variableMinMaxWhenRequired(DataVariableDTO data, boolean strict) {
		List<String> minMaxReq = Arrays.asList(
				"strings", 
				"literals", 
				"dates", 
				"numbers", 
				"schedulers",
				"registers", 
				"intervals"
				);
		String clazz=data.getClazz().getValue().getCode();
		if(minMaxReq.contains(clazz.toLowerCase())) {
			FormFieldDTO<OptionDTO> ropt = data.getRequired();
			YesNoNA required=dtoServ.optionToEnum(YesNoNA.values(), ropt.getValue());
			if(required.equals(YesNoNA.YES)) {
				long min = boilerServ.nullIsZero(data.getMinLen().getValue());
				long max = boilerServ.nullIsZero(data.getMaxLen().getValue());
				if(!validateMinMax(min, max, clazz)) {
					suggest(data.getMinLen(),messages.get("valueReq"),strict);
					suggest(data.getMaxLen(),messages.get("valueReq"),strict);
					data.setValid(false);
					if(strict) {
						data.setIdentifier(messages.get("error"));
					}else {
						data.setIdentifier(messages.get("warning"));
					}
				}else {
					data.getMinLen().clearValidation();
					data.getMaxLen().clearValidation();
				}
			}
		}
		return data;
	}
	/**
	 * The validatio nresult depends on the clazz
	 * @param min
	 * @param max
	 * @param clazz
	 * @return
	 */
	private boolean validateMinMax(long min, long max, String clazz) {
		if(clazz.equalsIgnoreCase("intervals")) {
			if(min>0 && max>0) {	//zero interval is not allowed
				return true;
			}else {
				return false;
			}
		}
		if(clazz.equalsIgnoreCase("schedulers")) {
			if(min>=0 && min<=max) {	//schedule in a past is not allowed
				return true;
			}else {
				return false;
			}
		}
		return min<=max;
	}

	/**
	 * Variable name should be defined
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 *  
	 * 18.11.2022 khomenska
	 * add verification with Pattern
	 */
	public DataVariableDTO variableName(DataVariableDTO data, boolean strict) throws ObjectNotFoundException {
		FormFieldDTO<String> vn= data.getVarName();
		vn.clearValidation();
		variableNameField(data, strict, vn);
		return data;
	}
	/**
	 * Validate FormFieldDTO with the variable name
	 * @param data
	 * @param strict
	 * @param vn
	 * @return
	 */
	public AllowValidation variableNameField(AllowValidation data, boolean strict, FormFieldDTO<String> vn) {
		if(vn.getValue().length()<3 || vn.getValue().length()>100) {
			suggest(vn,3,100,strict);
			return data;
		}
		if(!patternMatchFull(vn.getValue().trim())) {
			data.setValid(false);
			//data.setValid(data.getVarNodeId()!=0 && data.isValid());
			// relaxed temporarily
			if(strict && !data.isValid()) {
				data.setIdentifier(messages.get("error")+ "! "+vn.getValue());
				vn.setError(true);
				vn.setStrict(true);
				vn.setSuggest(messages.get("errorVariableName"));
			}else {
				data.setIdentifier(messages.get("warning")+ "! "+vn.getValue());
			}
		}
		return data;
	}

	/**
	 * VariableExt name should be defined
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 *  
	 * 18.11.2022 khomenska
	 * add verification with Pattern
	 */
	public DataVariableDTO variableExtName(DataVariableDTO data, boolean strict) throws ObjectNotFoundException {
		FormFieldDTO<String> vn= data.getVarNameExt();
		if(vn.getValue().length() > 0){
			if(!patternMatchFull(vn.getValue())) {
				// не подходит по патерну - выдаем сообщение, но разрешаем сохранять ПОКА
				data.setValid(false);
				data.setStrict(data.getNodeId()>0);
				if(strict) {
					data.setIdentifier(messages.get("error"));
				}else {
					data.setIdentifier(messages.get("warning"));
				}
			}
		}

		return data;
	}
	/**
	 * URL is mandatory! dictUrl is mandatory
	 * @param data
	 * @param strict
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ResourceDTO resourceDefinition(ResourceDTO data, boolean strict) throws ObjectNotFoundException {
		data.clearErrors();
		if(data.getUrl().getValue() == null) {
			data.setUrl(FormFieldDTO.of(""));
		}
		if(data.getUrl().getValue().length()<3 || data.getUrl().getValue().length()>120) {
			suggest(data.getUrl(), 3,120,strict);
		}
		if(data.getConfigUrl().getValue().length()<3 || data.getConfigUrl().getValue().length()>120) {
			suggest(data.getConfigUrl(), 3,120,strict);
		}
		data.propagateValidation();
		return data;
	}
	/**
	 * Validate a resource using general thing validation 
	 * @param data
	 * @param strict for future ext
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO resource(ThingDTO data, boolean strict) throws ObjectNotFoundException {
		data=thing(data,new ArrayList<AssemblyDTO>(),strict);
		return data;
	}

	@Transactional
	public MessageDTO message(MessageDTO data) {
		data.setValid(true);
		if(!(data.getRes_key().getValue() != null && data.getRes_key().getValue().length() > 0)) {
			data.setValid(false);
			data.getRes_key().setError(true);
			data.getRes_key().setStrict(true);
			data.setIdentifier(messages.get("res_key") + ": " + messages.get("valueisempty"));
		}
		Iterator<String> it = data.getValues().keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String value = data.getValues().get(key).getValue();
			if(!(value != null && value.length() > 0)) {
				data.setValid(false);
				data.getValues().get(key).setError(true);
				data.getValues().get(key).setStrict(true);
				data.setIdentifier(key + ": " + messages.get("valueisempty"));
				break;
			}
		}

		return data;
	}
	/**
	 * Cancel is allowed when:
	 * <ul>
	 * <li> only for foreground or background opened activities
	 * <li> background, only if at least yet another exists, regardless opened or not
	 * <li> foreground, only if exists at least one opened foreground activity with the same config 
	 * </ul>
	 * @param curHis 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO actionCancel(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		if(curHis.getGo()==null && curHis.getActConfig()!=null) {																			//current is opened
			for(History h : allHis) {
				if(h.getActConfig()!=null && h.getActConfig().getID()!=curHis.getActConfig().getID()) {		//configuration is not the same
					return data;
				}
			}
		}
		data.setIdentifier(messages.get("error_cancelactivity"));
		data.setValid(false);
		return data;
	}
	/**
	 * Is this activity background
	 * @param actConf
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isActivityBackground(Concept actConf) throws ObjectNotFoundException {
		if(actConf!=null) {
			YesNoNA result = dtoServ.readLogicalLiteral("background", actConf);
			return result.equals(YesNoNA.YES);
		}else {
			return false;
		}
	}

	/**
	 * Is this activity attention
	 * @param actConf
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isActivityAttention(Concept actConf) throws ObjectNotFoundException {
		if(actConf!=null) {
			YesNoNA result = dtoServ.readLogicalLiteral("attention", actConf);
			return result.equals(YesNoNA.YES);
		}else {
			return false;
		}
	}
	/**
	 * Is this activity foreground?
	 * @param actConfig
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private boolean isActivityForeground(Concept actConf) throws ObjectNotFoundException {
		if(actConf!=null) {
			YesNoNA result = dtoServ.readLogicalLiteral("background", actConf);
			return !result.equals(YesNoNA.YES);
		}else {
			return true;		//it is monitoring
		}
	}
	/**
	 * Supervisor can create new activity from any background and foreground
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO actionNew(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		//if(!data.isReassign()) {
		if(isActivityBackground(curHis.getActConfig()) || isActivityForeground(curHis.getActConfig())) {
			return data;
		}
		//}
		data.setIdentifier(messages.get("error_finishmonia"));
		data.setValid(false);
		return data;
	}
	/**
	 * Supervisor & NMRA can reassign executor:
	 * <ul>
	 * <li> only for opened foreground and background activities
	 * </ul>
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO actionReassign(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		if(isActivityBackground(curHis.getActConfig()) || isActivityForeground(curHis.getActConfig())) {
			if(curHis.getGo()==null) {
				return data;
			}
		}
		data.setIdentifier(messages.get("error_sendsent"));
		data.setValid(false);
		return data;
	}
	/**
	 * NMRA executor can submit only:
	 * <ul>
	 * <li> Only own opened foreground activity activity
	 * </ul>
	 * @param curHis
	 * @param user 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO submitNext(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		Concept exec = closureServ.getParent(curHis.getActivity());
		if(accServ.sameEmail(exec.getIdentifier(), user.getEmail())) {	//the current activity belongs to the current user
			if(isActivityForeground(curHis.getActConfig())) {	//the current activity is foreground
				if(curHis.getGo()==null) {	//the current activity is not finished yet
					return data; //next is allowed
				}
			}
		}
		data.setIdentifier(messages.get("error_sendsent")); 
		data.setValid(false);
		return data;
	}


	/**
	 * NMRA executor can route activity to other executor only:
	 * <ul>
	 * <li> own foreground activity
	 * <li> and if no the same opened activity assigned to other NMRA executor
	 * </ul>
	 * @param curHis
	 * @param user 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO submitRoute(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		if(!data.isReject()) {
			Concept exec = closureServ.getParent(curHis.getActivity());
			if(accServ.sameEmail(exec.getIdentifier(), user.getEmail())) {
				if(isActivityForeground(curHis.getActConfig()) || (data.isReassign())) {
					if(curHis.getGo()==null) {
						return data;
					}
				}
			}
		}
		data.setIdentifier(messages.get("error_activityroute"));
		data.setValid(false);
		return data;
	}
	/**
	 * is it approve finalization activity that is allowed for the current user?
	 * @param curHis
	 * @param user
	 * @param data
	 * @param nextActs
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO submitApprove(History curHis, UserDetailsDTO user, ActivitySubmitDTO data, List<Concept> nextActs) throws ObjectNotFoundException {
		data.clearErrors();
		data.setColorAlert("info");
		if(!data.isReassign()) {
			Concept exec = closureServ.getParent(curHis.getActivity());
			if(accServ.sameEmail(exec.getIdentifier(), user.getEmail())) {	//allowed only for the owner
				if(isActivityForeground(curHis.getActConfig())) {					//only foreground
					if(curHis.getGo()==null) {													//not submitted yet
						if(isActivitySubmitApprove(curHis)) {							//approval
							return data;
						}
					}
				}
			}
		}
		data.setColorAlert("danger");
		data.setIdentifier(messages.get("error_activityfinal"));
		data.setValid(false);
		return data;
	}
	/**
	 * Under approve we've collected accept, deregistration, company and amend finalization actions
	 * @param curHis
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public boolean isActivitySubmitApprove(History curHis) throws ObjectNotFoundException {
		//return isActivityFinalAction(curHis, SystemService.FINAL_ACCEPT) || isActivityFinalAction(curHis, SystemService.FINAL_COMPANY);
		return isActivityFinalAction(curHis, SystemService.FINAL_DEREGISTRATION)
				|| isActivityFinalAction(curHis, SystemService.FINAL_ACCEPT) 
				|| isActivityFinalAction(curHis, SystemService.FINAL_COMPANY)
				|| isActivityFinalAction(curHis, SystemService.FINAL_AMEND);
	}

	/**
	 * Is it reject action. Is reject action is allowed
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitReject(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		if(!data.isReject()) {
			data.setValid(false);
			data.setIdentifier(messages.get("error_reject"));
		}
		return data;
	}

	/**
	 * Deregistration is allowed if this application is de-registration and application data thing has valid link to 
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitDeregistration(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		if(!data.isReassign()) {
			if(isActivityFinalAction(curHis, SystemService.FINAL_DEREGISTRATION)
					|| isActivityFinalAction(curHis, SystemService.FINAL_ACCEPT) 
					|| isActivityFinalAction(curHis, SystemService.FINAL_COMPANY)) {
				return data;
			}else {
				data.setIdentifier(messages.get("error_workflowinappropriate"));
				data.setValid(false);
			}
			/*}else {
					data.setIdentifier(messages.get("error_workflowinappropriate"));
					data.setValid(false);
				}*/
		}else {
			data.setIdentifier(messages.get("error_accessdenied"));
			data.setValid(false);
		}
		/*}else {
			data.setIdentifier(messages.get("error_workflowinappropriate"));
			data.setValid(false);
		}*/
		return data;
	}
	/**
	 * Current activity data should contain a register
	 * This register should contain de-registration date and number and date to remove the object from the database (expiry)  
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO submitDeregistrationData(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		if(curHis.getActivityData() != null) {
			Thing thing = boilerServ.thingByNode(curHis.getActivityData());
			if(thing.getRegisters().size()==0) {
				data.setIdentifier(messages.get("error_registershouldbedefined"));
				data.setValid(false);
			}
		}else {
			data.setIdentifier(messages.get("error_registershouldbedefined"));
			data.setValid(false);
		}
		return data;
	}

	/**
	 * Is this workflow to implement an amendment
	 * @param curHis
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isAmendmentWorkflow(History curHis) throws ObjectNotFoundException {
		if(curHis.getApplicationData()!=null) {
			Thing thing = boilerServ.thingByNode(curHis.getApplicationData());
			return thing.getAmendments().size()>0;
		}
		return false;
	}

	/**
	 * Is this activity configured with the finalization action given
	 * @param curHis history record for the activity
	 * @param finalAction finalization action given
	 * @return true, if the activity is configured with the finalization action from the second parameter
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isActivityFinalAction(History curHis, String finalAction) throws ObjectNotFoundException {
		Concept actConf = curHis.getActConfig();
		if(actConf!=null) {
			if(!isActivityBackground(actConf)) {
				Thing th = new Thing();
				th=boilerServ.thingByNode(actConf,th);
				if(th.getID()>0) {
					for(ThingDict td :th.getDictionaries()) {
						if(td.getVarname().equalsIgnoreCase(AssemblyService.ACTIVITY_CONFIG_FINALIZE)) {
							return td.getConcept().getIdentifier().equalsIgnoreCase(finalAction);
						}
					}
				}
			}
		}
		return false;
	}
	/**
	 * NMRA executor want to submit an additional activity.
	 * It is possible any time
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitAddActivity(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		if(!data.isReject()) {
			//if(!data.isReassign()) {
			return data;
			//}
		}
		error(data, messages.get("error_submitaddactivity"), true);
		return data;
	}
	/**
	 * Data can be submitted if next activity and executor ids are defined
	 * It should not be opened same activity to the same executor !
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO submitNextData(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		long nextActConfId = data.nextActivity();
		List<Long> executors = data.executors();
		if(nextActConfId>0 && executors.size()>0) {
			data = submitNextOrRouteData(curHis, data, executors);
			return data;
		}
		error(data, messages.get("error_nextactivitydata"), true);
		return data;
	}
	/**
	 * Common check for submit next and route
	 * 
	 * @param curHis
	 * @param data
	 * @param executors
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitNextOrRouteData(History curHis, ActivitySubmitDTO data, List<Long> executors) throws ObjectNotFoundException {
		data.clearErrors();
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		for(History h : allHis) {
			if(h.getGo()==null) {																			//opened
				if(h.getActConfig()!= null && curHis.getActConfig()!=null && h.getActConfig().getID()==curHis.getActConfig().getID()) {			//same activity configuration
					Concept userConc = closureServ.getParent(h.getActivity());		//take a user
					if(executors.contains(userConc.getID())) {									//selected user's contains it
						error(data, messages.get("error_nextactivitydata"), true);
						return data;
					}
				}
			}
		}
		return data;
	}

	/**
	 * Is there an action to decline
	 * @param curHis
	 * @param data
	 * @param executors
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public boolean submitDecline(History curHis, ActivitySubmitDTO data, boolean isHost) throws ObjectNotFoundException {
		if(isActivityFinalAction(curHis,SystemService.FINAL_DECLINE)) {
			if(isHost) {
				List<Long> hostsRunning = hostsRunning(curHis);
				if(!hostsRunning.isEmpty() && hostsRunning.size()>1) {
					return true;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Common check for submit decline
	 * 
	 * @param curHis
	 * @param data
	 * @param executors
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitDeclineData(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		//if(nextActConfId>0 && executors.size()>0) {
		Concept exec = closureServ.getParent(curHis.getActivity());
		if(accServ.sameEmail(exec.getIdentifier(), user.getEmail())) {
			if(isActivityFinalAction(curHis,SystemService.FINAL_DECLINE)) {
				return data;
			}else {
				data.setIdentifier(messages.get("error_workflowinappropriate"));
				data.setValid(false);
			}
		}else {
			data.setIdentifier(messages.get("error_accessdenied"));
			data.setValid(false);
		}
		return data;
		//}
		//error(data, messages.get("error_nextactivitydata"), true);
		//return data;
	}

	/**
	 * Re-assign to the next user
	 * The same as submit to next
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitReAssign(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		List<Long> executors = data.executors();
		if(executors.size()>0) {
			data = submitNextOrRouteData(curHis, data, executors);
			data = submitNotesIsMandatory(curHis, data);
			return data;
		}
		error(data, messages.get("error_nextactivitydata"), true);
		return data;
	}

	/**
	 * Is it RevokePermit action. Is RevokePermit action is allowed
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitRevokePermit(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data = submitNotesIsMandatory(curHis, data);

		return data;
	}


	/**
	 * For some submits notes field is mandatory
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ActivitySubmitDTO submitNotesIsMandatory(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		String notes = data.getNotes().getValue();
		if(notes.length()<=3 || notes.length()>1000) {
			suggest(data.getNotes(), 3, 1000, true);
			data.propagateValidation();
		}
		return data;
	}
	/**
	 * Add new activity data check
	 * It should not be opened same activity to the same executor !
	 * Notes are mandatory
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitAddActivityData(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		long addAct = data.nextActivity();
		List<Long> executors=data.executors();
		if(addAct>0 && (executors.size()>0 ||data.getApplicantEmail().length()>0)) {
			if(data.getApplicantEmail().length()==0) {
				data=submitNextOrRouteData(curHis, data, executors);
			}
			data=submitNotesIsMandatory(curHis, data);
			return data;
		}
		error(data,messages.get("error_nextactivitydata"),true);
		return data;
	}
	/**
	 * Notes are mandatory for cancellation
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO actionCancelData(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data=submitNotesIsMandatory(curHis, data);
		return data;
	}


	/**
	 * Any extra requirements to data yet
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitRejectData(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data=submitNotesIsMandatory(curHis, data);
		return data;
	}
	/**
	 * Should be selected anyway
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public AmendmentDTO amendment(AmendmentDTO dto) throws ObjectNotFoundException {
		dto.clearErrors();
		long id = 0;
		for(TableRow row :dto.getTable().getRows()) {
			if(row.getSelected()) {
				id=row.getDbID();
			}
		}
		if(id==0) {
			dto.setValid(false);
			dto.setIdentifier(messages.get("error_dictionaryempty"));
		}
		return dto;
	}

	/**
	 * Is his application from HOST dictionary
	 * @param applDict
	 * @return
	 */
	@Transactional
	public boolean isHostApplication(Concept applDict) {
		if(applDict!=null) {
			Concept parent = closureServ.getParent(applDict);
			if(parent != null) {
				return parent.getIdentifier().toUpperCase().contains("HOST");
			}else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Validate file uploaded
	 * Read the configuration and check min/max size for non-images or width/height for images 
	 * zero/zero for non images means any acceptable size
	 * @param data
	 * @param fileBytes
	 * @param storedAssms 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public FileDTO file(FileDTO data, byte[] fileBytes, List<AssemblyDTO> storedAssms) throws ObjectNotFoundException {
		data.clearErrors();
		List<Assembly> allAssms = assemblyServ.loadDataConfiguration(data.getThingUrl());
		List<AssemblyDTO> assms = assemblyServ.auxDocuments(data.getThingUrl(),allAssms,storedAssms);
		String media = data.getMediaType().toUpperCase();
		String description="";
		String dimen="";
		for(AssemblyDTO asm : assms) {
			if(asm.getPropertyName().equalsIgnoreCase(data.getVarName())) {
				if(asm.getMax().intValue()>0) {
					if(media.contains("IMAGE") && (media.contains("JPEG") || media.contains("PNG"))) {
						dimen=asm.getMin().intValue()+"X"+asm.getMax().intValue();
						data=image(asm, data,fileBytes);
					}else {
						/*dimen=asm.getMin().intValue()+"-"+asm.getMax().intValue();
						long fileSizeK = data.getFileSize()/1024;
						if(fileSizeK==0) {
							fileSizeK=1;
						}
						if(fileSizeK<asm.getMin().longValue() || fileSizeK>asm.getMax().longValue()) {
							data.setValid(false);
						}*/
					}
					description=asm.getDescription();
					break;
				}
			}
		}
		if(!data.isValid()) {
			data.setStrict(true);
			String message = messages.get("Error.uploadFile")+ " " + description;
			data.setIdentifier(message.trim()+" ("+dimen+")");
		}
		return data;
	}
	/**
	 * Validate an image file - real format, width, height
	 * @param asm 
	 * @param data
	 * @param picture
	 * @return
	 */
	private FileDTO image(AssemblyDTO asm, FileDTO data, byte[] picture) {
		ByteArrayInputStream istream =new ByteArrayInputStream(picture);
		try {
			BufferedImage img = ImageIO.read(istream);
			int height= img.getHeight();
			int width = img.getWidth();
			if(width != asm.getMin().intValue() || height != asm.getMax().intValue()) {
				data.setValid(false);
			}
		} catch (IOException e) {
			data.setValid(false);
		}
		return data;
	}
	/**Outdated function, slows down work. DO NOT USE. 21.12.2023 IK
	 * Validate all additional pages if ones
	 * @param assemblies
	 * @param storedConfig 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	
	@Transactional
	public ThingDTO validateThingsIncluded(List<Assembly> assemblies, List<AssemblyDTO> storedConfig, ThingDTO data) throws ObjectNotFoundException {
		if(storedConfig.isEmpty()) {
		for(Assembly  assm : assemblies ) {
			if(assm.getClazz().equalsIgnoreCase("things")) {
				AssemblyDTO dto = dtoServ.assemblyDto(assm);
				data=validateThingIncluded(dto,data);
			}
		}
		}else {
			for(AssemblyDTO dto : storedConfig) {
				data=validateThingIncluded(dto,data);
			}
		}
		return data;
	} */
	/** Outdated function, slows down work. DO NOT USE. 21.12.2023 IK
	 * Validate an additional page definition
	 * @param dto
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 
	@Transactional
	private ThingDTO validateThingIncluded(AssemblyDTO dto, ThingDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		//rule 1. The URL should be defined
		if(data.getUrl() == null) {
			data.addError(messages.get("emptyurl"));
		}else {
			if(data.getUrl().length()==0) {
				data.addError(messages.get("emptyurl"));
			}else {
				//rule 2. If the thing contains class "persons" the aux URL should be defined 
				List<Assembly> clazzes =  assemblyServ.loadDataConfiguration(dto.getUrl());
				for(Assembly clazz : clazzes) {
					if(clazz.getClazz().equalsIgnoreCase("persons")) {
						if(dto.getAuxDataUrl()==null) {
							data.addError(messages.get("emptyauxurl"));
						}else {
							if(dto.getAuxDataUrl().length()==0) {
								data.addError(messages.get("emptyauxurl"));
							}
						}
					}
				}
			}
		}
		return data;
	}
	*/
	/**
	 * Validate email address using regex
	 * @param data
	 * @return
	 */
	public AskForPass validEmail(AskForPass data) {
		Matcher matcher = emailPattern.matcher(data.getEmail());
		if(!matcher.find()) {
			data.addError(messages.get("valid_email"));
		}
		return data;
	}

	/**
	 * verification workflow configuration before create new application
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public VerifItemDTO verifAddApplication(VerifItemDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		data.clearErrors();
		data.setValid(true);
		// valid configuration
		Concept dictNode = closureServ.loadConceptById(data.getApplDictNodeId());
		String url = literalServ.readValue("applicationurl", dictNode);
		data = (VerifItemDTO)validWorkFlowConfig(data, url);
		return data;
	}

	/**
	 * verification workflow configuration before create new Deregistration
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public VerifItemDTO verifAddDeristrationModificationInspection(VerifItemDTO data, History curHis) throws ObjectNotFoundException {
		data.clearErrors();
		data.setValid(true);
		Concept dictNode = closureServ.loadConceptById(data.getApplDictNodeId());
		data.setCanAdd(dictNode.getActive());
		if(dictNode.getActive()) {
			String url = literalServ.readValue("applicationurl", dictNode);
			data = (VerifItemDTO)validWorkFlowConfig(data, url);
			if(data.isValid()) {
				Concept applData=closureServ.loadConceptById(data.getApplID());
				data = (VerifItemDTO) hasActivities(SystemService.DICTIONARY_GUEST_AMENDMENTS, applData, data, curHis);
				if(data.isValid()) {
					data = (VerifItemDTO) hasActivities(SystemService.DICTIONARY_GUEST_DEREGISTRATION,applData,data, curHis);
				}
				if(data.isValid()) {
					data = (VerifItemDTO) hasActivities(SystemService.DICTIONARY_SHUTDOWN_APPLICATIONS,applData,data,curHis);
				}
			}
		}else {
			data.setValid(false);
			data.setIdentifier("");
		}
		return data;
	}
	/**
	 * Has permit data opened activities at least in a one process in a dictionary given
	 * @param dictURL
	 * @param applData
	 * @param curHis 
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private AllowValidation hasActivities(String dictURL, Concept applData, AllowValidation data, History curHis) throws ObjectNotFoundException {
		Concept permitData = amendmentServ.initialApplicationData(applData);
		jdbcRepo.has_activities(dictURL, null, permitData.getID());
		Headers headers = new Headers();
		String where="";
		if(curHis!=null) {
			where="historyID not in ('"+curHis.getID()+"')";
		}
		headers.getHeaders().add(TableHeader.instanceOf("applDictID", TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from has_activities", "",
				where, headers);
		if(!rows.isEmpty()) {
			//decode the application
			Object dictItemIdO = rows.get(0).getCellByKey("applDictID").getOriginalValue();
			String mess = dictURL;		//roughly
			if(dictItemIdO instanceof Long) {
				Long dictItemID= (Long) dictItemIdO;
				Concept dictItem = closureServ.loadConceptById(dictItemID);
				mess=literalServ.readPrefLabel(dictItem);	//fine
			}
			String prefix = messages.get("singletonError");
			data.addError(prefix + " - " + mess);
		}
		return data;
	}
	/**
	 * Validate workflow configuration
	 * @param data
	 * @param urlConfig
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public AllowValidation validWorkFlowConfig(AllowValidation data, String urlConfig) throws ObjectNotFoundException {		
		Concept rootConfig = closureServ.loadRoot("configuration." + urlConfig.toLowerCase());
		List<Concept> nextActs = boilerServ.loadActivities(rootConfig);
		data = validActivities(nextActs, data, rootConfig);
		return data;
	}
	/**
	 * Any given activity is valid if configuration is valid
	 * In the activities at least one has finalization action
	 * @param nextActs
	 * @param data
	 * @param configRoot
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private AllowValidation validActivities(List<Concept> nextActs, AllowValidation data, Concept configRoot) throws ObjectNotFoundException {		
		boolean hasFinal = false;
		if(nextActs != null && nextActs.size() > 0) {
			for(Concept c:nextActs) {
				data = validThingInWorkflowConfig(c, configRoot, data);
				if(!data.isValid()) {
					return data;
				}
				if(!hasFinal) {
					hasFinal = hasFinals(c);
				}
			}
			if(!hasFinal) {
				data.setIdentifier(messages.get("badconfiguration")+" Configuration has not Finalization activity. Config root is " + configRoot.getIdentifier());
				data.setValid(false);
				return data;
			}
		}else {
			data.setIdentifier(messages.get("badconfiguration")+" Empty path in configuration. Config root is " + configRoot.getIdentifier());
			data.setValid(false);
			return data;
		}
		return data;
	}


	@Transactional
	private AllowValidation validThingInWorkflowConfig(Concept activity, Concept dictRoot, AllowValidation data) throws ObjectNotFoundException {
		data.clearErrors();
		data.setValid(true);
		//each activity definition should has prefLabel,activityurl, checklisturl
		if(activity != null && activity.getID() > 0){
			String prefLabel=literalServ.readPrefLabel(activity);
			String activityUrl = literalServ.readValue("activityurl", activity);
			String checklisturl = literalServ.readValue("checklisturl", activity);
			//String dataurl = literalServ.readValue("dataurl", activity);
			if(prefLabel.length()==0
					|| activityUrl.length()==0
					|| checklisturl.length()==0){
				data.setIdentifier(messages.get("badconfiguration")+"/"+messages.get("error_preflabel")+"/"+activityUrl+"/"+checklisturl);
				data.setValid(false);
				return data;
			}
			Thing thing = boilerServ.thingByNode(activity);
			if(thing.getID() > 0) {
				if(thing.getDictionaries().size()==0) {
					data.setIdentifier(messages.get("badconfiguration")+" ROLES ZERO config root is " + dictRoot.getIdentifier());
					data.setValid(false);
					return data;
				}
			}else {
				data.setIdentifier(messages.get("badconfiguration")+"ThingID is ZERO config root is " + dictRoot.getIdentifier());
				data.setValid(false);
				return data;
			}
		}
		return data;
	}

	private boolean hasFinals(Concept c) throws ObjectNotFoundException {
		boolean hasFinalization = false;
		Thing thing = boilerServ.thingByNode(c);
		for(ThingDict dict : thing.getDictionaries()) {
			if(dict.getUrl().equalsIgnoreCase(SystemService.DICTIONARY_SYSTEM_FINALIZATION)){
				if(!dict.getConcept().getIdentifier().equalsIgnoreCase(SystemService.FINAL_NO)){
					return true;
				}
			}
		}
		return hasFinalization;
	}

	/**
	 * Does not submitted guest application exist for the permit
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean IsNotSubmittedGuest(Long applDataID) throws ObjectNotFoundException {
		Concept applData=closureServ.loadConceptById(applDataID);
		Concept permData=amendmentServ.initialApplicationData(applData);
		jdbcRepo.has_activities(null, null, permData.getID());
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("historyID", TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from has_activities",
				"", "", headers);
		for(TableRow row : rows) {
			Object hIdO = row.getCellByKey("historyID").getOriginalValue();
			if(hIdO instanceof Long) {
				//is it not submitted history?
				Long hID = (Long) hIdO;
				History h = boilerServ.historyById(hID);
				if(h.getActivityData()!=null) {
					//check
					return h.getApplicationData().getID()==h.getActivityData().getID() 
							&& h.getGo()==null
							&& h.getActConfig()==null;
				}
			}
		}
		return false;
	}


	public Concept findDictionaryByApplUrl(String applUrl) throws ObjectNotFoundException {
		Concept dictItem = null;
		List<String> lifeUrls = new ArrayList<String>();
		lifeUrls.add(SystemService.DICTIONARY_GUEST_APPLICATIONS);
		lifeUrls.add(SystemService.DICTIONARY_HOST_APPLICATIONS);
		lifeUrls.add(SystemService.DICTIONARY_GUEST_AMENDMENTS);
		lifeUrls.add(SystemService.DICTIONARY_GUEST_DEREGISTRATION);
		lifeUrls.add(SystemService.DICTIONARY_SHUTDOWN_APPLICATIONS);
		lifeUrls.add(SystemService.DICTIONARY_GUEST_INSPECTIONS);

		for(String url:lifeUrls) {
			Concept dict = closureServ.loadRoot(url);
			List<Concept> items = literalServ.loadOnlyChilds(dict);
			for (Concept item : items) {
				if (item.getActive()) {
					String applurlLit = literalServ.readValue(LiteralService.APPLICATION_URL, item);
					if(applurlLit != null && applurlLit.contains(applUrl)) {
						dictItem = item;
						return dictItem;
					}
				}
			}
		}

		return dictItem;
	}
	/**
	 * Run host process is possible when:
	 * <ul>
	 * <li> at least one host activity is opened or there are no opened activities at all (lost or wrong)
	 * <li> at least one host process is defined and is not already running
	 * </ul>
	 * Additionally, this procedure builds runHosts table
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO runHosts(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		Concept applData= amendmentServ.initialApplicationData(curHis.getApplicationData());
		data = loadHostTable(applData, data);
		if(!data.getRunHosts().getRows().isEmpty()) {
			//extract hosts running
			List<Long> hostsRunning = hostsRunning(curHis);
			if(!hostsRunning.isEmpty()) {
				// do not allow the already running process
				List<TableRow> validRows = new ArrayList<TableRow>();
				for(TableRow row :data.getRunHosts().getRows()) {
					if(!hostsRunning.contains(row.getDbID())) {
						validRows.add(row);
					}
				}
				data.getRunHosts().getRows().clear();
				data.getRunHosts().getRows().addAll(validRows);
				if(validRows.isEmpty()) {
					data.addError(messages.get("processcantrun3"));
				}
			}else {
				data.addError(messages.get("processcantrun2"));
			}
		}else {
			data.addError(messages.get("processcantrun1"));
		}
		return data;
	}
	/**
	 * Get host processes running for permit data of the history given
	 * @param curHis
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<Long> hostsRunning(History curHis) throws ObjectNotFoundException {
		List<Long> hostsRunning = new ArrayList<Long>();
		Concept permitData=amendmentServ.initialApplicationData(curHis.getApplicationData());
		List<History> allHis = boilerServ.historyAll(permitData);	//we need only on host 
		for(History h : allHis) {
			if(h.getGo()==null) {
				if(isHostApplication(h.getApplDict())) {
					hostsRunning.add(h.getApplDict().getID());
				}
			}
		}
		return hostsRunning;
	}
	/**
	 * Load a table for host application that are possible to run
	 * <ul>
	 * <li> ID is appl dictionary id
	 * <li>name is an application name>
	 * </ul>
	 * @param applData
	 * @param data 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ActivitySubmitDTO loadHostTable(Concept applData, ActivitySubmitDTO data) throws ObjectNotFoundException {
		String dataurl = boilerServ.url(applData);
		jdbcRepo.host_applications_dataurl(dataurl);
		TableQtb table = data.getRunHosts();
		if(table.getHeaders().getHeaders().isEmpty()) {
			table.getHeaders().getHeaders().add(TableHeader.instanceOf("prefLabel",
					messages.get("prefLabel"),
					true, true, false, TableHeader.COLUMN_STRING, 0));
			table.getHeaders().getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
			table.getHeaders().setPageSize(Integer.MAX_VALUE);	// to ensure all records will be here
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from host_applications_dataurl",
				"", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		return data;
	}
	/**
	 * Is it possible to run host processes required
	 * at least one process should be selected and no one of such process should be running and notes should be filled
	 * @param curHis 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO submitRunHost(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		if(data.getRunHosts().getRows().isEmpty()) {
			data.addError(messages.get("processcantrun1"));
		}
		List<Long> selected = new ArrayList<Long>();
		for(TableRow row : data.getRunHosts().getRows()) {
			if(row.getSelected()) {
				selected.add(row.getDbID());
			}
		}
		if(selected.isEmpty()) {
			data.addError(messages.get("submitrunhosts1"));
		}
		data = submitNotesIsMandatory(curHis, data);
		if(data.isValid()){
			List<Long> hostsRunning= hostsRunning(curHis);
			for(Long dictId :selected) {
				if(hostsRunning.contains(dictId)) {
					Concept node = closureServ.loadConceptById(dictId);
					data.addError(messages.get("submitrunhosts2")+ " "+ literalServ.readPrefLabel(node));
					break;
				}
			}
		}
		return data;
	}
	/**
	 * Validate format string against DateTimeFormatter rules
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public FormatsDTO format(FormatsDTO data) throws ObjectNotFoundException {
		String oldValue=Messages.dateFormat;
		Messages.dateFormat=data.getFormatDate().getValue();
		DateTimeFormatter formatter = TableCell.dateFormat();
		data.clearErrors();
		try {
			data.getDateInputSample().getValue().format(formatter);
		} catch (Exception e) {
			data.addError(messages.get("invaliddateformat"));
			data.getFormatDate().invalidate(messages.get("invaliddateformat"));
		} finally {
			Messages.dateFormat=oldValue;
		}
		return data;
	}



}