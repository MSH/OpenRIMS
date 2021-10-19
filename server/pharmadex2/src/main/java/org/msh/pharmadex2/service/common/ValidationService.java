package org.msh.pharmadex2.service.common;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

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
import org.msh.pdex2.model.r2.ThingPerson;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.common.UserRepo;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.AddressDTO;
import org.msh.pharmadex2.dto.AmendmentDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.MessageDTO;
import org.msh.pharmadex2.dto.PersonSpecialDTO;
import org.msh.pharmadex2.dto.PublicOrgDTO;
import org.msh.pharmadex2.dto.QuestionDTO;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.RootNodeDTO;
import org.msh.pharmadex2.dto.SchedulerDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.UserElementDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FieldSuggest;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.r2.AccessControlService;
import org.msh.pharmadex2.service.r2.AssemblyService;
import org.msh.pharmadex2.service.r2.ClosureService;
import org.msh.pharmadex2.service.r2.LiteralService;
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
	private static final Logger logger = LoggerFactory.getLogger(DtoService.class);
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
			if(prefLabel.getValue().length()<2 || prefLabel.getValue().length()>100) {
				suggest(prefLabel, 2,100,strict);
				//data.setValid(false);
			}
		}else {
			error((AllowValidation)data, messages.get("error_preflabel"),strict);
		}
		//aux
		List<AssemblyDTO> auxFlds = assemblyServ.auxLiterals(data.getUrl());
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
		for(String key : data.getDictionaries().keySet()) {
			DictionaryDTO dict=data.getDictionaries().get(key);
			dict=dictionary(dict);
		}
		data.propagateValidation();
		return data;
	}
	/**
	 * Validate a dictionary
	 * @param dict
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private DictionaryDTO dictionary(DictionaryDTO dict) throws ObjectNotFoundException {
		dict.clearErrors();
		if(dict.isRequired()) {
			long sumIds=dict.getSelection().getValue().getId() * dict.getSelection().getValue().getOptions().size();		//value exists, list is empty
			for(Long id : dict.getPrevSelected()) {
				sumIds+=id;
			}
			if(sumIds==0 ) {
				dict.setValid(false);
				dict.setIdentifier(messages.get("error_dictionaryempty"));
			}else {
				dict.clearErrors();
			}
		}else {
			dict.clearErrors();
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO thing(ThingDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		
		List<AssemblyDTO> s = assemblyServ.auxStrings(data.getUrl());
		for(AssemblyDTO str : s) {
			if(str.isRequired()) {
				mandatoryLiteral(data.getStrings().get(str.getPropertyName()), str);
			}
		}
		List<AssemblyDTO> lits = assemblyServ.auxLiterals(data.getUrl());
		for(AssemblyDTO lit : lits) {
			if(lit.isRequired()) {
				mandatoryLiteral(data.getLiterals().get(lit.getPropertyName()), lit);
			}
		}
		List<AssemblyDTO> dats = assemblyServ.auxDates(data.getUrl());
		for(AssemblyDTO dat : dats) {
			if(dat.isRequired()) {
				mandatoryDate(data.getDates().get(dat.getPropertyName()),dat);
			}
		}
		List<AssemblyDTO> nums = assemblyServ.auxNumbers(data.getUrl());
		for(AssemblyDTO num : nums) {
			if(num.isRequired()) {
				mandatoryNumber(data.getNumbers().get(num.getPropertyName()),num);
			}
		}
		List<AssemblyDTO> dicts = assemblyServ.auxDictionaries(data.getUrl());
		for(AssemblyDTO dic : dicts) {
			if(dic.isRequired()) {
				DictionaryDTO dict = data.getDictionaries().get(dic.getPropertyName());
				dictionary(dict);
			}
		}
		List<AssemblyDTO> addrs = assemblyServ.auxAddresses(data.getUrl());
		for(AssemblyDTO addr : addrs) {
			if(addr.isRequired()) {
				mandatoryAddress(data.getAddresses().get(addr.getPropertyName()));
			}
		}
		List<AssemblyDTO> docs = assemblyServ.auxDocuments(data.getUrl());
		for(AssemblyDTO doc: docs) {
			if(doc.isRequired()) {
				mandatoryDoc(data.getDocuments().get(doc.getPropertyName()));
			}
		}
		List<AssemblyDTO> schds = assemblyServ.auxSchedulers(data.getUrl());
		for(AssemblyDTO sch : schds) {
			if(sch.isRequired()) {
				mandatoryScheduler(sch, data.getSchedulers().get(sch.getPropertyName()));
			}
		}
		List<AssemblyDTO> regs = assemblyServ.auxRegisters(data.getUrl());
		for(AssemblyDTO reg : regs) {
			if(reg.isRequired()) {
				mandatoryRegister(reg, data.getRegisters().get(reg.getPropertyName()));
			}
		}
		List<AssemblyDTO> pers = assemblyServ.auxPersons(data.getUrl());
		for(AssemblyDTO per : pers) {
			if(per.isRequired()) {
				mandatoryPersons(data, per.getPropertyName());
			}
		}

		List<AssemblyDTO> personSpec = assemblyServ.auxPersonSpecials(data.getUrl());
		for(AssemblyDTO per : personSpec) {
			if(per.isRequired()) {
				mandatoryPersonSpec(data, per.getPropertyName());
			}
		}

		List<AssemblyDTO> things = assemblyServ.auxThings(data.getUrl());
		for(AssemblyDTO thing :things) {
			if(thing.isRequired()) {
				mandatoryThing(data.getThings().get(thing.getIdentifier()));
			}
		}
		data.propagateValidation();
		return data;
	}

	/**
	 * Special person, like Pharmacist
	 * @param data
	 * @param propertyName
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryPersonSpec(ThingDTO data, String propertyName) throws ObjectNotFoundException {
		PersonSpecialDTO dto = data.getPersonspec().get(propertyName);
		//person should be selected
		List<TableRow> rows = dto.getTable().getRows();
		int selected = 0;
		if(rows.size()>0) {
			for(TableRow row : rows) {
				if(row.getSelected()) {
					selected++;
				}
			}
			if(selected==0) {
				dto.setValid(false);
				dto.setIdentifier(messages.get("error_dictionaryempty"));
			}
		}
	}

	/**
	 * Register record should...
	 * @param ar
	 * @param registerDTO
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryRegister(AssemblyDTO ar, RegisterDTO dto) throws ObjectNotFoundException {
		dto.clearErrors();
		LocalDate regDate = dto.getRegistration_date().getValue();
		LocalDate expDate = dto.getExpiry_date().getValue();
		LocalDate createdAt = LocalDate.now();
		if(dto.getNodeID()>0) {
			Concept node = closureServ.loadConceptById(dto.getNodeID());
			Register reg = boilerServ.registerByConcept(node);
			createdAt=boilerServ.convertToLocalDateViaMilisecond(reg.getCreatedAt());
		}
		String regNum = dto.getReg_number().getValue();
		//register date should fit an interval
		LocalDate maxDate = createdAt;
		LocalDate minDate = createdAt.minusMonths(1);
		String errorMess = messages.get("valuerangeerror")+" " + minDate +", " + maxDate;
		if(regDate.isBefore(minDate) || regDate.isAfter(maxDate)) {
			dto.getRegistration_date().invalidate(errorMess);
		}
		//register number should be not empty, not duplicated for url given
		if(regNum.length()>=3 && regNum.length()<=255) {
			List<Register> rr = boilerServ.registerByNumber(regNum);
			if(rr.size()>1) {
				dto.getReg_number().invalidate(messages.get("registrationexists"));
			}else {
				if(rr.size()==1) {
					if(rr.get(0).getConcept().getID()!=dto.getNodeID()) {
						dto.getReg_number().invalidate(messages.get("registrationexists"));
					}
				}
			}
		}else {
			suggest(dto.getReg_number(), 3, 255, true);
		}
		//expiration date should fit an interval if defined
		if(ar.isMult()) {
			maxDate = createdAt.plusMonths(ar.getMaxQuantity());
			minDate = createdAt.plusMonths(1);
			errorMess = messages.get("valuerangeerror")+" " + regDate.plusMonths(2) +", " + maxDate;
			if(expDate.isBefore(minDate) || expDate.isAfter(maxDate)) {
				dto.getExpiry_date().invalidate(errorMess);
			}
		}
		dto.propagateValidation();
	}

	/**
	 * Schedule data should fit into interval
	 * @param sch 
	 * @param data
	 * @param propertyName
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryScheduler(AssemblyDTO ad, SchedulerDTO schDTO) throws ObjectNotFoundException {
		schDTO.clearErrors();
		LocalDate createdAt = schDTO.getCreatedAt();
		if(createdAt != null) {
			LocalDate minDate=createdAt.plusMonths(ad.getMinQauntity());
			LocalDate maxDate=createdAt.plusMonths(ad.getMaxQuantity()+1);
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
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private void mandatoryPersons(ThingDTO data, String propertyName) throws ObjectNotFoundException {
		if(data.isStrict()) {
			if(data.getNodeId()>0) {
				for(String key : data.getPersons().keySet()) {
					data.getPersons().get(key).clearErrors();
				}
				Concept node = closureServ.loadConceptById(data.getNodeId());
				Thing thing = boilerServ.loadThingByNode(node);
				int counter=0;
				for(ThingPerson tp : thing.getPersons()) {
					if(tp.getVarName().equalsIgnoreCase(propertyName)) {
						counter++;
					}
				}
				if(counter==0) {
					data.getPersons().get(propertyName).setValid(false);
					data.getPersons().get(propertyName).setIdentifier(messages.get("mandatorypersons"));
				}
			}else {
				data.getPersons().get(propertyName).setValid(false);
				data.getPersons().get(propertyName).setIdentifier(messages.get("mandatorypersons"));
			}
		}
	}

	/**
	 * All things are required
	 * @param thingDTO
	 */
	private void mandatoryThing(ThingDTO thingDTO) {
		//TODO create it later
		return;
	}

	/**
	 * All documents are required
	 * @param filesDTO
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryDoc(FileDTO filesDTO) throws ObjectNotFoundException {
		if(filesDTO==null) {
			return;
		}
		filesDTO.clearErrors();
		if (filesDTO.getLinked()==null || filesDTO.getLinked().size()==0) {
			filesDTO.setValid(false);
			filesDTO.setIdentifier(messages.get("upload_file"));
		}
		filesDTO.propagateValidation();
		return;
	}

	/**
	 * Address should be defined
	 * @param data
	 * TODO something more advanced!!!
	 * @throws ObjectNotFoundException 
	 */
	private void mandatoryAddress(AddressDTO data) throws ObjectNotFoundException {
		if(data!=null) {
			data.clearErrors();
			data.getDictionary().clearErrors();
			dictionary(data.getDictionary());
			if(!data.getDictionary().isValid()) {
				data.setValid(false);
				data.setIdentifier(messages.get("addressisempty"));
				return;
			}
			if(data.getMarker().isEmpty()) {
				data.setValid(false);
				data.setIdentifier(messages.get("selectgislocation"));
				return;
			}
		}
	}

	/**
	 * Check mandatory date
	 * @param formFieldDTO
	 * @param dat - rules to check date
	 */
	private void mandatoryDate(FormFieldDTO<LocalDate> dateFld, AssemblyDTO dat) {
		if(dateFld != null) {
			String errorMess = messages.get("valuerangeerror")+" " + dat.getMinDate()+", " + dat.getMaxDate();
			if(dateFld.getValue() != null) {
				LocalDate val = dateFld.getValue();
				if(val.isBefore(dat.getMinDate()) || val.isAfter(dat.getMaxDate())) {
					dateFld.invalidate(errorMess);
				}
			}else {
				dateFld.invalidate(errorMess);
			}
		}
	}
	/**
	 * Check mandatory number
	 * @param numFld
	 * @param num
	 */
	private void mandatoryNumber(FormFieldDTO<Long> numFld, AssemblyDTO num) {
		String errorMess = messages.get("valuerangeerror") +" "+ num.getMin()+", " + num.getMax();
		if(numFld != null) {
			numFld.clearValidation();
			if(numFld.getValue()!=null) {
				Long val = numFld.getValue();
				if(val.compareTo(num.getMin().longValue())<0 || val.compareTo(num.getMax().longValue())>0){
					numFld.invalidate(errorMess);
				}
			}
		}
	}

	/**
	 * Check mandatory literal
	 * @param litFld
	 * @param lit
	 * TODO more precisely, using lit
	 */
	private void mandatoryLiteral(FormFieldDTO<String> litFld, AssemblyDTO lit) {
		if(litFld != null) {
			if(litFld.getValue().length()<lit.getMin().intValue() || litFld.getValue().length()>lit.getMax().intValue()) {
				suggest(litFld, lit.getMin().intValue(), lit.getMax().intValue(), true);
			}
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
	@Transactional
	public AllowValidation workflowConfig(List<Concept> activities, Concept configRoot, AllowValidation data) throws ObjectNotFoundException {
		data.clearErrors();
		//each activity definition should has prefLabel,activityurl, checklisturl
		for(Concept aco : activities) {
			String prefLabel=literalServ.readPrefLabel(aco);
			String activityUrl = literalServ.readValue("activityurl", aco);
			String checklisturl = literalServ.readValue("checklisturl",aco);
			if(prefLabel.length()==0
					|| activityUrl.length()==0
					|| checklisturl.length()==0){
				data.setIdentifier(messages.get("badconfiguration")+prefLabel+"/"+activityUrl+"/"+checklisturl);
				data.setValid(false);
				return data;
			}
			Thing thing = boilerServ.loadThingByNode(aco);
			if(thing.getID()>0) {
				if(thing.getDictionaries().size()==0) {
					data.setIdentifier(messages.get("badconfiguration")+" ROLES ZERO config root is " + configRoot.getID());
					data.setValid(false);
					return data;
				}
			}else {
				data.setIdentifier(messages.get("badconfiguration")+"ThingID is ZERO config root is " + configRoot.getID());
				data.setValid(false);
				return data;
			}
		}
		return data;
	}
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DataVariableDTO variable(DataVariableDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data=variableName(data);
		data=variableClazzAndParams(data);
		data=variableScreen(data);
		data=variableDictionary(data);
		return data;
	}

	/**
	 * Should be URL
	 * @param data
	 * @return
	 */
	private DataVariableDTO variableDictionary(DataVariableDTO data) {
		if(data.getUrl().getValue().length()==0 && data.getClazz().getValue().getCode().equalsIgnoreCase("dictionaries")) {
			data.setValid(false);
			String mess = data.getIdentifier();
			mess = mess + "; "+messages.get("emptyurl");
			data.setIdentifier(mess);
		}
		return data;
	}

	/**
	 * Shouldn't be variables on the same place
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private DataVariableDTO variableScreen(DataVariableDTO data) throws ObjectNotFoundException {
		if(data.getNodeId()>0) {
			long row=data.getRow().getValue();
			long col=data.getCol().getValue();
			long ord=data.getOrd().getValue();
			Concept root = closureServ.loadConceptById(data.getNodeId());
			List<Concept> variables = literalServ.loadOnlyChilds(root);
			for(Concept var : variables) {
				if(var.getID()!=data.getVarNodeId() && var.getActive()) {
					Assembly assm = boilerServ.assemblyByVariable(var,true);
					if(
							assm.getRow()==row
							&& assm.getCol()==col
							&& assm.getOrd()==ord
							) {
						data.setIdentifier(messages.get("busyscreenposition"));
						data.setValid(false);
						return data;
					}
				}

			}
		}else {
			throw new ObjectNotFoundException("variableScreen. Data Collection node ID not found",logger);
		}
		return data;
	}

	/**
	 * Class of variable should be defined
	 * If variable is mandatory, validation rules should be defined
	 * @param data
	 * @return
	 */
	private DataVariableDTO variableClazzAndParams(DataVariableDTO data) {
		FormFieldDTO<OptionDTO> clazz = data.getClazz();
		clazz.clearValidation();
		if(clazz.getValue().getId()==0) {
			suggest(clazz,3,100,true);
		}
		FormFieldDTO<OptionDTO> ropt = data.getRequired();
		YesNoNA required=dtoServ.optionToEnum(YesNoNA.values(), ropt.getValue());
		if(required.equals(YesNoNA.YES)) {
			FormFieldDTO<Long> min = data.getMinLen();
			FormFieldDTO<Long> max = data.getMaxLen();
			if(min==max) {
				suggest(max,messages.get("valueReq"),true);
				suggest(max,messages.get("valueReq"),true);
			}
		}
		return data;
	}

	/**
	 * Variable name should be defined
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private DataVariableDTO variableName(DataVariableDTO data) throws ObjectNotFoundException {
		FormFieldDTO<String> vn= data.getVarName();
		vn.clearValidation();
		if(vn.getValue().length()<3 || vn.getValue().length()>100) {
			suggest(vn,3,100,true);
		}
		data.propagateValidation();
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
		data=thing(data);
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
		List<History> allHis = boilerServ.historyAll(curHis.getApplication());
		if(curHis.getGo()==null) {																			//current is opened
			if(isActivityBackground(curHis.getActConfig())) {
				for(History h : allHis) {
					if(h.getID()!=curHis.getID()) {														//not the same activity
						if(h.getActConfig()!=null && h.getActConfig().getID()==curHis.getActConfig().getID()) {		//configuration is the same
							return data;
						}
					}
				}
			}
			if(isActivityForeground(curHis.getActConfig())) {
				for(History h : allHis) {
					if(h.getID()!=curHis.getID()) {															//not the same activity
						if(h.getGo()==null) {																		//opened
							if(isActivityForeground(h.getActConfig())) {								//at least one opened foreground
								return data;
							}
						}
					}
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
			return false;
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
		if(!data.isReassign()) {
			if(isActivityBackground(curHis.getActConfig()) || isActivityForeground(curHis.getActConfig())) {
				return data;
			}
		}
		data.setIdentifier(messages.get("error_finishmonia"));
		data.setValid(false);
		return data;
	}
	/**
	 * Supervisor can reassign executor:
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
		if(!data.isReject()) {
			if(!data.isReassign()) {
				Concept exec = closureServ.getParent(curHis.getActivity());
				if(accServ.sameEmail(exec.getIdentifier(), user.getEmail())) {
					if(isActivityForeground(curHis.getActConfig())) {
						if(curHis.getGo()==null) {
							return data;
						}
					}
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
	 * NMRA user can finalize an application only 
	 * <ul>
	 * <li> own opened foreground activity
	 * <li> the latest foreground activity in the configuration
	 * </ul>
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ActivitySubmitDTO submitApproveReject(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		if(data.isReject()) {
			return data;
		}
		if(!data.isReassign()) {
			Concept exec = closureServ.getParent(curHis.getActivity());
			if(accServ.sameEmail(exec.getIdentifier(), user.getEmail())) {
				if(isActivityForeground(curHis.getActConfig())) {
					if(curHis.getGo()==null) {
						if(isActivityLatest(curHis)) {
							return data;
						}
					}
				}
			}
		}
		data.setIdentifier(messages.get("error_activityfinal"));
		data.setValid(false);
		return data;
	}
	/**
	 * Is this activity the finalization activity
	 * @param curHis
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isActivityLatest(History curHis) throws ObjectNotFoundException {
		YesNoNA result = dtoServ.readLogicalLiteral("finalize", curHis.getActConfig());
		if(result.equals(YesNoNA.YES)) {
			return true;
		}
		TableQtb fgActs = new TableQtb();
		fgActs.getHeaders().getHeaders().add(TableHeader.instanceOf("pref", TableHeader.COLUMN_STRING));
		jdbcRepo.workflowActivities(curHis.getApplConfig().getID());
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from workflow_activities", "",
				"bg!=1", fgActs.getHeaders());
		if(rows.size()==1) {
			return true;
		}
		for(int i=1; i<rows.size();i++) {
			if(rows.get(i).getDbID()==curHis.getActConfig().getID()) {
				return i==rows.size()-1;
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
		List<History> allHis = boilerServ.historyAll(curHis.getApplication());
		for(History h : allHis) {
			if(h.getGo()==null) {																			//opened
				if(h.getActConfig()!= null && h.getActConfig().getID()==curHis.getActConfig().getID()) {			//same activity configuration
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
	 * Re-assign to the next user
	 * The same as submit to next
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ActivitySubmitDTO submitRouteData(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
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
		if(addAct>0 && executors.size()>0) {
			data=submitNextOrRouteData(curHis, data, executors);
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
	 */
	public ActivitySubmitDTO submitApproveData(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) {
		// TODO for future extensions
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

}