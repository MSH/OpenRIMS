package org.msh.pharmadex2.controller.r2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.controller.common.ExcelView;
import org.msh.pharmadex2.dto.AboutDTO;
import org.msh.pharmadex2.dto.ActuatorAdmDTO;
import org.msh.pharmadex2.dto.AsyncInformDTO;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataConfigDTO;
import org.msh.pharmadex2.dto.DataPreviewDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.Dict2DTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionariesDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ELAssistantBuildDTO;
import org.msh.pharmadex2.dto.ELAssistantSelectDTO;
import org.msh.pharmadex2.dto.FormatsDTO;
import org.msh.pharmadex2.dto.ImportLocalesDTO;
import org.msh.pharmadex2.dto.ImportWorkflowDTO;
import org.msh.pharmadex2.dto.MessageDTO;
import org.msh.pharmadex2.dto.ProcessComponentsDTO;
import org.msh.pharmadex2.dto.PublicOrgDTO;
import org.msh.pharmadex2.dto.ReassignActivitiesDTO;
import org.msh.pharmadex2.dto.ReassignUserDTO;
import org.msh.pharmadex2.dto.ReportConfigDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.RootNodeDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.TilesDTO;
import org.msh.pharmadex2.dto.URLAssistantDTO;
import org.msh.pharmadex2.dto.UserElementDTO;
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.log.EventLogDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.msh.pharmadex2.service.r2.ActuatorService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.AssemblyService;
import org.msh.pharmadex2.service.r2.AssistanceService;
import org.msh.pharmadex2.service.r2.AsyncService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.msh.pharmadex2.service.r2.DWHService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.ELAssistantService;
import org.msh.pharmadex2.service.r2.ImportATCcodesService;
import org.msh.pharmadex2.service.r2.ImportAdmUnitsService;
import org.msh.pharmadex2.service.r2.ImportBService;
import org.msh.pharmadex2.service.r2.ImportExportDataConfigService;
import org.msh.pharmadex2.service.r2.ImportExportDictionaryService;
import org.msh.pharmadex2.service.r2.ImportExportWorkflowService;
import org.msh.pharmadex2.service.r2.ImportLocalesService;
import org.msh.pharmadex2.service.r2.ImportWorkflowService;
import org.msh.pharmadex2.service.r2.LoggerEventService;
import org.msh.pharmadex2.service.r2.MailService;
import org.msh.pharmadex2.service.r2.MetricService;
import org.msh.pharmadex2.service.r2.ProcessComponentsService;
import org.msh.pharmadex2.service.r2.PubOrgService;
import org.msh.pharmadex2.service.r2.ReassignActivitiesService;
import org.msh.pharmadex2.service.r2.ReassignUserService;
import org.msh.pharmadex2.service.r2.ReportService;
import org.msh.pharmadex2.service.r2.ResolverService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.msh.pharmadex2.service.r2.SupervisorService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.msh.pharmadex2.service.r2.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Admin (Supervisor) API
 * 
 * @author alexk
 *
 */
@RestController
public class AdminAPI {
	@Autowired
	UserService userService;
	@Autowired
	ContentService contentService;
	@Autowired
	SupervisorService superVisServ;
	@Autowired
	private PubOrgService orgServ;
	@Autowired
	DictService dictServ;
	@Autowired
	ContentService сontentService;
	@Autowired
	ApplicationService applService;
	@Autowired
	ThingService thingService;
	@Autowired
	Messages messages;
	@Autowired
	private SystemService systemServ;
	@Autowired
	private ReportService reportServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private ImportBService importBServ;
	@Autowired
	private ActuatorService actuatorService;
	@Autowired
	private MetricService metricServ;
	@Autowired
	private DWHService dwhServ;
	@Autowired
	private ImportAdmUnitsService importAdmUnitsService;
	@Autowired
	private ImportATCcodesService importATCcodesService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MailService mailServ;
	@Autowired
	private ValidationService validation;
	@Autowired
	private ResourceService resourceServ;
	@Autowired
	ImportExportDictionaryService dictionaryServ;
	@Autowired
	ImportExportDataConfigService importExportDataConfigService;
	@Autowired
	ImportExportWorkflowService importExportWorkflowService;
	@Autowired
	ResolverService resolverServ;
	@Autowired
	ReassignUserService reassignService;
	@Autowired
	private AssistanceService assistServ;
	@Autowired
	private ImportLocalesService importLocalesServ;
	@Autowired
	private LoggerEventService logEventServ;
	@Autowired
	private BoilerService boiler;
	@Autowired
	private ImportWorkflowService importwfServ;
	@Autowired
	private ReassignActivitiesService reassignActivServ;
	@Autowired
	private AsyncService asyncService;
	@Autowired
	private ProcessComponentsService processComponents;
	@Autowired
	private ELAssistantService elAssistant;
	/**
	 * Tiles for landing page
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/content")
	public ContentDTO adminContent(@RequestBody ContentDTO data) throws DataNotFoundException {
		try {
			data = contentService.loadContent(data, "admin");
			if (data.getTiles().size() == 0) {
				data = contentService.adminStartTile(data);
			}
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Load the organization
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/organization/load")
	public PublicOrgDTO organizationLoad(@RequestBody PublicOrgDTO data) throws DataNotFoundException {
		try {
			data = orgServ.loadByConcept(data.getNode());
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Load the administrative units responsibility of the organization
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/organization/load/responsibility")
	public PublicOrgDTO organizationLoadResponsibility(@RequestBody PublicOrgDTO data) throws DataNotFoundException {
		data = orgServ.loadResponsibility(data);
		return data;
	}

	/**
	 * Load the administrative units responsibility of the organization
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/organization/load/responsibility/select")
	public PublicOrgDTO organizationLoadResponsibilitySelect(@RequestBody PublicOrgDTO data)
			throws DataNotFoundException {
		data = orgServ.loadResponsibilitySelect(data);
		return data;
	}

	@PostMapping("/api/admin/organization/save")
	public PublicOrgDTO organizationSave(@RequestBody PublicOrgDTO data) throws DataNotFoundException {
		try {
			data = orgServ.save(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Make an organization inactive for selection
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/organization/suspend")
	public PublicOrgDTO organizationDelete(@RequestBody PublicOrgDTO data) throws DataNotFoundException {
		try {
			data = orgServ.suspend(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/list/users/search={s}")
	public UserElementDTO listUsers(@RequestBody UserElementDTO data, @PathVariable(value = "s") String s) throws DataNotFoundException {
		try {
			data = userService.listUsers(data, s);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/user/load")
	public UserElementDTO userLoad(@RequestBody UserElementDTO data) throws DataNotFoundException {
		try {
			data = userService.userLoad(data);
			dictServ.page(data.getRoles());
			for(String key : data.getApplDicts().keySet()) {
				dictServ.page(data.getApplDicts().get(key));
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/user/save")
	public UserElementDTO userSave(@RequestBody UserElementDTO data) throws DataNotFoundException {
		try {
			data = userService.userSave(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * unlink a user from organization. Mainly to link to another organization
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/user/suspend")
	public UserElementDTO userSuspend(@RequestBody UserElementDTO data) throws DataNotFoundException {
		try {
			data = userService.userSuspend(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save a node
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/dictionary/node/save")
	public DictNodeDTO dictionaryNodeSave(@RequestBody DictNodeDTO data) throws DataNotFoundException {
		try {
			data = dictServ.save(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save a node
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/dictionary/node/suspend")
	public DictNodeDTO dictionaryNodeSuspend(@RequestBody DictNodeDTO data) throws DataNotFoundException {
		try {
			data = dictServ.nodeSuspend(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * All dicts
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/dictionary/all")
	public DictionariesDTO dictionaryAll(@RequestBody DictionariesDTO data) throws DataNotFoundException {
		try {
			data = dictServ.all(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load a node
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/root/node/load")
	public RootNodeDTO rootNodeLoad(@RequestBody RootNodeDTO data) throws DataNotFoundException {
		try {
			data = dictServ.rootNode(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save a node
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/root/node/save")
	public RootNodeDTO rootNodeSave(@RequestBody RootNodeDTO data) throws DataNotFoundException {
		try {
			data = dictServ.rootNodeSave(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/tiles")
	public TilesDTO tilesLoad(Authentication auth, @RequestBody TilesDTO data) throws DataNotFoundException {
		try {
			data = сontentService.buildTiles(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/tiles/update")
	public TilesDTO tilesUpdate(Authentication auth, @RequestBody TilesDTO data) throws DataNotFoundException {
		try {
			data = сontentService.updateTiles(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/tiles/save")
	public TilesDTO tilesSave(Authentication auth, @RequestBody TilesDTO data) throws DataNotFoundException {
		try {
			data = сontentService.saveTiles(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}


	/**
	 * load/create configuration for workflow for known dictNodeId
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/workflow/configuration/load")
	public WorkflowDTO workflowConfigurationLoad(Authentication auth, @RequestBody WorkflowDTO data)
			throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data = superVisServ.workflowConfiguration(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	
	/**
	 * Append an new created activity to workflow to the end of path
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/workflow/activity/add")
	public WorkflowDTO workflowActivityAdd(Authentication auth, @RequestBody WorkflowDTO data)
			throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data = superVisServ.workflowActivityAdd(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Insert an activity before the current one
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/workflow/activity/insert")
	public WorkflowDTO workflowActivityInsert(Authentication auth, @RequestBody WorkflowDTO data)
			throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data = superVisServ.workflowActivityInsert(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Suspend an activity and remove from the path
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/workflow/activity/suspend")
	public WorkflowDTO workflowActivitySuspend(Authentication auth, @RequestBody WorkflowDTO data)
			throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data = superVisServ.workflowActivitySuspend(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load table with data collections
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/collections/load/search={s}/svars={svars}")
	public DataConfigDTO dataCollectionsLoad(@RequestBody DataConfigDTO data, @PathVariable(value = "s") String s, @PathVariable(value = "svars") String svars) throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionsLoad(data, s, svars);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load a definition of a data collection
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/collection/definition/load")
	public DataCollectionDTO dataCollectionDefinitionLoad(@RequestBody DataCollectionDTO data)
			throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionDefinitionLoad(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save a definition of a data collection
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/collection/definition/save")
	public DataCollectionDTO dataCollectionDefinitionSave(@RequestBody DataCollectionDTO data)
			throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionDefinitionSave(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Suspend a data storage (will not be used)
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/collection/definition/suspend")
	public DataCollectionDTO dataCollectionDefinitionSuspend(@RequestBody DataCollectionDTO data)
			throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionDefinitionSuspend(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Suspend a data storage (will not be used)
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/collection/definition/duplicate")
	public DataCollectionDTO dataCollectionDefinitionDuplicate(@RequestBody DataCollectionDTO data)
			throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionDefinitionDuplicate(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load structure and screen layout for a data collection given
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/collection/variables/load")
	public DataConfigDTO dataCollectionVariablesLoad(@RequestBody DataConfigDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionVariablesLoad(data, "");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/data/collection/variables/export")
	public ResponseEntity<Resource> dataCollectionVariablesExport(@RequestBody DataConfigDTO data)
			throws DataNotFoundException {
		try {
			String mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			String fileName = importExportDataConfigService.variablesExportFileName(data);
			Resource res = importExportDataConfigService.variablesExport(data);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(mediaType))
					// .header(HttpHeaders.CONTENT_DISPOSITION, fres.getContentDisp() + ";
					// filename=\"" + fres.getFileName() + "\"")
					.header("filename", fileName).body(res);
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Load a variable
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/configuration/variable/load")
	public DataVariableDTO dataCollectionVariableLoad(@RequestBody DataVariableDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionVariableLoad(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Get a simple help
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/configuration/variable/help")
	public DataVariableDTO dataCollectionVariableHelp(@RequestBody DataVariableDTO data) throws DataNotFoundException {
		try {
			data = validation.variable(data, false, false);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save a variable definition
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/configuration/variable/save")
	public DataVariableDTO dataCollectionVariableSave(@RequestBody DataVariableDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionVariableSave(data, true);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Suspend a variable definition
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/configuration/variable/suspend")
	public DataVariableDTO dataCollectionVariableSuspend(@RequestBody DataVariableDTO data)
			throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionVariableSuspend(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/data/collection/definition/preview")
	public DataPreviewDTO dataCollectionDefinitionPreview(Authentication auth, @RequestBody DataPreviewDTO data)
			throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data = superVisServ.dataCollectionDefinitionPreview(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load all messages from table resource_message
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/messages/load")
	public MessageDTO messagesLoad(@RequestBody MessageDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.messagesLoad(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;

	}

	@PostMapping("/api/admin/messages/save")
	public MessageDTO messagesSave(@RequestBody MessageDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.messagesSave(data);

			// reload messages
			messages.getMessages().clear();
			messages.loadLanguages();
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;

	}

	@PostMapping("/api/admin/reloadmessages")
	public String reloadMessages(@RequestBody String data) throws DataNotFoundException {
		messages.getMessages().clear();
		messages.loadLanguages();
		return data;
	}

	/**
	 * Responsible for a list of resources
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/resources/load")
	public ResourceDTO resourcesLoad(@RequestBody ResourceDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.resourcesLoad(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;

	}

	/**
	 * Responsible for save a resource definition to the database
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 * @throws ObjectNotFoundException
	 */
	@PostMapping("/api/admin/resource/definition/save")
	public ResourceDTO resurceDefinitionSave(@RequestBody ResourceDTO data) throws DataNotFoundException {
		try {
			data=superVisServ.resurceDefinitionSave(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Responsible for suspend the resource definition
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 * @throws ObjectNotFoundException
	 */
	@PostMapping("/api/admin/resource/definition/suspend")
	public ResourceDTO resurceDefinitionSuspend(@RequestBody ResourceDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.resourceDefinitionSuspend(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Prepare thing in accordance with the resource selected url and nodeId
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/resource/thing/prepare")
	public ThingDTO resourceThingPrepare(Authentication auth, @RequestBody ResourceDTO data) throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			ThingDTO ret = superVisServ.resourceThingPrepare(data, user);
			return ret;
		} catch (ObjectNotFoundException | JsonProcessingException e) {
			throw new DataNotFoundException(e);
		}
	}
	@PostMapping("/api/admin/resource/dictionary/prepare")
	public DictionaryDTO resourceDictPrepare(@RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			DictionaryDTO ret = superVisServ.resourceDictPrepare(data);
			ret=dictServ.page(ret);
			return ret;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * Save a thing represent a resource
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/resource/save")
	public ThingDTO resourceSave(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data = superVisServ.resourceSave(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load the system dictionary "Actions"
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/processes/actions")
	public DictionaryDTO processesActions(@RequestBody DictionaryDTO data) throws DataNotFoundException {
		try {
			data = systemServ.submitActionDictionary();
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	@PostMapping("/api/admin/report/configuration/load")
	public ReportConfigDTO reportConfigurationLoad(Authentication auth,@RequestBody ReportConfigDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = reportServ.reportConfigurationLoad(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/*
	 * We don't need it anymore
	 * 	*//**
	 * Keep actual addresses cache etc
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 *//*
				@PostMapping("/api/admin/report/parameters/renew")
				@Deprecated
				public ReportConfigDTO reportParametersRenew(@RequestBody ReportConfigDTO data) throws DataNotFoundException {
				try {
					data = reportServ.reportParametersRenew(data);
				} catch (ObjectNotFoundException e) {
					throw new DataNotFoundException(e);
				}
				return data;
				}*/

	/**
	 * To avoid concurrent data import
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/data/import/check")
	public ThingDTO dataImportCheck(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		if(asyncService.hasDataImportThread()) {
			data.addError(messages.get("errorconcurrentdataimport"));
		}else {
			try {
				data.clearErrors();
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
		}
		return data;
	}

	/**
	 * To avoid concurrent data import
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/data/import/progress")
	public AsyncInformDTO dataImportProgress(Authentication auth, @RequestBody AsyncInformDTO data) throws DataNotFoundException {
		try {
			data=asyncService.dataImportProgress(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load import admin units feature
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/import/adminunits/load")
	public ThingDTO importAdminunitsLoad(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importAdmUnitsService.importAdminunitsLoad(data, user);// importAService.importAdminunitsLoad(data,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/importa/verif")
	public ThingDTO importAVerif(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			data.clearErrors();
			AsyncInformDTO asyncDTO = new AsyncInformDTO();
			asyncDTO = asyncService.dataImportProgress(asyncDTO);
			if(asyncDTO.isCompleted()){
				data=importAdmUnitsService.importAdminunitsVerify(data);
			}else {	
				data.addError(messages.get("anotherprocessisrunning"));
			}
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/importa/run")
	public ThingDTO importAdminUnitsRun(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data.clearErrors();
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		if(!AsyncService.hasDataImportThread()) {
			asyncService.importAdminunitsRun(data, user);
		}else {
			data=(ThingDTO) asyncService.concurrentError(data);
		}
		return data;
	}

	@PostMapping("/api/admin/import/adminunits/reload")
	public ThingDTO importAdminunitsReLoad(Authentication auth, @RequestBody ThingDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importAdmUnitsService.importAdminunitsReload(data, user);// importAService.importAdminunitsReload(data,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/import/adminunits/progress")
	public AsyncInformDTO importAdminUnitsProgress(@RequestBody AsyncInformDTO data) throws DataNotFoundException {
		try {
			data=asyncService.dataImportProgress(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load import admin units feature
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/import/legacydata/load")
	public ThingDTO importLegacyDataLoad(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importBServ.importLegacyDataLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}



	@PostMapping("/api/admin/import/legacydata/verif")
	public ThingDTO importLegacyDataVerif(Authentication auth, @RequestBody ThingDTO data)
			throws DataNotFoundException {
		data = importBServ.importLegacyDataVerify(data);
		return data;
	}

	@PostMapping("/api/admin/import/legacydata/run")
	public ThingDTO importLegacyDataRun(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data.clearErrors();

			if(!AsyncService.hasDataImportThread()) {
				asyncService.importLegacyDataRun(data, user);
			}else {
				data=(ThingDTO) asyncService.concurrentError(data);
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/import/legacydata/progress")
	public AsyncInformDTO importLegacyDataProgress(@RequestBody AsyncInformDTO data) throws DataNotFoundException {
		try {
			data = asyncService.dataImportProgress(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/import/legacydata/reload")
	public ThingDTO importLegacyDataReLoad(Authentication auth, @RequestBody ThingDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importBServ.importLegacyDataReload(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/actuator/load")
	public ActuatorAdmDTO actuatorLoad(Authentication auth, @RequestBody ActuatorAdmDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		data = actuatorService.loadData(data);
		return data;
	}

	@PostMapping("/api/admin/metric/test")
	public ActuatorAdmDTO metricTest(Authentication auth, @RequestBody ActuatorAdmDTO data)
			throws DataNotFoundException {
		metricServ.collectMetricTTR();
		return data;
	}
	/**
	 * DWH Update
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/report/renewexternal")
	public ReportConfigDTO reportsRenewExternal(Authentication auth, @RequestBody ReportConfigDTO data)
			throws DataNotFoundException {
		try {
			data.clearErrors();
			if(!AsyncService.hasDataImportThread()) {
				asyncService.dwhUploadRun();
			}else {
				data=(ReportConfigDTO) asyncService.concurrentError(data);
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/dwh/update/progress")
	public AsyncInformDTO reportsRenewExternal(@RequestBody AsyncInformDTO data)
			throws DataNotFoundException {
		try {
			data=asyncService.dataImportProgress(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load data by change password from admin
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/changepass/load")
	public ThingDTO changePassAdminLoad(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = thingServ.changePassAdminLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save data by change password from admin
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/changepass/save")
	public ThingDTO changePassAdminSave(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = thingServ.changePassAdminSave(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load import admin units feature
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/import/atccodes/load")
	public ThingDTO importATCcodesLoad(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importATCcodesService.importLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/import/atccodes/run")
	public ThingDTO importATCcodesRun(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data.clearErrors();

			if(!AsyncService.hasDataImportThread()) {
				asyncService.importAtccodesRun(data, user);
			}else {
				data=(ThingDTO) asyncService.concurrentError(data);
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}

		return data;
	}

	@PostMapping("/api/admin/import/atccodes/progress")
	public AsyncInformDTO importATCcodesProgress(@RequestBody AsyncInformDTO data) throws DataNotFoundException {
		try {
			data = asyncService.dataImportProgress(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/workflow/export/excel")
	public ResponseEntity<Resource> workflowExportExcel(Authentication auth,@RequestBody WorkflowDTO data)
			throws DataNotFoundException {
		try {
			String mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			//String fileName = "reportDataStructure.xlsx";
			String fileName=importExportWorkflowService.fileName(data);
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			Resource res = importExportWorkflowService.workflowExportExcel(user,data);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(mediaType))
					// .header(HttpHeaders.CONTENT_DISPOSITION, fres.getContentDisp() + ";
					// filename=\"" + fres.getFileName() + "\"")
					.header("filename", fileName).body(res);
		} catch (IOException | ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * Test mail configuration - send mail to the Supervisor's address
	 * @param message
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/mail/test")
	public AboutDTO mailTest(Authentication auth, @RequestBody AboutDTO message) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			message = (AboutDTO) mailServ.testMail(user,message);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return message;
	}

	/**
	 * Download or read "EL Reference instruction
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/elreference", method = RequestMethod.GET)
	public ResponseEntity<Resource> elreference() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminElreferenceGuide();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Download or read DictionaryCreationMaintenance
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/reassign/applicant", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpReassignApplicant() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpReassignApplicant();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * URL assistance help
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/url/assistant", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpUrlAssistant() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpUrlAssistant();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * URL assistance help
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/elassistant", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpELAssistant() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpELAssistant();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * Workflow Assistance help
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/process/validator/manual", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpWorkflowAssistant() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpWorkflowAssistant();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	@RequestMapping(value="/api/admin/help/import/atc", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpImportATC() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpImportATC();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}


	/**
	 * Import a national language help
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/import/messages", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpImportMessages() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpImportMessages();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Import a national language help
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/messages", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpMessages() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpMessages();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Download or read DictionaryCreationMaintenance
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/dictionaries", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpDictionaries() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpDictionaries();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Download or read ImportConfigProcessInstruction
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/impconfigprocess", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpImpConfigProcess() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpImpCongigProcess();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * The Workflow Guide
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/manual/workflow", method = RequestMethod.GET)
	public ResponseEntity<Resource> manualWorkflow() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminManualWorkflow();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Download or read workflow reference guide
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/wfrguide", method = RequestMethod.GET)
	public ResponseEntity<Resource> loadPrivacy() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpWfrGuide();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * Download or read resource creation guide
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/resource/help", method = RequestMethod.GET)
	public ResponseEntity<Resource> resourceHelp() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.resourceHelp();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Download or read resource creation guide
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/date/format/help", method = RequestMethod.GET)
	public ResponseEntity<Resource> dateFormatHelp() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.dateFormatHelp();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	@RequestMapping(value = {"/api/admin/dictionary/export/dicturl={url}&curid={id}"}, method = RequestMethod.GET)
	public ResponseEntity<Resource> dictionaryExport(@PathVariable(value = "url", required = true) String dicturl, @PathVariable(value = "id", required = true) Long currentId)
			throws DataNotFoundException, ObjectNotFoundException {
		try {
			return dictionaryServ.exportDictionary(dicturl, currentId);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Load import admin units feature
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/dictionary/import/load")
	public ThingDTO dictionaryImportLoad(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = dictionaryServ.importLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping({"/api/admin/dictionary/import/run"})
	public DictionaryDTO dictionaryImportRun(Authentication auth, @RequestParam("dict") String jsonDict,
			@RequestParam("file") Optional<MultipartFile> file) throws DataNotFoundException {
		DictionaryDTO dict = new DictionaryDTO();
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			dict = objectMapper.readValue(jsonDict, DictionaryDTO.class);
			byte[] fileBytes = new byte[0];
			if (file.isPresent()) {
				fileBytes = file.get().getBytes();
				dict = dictionaryServ.importRun(dict, fileBytes);
			}
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
		return dict;
	}

	@RequestMapping(value = {"/api/admin/dictionary/import/errorfile"}, method = RequestMethod.GET)
	public ResponseEntity<Resource> dictionaryExport()
			throws DataNotFoundException, ObjectNotFoundException, IOException {
		ResponseEntity<Resource> resp = dictionaryServ.getFileErrors();
		return resp;
	}

	/**
	 * Load an electronic form to upload the data file in xlsx
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/configuration/load/import")
	public ThingDTO dataConfigurationLoadImport(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data=importExportDataConfigService.importLoad(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load an electronic form to upload the data file in xlsx
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/workflow/load/import")
	public ThingDTO dataWorkflowLoadImport(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data=importExportWorkflowService.importWorkflow(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Import data configuration from the XLSX file uploaded
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/configuration/run/import")
	public ThingDTO dataConfigurationRunImport(Authentication auth, @RequestBody ThingDTO data){
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		data.setUrl(AssemblyService.SYSTEM_IMPORT_DATA_CONFIGURATION);
		try {
			data.getStrings().get("description").setValue("");
			data=importExportDataConfigService.importRun(user, data);
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier(e.getMessage());
			data.getStrings().get("description").setValue(data.getIdentifier());
		}
		return data;
	}
	/**
	 * Import data configuration from the XLSX file uploaded
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/workflow/run/import")
	public ThingDTO dataWorkflowRunImport(Authentication auth, @RequestBody ThingDTO data){
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		data.setUrl(AssemblyService.SYSTEM_IMPORT_DATA_WORKFLOW);
		try {
			data=importExportWorkflowService.importRun(user, data);
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier(e.getMessage());
		}
		return data;
	}

	@PostMapping("/api/admin/importwf/load")
	public ImportWorkflowDTO importWFload(Authentication auth, @RequestBody ImportWorkflowDTO data) throws DataNotFoundException{
		try {
			data = importwfServ.load(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}

		return data;
	}

	/**
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/importwf/connect")
	public ImportWorkflowDTO importWFconnectmainserver(Authentication auth, @RequestBody ImportWorkflowDTO data) throws DataNotFoundException{
		try {
			data = importwfServ.connectMainServer(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}

		return data;
	}

	/*@PostMapping("/api/admin/importwf/reload")
	public ImportWorkflowDTO importWFreload(Authentication auth, @RequestBody ImportWorkflowDTO data) throws DataNotFoundException{
		try {
			data = importwfServ.reload(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}

		return data;
	}*/



	@PostMapping("/api/admin/importwf/runimport")
	public ImportWorkflowDTO importWFrun(Authentication auth, @RequestBody ImportWorkflowDTO data) throws DataNotFoundException{
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data.clearErrors();

			if(!AsyncService.hasDataImportThread()) {
				asyncService.importWFRun(data, user);
			}else {
				data = (ImportWorkflowDTO) asyncService.concurrentError(data);
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/importwf/progress")
	public AsyncInformDTO importWFprogress(@RequestBody AsyncInformDTO data) throws DataNotFoundException {
		try {
			data = asyncService.dataImportProgress(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/importwf/loadresult")
	public ImportWorkflowDTO importWFloadresult(Authentication auth, @RequestBody ImportWorkflowDTO data) throws DataNotFoundException, ObjectNotFoundException{
		data = importwfServ.loadResultTabel(data);

		return data;
	}

	/**
	 * load "dictionary.guest.applications"
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/stages/workflow")
	public Dict2DTO stagesWorkflow(@RequestBody Dict2DTO data) throws DataNotFoundException {
		try {
			data = systemServ.stagesWorkflow(data);
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * Load date and number format definitions form
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping({"/api/admin/formats","/api/admin/formats/reset"})
	public FormatsDTO formats(@RequestBody FormatsDTO data) throws DataNotFoundException {
		try {
			data.clearErrors();
			data=systemServ.formatDates(data);
			data=resolverServ.formatDateEL(data);
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}


	}

	/**
	 * Test new formats 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/formats/test")
	public FormatsDTO formatsTest(@RequestBody FormatsDTO data) throws DataNotFoundException {
		String dateFormat = Messages.dateFormat;
		Messages.dateFormat=data.getFormatDate().getValue();
		try {
			data=validation.format(data);
			if(data.isValid()) {
				data=systemServ.formatSamples(data);
				data=resolverServ.formatDateEL(data);
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		} finally {
			Messages.dateFormat=dateFormat;
		}
		return data;
	}
	/**
	 * Save a date format definitions
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/formats/save")
	public FormatsDTO formatsSave(@RequestBody FormatsDTO data) throws DataNotFoundException {
		try {
			data=systemServ.formatsSave(data);
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Save a date format definitions
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/url/assist")
	public URLAssistantDTO urlAssist(@RequestBody URLAssistantDTO data) throws DataNotFoundException {
		try {
			data=assistServ.assistanceMain(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException();
		}
		return data;
	}
	/**
	 * Save a date format definitions
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/url/assist/validate")
	public URLAssistantDTO urlAssistValidate(@RequestBody URLAssistantDTO data) throws DataNotFoundException {
		try {
			data=assistServ.validate(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Preview dictionary, thing, or nothing :)
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/url/assist/preview")
	public URLAssistantDTO urlAssistPreview(Authentication auth,@RequestBody URLAssistantDTO data) throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data=assistServ.preview(data,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Search for an appliant to reassign Gmail
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/reassign/applicant/search")
	public ReassignUserDTO reassignApplicantSearch( @RequestBody ReassignUserDTO data) throws DataNotFoundException {
		try {
			data=reassignService.applicantSearch(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Get appicant's details for the reassigning form
	 * @param data
	 * @return
	 */
	@PostMapping("/api/admin/reassign/applicant/details")
	public ReassignUserDTO reassignApplicantDetails( @RequestBody ReassignUserDTO data) {
		data=reassignService.applicantDetails(data);
		data = logEventServ.userReassignLog(data);
		return data;
	}
	/**
	 * Validate and run applicant reassignment
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/reassign/applicant/run")
	public ReassignUserDTO reassignApplicantRun(Authentication auth,@RequestBody ReassignUserDTO data) throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data.clearErrors();
			if(!AsyncService.hasDataImportThread()) {
				data= reassignService.validateEmails(data);
				if(data.isValid()) {
					data.setExecName(user.getName() + " ("+user.getEmail()+")");
					asyncService.reassignApplicantRun(data);
				}
			}else {
				data=(ReassignUserDTO) asyncService.concurrentError(data);
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Load applicant reassigning progress data
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/reassign/appicant/progress/load")
	public AsyncInformDTO reassignApplicantProgressLoad(@RequestBody AsyncInformDTO data) throws DataNotFoundException {
		try {
			data=asyncService.dataImportProgress(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/reassign/users/log")
	public ReassignUserDTO reassignUsersLog(@RequestBody ReassignUserDTO data) {
		data = logEventServ.userReassignLog(data);
		return data;
	}

	@PostMapping("/api/admin/import/locales/load")
	public ImportLocalesDTO importLocalesLoad(Authentication auth, @RequestBody ImportLocalesDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importLocalesServ.importLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/import/locales/loadmessages")
	public TableQtb importLocalesLoadMessages(Authentication auth, @RequestBody TableQtb data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		//try {
		data = importLocalesServ.loadMessages(data);
		//} catch (ObjectNotFoundException e) {
		//	throw new DataNotFoundException(e);
		//}
		return data;
	}

	@PostMapping("/api/admin/import/locales/verif")
	public ImportLocalesDTO importLocalesVerif(Authentication auth, @RequestBody ImportLocalesDTO data) throws DataNotFoundException {
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			data = importLocalesServ.importVerify(data);
			if(data.isValid()) {
				data=importLocalesServ.save(data,user);
			}
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/import/locales/run")
	public ImportLocalesDTO importLocalesRun(Authentication auth, @RequestBody ImportLocalesDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importLocalesServ.importRun(data, user);
			//data.setThing(thingServ.loadThing(data.getThing(), user));
			return data;
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
	}

	@PostMapping("/api/admin/import/locales/download")
	public ModelAndView importLocalesDownload(Authentication auth, UriComponentsBuilder uri, 
			@RequestBody ThingDTO data,
			HttpServletResponse response) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		TableQtb table = new TableQtb();

		try {
			table = importLocalesServ.downloadTemplate(user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}

		Map<String, Object> model = new HashMap<String, Object>();
		//Sheet Name - locale
		model.put(ExcelView.SHEETNAME, "Locales");
		//Headers List - set width column
		model.put(ExcelView.HEADERS, table.getHeaders().getHeaders());
		//Rows
		model.put(ExcelView.ROWS, table.getRows());
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"messages.xlsx\"");
		response.setHeader("filename", "messages.xlsx");
		return new ModelAndView(new ExcelView(), model);
	}
	/**
	 * Get import language log
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/import/locales/log")
	public EventLogDTO importLocalesLog(Authentication auth, @RequestBody EventLogDTO data) throws DataNotFoundException {
		data=logEventServ.importLocalesLog(data);
		return data;
	}

	/**
	 * Download lost messages keys in xlsx format
	 * @param data
	 * @param response
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/messages/lost/download")
	public ModelAndView messagesLostUpload(HttpServletResponse response) throws DataNotFoundException{
		// get table
		try {
			TableQtb table=importLocalesServ.messagesLost();
			//add table to model
			Map<String, Object> model = new HashMap<String, Object>();
			//Sheet Name
			model.put(ExcelView.SHEETNAME, messages.get("lostmessages"));
			//Title
			model.put(ExcelView.TITLE, boiler.dateToString(new Date()));
			//Headers List
			model.put(ExcelView.HEADERS, table.getHeaders().getHeaders());
			//Rows
			model.put(ExcelView.ROWS, table.getRows());
			response.setHeader( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lostmessages.xlsx\"");
			response.setHeader("filename", "lostmessages.xlsx");       
			return new ModelAndView(new ExcelView(), model);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * append lost messages provided in the xlsx file
	 * @param auth
	 * @param uri
	 * @param jsonDto
	 * @param file
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/messages/lost/append")
	public AboutDTO messagesLostAppend(Authentication auth, UriComponentsBuilder uri, @RequestParam("dto") String jsonDto,
			@RequestParam("file") MultipartFile file) throws DataNotFoundException {
		AboutDTO data = new AboutDTO();
		try {
			data = objectMapper.readValue(jsonDto, AboutDTO.class);
			InputStream inputStream = new ByteArrayInputStream(file.getBytes());
			data=superVisServ.messagesLostAppend(inputStream,data);
		} catch (IOException | ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Download or read resource creation guide
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/report/config/manual", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpReportConfigManual() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpReportConfigManual();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Search for an employee to reassign activities
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/reassign/employee/load")
	public ReassignActivitiesDTO reassignEmployeeSearch( @RequestBody ReassignActivitiesDTO data) throws DataNotFoundException, ObjectNotFoundException {
		data=reassignActivServ.employeeLoad(data);
		return data;

	}
	/**
	 * Validate and run employee reassignment
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/reassign/employee/run")
	public ReassignActivitiesDTO reassignEmployeeRun(Authentication auth,@RequestBody ReassignActivitiesDTO data) throws DataNotFoundException {
		try {
			data=reassignActivServ.employeeReassign(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Check all rows in table vailableActivities 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/reassign/employee/selectall")
	public ReassignActivitiesDTO reassignEmployeeSelectAll(Authentication auth,@RequestBody ReassignActivitiesDTO data){
		data=reassignActivServ.selectAll(data);
		return data;
	}

	/**
	 * De-select all rows in table vailableActivities 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/reassign/employee/deselectall")
	public ReassignActivitiesDTO reassignEmployeeDeselectAll(Authentication auth,@RequestBody ReassignActivitiesDTO data){
		data=reassignActivServ.deselectAll(data);
		return data;
	}

	/**
	 * Show selected rows only in table vailableActivities 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/reassign/employee/selectonly")
	public ReassignActivitiesDTO reassignEmployeeSelectOnly(Authentication auth,@RequestBody ReassignActivitiesDTO data){
		data=reassignActivServ.selectOnly(data);
		return data;
	}
	/**
	 * Download or read DictionaryCreationMaintenance
	 * @return
	 * @throws DataNotFoundException
	 * @throws IOException
	 */
	@RequestMapping(value="/api/admin/help/reassign/activities", method = RequestMethod.GET)
	public ResponseEntity<Resource> helpReassignEmployee() throws DataNotFoundException, IOException {
		ResponseEntity<Resource> res;
		try {
			res = resourceServ.adminHelpReassignActivities();
			return res;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Show selected rows only in table vailableActivities 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/process/components")
	public ProcessComponentsDTO processComponents(@RequestBody ProcessComponentsDTO data) throws DataNotFoundException{
		try {
			data=processComponents.load(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Show selected rows only in table vailableActivities 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/data/config/nodeid")
	public DictNodeDTO dataConfigNodeIdByUrl(@RequestBody DictNodeDTO data) throws DataNotFoundException{
		data=superVisServ.dataConfigNodeIdByUrl(data);
		return data;
	}
	/**
	 * Show selected rows only in table vailableActivities 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/admin/resources/nodeid")
	public DictNodeDTO dataResourceNodeIdByUrl(@RequestBody DictNodeDTO data) throws DataNotFoundException{
		data=superVisServ.dataResourceNodeIdByUrl(data);
		return data;
	}
	/**
	 * Show selected rows only in table vailableActivities 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 * @throws ObjectNotFoundException 
	 */
	@PostMapping("/api/admin/dictionaries/nodeid")
	public DictNodeDTO dataDictionaryNodeIdByUrl(@RequestBody DictNodeDTO data) throws DataNotFoundException, ObjectNotFoundException{
		data=superVisServ.dataDictNodeIdByUrl(data);
		return data;
	}
	/**
	 * Get workflows to build ELs
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/el/assitant/workflows")
	public ELAssistantSelectDTO elAssistantWorkflows( @RequestBody ELAssistantSelectDTO data) throws DataNotFoundException {
		try {
			data=elAssistant.elAssistantWorkflows(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Build EL using selected workflow and user's choices
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/el/assitant/build")
	public ELAssistantBuildDTO elAssistantBuild( @RequestBody ELAssistantBuildDTO data) throws DataNotFoundException {
		try {
			data=elAssistant.elAssistantBuild(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Test EL expression just built
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/el/assitant/test")
	public ResponseEntity<Resource> elAssistanceTest(Authentication auth,@RequestBody ELAssistantBuildDTO data)
			throws DataNotFoundException {
		Resource res;
		try {
			UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
			res = elAssistant.test(data,user);
		} catch (IOException | ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return ResponseEntity.ok()
				.contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline" + "; filename=\"" + "eltest.docx" + "\"")
				.header("filename", "eltest.docx")
				.body(res);
	}
}
