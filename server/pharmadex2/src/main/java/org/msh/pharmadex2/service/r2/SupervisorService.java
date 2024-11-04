package org.msh.pharmadex2.service.r2;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.plaf.TableHeaderUI;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.enums.YesNoNA;
import org.msh.pdex2.model.i18n.ResourceBundle;
import org.msh.pdex2.model.i18n.ResourceMessage;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Closure;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.i18n.ResourceBundleRepo;
import org.msh.pdex2.repository.i18n.ResourceMessageRepo;
import org.msh.pdex2.repository.r2.ClosureRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AboutDTO;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataConfigDTO;
import org.msh.pharmadex2.dto.DataPreviewDTO;
import org.msh.pharmadex2.dto.DataUnitDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.MessageDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.RootNodeDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.TileDTO;
import org.msh.pharmadex2.dto.URLAssistantDTO;
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Services for supervisor
 * 
 * @author alexk
 *
 */
@Service
public class SupervisorService {
	private static final Logger logger = LoggerFactory.getLogger(SupervisorService.class);

	@Value("${pharmadex.country:NNN}")
	String currentCountry;
	@Autowired
	Messages messages;
	@Autowired
	DtoService dtoServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	DictService dictServ;
	@Autowired
	EntityService entityServ;
	@Autowired
	AssemblyService assemblyServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	ResourceMessageRepo resourceMessageRepo;
	@Autowired
	ResourceBundleRepo resourceBundleRepo;
	@Autowired
	UrlAssistantService assisServ;
	@Autowired
	private ClosureRepo closureRepo;

	@Value("${variables.properties.edit}")
	private boolean variablesPropertiesEdit;
	@Value("${spring.servlet.multipart.max-file-size}" )
	String maxFileSize;

	/**
	 * Create content for administrative tile, using existed supervisor features
	 * 
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public TileDTO adminitrativeTile(TileDTO ret) throws ObjectNotFoundException {
		// ret.getContent().clear();
		// ret.getContent().add(new FeatureStateDTO());
		return ret;
	}

	/**
	 * Create or load workflow configuration
	 * 
	 * @param user
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public WorkflowDTO workflowConfiguration(WorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if (data.getDictNodeId() > 0) {
			Concept dictNode = closureServ.loadConceptById(data.getDictNodeId());
			data.setTitle(literalServ.readPrefLabel(dictNode));
			String url = literalServ.readValue("applicationurl", dictNode);
			if (url.length() > 5) {
				Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
				List<ThingDTO> path = boilerServ.createActivitiesPath(firstActivity, new ArrayList<ThingDTO>());
				//set parents
				long parent=0;
				for(ThingDTO th: path) {
					th.setParentId(parent);
					parent=th.getNodeId();
				}
				data.getPath().clear();
				data.getPath().addAll(path);
			} else {
				throw new ObjectNotFoundException(
						"workflowConfiguration. Configuration url is wrong defined. It is " + url, logger);
			}
		} else {
			throw new ObjectNotFoundException("workflowConfiguration. Dictionary node id is zero", logger);
		}
		return data;
	}



	/**
	 * Create empty activity and add it to the end of path
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public WorkflowDTO workflowActivityAdd(WorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		ThingDTO dto = new ThingDTO();
		dto.setUrl(AssemblyService.ACTIVITY_CONFIGURATION);
		dto.setTitle(messages.get("newactivity"));
		dto.setNodeId(0);
		int lastPath = data.getPath().size() - 1;
		if (lastPath >= 0) {
			long parentId = data.getPath().get(lastPath).getNodeId();
			if (parentId > 0) {
				dto.setParentId(parentId);
				dto = thingServ.createContent(dto, user);
				data.getPath().add(dto);
				return data;
			} else {
				throw new ObjectNotFoundException("workflowActivityAdd. Parent activity is not defined. ", logger);
			}
		} else {
			throw new ObjectNotFoundException("workflowActivityAdd. Empty path", logger);
		}
	}

	/**
	 * Suspend the current activity
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public WorkflowDTO workflowActivitySuspend(WorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if (data.getSelected() >= 0) {
			ThingDTO selected = data.getPath().get(data.getSelected());
			Concept actConf = closureServ.loadConceptById(selected.getNodeId());
			actConf.setActive(false);
			actConf = closureServ.save(actConf);
			data.setSelected(0);
			data.getPath().clear();
			data = workflowConfiguration(data, user);
		}
		return data;
	}

	/**
	 * Load a table with data collections
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DataConfigDTO dataCollectionsLoad(DataConfigDTO data, String searchStr, String searchStrVars) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
		if (data.getTable().getHeaders().getHeaders().size() == 0) {
			data.getTable().setHeaders(headersDataCollections(data.getTable().getHeaders()));

			// только когда снова создаем заголовки, только тогда проверяем был ли текст в строке поиска
			if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
				for(TableHeader th:data.getTable().getHeaders().getHeaders()) {
					th.setGeneralCondition(searchStr);
				}
				data.getTable().setGeneralSearch(searchStr);
			}
		}else {
			//change page should deselect all
			if(data.getTable().getHeaders().getPage()!=data.getPageNo()) {
				data.setNodeId(0l);
				data.setVarNodeId(0l);
				data.setVarTable(new TableQtb());
			}
		}

		//load a table
		jdbcRepo.prepareDictionaryLevel(root.getID());
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from _dictlevel", "", "active=true", data.getTable().getHeaders());
		// set a page with the a currently selected object
		TableQtb.tablePage(rows, data.getTable());					//determine the size in pages
		int pages=data.getTable().getHeaders().getPages();
		int pageSize=data.getTable().getHeaders().getPageSize();
		int pageNo=0;
		for(int page=1;page<=pages;page++) {
			List<TableRow> rows1=TableHeader.fetchPage(rows, page, pageSize);
			for(TableRow row : rows1) {
				if(row.getDbID()==data.getNodeId()) {
					row.setSelected(true);
					pageNo=page;
					data.getTable().getHeaders().setPage(page);
				}
			}
		}
		TableQtb.tablePage(rows, data.getTable());				//set the right page
		// reload variables table, if some selected
		data = dataCollectionVariablesLoad(data, searchStrVars);
		data.setPageNo(data.getTable().getHeaders().getPage());
		return data;
	}

	/**
	 * Create headers for data collection table
	 * 
	 * @param headers
	 * @return
	 */
	private Headers headersDataCollections(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders()
		.add(TableHeader.instanceOf("identifier", "url", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("pref", "description", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.setPageSize(20);
		headers.getHeaders().get(0).setSort(true);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}

	/**
	 * Load a definition of data collection
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DataCollectionDTO dataCollectionDefinitionLoad(DataCollectionDTO data) throws ObjectNotFoundException {
		if (data.getNodeId() > 0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			//check if it is possible to change the data url
			List<Concept>cons=closureServ.loadAllConceptsByIdentifier(node.getIdentifier());
			if(cons.size()>0) {
				for(Concept c:cons) {
					if(closureServ.getParent(c)==null) {
						if(closureRepo.findByChild(c).size()>0) {
							data.getUrl().setAssistant(AssistantEnum.NO);
						};
					}
				}
			}		
			data.getUrl().setValue(node.getIdentifier());
			data.getDescription().setValue(literalServ.readPrefLabel(node));
		}
		return data;
	}

	/**
	 * Save a collection data definition
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DataCollectionDTO dataCollectionDefinitionSave(DataCollectionDTO data) throws ObjectNotFoundException {
		data = validServ.dataCollection(data);
		if (data.isValid()) {
			Concept node = new Concept();
			if (data.getNodeId() > 0) {
				node = closureServ.loadConceptById(data.getNodeId());
			}
			node.setIdentifier(data.getUrl().getValue());
			Concept root = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
			node = closureServ.saveToTree(root, node);
			literalServ.createUpdatePrefLabel(data.getDescription().getValue(), node);
			literalServ.createUpdateDescription("", node);
			data.setNodeId(node.getID());
		}
		return data;
	}
	//autoCreate configuration for resource
	public DataCollectionDTO createConfigResource(String url, String dictUrl) throws ObjectNotFoundException {
		DataCollectionDTO conf=new DataCollectionDTO();
		conf.getUrl().setValue("configuration."+url);
		conf.getDescription().setValue(url.replaceAll("."," "));
		conf=dataCollectionDefinitionSave(conf);
		DataVariableDTO item= new DataVariableDTO();
		item.setNodeId(conf.getNodeId());
		item.getPublicavailable().setValue(dtoServ.enumToOptionDTO(YesNoNA.NO, YesNoNA.values()));
		item.getHidefromapplicant().setValue(dtoServ.enumToOptionDTO(YesNoNA.NO, YesNoNA.values()));
		item.getVarName().setValue("item");
		item.getRow().setValue(0l);
		item.getCol().setValue(0l);
		item.getOrd().setValue(0l);
		item.getClazz().getValue().setCode("documents");
		item.getMinLen().setValue(0l);
		item.getMaxLen().setValue(0l);
		item.getRequired().setValue(dtoServ.enumToOptionDTO(YesNoNA.YES, YesNoNA.values()));
		item.getMult().setValue(dtoServ.enumToOptionDTO(YesNoNA.NO, YesNoNA.values()));
		item.getUrl().setValue("resources.files");
		item.getDictUrl().setValue(dictUrl);
		item.getReadOnly().setValue(dtoServ.enumToOptionDTO(YesNoNA.NO, YesNoNA.values()));
		item.getUnique().setValue(dtoServ.enumToOptionDTO(YesNoNA.NO, YesNoNA.values()));
		item.getPrefLabel().setValue(dtoServ.enumToOptionDTO(YesNoNA.NO, YesNoNA.values()));
		item.getFileTypes().setValue("");
		item=dataCollectionVariableSave(item, true);
		return conf;
	}

	public ResourceDTO resurceDefinitionSave(ResourceDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		String url=data.getUrl().getValue();
		RootNodeDTO dict=new RootNodeDTO();
		dict=dictServ.createDictResource(url);
		if(dict.isValid()) {
			DataCollectionDTO conf=new DataCollectionDTO();
			conf=createConfigResource(url,dict.getUrl().getValue());
			data.getConfigUrl().setValue(conf.getUrl().getValue());
			data.getDictUrl().setValue(dict.getUrl().getValue());
			data = resourceDefinitionSave(data);
		}else {
			data.addError(dict.getUrl().getValue()+" - "+messages.get("error_dict_url"));
		}
		return data;
	}

	/**
	 * Suspend data definition
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DataCollectionDTO dataCollectionDefinitionSuspend(DataCollectionDTO data) throws ObjectNotFoundException {
		if (data.getNodeId() > 0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			node.setActive(false);
			node = closureServ.save(node);
		}
		return data;
	}

	/**
	 * Load variables for a data collection given
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DataConfigDTO dataCollectionVariablesLoad(DataConfigDTO data, String searchStr) throws ObjectNotFoundException {
		data.setRestricted(true);
		if (data.getNodeId() > 0) {
			//URL
			Concept node = closureServ.loadConceptById(data.getNodeId());
			data.setUrl(node.getIdentifier());
			// variables
			TableQtb table = data.getVarTable();
			if (table.getHeaders().getHeaders().size() == 0) {
				table.setHeaders(headersVariables(table.getHeaders()));
				// только когда снова создаем заголовки, только тогда проверяем был ли текст в строке поиска
				if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
					for(TableHeader th:table.getHeaders().getHeaders()) {
						th.setGeneralCondition(searchStr);
					}
					table.setGeneralSearch(searchStr);
				}
			}
			String where = "p.Active=true and p.nodeID='" + data.getNodeId() + "' and p.lang='"
					+ LocaleContextHolder.getLocale().toString().toUpperCase() + "'";
			String select="select * from(select distinct av.*, c.Label as 'ext' from assm_var av "
					+ "join assembly a on a.ID=av.assemblyID join concept c on c.ID=a.conceptID) p";
			List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
			TableQtb.tablePage(rows, table);
			table.setSelectable(false);
			// is edit restricted?
			if(!variablesPropertiesEdit) {
				Concept conc = closureServ.loadConceptById(data.getNodeId());
				jdbcRepo.data_url_references(conc.getIdentifier());
				List<TableRow> rows1 = jdbcRepo.qtbGroupReport("select * from data_url_references", "", "", new Headers());
				data.setRestricted(rows1.size()==0);	//at least one reference is existed
			}else{
				data.setRestricted(true);
			}
		}
		return data;
	}

	private Headers headersVariables(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf("row", "row", true, true, true, TableHeader.COLUMN_LONG, 0));
		headers.getHeaders().add(TableHeader.instanceOf("col", "col", true, true, true, TableHeader.COLUMN_LONG, 0));
		headers.getHeaders().add(TableHeader.instanceOf("ord", "order", true, true, true, TableHeader.COLUMN_LONG, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("propertyName", "variables", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("ext", "ext", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("clazz", "clazz", true, true, true, TableHeader.COLUMN_STRING, 0));

		headers.getHeaders()
		.add(TableHeader.instanceOf("pref", "description", true, true, true, TableHeader.COLUMN_STRING, 0));

		TableHeader h = headers.getHeaders().get(0);
		h.setSort(true);
		h.setSortValue(TableHeader.SORT_ASC);
		TableHeader h1 = headers.getHeaders().get(1);
		h1.setSort(true);
		h1.setSortValue(TableHeader.SORT_ASC);
		TableHeader h2 = headers.getHeaders().get(2);
		h2.setSort(true);
		h2.setSortValue(TableHeader.SORT_ASC);
		headers = boilerServ.translateHeaders(headers);
		headers.setPageSize(100);
		return headers;
	}

	/**
	 * Load a variable configuration
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DataVariableDTO dataCollectionVariableLoad(DataVariableDTO data) throws ObjectNotFoundException {
		if (data.getNodeId() > 0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			if (data.getVarNodeId() > 0) {
				Concept varNode = closureServ.loadConceptById(data.getVarNodeId());
				Assembly assm = boilerServ.assemblyByVariable(varNode, true);
				data = dtoServ.assembly(assm, node, varNode, data);
			}else {
				data=dtoServ.initializeLogical(data);
				data=dtoServ.initializeClazz(data);
			}
			return data;
		} else {
			throw new ObjectNotFoundException("dataCollectionVariableLoad. Node ID is ZERO", logger);
		}
	}





	/**
	 * Verify and save a definition of variable
	 * 30012024 khomka
	 * в импорте нужен strict=false
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DataVariableDTO dataCollectionVariableSave(DataVariableDTO data, boolean strict) throws ObjectNotFoundException {
		if (data.getNodeId() > 0) {
			data.setStrict(strict);
			if (data.getClazz().getValue().getCode().equalsIgnoreCase("things")) {
				data.getRow().setValue(100l);
				data.getCol().setValue(100l);
			}
			data = validServ.variable(data, strict, false);
			if(data.isValid() || !data.isStrict()) {
				// save a node
				Concept node = new Concept();
				if (data.getVarNodeId() > 0) {
					node = closureServ.loadConceptById(data.getVarNodeId());
				}

				node.setIdentifier(data.getVarName().getValue().trim()+data.getVarNameExt().getValue());	//deprecated 2023-01-13
				node.setLabel(data.getVarNameExt().getValue().trim()); //deprecated 2023-01-1

				Concept root = closureServ.loadConceptById(data.getNodeId());
				node = closureServ.saveToTree(root, node);
				node = literalServ.prefAndDescription(data.getDescription().getValue(),
						data.getDescription().getValue(), node);
				// save a table record так (node, false) и было до 30012024
				Assembly assm = boilerServ.assemblyByVariable(node, false);
				assm = entityServ.assembly(data, node, assm);
				assm = boilerServ.assemblySave(assm);
			}
			return data;
		} else {
			throw new ObjectNotFoundException("dataCollectionVariableSave. Data collection node id is ZERO", logger);
		}
	}

	/**
	 * Preview a thing created from data definition
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public DataPreviewDTO dataCollectionDefinitionPreview(DataPreviewDTO data, UserDetailsDTO user)
			throws ObjectNotFoundException {
		if (data.getNodeId() > 0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			data.getThing().setUrl(node.getIdentifier());
			data.getThing().setReadOnly(false);
			data.getThing().setTitle(messages.get("preview"));
			data.setThing(thingServ.createContent(data.getThing(), user));
		} else {
			throw new ObjectNotFoundException("dataCollectionDefinitionPreview. Node ID is ZERO", logger);
		}
		return data;
	}

	/**
	 * Suspend a data variable
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public DataVariableDTO dataCollectionVariableSuspend(DataVariableDTO data) throws ObjectNotFoundException {
		if (data.getNodeId() > 0 && data.getVarNodeId() > 0) {
			Concept conc = closureServ.loadConceptById(data.getVarNodeId());
			conc.setActive(false);
			conc = closureServ.save(conc);
		}
		return data;
	}


	/**
	 * Save a definition of the resource
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ResourceDTO resourceDefinitionSave(ResourceDTO data) throws ObjectNotFoundException {
		data = validServ.resourceDefinition(data, true);
		if (data.isValid()) {
			Concept root = closureServ.loadRoot("configuration.resources");
			String localeStr = LocaleContextHolder.getLocale().toString();
			if (data.getNodeId() == 0) {
				// prepare resources for all languages, however return a reference for the
				// current one
				List<String> langs = messages.getAllUsedUpperCase();
				String currentLang = localeStr.toUpperCase();
				for (String lang : langs) {
					Concept resDef = resourceDefinitionCreate(data, root, lang);
					/* khomka 21082023 | 01.09.2023 ika
					 * error open new Resources
					 * if present Thing - OK, if NO - setNodeId(0) - he is create*/
					Thing th = new Thing();
					th = boilerServ.thingByNode(resDef, th);
					if(th.getID() == 0) {
						th.setUrl(resDef.getIdentifier());
						th.setConcept(resDef);
						th = boilerServ.saveThing(th);				
					}
					if (lang.equalsIgnoreCase(currentLang)) {
						data.setNodeId(resDef.getID());
					}
				}

				//create configuration data resource 29.08.2023 ik
				root = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
				List<Concept> concepts=literalServ.loadOnlyChilds(root);
				Concept ret= new Concept();
				for(Concept c:concepts) {
					if(c.getIdentifier().equalsIgnoreCase(data.getConfigUrl().getValue())) {
						ret=c;
					}
				}
				if(ret.getID() == 0 ) {
					DataCollectionDTO config=new DataCollectionDTO();
					config.setUrl(data.getConfigUrl());
					config.setDescription(data.getConfigUrl());
					config=dataCollectionDefinitionSave(config);
					DataVariableDTO dataConfig =new DataVariableDTO();
					dataConfig.setNodeId(config.getNodeId());
					dataConfig.getVarName().setValue("templates");
					dataConfig.getClazz().getValue().setCode("documents");
					dataConfig.getRequired().getValue().setCode("Yes");
					dataConfig.setUrl(data.getConfigUrl());
					dataConfig.getDictUrl().setValue("dictionary."+data.getUrl());
					dataCollectionVariableSave(dataConfig, true);
				}else {
					if(!ret.getActive()) {
						ret.setActive(true);
						ret=closureServ.save(ret);
					}
					List<Assembly> datas = assemblyServ.loadDataConfiguration(data.getConfigUrl().getValue());
					if(!datas.isEmpty()) {
						List<Assembly> doc=new ArrayList<Assembly>();
						List<Assembly> hea=new ArrayList<Assembly>();
						for(Assembly d:datas) {
							if(d.getClazz().equalsIgnoreCase("documents")){
								doc.add(d);
							}else if(d.getClazz().equalsIgnoreCase("heading")) {
								hea.add(d);
							}
							if(doc.size()==0 || doc.size()>1 || doc.size()+hea.size()!=datas.size()) {
								data.addError(messages.get("errorConfigDataResource"));
								logger.error(messages.get("errorConfigDataResource"));
							}
						}//
					}else {
						//data.addError(messages.get("errorConfigDataResource"));
						DataVariableDTO dataConfig =new DataVariableDTO();
						dataConfig.setNodeId(ret.getID());
						dataConfig.getVarName().setValue("templates");
						dataConfig.getClazz().getValue().setCode("documents");
						dataConfig.getRequired().getValue().setCode("Yes");
						dataConfig.setUrl(data.getConfigUrl());
						dataConfig.getDictUrl().setValue("dictionary."+data.getUrl());
						dataCollectionVariableSave(dataConfig, true);
					}
				}
				//create dictionary resource 29/0/2023
				ret = closureServ.loadConceptByIdentifier("dictionary."+data.getUrl());
				if(ret == null ) {
					String nameDict= data.getUrl().getValue().replace(".", " ");
					RootNodeDTO dict=new RootNodeDTO();
					dict.getPrefLabel().setValue(nameDict);
					dict.getUrl().setValue("dictionary."+data.getUrl());
					dictServ.rootNodeSave(dict);
				}
			} else {
				// url may be changed :(
				Concept resDef = closureServ.loadConceptById(data.getNodeId());
				resDef.setIdentifier(data.getUrl().getValue()); // identifier is URL
				resDef.setLabel(data.getConfigUrl().getValue()); // label is a data configuration url
				resDef = literalServ.prefAndDescription(data.getUrl().getValue(), data.getDescription().getValue(),
						resDef);
				Concept lang = closureServ.saveToTree(root, localeStr.toUpperCase());
				resDef = closureServ.saveToTree(lang, resDef);
				data.setNodeId(resDef.getID());

				/* khomka 21082023 
				 * error open new Resources
				 * if present Thing - OK, if NO - setNodeId(0) - he is create
				 */
				Thing th = new Thing();
				th = boilerServ.thingByNode(resDef, th);
				if(th.getID() > 0) {
					th.setUrl(resDef.getIdentifier());
					th = boilerServ.saveThing(th);
				}else if(th.getID() == 0) {
					th.setUrl(resDef.getIdentifier());
					th.setConcept(resDef);
					th = boilerServ.saveThing(th);
				}
			}
		}
		return data;
	}

	/**
	 * Create a definition of the resource
	 * 
	 * @param data
	 * @param root
	 * @param localeStr
	 * @throws ObjectNotFoundException
	 */
	private Concept resourceDefinitionCreate(ResourceDTO data, Concept root, String localeStr)
			throws ObjectNotFoundException {
		Concept lang = closureServ.saveToTree(root, localeStr.toUpperCase());
		Concept resDef = closureServ.saveToTree(lang, data.getUrl().getValue());
		resDef.setLabel(data.getConfigUrl().getValue());
		resDef = closureServ.save(resDef);
		resDef = literalServ.prefAndDescription(data.getDescription().getValue(), data.getDescription().getValue(),
				resDef);
		return resDef;
	}

	/**
	 * Load a list of resources on this language
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ResourceDTO resourcesLoad(ResourceDTO data) throws ObjectNotFoundException {
		// load a table
		TableQtb table = data.getTable();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(headerResources(table.getHeaders()));
		}
		for (TableRow row : table.getRows()) {
			if (row.getSelected()) {
				if (data.getNodeId() > 0) {
					data.setSelected(row.getDbID());
				}else {
					data.setSelected(0l);
				}
				break;
			}
		}
		String mainWhere = "lang='" + LocaleContextHolder.getLocale().toString().toUpperCase() + "'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from resources", "", mainWhere, table.getHeaders());
		TableQtb.tablePage(rows, table);
		for (TableRow row : table.getRows()) {
			if (row.getDbID() == data.getSelected()) {
				row.setSelected(true);
			} else {
				row.setSelected(false);
			}
		}
		// load data if one
		if (data.getNodeId() > 0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			data=resourceDtoByNode(data, node);
		} else {
			data.getUrl().setValue("");
			data.getDescription().setValue("");
			data.getConfigUrl().setValue("");
			data.getDictUrl().setValue("");
		}
		data.getTable().getHeaders().setPageSize(200);
		return data;
	}

	/**
	 * load resource definition
	 * @param data
	 * @param node
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ResourceDTO resourceDtoByNode(ResourceDTO data, Concept node) throws ObjectNotFoundException {
		data.getUrl().setValue(node.getIdentifier());
		data.getConfigUrl().setValue(node.getLabel());
		data.getDescription().setValue(literalServ.readDescription(node));
		List<Assembly> allAssms = assemblyServ.loadDataConfiguration(node.getLabel(),"documents");
		String urlDict="";
		for(Assembly clazz : allAssms) {
			if(clazz.getClazz().equalsIgnoreCase("documents")) {
				if(clazz.getUrl()!=null || clazz.getUrl().length()>0) {
					urlDict=clazz.getDictUrl();
				}
				/*	data.getDictUrl().setValue(clazz.getDictUrl());
				}else {
					data.addError(messages.get("errorConfigDataResource"));
				}
				break;*/
			}
		}
		data.getDictUrl().setValue(urlDict);
		if(urlDict.isEmpty()){
			data.addError(messages.get("errorConfigDataResource"));
		}
		return data;
	}

	/**
	 * Headers for resources table
	 * 
	 * @param headers
	 * @return
	 */
	private Headers headerResources(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders()
		.add(TableHeader.instanceOf("url", "dataurl", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders().add(
				TableHeader.instanceOf("description", "description", true, true, true, TableHeader.COLUMN_STRING, 0));
		boilerServ.translateHeaders(headers);
		headers.setPageSize(200);
		return headers;
	}

	/**
	 * Put right node id and url to the thing definition
	 * 
	 * @param data
	 * @param user 
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws JsonProcessingException 
	 */
	@Transactional
	public ThingDTO resourceThingPrepare(ResourceDTO data, UserDetailsDTO user) throws ObjectNotFoundException, JsonProcessingException {
		if (data.getNodeId() > 0) {
			ThingDTO ret = new ThingDTO();
			Concept node = closureServ.loadConceptById(data.getNodeId());
			ret.setVarName(node.getIdentifier());
			ret.setNodeId(node.getID());
			ret.setUrl(node.getLabel());
			Thing th=boilerServ.thingByNode(node, new Thing());
			if(th.getID()==0) {
				th.setConcept(node);
				th.setUrl(node.getLabel());
				th=boilerServ.saveThing(th);
				node.setActive(true);
				closureServ.save(node);
			}
			return ret;
		} else {
			throw new ObjectNotFoundException("resourceThingPrepare. Node ID is ZERO", logger);
		}
	}
	@Transactional
	public DictionaryDTO resourceDictPrepare(ThingDTO data) throws ObjectNotFoundException {
		//get dictionary this resource
		DictionaryDTO dict= new DictionaryDTO();
		if(data.getUrl()!=null && !data.getUrl().isEmpty()) {
			List<Assembly> allAssms = assemblyServ.loadDataConfiguration(data.getUrl(),"documents");
			String urlDict="";
			for(Assembly clazz : allAssms) {
				if(clazz.getClazz().equalsIgnoreCase("documents")) {
					if(clazz.getUrl()!=null || clazz.getUrl().length()>0) {
						urlDict=clazz.getDictUrl();
					}else {
						dict.addError(messages.get("errorConfigDataResource"));
						return dict;
					}
				}
			}
			if(!urlDict.isEmpty()) {
				dict.setUrl(urlDict);
				dict=dictServ.createDictionary(dict);
				return dict;
			}else {
				dict.addError(messages.get("errorConfigDataResource"));
				return dict;
			}
		}else {
			dict.addError(messages.get("errorConfigDataResource"));
			return dict;
		}
	}
	/**
	 * Save a resource using thing - specific methods
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO resourceSave(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		data = validServ.resource(data, true);
		if (data.isValid()) {
			if (data.getNodeId() > 0 && data.getUrl().length() > 0) {
				Concept node = closureServ.loadConceptById(data.getNodeId());
				Thing thing = new Thing();
				thing = boilerServ.thingByNode(node, thing);
				thing.setConcept(node);
				thing.setUrl(data.getVarName());
				// store data under the node and thing
				data = thingServ.storeDataUnderThing(data, user, node, thing);
				thing = boilerServ.saveThing(thing);
			} else {
				throw new ObjectNotFoundException("resourceSave. Node ID and/or url are(is) undefined", logger);
			}
		}
		return data;
	}

	/**
	 * Suspend the definition of this resource
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ResourceDTO resourceDefinitionSuspend(ResourceDTO data) throws ObjectNotFoundException {
		if (data.getNodeId() > 0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			node.setActive(false);
			node = closureServ.save(node);
			return data;
		} else {
			throw new ObjectNotFoundException("resourceSuspend. Node ID is ZERO", logger);
		}
	}

	public MessageDTO messagesLoad(MessageDTO data) throws ObjectNotFoundException {
		data.getValues().clear();
		String keyMessage = "";

		Optional<ResourceMessage> opt = resourceMessageRepo.findById(data.getSelected());
		if (opt.isPresent()) {
			ResourceMessage rm = opt.get();
			keyMessage = rm.getMessage_key();
		}
		data.getRes_key().setValue(keyMessage);

		List<ResourceBundle> bundles = resourceBundleRepo.findAllByOrderBySortOrder();
		if (bundles != null) {
			for (ResourceBundle rb : bundles) {
				String keyLocale = rb.getLocale().toUpperCase();
				int idrb = new Long(rb.getId()).intValue();
				List<ResourceMessage> list = resourceMessageRepo.findByMessage_key(keyMessage, idrb);
				if (list != null && list.size() > 0) {
					ResourceMessage rmRB = list.get(0);
					data.getValues().put(keyLocale, new FormFieldDTO<String>(rmRB.getMessage_value()));
					data.getSelectedIds().put(keyLocale, new Long(rmRB.getId()));
				} else {
					data.getValues().put(keyLocale, new FormFieldDTO<String>(""));
					data.getSelectedIds().put(keyLocale, new Long(0));
				}
			}
		}
		loadMessagesTable(data);
		// max file size for upload lost messages 
		String s = "1";
		if(maxFileSize.length() > 2) {
			s = maxFileSize.substring(0, maxFileSize.length() - 2);
		}
		Long maxsize = new Long(s) * 1048576l;
		data.setMaxFileSize(maxsize);
		return data;
	}

	private MessageDTO loadMessagesTable(MessageDTO data) {
		// load a table
		TableQtb table = data.getTable();
		table.setSelectable(false);
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(headerMessages(table.getHeaders()));
		}

		String mainWhere = "";
		ResourceBundle curRB = messages.getCurrentBundle();
		if (curRB != null) {
			mainWhere = " key_bundle=" + curRB.getId();
		}
		if (data.getSearch().getValue() != null && data.getSearch().getValue().length() > 0) {
			mainWhere += (mainWhere.isEmpty() ? "" : " and ") + " message_key like '%" + data.getSearch().getValue()
					+ "%'";
		}

		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from resource_message", "", mainWhere,
				table.getHeaders());
		TableQtb.tablePage(rows, table);

		return data;
	}

	@Transactional
	public MessageDTO messagesSave(MessageDTO data) throws ObjectNotFoundException {
		data = validServ.message(data);
		if (data.isValid()) {
			String key = data.getRes_key().getValue();

			List<ResourceBundle> bundles = resourceBundleRepo.findAllByOrderBySortOrder();
			if (bundles != null) {
				for (ResourceBundle rb : bundles) {
					int idrb = (new Long(rb.getId())).intValue();
					String value = data.getValues().get(rb.getLocale().toUpperCase()).getValue();

					ResourceMessage rm = null;
					List<ResourceMessage> list = resourceMessageRepo.findByMessage_key(key, idrb);
					if (list != null && list.size() > 0) {
						rm = list.get(0);
						if (list.size() == 1){  // && rm.getId() == data.getSelectedIds().get(rb.getLocale().toUpperCase())) {
							// это редактирование записи
							rm.setMessage_key(key);
							rm.setMessage_value(value);
							resourceMessageRepo.save(rm);
						} else {
							data.setValid(false);
							data.setIdentifier(messages.get("dublicatekey"));
							break;
						}
					} else {
						rm = new ResourceMessage();
						rm.setMessage_key(key);
						rm.setMessage_value(value);

						resourceMessageRepo.save(rm);
						rb.getMessages().add(rm);
						resourceBundleRepo.save(rb);
					}
				}
			}

		}
		return data;
	}

	/**
	 * Headers for resources table
	 * 
	 * @param headers
	 * @return
	 */
	private Headers headerMessages(Headers headers) {
		headers.getHeaders().clear();
		headers.setPageSize(50);
		headers.getHeaders()
		.add(TableHeader.instanceOf("message_key", "res_key", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders().add(
				TableHeader.instanceOf("message_value", "res_value", true, true, true, TableHeader.COLUMN_LINK, 0));
		boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}

	/**
	 * Duplicate the data configuration to the url old_data_configuration.copy
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DataCollectionDTO dataCollectionDefinitionDuplicate(DataCollectionDTO data) throws ObjectNotFoundException {
		// load an "old" collection
		data = dataCollectionDefinitionLoad(data);
		List<DataVariableDTO> variables = new ArrayList<DataVariableDTO>();
		DataConfigDTO datas = new DataConfigDTO();
		datas.setNodeId(data.getNodeId());
		datas = dataCollectionVariablesLoad(datas, "");
		for (TableRow row : datas.getVarTable().getRows()) {
			DataVariableDTO dvar = new DataVariableDTO();
			dvar.setNodeId(datas.getNodeId());
			dvar.setVarNodeId(row.getDbID());
			dvar = dataCollectionVariableLoad(dvar);
			variables.add(dvar);
		}
		// duplicate a root definition
		data.setNodeId(0);
		String url = data.getUrl().getValue();
		data.getUrl().setValue(url + ".copy");
		data.setUrl(data.getUrl());
		data = dataCollectionDefinitionSave(data);
		// new root
		datas.setNodeId(data.getNodeId());
		datas = dataCollectionVariablesLoad(datas, "");
		// duplicate a collection
		for (DataVariableDTO dv : variables) {
			dv.setNodeId(data.getNodeId());
			dv.setVarNodeId(0);
			dv = dataCollectionVariableSave(dv, true);
		}

		return data;
	}

	/**
	 * Insert an activity before the current. Works differently for the first
	 * activity
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public WorkflowDTO workflowActivityInsert(WorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		int selected = data.getSelected();
		if (selected > -1 && selected < data.getPath().size()) {
			ThingDTO thing = data.getPath().get(selected);
			Concept node = closureServ.loadConceptById(thing.getNodeId());
			Concept parent = closureServ.getParent(node);
			if (parent == null) {
				data = insertRootActivity(node, data, user);
			} else {
				data = insertActivityBetween(parent, node, data, user);
			}
		} else {
			throw new ObjectNotFoundException("workflowActivityInsert Bad selection " + selected, logger);
		}
		return data;
	}

	/**
	 * Insert an activity between parent and node
	 * 
	 * @param parent
	 * @param node
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public WorkflowDTO insertActivityBetween(Concept parent, Concept node, WorkflowDTO data, UserDetailsDTO user)
			throws ObjectNotFoundException {
		// node to insert
		Instant instant = Instant.now();
		long timeStampMillis = instant.toEpochMilli();
		Concept iNode = closureServ.loadRoot(timeStampMillis + "");
		// move whole tree from the node to the new node
		jdbcRepo.moveSubTree(node, iNode);
		// move result tree to the parent
		jdbcRepo.moveSubTree(iNode, parent);
		// restore the path
		data.getPath().clear();
		data = workflowConfiguration(data, user);
		return data;
	}

	/**
	 * Insert root activity
	 * 
	 * @param thing
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public WorkflowDTO insertRootActivity(Concept root, WorkflowDTO data, UserDetailsDTO user)
			throws ObjectNotFoundException {
		// create new root
		String url = root.getIdentifier();
		root.setIdentifier(root.getID() + "");
		root = closureServ.save(root);
		Concept newRoot = closureServ.loadRoot(url);
		// move whole tree to the new root
		jdbcRepo.moveSubTree(root, newRoot);
		// restore the path
		data.getPath().clear();
		data = workflowConfiguration(data, user);
		return data;
	}
	/**
	 * Add lost messages to resource_messages from xlsx file
	 * The structure of each row in this file
	 * @param inputStream
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public AboutDTO messagesLostAppend(InputStream inputStream, AboutDTO data) throws ObjectNotFoundException {
		XSSFWorkbook book;
		try {
			book = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = boilerServ.getSheetAt(book, 0);
			//determine column-resourcebundleID
			Map<Integer, ResourceBundle> bundles = messagesLostHeader(sheet.getRow(1));	//column number, bundle
			// scan data rows and store results to resource_messages
			int rowNo=2;
			XSSFRow row = sheet.getRow(rowNo);
			while(row != null) {
				List<String> values = new ArrayList<String>();
				for(Integer colNo : bundles.keySet()) {
					String value=boilerServ.getStringCellValue(row, colNo);
					if(value!=null && !value.isEmpty()) {
						values.add(value);
					}else {
						// only full defined rows are allowed
						values.clear();
						break;
					}
				}
				//store a row
				String key=boilerServ.getStringCellValue(row, 0);
				if(key!=null && values.size()==bundles.keySet().size()) {
					for(Integer colNo :bundles.keySet()) {
						messagesLostAddToBundle(key,values.get(colNo-1),bundles.get(colNo));
					}
				}
				rowNo++;
				row = sheet.getRow(rowNo);
			}
			//store bundles
			for(Integer key : bundles.keySet()) {
				ResourceBundle bundle = bundles.get(key);
				resourceBundleRepo.save(bundle);
			}
			messages.getMessages().clear();
			messages.loadLanguages();
		} catch (IOException e) {
			data.setIdentifier(messages.get("error_bmpFile"));
		}
		return data;
	}
	/**
	 * Store a message for a key, bundle pair
	 * @param key
	 * @param value
	 * @param bundle
	 */
	@Transactional
	public void messagesLostAddToBundle(String key, String value, ResourceBundle bundle) {
		ResourceMessage mess = new ResourceMessage();
		List<ResourceMessage> messlist=resourceMessageRepo.findByMessage_key(key, bundle.getId());
		if(messlist.size()!=0) {
			mess=messlist.get(0);
		}
		mess.setMessage_key(key);
		mess.setMessage_value(value);
		resourceMessageRepo.save(mess);
		if(messlist.size()==0) {
			bundle.getMessages().add(mess);
		}
	}

	/**
	 * decode bundles from headers
	 * @param row
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<Integer, ResourceBundle> messagesLostHeader(XSSFRow row) throws ObjectNotFoundException {
		Map<Integer, ResourceBundle> ret = new HashMap<Integer, ResourceBundle>();
		if(row!=null) {
			//prepare bundles
			Map<String, ResourceBundle> bundleLangs = new HashMap<String, ResourceBundle>();
			List<ResourceBundle> bundles = resourceBundleRepo.findAllByOrderBySortOrder();
			for(ResourceBundle bundle : bundles) {
				bundleLangs.put(bundle.getLocale().toUpperCase(), bundle);
			}
			//get result
			Long column=1l;
			String localeName=boilerServ.getStringCellValue(row, column.intValue());
			while(localeName != null) {
				ResourceBundle bundle = bundleLangs.get(localeName.toUpperCase());
				if(bundle!=null) {
					ret.put(column.intValue(), bundle);
				}else {
					throw new ObjectNotFoundException("messagesLostBundles. Bundle for locale "+localeName.toUpperCase() 
					+" not found",logger);
				}
				column++;
				localeName=boilerServ.getStringCellValue(row, column.intValue());
			}
		}else {
			throw new ObjectNotFoundException("messagesLostBundles. Headers not defined",logger);
		}
		return ret;
	}
	/**
	 * Get data configuratuion node id by data configuration url
	 * @param data
	 * @return nodeId in the data.nodeID or -1 in the nodeId if configuration is not found
	 */
	@Transactional
	public DictNodeDTO dataConfigNodeIdByUrl(DictNodeDTO data) {
		String select = "SELECT ID \r\n" + 
				"FROM dataconfig_all dc";
		String where="dc.Identifier='"+data.getUrl()+"'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, new Headers());
		if(rows.size()==1) {
			data.setNodeId(rows.get(0).getDbID());
		}else {
			data.setNodeId(-1l);
		}
		return data;
	}
	@Transactional
	public DictNodeDTO dataResourceNodeIdByUrl(DictNodeDTO data) throws ObjectNotFoundException {
		data.setNodeId(-1l);
		//in the old resources for some languages the resource definition may not exist
		String select = "SELECT ID \r\n" + 
				"FROM resources dc";
		String where="dc.url='"+data.getUrl()+"'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, new Headers());
		Concept node = new Concept();
		Concept lang=new Concept();
		for(TableRow row : rows) {
			node=closureServ.loadConceptById(row.getDbID());
			lang=closureServ.getParent(node);
			if(lang.getIdentifier().equalsIgnoreCase(LocaleContextHolder.getLocale().toString())) {
				data.setNodeId(node.getID());
			}
		}
		if(data.getNodeId()==-1l && !rows.isEmpty()) {
			ResourceDTO rDto = new ResourceDTO();
			rDto=resourceDtoByNode(rDto, node);
			String localeStr = LocaleContextHolder.getLocale().toString().toUpperCase();
			Concept root = closureServ.loadRoot("configuration.resources");
			node=resourceDefinitionCreate(rDto, root, localeStr);
			data.setNodeId(node.getID());
		}
	return data;
}

public DictNodeDTO dataDictNodeIdByUrl(DictNodeDTO data) throws ObjectNotFoundException {
	String select = "SELECT ID \r\n" + 
			"FROM alldictionary dc";
	String where="dc.url='"+data.getUrl()+"' and dc.lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
	List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, new Headers());
	if(rows.size()==1) {
		data.setNodeId(rows.get(0).getDbID());
		DictionaryDTO dict = new DictionaryDTO();
		dict.setUrlId(rows.get(0).getDbID());
		dict.setUrl(data.getUrl());
		data.setDict(dictServ.createDictionary(dict));
		dict=dictServ.page(dict);
	}else {
		data.setNodeId(-1l);
	}
	return data;
}
}