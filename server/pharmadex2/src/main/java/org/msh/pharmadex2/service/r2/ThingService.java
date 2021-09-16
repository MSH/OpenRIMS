package org.msh.pharmadex2.service.r2;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
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
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.FileResourceRepo;
import org.msh.pdex2.repository.r2.HistoryRepo;
import org.msh.pdex2.repository.r2.ThingRepo;
import org.msh.pharmadex2.dto.AddressDTO;
import org.msh.pharmadex2.dto.AddressValuesDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DictValuesDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.PersonDTO;
import org.msh.pharmadex2.dto.PersonSelectorDTO;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.SchedulerDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.ThingValuesDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * CRUD for any activity
 * @author alexk
 *
 */
@Service
public class ThingService {
	private static final Logger logger = LoggerFactory.getLogger(ThingService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private AssemblyService assemblyServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private Messages messages;
	@Autowired
	private AccessControlService accessControlServ;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private ThingRepo thingRepo;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private FileResourceRepo fileRepo;
	@Autowired
	private HistoryRepo historyRepo;
	@Autowired
	private ResourceService resourceServ;

	/**
	 * Create a new thing
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO createThing(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(accessControlServ.createAllowed(data, user)) {
			//let's start
			//data.setReadOnly(false);
			if(data.getVarName().length()==0) {
				data.setTitle(messages.get("RegState.NEW_APPL"));
			}else {
				data.setTitle(messages.get(data.getVarName()));
			}
			data = createContent(data);
		}else {
			throw new ObjectNotFoundException("User is not allowed to initiate application. User is "+user.getEmail(),logger);
		}
		return data;
	}
	/**
	 * Create all content in thing DTO
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO createContent(ThingDTO data) throws ObjectNotFoundException {
		//literals
		List<AssemblyDTO> headings = assemblyServ.auxHeadings(data.getUrl());
		data.getHeading().clear();
		for(AssemblyDTO head : headings) {
			data.getHeading().put(head.getPropertyName(), messages.get(head.getPropertyName()));
		}
		List<AssemblyDTO> literals = assemblyServ.auxLiterals(data.getUrl());
		data=dtoServ.createLiterals(data, literals);
		//dates
		List<AssemblyDTO> dates=assemblyServ.auxDates(data.getUrl());
		data=dtoServ.createDates(data, dates);
		//numbers
		List<AssemblyDTO> numbers=assemblyServ.auxNumbers(data.getUrl());
		data=dtoServ.createNumbers(data,numbers);
		//logical
		List<AssemblyDTO> logicals=assemblyServ.auxLogicals(data.getUrl());
		data=dtoServ.createLogicals(data,logicals);
		//dictionaries
		List<AssemblyDTO> dictionaries = assemblyServ.auxDictionaries(data.getUrl());
		data=createDictonaries(data, dictionaries);
		//addresses
		List<AssemblyDTO> addresses = assemblyServ.auxAddresses(data.getUrl());
		data=createAddresses(data, addresses);
		//files
		List<AssemblyDTO> documents = assemblyServ.auxDocuments(data.getUrl());
		data=createDocuments(documents, data);
		List<AssemblyDTO> resources = assemblyServ.auxResources(data.getUrl());
		data=createResources(resources,data);
		//things
		List<AssemblyDTO> things = assemblyServ.auxThings(data.getUrl());
		data=createThings(things,data);
		//persons
		List<AssemblyDTO> persons =assemblyServ.auxPersons(data.getUrl());
		data=createPersons(persons, data);
		//person selectors
		List<AssemblyDTO> personselectors = assemblyServ.auxPersonSelector(data.getUrl());
		data=createPersonSelectors(personselectors,data);
		//Schedulers
		List<AssemblyDTO> schedulers = assemblyServ.auxSchedulers(data.getUrl());
		data=createSchedulers(schedulers, data);
		List<AssemblyDTO> registers = assemblyServ.auxRegisters(data.getUrl());
		data=createRegisters(registers,data);
		//form actions
		data.setActionBar(assemblyServ.formActions(data.getUrl()));
		//layout
		data=createLayout(data);
		//main labels rewrite
		data.getMainLabels().clear();
		data.getMainLabels().putAll(assemblyServ.mainLabelsByUrl(data.getUrl()));
		return data;
	}

	/**
	 * create empty and load existing registers
	 * @param registers
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO createRegisters(List<AssemblyDTO> registers, ThingDTO data) throws ObjectNotFoundException {
		data.getRegisters().clear();
		//create
		for(AssemblyDTO ad : registers) {
			String prevNumber=boilerServ.registerPrev(ad.getUrl());		//prev number
			RegisterDTO dto = new RegisterDTO();
			dto.setUrl(ad.getUrl());
			dto.setPrev(FormFieldDTO.of(prevNumber));
			dto.setVarName(ad.getPropertyName());
			FormFieldDTO<LocalDate> expiry = FormFieldDTO.of(ad.getMaxDate());
			dto.setExpiry_date(expiry);
			dto.setExpirable(ad.isMult());
			data.getRegisters().put(dto.getVarName(),dto);
		}
		//try to load
		if(data.getNodeId()>0 && data.getRegisters().size()>0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			History his = boilerServ.historyById(data.getHistoryId());
			Thing thing = boilerServ.loadThingByNode(node);
			for(ThingRegister tr : thing.getRegisters()) {
				RegisterDTO dto = data.getRegisters().get(tr.getVarName());
				if(dto!=null) {
					Register reg = boilerServ.registerByConcept(tr.getConcept());
					dto.setReg_number(FormFieldDTO.of(reg.getRegister()));
					FormFieldDTO<LocalDate> regd = FormFieldDTO.of(boilerServ.convertToLocalDateViaMilisecond(reg.getRegisteredAt()));
					FormFieldDTO<LocalDate> expd = FormFieldDTO.of(boilerServ.convertToLocalDateViaMilisecond(reg.getValidTo()));
					dto.setRegistration_date(regd);
					dto.setExpiry_date(expd);
					dto.setNodeID(tr.getConcept().getID());
					dto.setAppDataID(his.getApplicationData().getID());
				}
			}			
		}
		return data;
	}
	/**
	 * Create schedulers
	 * @param schedulers
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO createSchedulers(List<AssemblyDTO> schedulers, ThingDTO data) throws ObjectNotFoundException {
		data.getSchedulers().clear();
		for(AssemblyDTO ad : schedulers) {
			//storage and workflow url and proposed schedule date
			//the previous data supposed to be undefined
			SchedulerDTO sc = new SchedulerDTO();
			sc.setVarName(ad.getPropertyName());
			sc.setDataUrl(ad.getUrl());
			sc.setProcessUrl(ad.getAuxDataUrl());
			sc.setCreatedAt(LocalDate.now());
			sc.getSchedule().setValue(LocalDate.now().plusMonths(ad.getMaxQuantity()));
			data.getSchedulers().put(ad.getPropertyName(), sc);
		}
		//try to load
		if(data.getNodeId()>0 && data.getSchedulers().size()>0 && data.getHistoryId()>0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			History his = boilerServ.historyById(data.getHistoryId());
			Thing thing = boilerServ.loadThingByNode(node);
			for(ThingScheduler ts : thing.getSchedulers()) {
				SchedulerDTO sched = data.getSchedulers().get(ts.getVarName());
				if(sched !=null) {
					Scheduler sc = boilerServ.loadSchedulerByNode(ts.getConcept());
					sched.setNodeId(ts.getConcept().getID());
					sched.getSchedule().setValue(boilerServ.convertToLocalDateViaMilisecond(sc.getScheduled()));
					sched.setTable(schedTable(sc, sched.getTable()));
					sched.setApplDataID(his.getApplicationData().getID());
					sched.setCreatedAt(boilerServ.convertToLocalDateViaMilisecond(sc.getCreatedAt()));
				}
			}
		}
		return data;
	}
	/**
	 * Table with all scheduled events this type (URL)
	 * @param sc
	 * @param table
	 * @return
	 */
	private TableQtb schedTable(Scheduler sc, TableQtb table) {
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(schedTableHeaders(table.getHeaders()));
		}
		//TODO query for rows
		return table;
	}
	/**
	 * Headers for schedule table
	 * @param headers
	 * @return
	 */
	private Headers schedTableHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"scheduled",
				"scheduled",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"completed",
				"completed",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"executor",
				"executor",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));

		TableHeader scheduled = headers.getHeaders().get(0);
		scheduled.setSortValue(TableHeader.SORT_DESC);
		boilerServ.translateHeaders(headers);
		return headers;
	}
	/**
	 * Load person lists using main data node and person data url 
	 * @param personselectors 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ThingDTO createPersonSelectors(List<AssemblyDTO> personselectors, ThingDTO data) throws ObjectNotFoundException {
		data.getPersonselector().clear();
		for(AssemblyDTO ad : personselectors) {
			PersonSelectorDTO dto = new PersonSelectorDTO();
			dto.setHistoryId(data.getHistoryId());
			dto.setPersonUrl(ad.getUrl());
			dto=resourceServ.personSelectorTable(dto);								//for current persons are resources
			data.getPersonselector().put(ad.getPropertyName(),dto);
		}
		return data;
	}
	/**
	 * Create resources table
	 * Headers are the same as for documents table
	 * @param resources
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO createResources(List<AssemblyDTO> resources, ThingDTO data) throws ObjectNotFoundException {
		data.getResources().clear();
		for(AssemblyDTO res : resources) {
			ResourceDTO resDto = new ResourceDTO();
			resDto.setHistoryId(data.getHistoryId());					//link to all application data
			resDto.getTable().setHeaders(createDocTableHeaders(resDto.getTable().getHeaders()));
			resDto =  resourceServ.table(res,resDto);
			data.getResources().put(res.getPropertyName(),resDto);
		}
		return data;
	}
	/**
	 *Create person records
	 * @param persons
	 * @param data
	 * @return
	 */
	@Transactional
	private ThingDTO createPersons(List<AssemblyDTO> persons, ThingDTO data) {
		data.getPersons().clear();
		for(AssemblyDTO pers : persons) {
			PersonDTO dto = new PersonDTO();
			dto.setDictUrl(pers.getDictUrl());
			dto.setReadOnly(pers.isReadOnly());
			dto.setRequired(pers.isRequired());
			dto.setVarName(pers.getPropertyName());
			dto.setThingNodeId(data.getNodeId());
			dto =createPersTable(dto);
			data.getPersons().put(pers.getPropertyName(),dto);
		}
		return data;
	}
	/**
	 * Create a table with persons
	 * @param data
	 * @param dto 
	 * @return
	 */
	@Transactional
	private PersonDTO createPersTable(PersonDTO dto) {
		if(dto.getTable().getHeaders().getHeaders().size()==0) {
			dto.getTable().setHeaders(personHeaders(dto.getTable().getHeaders()));
		}
		if(dto.getThingNodeId()>0) {
			jdbcRepo.persons(dto.getThingNodeId());
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from _persons", "","", dto.getTable().getHeaders());
			TableQtb.tablePage(rows, dto.getTable());
			dto.getTable().setSelectable(false);
		}
		return dto;
	}
	/**
	 * Create headers for person table
	 * @param headers
	 * @return
	 */
	private Headers personHeaders(Headers headers) {
		headers.getHeaders().add((TableHeader.instanceOf(
				"pref",
				"global_name",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				0)));
		headers=boilerServ.translateHeaders(headers);
		return headers;
	}
	/**
	 * Create addresses
	 * @param data
	 * @param addresses
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ThingDTO createAddresses(ThingDTO data, List<AssemblyDTO> addresses) throws ObjectNotFoundException {
		data.getAddresses().clear();
		Concept thingNode = new Concept();
		Thing thing = new Thing();
		if(data.getNodeId()>0) {
			thingNode=closureServ.loadConceptById(data.getNodeId());
			thing = boilerServ.loadThingByNode(thingNode,thing);
		}
		for(AssemblyDTO addr: addresses) {
			data.getAddresses().put(addr.getPropertyName(), createAddress(data, thing, addr));
		}
		return data;
	}
	/**
	 * Create an address DTO from the configuration and, possible, data stored in things in a tree
	 * @param data 
	 * @param node Thing's node, maybe null
	 * @param configuration data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public AddressDTO createAddress(ThingDTO data, Thing thing, AssemblyDTO assm) throws ObjectNotFoundException {
		AddressDTO addr = new AddressDTO();
		addr.setVarName(assm.getPropertyName());
		addr.setUrl(assm.getUrl());
		addr.getDictionary().setUrl(assemblyServ.adminUnitsDict());
		addr.getDictionary().getPrevSelected().clear();
		addr.getDictionary().setReadOnly(data.isReadOnly());
		addr.getDictionary().setSelectedOnly(data.isReadOnly());
		for(ThingThing th : thing.getThings()) {
			if(th.getUrl().equalsIgnoreCase(addr.getUrl())
					&& th.getVarname().equalsIgnoreCase(addr.getVarName())) {
				Concept addrNode= th.getConcept();
				Thing addrThing = new Thing();
				addrThing = boilerServ.loadThingByNode(addrNode, addrThing);
				if(addrThing.getDictionaries().size()>0){
					ThingDict td = addrThing.getDictionaries().iterator().next();
					addr.getDictionary().getPrevSelected().add(td.getConcept().getID());
				}
				String loc = addrNode.getLabel();
				if(loc != null && loc.length() > 0) {
					addr.setMarker(dtoServ.createLocationDTO(loc));
				}
				addr.setNodeId(th.getConcept().getID());
			}
		}
		addr.setDictionary(dictServ.createDictionary(addr.getDictionary()));
		dictServ.loadHomeLocation(addr);
		return addr;
	}
	/**
	 * Create things for this thing
	 * Try to read ones if existing
	 * @param asms - assemblyDTOs
	 * @param data supposed that data.url is defined
	 * @TODO creation table of things!!!!!
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO createThings(List<AssemblyDTO> asms, ThingDTO data) throws ObjectNotFoundException {
		data.getThings().clear();
		for(AssemblyDTO asm: asms) {
			ThingDTO dto = ThingDTO.createIncluded(data, asm);
			dto.setTitle(messages.get(asm.getPropertyName()));
			dto.setVarName(asm.getPropertyName());
			dto.setApplicationUrl(data.getApplicationUrl());
			data.getThings().put(asm.getPropertyName(), dto);
		}
		Thing thing = new Thing();
		if(data.getNodeId()>0) {
			Concept node=closureServ.loadConceptById(data.getNodeId());
			thing = boilerServ.loadThingByNode(node,thing);
		}
		for(ThingThing th : thing.getThings()) {
			Set<String> keys = data.getThings().keySet();
			for(String key : keys) {
				if(th.getUrl().equalsIgnoreCase(data.getThings().get(key).getUrl()) 
						&& th.getVarname().equalsIgnoreCase(key)) {
					data.getThings().get(key).setNodeId(th.getConcept().getID());
					data.getThings().get(key).setVarName(key);
				}
			}
		}
		return data;
	}


	/**
	 * Prepare files objects
	 * @param files
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO createDocuments(List<AssemblyDTO> files, ThingDTO data) throws ObjectNotFoundException {
		data.getDocuments().clear();
		//prepare files
		for(AssemblyDTO asm : files) {
			FileDTO fdto=new FileDTO();
			fdto.setAccept(asm.getFileTypes());
			fdto.setReadOnly(data.isReadOnly());
			fdto.setUrl(asm.getUrl());
			fdto.setDictUrl(asm.getDictUrl());
			fdto.setVarName(asm.getPropertyName());
			fdto.setThingNodeId(data.getNodeId());
			fdto=createDocUploaded(fdto);
			fdto = createDocTable(fdto);
			data.getDocuments().put(asm.getPropertyName(),fdto);
		}
		return data;
	}
	/**
	 * create list of uploaded 
	 * @param data
	 * @param fdto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private FileDTO createDocUploaded(FileDTO data) throws ObjectNotFoundException {
		data.getLinked().clear();
		if(data.getThingNodeId()>0) {
			Concept thingConc = closureServ.loadConceptById(data.getThingNodeId());
			Thing thing = new Thing();
			thing = boilerServ.loadThingByNode(thingConc, thing);
			for(ThingDoc td : thing.getDocuments()) {
				long dictItemId = td.getDictNode().getID();
				long fileNodeId = td.getConcept().getID();
				data.getLinked().put(dictItemId, fileNodeId);
			}
		}
		return data;
	}
	/**
	 * create a table with list of documents
	 * @param fdto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private FileDTO createDocTable(FileDTO data) throws ObjectNotFoundException {
		if(data.getTable().getHeaders().getHeaders().size()==0) {
			data.getTable().setHeaders(createDocTableHeaders(data.getTable().getHeaders()));
		}
		Concept dictRoot = closureServ.loadRoot(data.getDictUrl());
		long thingId=0;
		if(data.getThingNodeId()>0) {
			Concept thingNode=closureServ.loadConceptById(data.getThingNodeId());
			Thing thing = new Thing();
			thing = boilerServ.loadThingByNode(thingNode, thing);
			thingId = thing.getID(); 
		}

		jdbcRepo.prepareFileList(dictRoot.getID(), thingId);
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from _filelist", "", "", data.getTable().getHeaders());
		TableQtb.tablePage(rows, data.getTable());
		for(TableRow row : rows) {
			Long fileNodeId = data.getLinked().get(row.getDbID());
			TableCell cell = row.getCellByKey("filename");
			if(data.isReadOnly()) {
				cell.setValue("");
				cell.setOriginalValue("");
			}
			if(fileNodeId != null) {
				Concept fileNode = closureServ.loadConceptById(fileNodeId);
				cell.setValue(fileNode.getLabel());
				cell.setOriginalValue(fileNode.getLabel());
			}
		}
		boilerServ.translateRows(data.getTable());
		data.getTable().setSelectable(false);
		return data;
	}

	/**
	 * Document's table
	 * @param headers
	 * @return
	 */
	private Headers createDocTableHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"filename",
				"filename",
				true,
				true,
				true,
				TableHeader.COLUMN_I18LINK,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"global_name",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"description",
				"description",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers=boilerServ.translateHeaders(headers);
		return headers;
	}
	/**
	 * Ask configuration for form layout
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ThingDTO createLayout(ThingDTO data) throws ObjectNotFoundException {
		data.getLayout().clear();
		data.getLayout().addAll(assemblyServ.formLayout(data.getUrl()));
		return data;
	}

	/**
	 * Create a set of dictionaries in the application's activity
	 * @param data - application's activity
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ThingDTO createDictonaries(ThingDTO data, List<AssemblyDTO> dictas) throws ObjectNotFoundException {
		data.getDictionaries().clear();
		//restore dictionaries
		for(AssemblyDTO dicta : dictas) {
			DictionaryDTO dict = new DictionaryDTO();
			dict.setUrl(dicta.getUrl());
			dict.setVarName(dicta.getPropertyName());
			dict.setRequired(dicta.isRequired());
			dict.setMult(dicta.isMult());
			dict.setReadOnly(data.isReadOnly());
			dict.setSelectedOnly(data.isReadOnly());
			data.getDictionaries().put(dicta.getPropertyName(), dict);
		}
		//restore selections if ones
		if(data.getNodeId()>0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			Thing thing = new Thing();
			thing= boilerServ.loadThingByNode(node,thing);
			for(ThingDict adict :thing.getDictionaries()) {
				for(String key :data.getDictionaries().keySet()) {
					DictionaryDTO dict = data.getDictionaries().get(key);
					if(adict.getUrl().equalsIgnoreCase(dict.getUrl())
							&& adict.getVarname().equalsIgnoreCase(dict.getVarName())) {
						dict.getPrevSelected().add(adict.getConcept().getID());
					}
				}
			}
		}
		//load data
		for(String key : data.getDictionaries().keySet()) {
			data.getDictionaries().put(key, dictServ.createDictionary(data.getDictionaries().get(key)));
		}
		return data;
	}

	/**
	 * Save a thing
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO save(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		data = validServ.thing(data);
		if(data.isValid() || !data.isStrict()) {
			data.setStrict(true);									//to ensure the next
			if(accessControlServ.writeAllowed(data, user)) {
				Concept node = new Concept();
				if(data.getNodeId()==0) {
					node = createNode(data.getUrl(), user.getEmail());
				}else {
					node=closureServ.loadConceptById(data.getNodeId());
				}
				data.setNodeId(node.getID());
				//thing
				Thing thing = new Thing();
				thing = boilerServ.loadThingByNode(node, thing);
				thing.setConcept(node);
				thing.setUrl(data.getUrl());

				//store data under the node and thing
				data = storeLiterals(node, data);
				data = storeDates(node,data);
				data = storeNumbers(node, data);
				data = storeLogical(node,data);
				data = storeDictionaries(thing,data);
				data = storeDocuments(thing, data);
				data = storeAddresses(user, node, thing,data);

				//store a thing
				thing = thingRepo.save(thing);
				/////////////////// Store data, end /////////////////////////////

				//application and activity
				if(data.getHistoryId()==0 && data.getParentId()==0) {
					if(data.getParentIndex()==-1) {
						data=createApplication(data,user.getEmail(),node);
					}
				}
				//schedules and registers should be stored only for an application
				data = storeSchedule(user, node, thing, data);
				data = storeRegister(user, thing, data);

				//attach to the parent auxiliary things
				if(data.getParentId()>0) {
					Concept incl=closureServ.loadConceptById(data.getParentId());
					Concept email=closureServ.getParent(incl);
					Concept root = closureServ.getParent(email);
					List<AssemblyDTO> things = assemblyServ.auxThings(root.getIdentifier());
					List<AssemblyDTO> persons = assemblyServ.auxPersons(root.getIdentifier());
					Thing inclThing = boilerServ.loadThingByNode(incl);
					for(AssemblyDTO th :things) {
						if(th.getPropertyName().equalsIgnoreCase(data.getVarName())) {
							saveToThings(data, node, inclThing);
							break;
						}
					}
					for(AssemblyDTO th :persons) {
						if(th.getPropertyName().equalsIgnoreCase(data.getVarName())) {
							saveToPersons(data, node, inclThing);
							break;
						}
					}
				}
				if(data.getHistoryId()>0) {
					for(ThingDTO dto : data.getPath()) {
						dto.setHistoryId(data.getHistoryId());
						dto.setActivityId(data.getActivityId());
					}
				}
			}else {
				throw new ObjectNotFoundException("Write access denied. URL "+data.getApplicationUrl() +" user "+user.getEmail());
			}
		}
		return data;
	}



	/**
	 * Attach the node to persons
	 * @param data
	 * @param node
	 * @param attachTo
	 */
	@Transactional
	private void saveToPersons(ThingDTO data, Concept node, Thing attachTo) {
		boolean found = false;
		for(ThingPerson tp : attachTo.getPersons()) {
			if(tp.getConcept().getID()==data.getNodeId()) {
				found=true;
				break;
			}
		}
		if(!found) {
			ThingPerson link = new ThingPerson();
			link.setConcept(node);
			link.setPersonUrl(data.getUrl());
			link.setVarName(data.getVarName());
			attachTo.getPersons().add(link);
		}
		attachTo=thingRepo.save(attachTo);
	}
	/**
	 * Attach this data as a thing to ThingThing
	 * @param data
	 * @param node
	 * @param attachTo
	 */
	public void saveToThings(ThingDTO data, Concept node, Thing attachTo) {
		boolean found = false;
		for(ThingThing tt : attachTo.getThings()) {
			if(tt.getVarname().equalsIgnoreCase(data.getVarName())) {
				found=true;
				break;
			}
		}
		if(!found) {
			ThingThing link = new ThingThing();
			link.setConcept(node);
			link.setUrl(data.getUrl());
			link.setVarname(data.getVarName());
			attachTo.getThings().add(link);
			attachTo=thingRepo.save(attachTo);
		}
	}

	/**
	 * Create the first activity in application and a history of it
	 * @param data
	 * @param email
	 * @param applicationData
	 * @return the first activity in the application
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ThingDTO createApplication(ThingDTO data, String email, Concept applicationData) throws ObjectNotFoundException {
		//create an application.
		Concept application = createNode(data.getApplicationUrl(), email);
		///do we need a special checklist for an applicant?
		String checklistUrl="dictionary.selfcheck.general";
		Concept aDictNode=closureServ.loadConceptById(data.getApplDictNodeId());
		String specUrl=literalServ.readValue("checklisturl", aDictNode);
		if(specUrl.length()>0) {
			checklistUrl=specUrl;
		}
		application.setLabel(checklistUrl);
		application = closureServ.save(application);

		//create a history
		Concept applConfig = closureServ.loadRoot("configuration."+data.getApplicationUrl());
		History history = new History();
		history.setApplDict(aDictNode);
		history.setApplConfig(applConfig);
		history.setApplication(application);
		history.setApplicationData(applicationData);
		history.setActivityData(applicationData);
		history.setActivity(application);						//the first activity in application is the application itself
		history.setCome(boilerServ.localDateTimeToDate(LocalDateTime.now()));
		history =historyRepo.save(history);
		data.setHistoryId(history.getID());
		data.setActivityId(application.getID());
		data.setActivityName(messages.get("init"));
		return data;
	}
	/**
	 * Store all addresses to own addresses tree
	 * @param data
	 * @param user
	 * @param node
	 * @param thing
	 * @throws ObjectNotFoundException
	 */
	public ThingDTO storeAddresses(UserDetailsDTO user, Concept node, Thing thing, ThingDTO data)
			throws ObjectNotFoundException {
		for(String addr:data.getAddresses().keySet()){
			AddressDTO addrDTO = data.getAddresses().get(addr);
			saveAddressAsThing(node, addrDTO,user, thing);
		}
		return data;
	}

	/**
	 * Store a schedule data to the scheduler table
	 * @param user
	 * @param node
	 * @param thing
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO storeSchedule(UserDetailsDTO user, Concept node, Thing thing, ThingDTO data) throws ObjectNotFoundException {
		if(data.getActivityId()>0) {													//we need activity ID to get a history record
			List<History> his = boilerServ.historyByActivityNode(closureServ.loadConceptById(data.getActivityId()));
			if(his.size()>0) {																//we need a history record to access application data
				Concept applData = his.get(0).getApplicationData();
				thing.getSchedulers().clear();									//do not replace :)
				for(String key :data.getSchedulers().keySet()) {			//for each scheduler...
					SchedulerDTO dto = data.getSchedulers().get(key);
					Scheduler sch = new Scheduler();
					Concept schedConc = new Concept();
					if(dto.getNodeId()>0) {
						//load existing...
						schedConc = closureServ.loadConceptById(dto.getNodeId());
						sch = boilerServ.loadSchedulerByNode(schedConc);
					}else {
						// or create a new one
						Concept root = closureServ.loadRoot(dto.getDataUrl());
						Concept owner = closureServ.saveToTree(root, user.getEmail());
						schedConc = closureServ.save(node);
						schedConc.setIdentifier(node.getID()+"");
						schedConc=closureServ.saveToTree(owner, node);
						dto.setNodeId(schedConc.getID());
					}
					ThingScheduler tch = new ThingScheduler();
					tch.setConcept(schedConc);
					tch.setUrl(dto.getDataUrl());
					tch.setVarName(key);
					thing.getSchedulers().add(tch);
					sch.setAppData(applData);								//all above is only for this
					sch.setActivityData(thing.getConcept());			//to ensure cascade removing
					sch.setConcept(schedConc);
					sch.setProcessUrl(dto.getProcessUrl());
					sch.setScheduled(boilerServ.localDateToDate(dto.getSchedule().getValue()));
					sch=boilerServ.saveSchedule(sch);
				}
				//and save it at last
				thing=boilerServ.saveThing(thing);
				return data;
			}else {
				throw new ObjectNotFoundException("storeSchedule. History record is not defined",logger);
			}
		}
		return data;
	}

	/**
	 * Store registers
	 * @param user
	 * @param thing
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO storeRegister(UserDetailsDTO user, Thing thing, ThingDTO data) throws ObjectNotFoundException {
		if(data.getActivityId()>0) {	//Registers have sense only when activity has been defined
			List<History> his = boilerServ.historyByActivityNode(closureServ.loadConceptById(data.getActivityId()));
			if(his.size()>0) {																//we need a history record to access application data
				Concept applData = his.get(0).getApplicationData();
				thing.getRegisters().clear();													//do not replace :)
				for(String key :data.getRegisters().keySet()) {			//for each register...
					RegisterDTO regDto = data.getRegisters().get(key);
					Register reg = new Register();
					Concept regNode = new Concept();
					if(regDto.getNodeID()>0) {
						//load
						regNode = closureServ.loadConceptById(regDto.getNodeID());
						reg=boilerServ.registerByConcept(regNode);
					}else {
						//create new
						Concept root = closureServ.loadRoot(regDto.getUrl());
						Concept owner = closureServ.saveToTree(root, user.getEmail());
						regNode = closureServ.save(regNode);
						regNode.setIdentifier(regNode.getID()+"");
						regNode=closureServ.saveToTree(owner, regNode);
						regDto.setNodeID(regNode.getID());
					}
					ThingRegister thre = new ThingRegister();
					thre.setConcept(regNode);
					thre.setUrl(regDto.getUrl());
					thre.setVarName(key);
					thing.getRegisters().add(thre);
					reg.setAppData(applData);
					reg.setActivityData(thing.getConcept());
					reg.setConcept(regNode);
					reg.setRegister(regDto.getReg_number().getValue());
					reg.setRegisteredAt(boilerServ.localDateToDate(regDto.getRegistration_date().getValue()));
					reg.setValidTo(boilerServ.localDateToDate(regDto.getExpiry_date().getValue()));
					//save all
					reg=boilerServ.saveRegister(reg);
				}
				//and save it at last
				thing=boilerServ.saveThing(thing);
				return data;
			}else {
				throw new ObjectNotFoundException("storeRegister. History record is not defined",logger);
			}
		}
		return data;
	}

	/**
	 * Store documents attached
	 * @param data
	 * @param thing
	 * @throws ObjectNotFoundException
	 */
	public ThingDTO storeDocuments(Thing thing, ThingDTO data) throws ObjectNotFoundException {
		for(String key : data.getDocuments().keySet()) {
			FileDTO docDto = data.getDocuments().get(key);
			//which are already stored
			for(ThingDoc td : thing.getDocuments()){
				if(td.getVarName().equalsIgnoreCase(key)) {
					Long nodeId = docDto.getLinked().get(td.getDictNode().getID());
					if(nodeId != null) {
						//replace concept for a case
						Concept fileNode = closureServ.loadConceptById(nodeId);
						td.setConcept(fileNode);
						docDto.getLinked().remove(td.getDictNode().getID());	//remove it
					}
				}
			}
			//The rest should be added
			for(Long dictId : docDto.getLinked().keySet()) {
				ThingDoc td = new ThingDoc();
				Concept dictNode = closureServ.loadConceptById(dictId);
				Concept fileNode =closureServ.loadConceptById(docDto.getLinked().get(dictId));
				FileResource fr = boilerServ.fileResourceByNode(fileNode);
				fr.setActivityData(thing.getConcept());
				td.setConcept(fileNode);
				td.setDictNode(dictNode);
				td.setDictUrl(docDto.getDictUrl());
				td.setDocUrl(docDto.getUrl());
				td.setVarName(key);
				thing.getDocuments().add(td);
				fr=fileRepo.save(fr);
			}
		}
		return data;
	}

	/**
	 * Store dictionaries selected
	 * @param data
	 * @param thing
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO storeDictionaries(Thing thing, ThingDTO data) throws ObjectNotFoundException {
		thing.getDictionaries().clear();
		Set<Long> selected = new HashSet<Long>();
		for(String key : data.getDictionaries().keySet()) {
			DictionaryDTO dict = data.getDictionaries().get(key);
			selected.clear();
			selected.addAll(dict.getPrevSelected());
			if(selected.size()==0 && dict.getSelection().getValue().getId()>0) {
				selected.add(dict.getSelection().getValue().getId());
			}
			for(Long id : selected) {
				if(id != 0) {
					Concept dictItem = closureServ.loadConceptById(id);
					ThingDict thingDict = new ThingDict();
					thingDict.setUrl(dict.getUrl());
					thingDict.setConcept(dictItem);
					thingDict.setVarname(dict.getVarName());
					thing.getDictionaries().add(thingDict);
				}
			}
		}
		return data;
	}
	@Transactional
	public ThingDTO storeDates(Concept node, ThingDTO data) throws ObjectNotFoundException {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
		for(String key :data.getDates().keySet()) {
			LocalDate dt = data.getDates().get(key).getValue();
			if(dt!= null) {
				literalServ.createUpdateLiteral(key, formatter.format(dt), node);
			}
		}
		return data;
	}
	/**
	 * Store numbers as literals
	 * @param node
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO storeNumbers(Concept node, ThingDTO data) throws ObjectNotFoundException {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(0);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		for(String key :data.getNumbers().keySet()) {
			Long num = data.getNumbers().get(key).getValue();
			if(num==null) {
				num=0l;
			}
			literalServ.createUpdateLiteral(key, df.format(num), node);
		}
		return data;
	}
	/**
	 * Store logicals values as string representation of enum's ord
	 * @param node
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO storeLogical(Concept node, ThingDTO data) throws ObjectNotFoundException {
		for(String key :data.getLogical().keySet()) {
			OptionDTO opt = data.getLogical().get(key).getValue();
			literalServ.createUpdateLiteral(key, opt.getId()+"", node);
		}
		return data;
	}
	/**
	 * Store literals under the node
	 * @param data
	 * @param node
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO storeLiterals(Concept node, ThingDTO data) throws ObjectNotFoundException {
		for(String key : data.getLiterals().keySet()) {
			literalServ.createUpdateLiteral(key, data.getLiterals().get(key).getValue(), node);
			if(key.equalsIgnoreCase("prefLabel") && data.getParentIndex()==-1) {
				String newTitle= data.getLiterals().get(key).getValue();
				if(newTitle.length()>0) {
					if(data.getVarName().length()==0) {
						data.setTitle(newTitle);
					}else {
						data.setTitle(messages.get(data.getVarName()));
					}
				}
			}
		}
		return data;
	}

	/**
	 * Save an address as a thing
	 * link to a dictionary and node.getLabel() as coordinates
	 * @param parentThingNode
	 * @param data
	 * @param parentThing 
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public AddressDTO saveAddressAsThing(Concept parentThingNode, AddressDTO data, UserDetailsDTO user, Thing parentThing) throws ObjectNotFoundException {
		//determine a node and Thing record
		Concept node = new Concept();
		Thing thing = new Thing();
		if(data.getNodeId()==0) {
			Concept root = closureServ.loadRoot(data.getUrl());
			Concept owner = closureServ.saveToTree(root, user.getEmail());
			node = closureServ.save(node);
			node.setIdentifier(node.getID()+"");
			node=closureServ.saveToTree(owner, node);
		}else {
			node=closureServ.loadConceptById(data.getNodeId());
		}
		try {
			thing=boilerServ.loadThingByNode(node,thing);
		} catch (Exception e) {
			//nothing to do
		}
		//prepare data to store
		node.setLabel(data.getMarker().gisLocation());
		thing.getDictionaries().clear();
		thing.setUrl(data.getUrl());
		Set<Long> selected = dictServ.selectedItems(data.getDictionary());
		if(selected.size()>0) {
			ThingDict dict = new ThingDict();
			dict.setUrl(data.getDictionary().getUrl());
			Concept dictNode = closureServ.loadConceptById(selected.iterator().next());
			dict.setConcept(dictNode);
			dict.setVarname(data.getVarName());
			thing.getDictionaries().add(dict);
		}
		boolean found=false;
		for(ThingThing th : parentThing.getThings()) {
			if(th.getUrl().equalsIgnoreCase(data.getUrl())
					&& th.getVarname().equalsIgnoreCase(data.getVarName())) {
				th.setConcept(node);
				found=true;
			}
		}
		if(!found) {
			ThingThing th = new ThingThing();
			th.setUrl(data.getUrl());
			th.setVarname(data.getVarName());
			th.setConcept(node);
			parentThing.getThings().add(th);
		}
		//store it
		node=closureServ.save(node);
		data.setNodeId(node.getID());
		thing.setConcept(node);
		thing=thingRepo.save(thing);
		parentThing.setConcept(parentThingNode);
		parentThing = thingRepo.save(parentThing);
		return data;
	}


	/**
	 * Create a node in the tree under the user's eMail 
	 * @param treeUrl
	 * @param eMAil
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept createNode(String treeUrl, String eMail) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(treeUrl);
		Concept eMailNode = closureServ.saveToTree(root, eMail);
		Concept node = new Concept();
		node = closureServ.save(node);
		node.setIdentifier(node.getID()+"");
		node = closureServ.saveToTree(eMailNode, node);
		return node;
	}

	/**
	 * load a thing
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO loadThing(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(data.getNodeId()>0) {
			Concept node= closureServ.loadConceptById(data.getNodeId());
			Thing thing = new Thing();
			thing = boilerServ.loadThingByNode(node,thing);
			//determine URL
			if(data.getUrl().length()==0) {
				data.setUrl(thing.getUrl());
			}
			if(accessControlServ.readAllowed(data,user)) {
				data.setReadOnly(!accessControlServ.writeAllowed(data, user) || data.isReadOnly());
				if(data.getActivityId()>0) {
					Concept activity=closureServ.loadConceptById(data.getActivityId());
					String prefLabel = literalServ.readPrefLabel(node);
					if(prefLabel.length()>0 && data.getParentIndex()==0) {
						data.setTitle(prefLabel);
					}else {
						data.setTitle(messages.get(data.getVarName()));
					}
					data.setActivityName(activity.getLabel());
				}
				data=createContent(data);
				data.setLiterals(dtoServ.readAllLiterals(data.getLiterals(), node));
				data.setDates(dtoServ.readAllDates(data.getDates(),node));
				data.setNumbers(dtoServ.readAllNumbers(data.getNumbers(),node));
				data.setLogical(dtoServ.readAllLogical(data.getLogical(), node));
			}else {
				throw new ObjectNotFoundException("loadThing. Read access dened for user "+user.getEmail(),logger);
			}
		}else {
			throw new ObjectNotFoundException("loadThing. Node  is not defined",logger);
		}
		return data;
	}

	/**
	 * Create a path to fill-out form
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO path(ThingDTO data) throws ObjectNotFoundException {
		//application name and description
		if(data.getApplDictNodeId()>0) {
			Concept adictNode = closureServ.loadConceptById(data.getApplDictNodeId());
			data.setApplName(literalServ.readPrefLabel(adictNode));
			data.setApplDescr(literalServ.readDescription(adictNode));
			data.setUrl(literalServ.readValue("dataurl", adictNode));
			data.setApplicationUrl(literalServ.readValue("applicationurl", adictNode));
		}

		//do we know applicationId, nodeId?
		if(data.getHistoryId()>0) {
			History his = boilerServ.historyById(data.getHistoryId());
			if(his.getActivity()!=null) {
				data.setActivityId(his.getActivity().getID());
				data.setActivityName(messages.get(his.getActivity().getLabel()));
			}
			if(data.isApplication()) {
				if(his.getApplication()!= null) {
					data.setNodeId(his.getApplicationData().getID());
				}
			}else {
				if(his.getActivityData()!=null){
					data.setNodeId(his.getActivityData().getID());
				}
			}
			if(data.getNodeId()==0) {
				throw new ObjectNotFoundException("path. Can't get nodeId on the existig activity/application",logger);
			}
		}
		//breadcrumb related things
		data.setTitle(messages.get("RegState.NEW_APPL"));
		if(data.getNodeId()>0) {
			Concept node= closureServ.loadConceptById(data.getNodeId());
			Thing thing = new Thing();
			thing = boilerServ.loadThingByNode(node,thing);
			data.setUrl(thing.getUrl());
			data.setTitle(literalServ.readPrefLabel(node));
		}
		data.getPath().clear();
		List<ThingDTO> path = createPath(data, new ArrayList<ThingDTO>(),-1);
		data.getPath().addAll(path);
		return data;
	}

	/**
	 * Create a path recursive
	 * @param dto thing dto to include to the path
	 * @param path path itself
	 * @param parentIndex index of thing dto, -1 is no parent 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<ThingDTO> createPath(ThingDTO dto, List<ThingDTO> path, int parentIndex) throws ObjectNotFoundException {
		dto.setParentIndex(parentIndex);
		if(path.size()==0) {
			path.add(deepCloneThing(dto));
		}
		List<AssemblyDTO> things = assemblyServ.auxThings(dto.getUrl());
		dto = createThings(things, dto);
		Set<String> keys = dto.getThings().keySet();
		int nextParIndex = path.size()-1;
		for(String key : keys) {
			ThingDTO dto1 = dto.getThings().get(key);
			path.add(dto1);
			path = createPath(dto1,path,nextParIndex);
		}
		return path;
	}

	/**
	 * Create a deep clone of the ThingDTO
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ThingDTO deepCloneThing(ThingDTO dto) throws ObjectNotFoundException {
		ThingDTO deepCopy;
		try {
			deepCopy = objectMapper
					.readValue(objectMapper.writeValueAsString(dto), ThingDTO.class);
			return deepCopy;
		} catch (JsonProcessingException e) {
			throw new ObjectNotFoundException(e,logger);
		}

	}
	/**
	 * Load a linked file or an empty record ready to upload
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public FileDTO fileLoad(FileDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		Long nodeIdL = data.getLinked().get(data.getDictNodeId());
		long nodeId = 0;
		if(nodeIdL != null) {
			nodeId=nodeIdL;
		}
		data.setNodeId(nodeId);
		if(nodeId>0) {
			//load data from existing
			Concept fileNode = closureServ.loadConceptById(data.getNodeId());
			Optional<FileResource> fro = fileRepo.findByConcept(fileNode);
			if(fro.isPresent()) {
				data.setFileName(fileNode.getLabel());
				data.setFileSize(fro.get().getFileSize());
				data.setMediaType(fro.get().getMediatype());
			}else {
				throw new ObjectNotFoundException("fileLoad File Resource by concept is not found. Concept id is "+data.getNodeId(),logger);
			}
		}
		return data;
	}

	/**
	 * Save a file to the user's storage
	 * @param data
	 * @param user
	 * @param fileBytes
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public FileDTO fileSave(FileDTO data, UserDetailsDTO user, byte[] fileBytes) throws ObjectNotFoundException {
		String email = user.getEmail();
		if((data.getFileName().length()==0 || fileBytes.length>1) 
				&& validServ.eMail(email) 
				&& data.getDictNodeId()>0) {
			//determine node ID
			long fileNodeId =fileNodeId(data);
			Concept node = new Concept();
			if(fileNodeId==0 ) {
				//create a new file node and store it to data
				Concept root = closureServ.loadRoot(data.getUrl());
				Concept owner=closureServ.saveToTree(root, user.getEmail());
				node = closureServ.save(node);
				node.setIdentifier(node.getID()+"");
				node=closureServ.saveToTree(owner, node);
				data.getLinked().put(data.getDictNodeId(),node.getID());
			}else {
				node = closureServ.loadConceptById(data.getNodeId());
			}
			Concept parent = closureServ.getParent(node);
			if(accessControlServ.sameEmail(parent.getIdentifier(), user.getEmail())) {
				//dictionary item
				Concept dictItem = closureServ.loadConceptById(data.getDictNodeId());
				//file name
				node.setLabel(data.getFileName());
				//file data
				FileResource fres = new FileResource();
				Optional<FileResource> freso = fileRepo.findByConcept(node);
				if(freso.isPresent()) {
					fres=freso.get();
				}
				fres.setClassifier(dictItem);
				fres.setConcept(node);
				fres.setFile(fileBytes);
				fres.setFileSize(data.getFileSize());
				fres.setMediatype(data.getMediaType());
				fres=fileRepo.save(fres);
				//link it to the thing, if possible
				if(data.getThingNodeId()>0) {
					Concept thingConc = closureServ.loadConceptById(data.getThingNodeId());
					Thing thing = new Thing();
					thing = boilerServ.loadThingByNode(thingConc, thing);
					if(thing.getID()>0) {
						ThingDoc tdoc = new ThingDoc();
						for(ThingDoc td : thing.getDocuments()) {
							if(td.getDictNode().getID()==data.getDictNodeId() 
									&& td.getVarName().toUpperCase().equalsIgnoreCase(data.getVarName())) {
								tdoc=td;
								break;
							}
						}
						tdoc.setConcept(node);
						tdoc.setDictNode(dictItem);
						tdoc.setDictUrl(data.getDictUrl());
						tdoc.setDocUrl(data.getUrl());
						tdoc.setVarName(data.getVarName());
						if(tdoc.getID()==0) {
							thing.getDocuments().add(tdoc);
						}
						thing=thingRepo.save(thing);
					}
				}
			}else {
				throw new ObjectNotFoundException("fileSave Access denied "+ user.getEmail()+"/"+data.getUrl(), logger);
			}
		}else {
			throw new ObjectNotFoundException("fileSave File is empty or eMAil/url/classifier is bad "
					+ user.getEmail()+"/"+data.getUrl()+"/"+data.getDictNodeId(), logger);
		}
		return data;
	}

	/**
	 * Determine file node ID from the stored
	 * @param data
	 * @return zero if new one
	 */
	public long fileNodeId(FileDTO data) {
		long fileNodeId=0;
		Long nodeIdL = data.getLinked().get(data.getDictNodeId());
		if(nodeIdL != null) {
			fileNodeId=nodeIdL;
		}
		return fileNodeId;
	}
	/**
	 * reload a table with files
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public FileDTO thingFiles(FileDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		data=createDocTable(data);
		return data;
	}
	/**
	 * download a file as a file :)
	 * @param nodeId	file concept id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ResponseEntity<Resource> fileDownload(long nodeId) throws ObjectNotFoundException {
		Concept fileNode = closureServ.loadConceptById(nodeId);
		Optional<FileResource> freso = fileRepo.findByConcept(fileNode);
		if(freso.isPresent()) {
			FileResource fres=freso.get();
			String fileName = fileNode.getLabel();
			Resource res = new ByteArrayResource(fres.getFile());

			String mediaType = fres.getMediatype();
			String typeOpen = "inline";
			if(mediaType == null || mediaType.length() == 0) {
				mediaType = "application/octet-stream";
				typeOpen = "attachment";
			}

			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(mediaType))
					.contentLength(fres.getFileSize())
					.header(HttpHeaders.CONTENT_DISPOSITION, typeOpen + "; filename=\"" + fileName +"\"")
					.header("filename", fileName)
					.body(res);
		}else {
			throw new ObjectNotFoundException(" load. File not found. Node id is "+fileNode.getID());
		}
	}
	/**
	 *Any ThingDTO at any given moment may has only one auxiliary dynamic path
	 *This path represents a loop in a main path to add/edit multiply things
	 *The auxiliary path is differ from the main path, because it calculates dynamically, depends of a user's choice
	 * For current, the auxiliary path is possible only for Person  
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO auxPath(ThingDTO data) throws ObjectNotFoundException {
		if(data.getNodeId()>0) {
			//create/load a core thing for auxiliary path (only for persons yet)
			PersonDTO pdto = data.getPersons().get(data.getAuxPathVar());
			if(pdto != null) {
				data=auxPathPerson(pdto,data);
			}
			return data;
		}else {
			throw new ObjectNotFoundException("auxPath. Thing node ID is ZERO", logger);
		}
	}

	/**
	 * 
	 * @param pdto
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ThingDTO auxPathPerson(PersonDTO personDTO, ThingDTO data) throws ObjectNotFoundException {
		//load a thing
		Concept node = closureServ.loadConceptById(data.getNodeId());
		Thing thing = boilerServ.loadThingByNode(node);
		//try to found selected items in the dictionary
		List<Long> selected = new ArrayList<Long>();
		for(ThingDict tdict : thing.getDictionaries()) {
			//if(tdict.getUrl().equalsIgnoreCase(personDTO.getDictUrl())) {
			selected.add(tdict.getConcept().getID());
			//}
		}
		//load a core thing in auxiliary path
		if(selected.size()<=1) {
			long dictNodeId=0;
			if(selected.size()==1) {
				dictNodeId=selected.get(0);
			}
			//core ThingDTO
			AssemblyDTO coreAssembly = assemblyServ.auxPathConfig(data, dictNodeId,data.getAuxPathVar());
			ThingDTO coreDTO= ThingDTO.createIncluded(data, coreAssembly);
			coreDTO.setParentId(data.getNodeId());
			coreDTO.setNodeId(personDTO.getNodeId());
			coreDTO.setTitle(messages.get(personDTO.getVarName()));
			coreDTO.setHistoryId(data.getHistoryId());
			coreDTO.setVarName(personDTO.getVarName());
			//calculate path and place it to auxiliary path of the thing
			List<ThingDTO> path = createPath(coreDTO, new ArrayList<ThingDTO>(),-1);
			data.getAuxPath().clear();
			data.getAuxPath().addAll(path);
			return data;
		}else {
			throw new ObjectNotFoundException("auxPath. Wrong dictionary selection. Only one is allowed "
					+ personDTO.getDictUrl()+"/"+selected.size());
		}
	}
	/**
	 * Aux data url
	 * @param data
	 * @param dicts
	 * @return empty string if not found
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public String  auxDataUrl(ThingDTO data, Set<String> dicts) throws ObjectNotFoundException {
		//default data url
		String auxDataUrl=assemblyServ.auxDataUrl(data.getUrl(), data.getAuxPathVar());
		//url from dictionary
		if(dicts.size()==1) {
			DictionaryDTO dict = data.getDictionaries().get(dicts.iterator().next());
			//get selection
			Set<Long>selected=dictServ.selectedItems(dict);
			if(selected.size()>0) {
				Concept dictNode=closureServ.loadConceptById(selected.iterator().next());
				String lit = literalServ.readValue("URL", dictNode);
				if(lit.length()>0) {
					auxDataUrl=lit;
				}
			}
		}
		return auxDataUrl;
	}


	/**
	 * Reload person's table
	 * @param data
	 * @param user
	 * @return
	 */
	public PersonDTO personTableLoad(PersonDTO data, UserDetailsDTO user) {
		data=createPersTable(data);
		return data;
	}
	/**
	 * Check access to thing, extract values
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ThingValuesDTO thingValuesExtract(UserDetailsDTO user, ThingDTO thing,  ThingValuesDTO data) throws ObjectNotFoundException {
		data.getLiterals().clear();
		for(String key :thing.getLiterals().keySet()) {
			data.getLiterals().put(key.toUpperCase(), thing.getLiterals().get(key).getValue());
		}
		data.getNumbers().clear();
		for(String key :thing.getNumbers().keySet()) {
			data.getNumbers().put(key.toUpperCase(), thing.getNumbers().get(key).getValue());
		}
		data.getDates().clear();
		for(String key :thing.getDates().keySet()) {
			data.getDates().put(key.toUpperCase(), thing.getDates().get(key).getValue());
		}
		data.getDictionaries().clear();
		for(String key :thing.getDictionaries().keySet()) {
			data.getDictionaries().put(key.toUpperCase(), dictionaryData(thing.getDictionaries().get(key)));
		}
		data.getAddresses().clear();
		for(String key :thing.getAddresses().keySet()) {
			data.getAddresses().put(key.toUpperCase(), addressData(thing.getAddresses().get(key)));
		}
		data.getPersonselection().clear();
		for(String key :thing.getPersonselector().keySet()) {
			Long selected=0l;
			for(TableRow row : thing.getPersonselector().get(key).getTable().getRows()) {
				if(row.getSelected()) {
					selected=row.getDbID();
					break;
				}
			}
			if(selected>0l) {
				data.getPersonselection().put(key,selected);
			}
		}
		data.getSchedulers().clear();
		for(String key :thing.getSchedulers().keySet()) {
			data.getSchedulers().put(key.toUpperCase(), thing.getSchedulers().get(key));
		}
		data.getRegisters().clear();
		for(String key :thing.getRegisters().keySet()) {
			data.getRegisters().put(key.toUpperCase(), thing.getRegisters().get(key));
		}
		data.setUrl(thing.getUrl());
		return data;
	}
	/**
	 * Extract data from a address
	 * @param addressDTO
	 * @return
	 */
	private AddressValuesDTO addressData(AddressDTO addressDTO) {
		AddressValuesDTO ret = new AddressValuesDTO();
		ret.setGisCoordinates(addressDTO.getMarker().gisLocation());
		ret.setAdminUnits(dictionaryData(addressDTO.getDictionary()));
		return ret;
	}
	/**
	 * Extract data from a dictionary
	 * @param dictionaryDTO
	 * @return
	 */
	private DictValuesDTO dictionaryData(DictionaryDTO dictionaryDTO) {
		DictValuesDTO ret = new DictValuesDTO();
		ret.getSelected().addAll(dictServ.selectedItems(dictionaryDTO));
		ret.setUrl(dictionaryDTO.getUrl());
		return ret;
	}

}
