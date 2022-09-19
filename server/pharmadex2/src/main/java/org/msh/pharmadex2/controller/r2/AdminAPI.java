package org.msh.pharmadex2.controller.r2;

import java.io.IOException;
import java.sql.SQLException;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.dto.ActuatorAdmDTO;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataConfigDTO;
import org.msh.pharmadex2.dto.DataPreviewDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.Dict2DTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionariesDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.MessageDTO;
import org.msh.pharmadex2.dto.PublicOrgDTO;
import org.msh.pharmadex2.dto.ReportConfigDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.RootNodeDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.TilesDTO;
import org.msh.pharmadex2.dto.UserElementDTO;
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.AccessControlService;
import org.msh.pharmadex2.service.r2.ActuatorService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.msh.pharmadex2.service.r2.DWHService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.ImportAService;
import org.msh.pharmadex2.service.r2.ImportATCcodesService;
import org.msh.pharmadex2.service.r2.ImportAdmUnitsService;
import org.msh.pharmadex2.service.r2.ImportBService;
import org.msh.pharmadex2.service.r2.MetricService;
import org.msh.pharmadex2.service.r2.PubOrgService;
import org.msh.pharmadex2.service.r2.ReportService;
import org.msh.pharmadex2.service.r2.SupervisorService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.msh.pharmadex2.service.r2.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Common API for all authenticated users
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
	private ImportAService importAService;
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
	private AccessControlService accessControlService;
	@Autowired
	private ImportATCcodesService importATCcodesService;
	
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
	public PublicOrgDTO organizationLoadResponsibilitySelect(@RequestBody PublicOrgDTO data) throws DataNotFoundException {
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

	@PostMapping("/api/admin/list/users")
	public UserElementDTO listUsers(@RequestBody UserElementDTO data) throws DataNotFoundException {
		try {
			data = userService.listUsers(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping("/api/admin/user/load")
	public UserElementDTO userLoad(@RequestBody UserElementDTO data) throws DataNotFoundException {
		try {
			data = userService.userLoad(data);
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

	/*	*//** 2011-11-11 DEPRECATED and useless!!!
	 * Load activity or user data configuration
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 *//*
				@PostMapping("/api/admin/thing/load")
				public ThingDTO thingLoad(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
				if(data.getNodeId()>0) {
					try {
						UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
						data=superVisServ.thingLoad(data,user);
					} catch (ObjectNotFoundException e) {
						throw new DataNotFoundException(e);
					}
				}
				return data;
				}*/

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
	@PostMapping("/api/admin/data/collections/load")
	public DataConfigDTO dataCollectionsLoad(@RequestBody DataConfigDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionsLoad(data);
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
			data = superVisServ.dataCollectionVariablesLoad(data);
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
			String fileName = "reportDataStructure.xlsx";
			Resource res = reportServ.excelReport(data);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(mediaType))
					//.header(HttpHeaders.CONTENT_DISPOSITION, fres.getContentDisp() + "; filename=\"" + fres.getFileName() + "\"")
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
	 * Save a variable definition
	 * 
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/data/configuration/variable/save")
	public DataVariableDTO dataCollectionVariableSave(@RequestBody DataVariableDTO data) throws DataNotFoundException {
		try {
			data = superVisServ.dataCollectionVariableSave(data);
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
			data = superVisServ.resourceDefinitionSave(data);
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
	public ThingDTO resourceThingPrepare(@RequestBody ResourceDTO data) throws DataNotFoundException {
		try {
			ThingDTO ret = superVisServ.resourceThingPrepare(data);
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
	public ReportConfigDTO reportConfigurationLoad(@RequestBody ReportConfigDTO data) throws DataNotFoundException {
		try {
			data = reportServ.reportConfigurationLoad(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Keep actual addresses cache etc
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/report/parameters/renew")
	public ReportConfigDTO reportParametersRenew(@RequestBody ReportConfigDTO data) throws DataNotFoundException {
		try {
			data = reportServ.reportParametersRenew(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Load import admin units feature
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/import/adminunits/load")
	public ThingDTO importAdminunitsLoad(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importAdmUnitsService.importAdminunitsLoad(data, user);//importAService.importAdminunitsLoad(data,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	@PostMapping("/api/admin/importa/verif")
	public ThingDTO importAVerif(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			data = importAdmUnitsService.importAdminunitsVerify(data);//importAService.importAdminunitsVerify(data);
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	@PostMapping("/api/admin/importa/run")
	public ThingDTO importARun(Authentication auth, @RequestBody ThingDTO data ) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			//importAService.importAdminunitsRun(data);
			importAdmUnitsService.importAdminunitsRun(data, user);
			data=thingServ.loadThing(data, user);
			return data;
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
	}
	
	@PostMapping("/api/admin/import/adminunits/reload")
	public ThingDTO importAdminunitsReLoad(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importAdmUnitsService.importAdminunitsReload(data, user);//importAService.importAdminunitsReload(data,user); 
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Load import admin units feature
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/import/legacydata/load")
	public ThingDTO importLegacyDataLoad(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data=importBServ.importLegacyDataLoad(data,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	@PostMapping("/api/admin/import/legacydata/verif")
	public ThingDTO importLegacyDataVerif(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException{
			data = importBServ.importLegacyDataVerify(data);
		return data;
	}
	
	@PostMapping("/api/admin/import/legacydata/run")
	public ThingDTO importLegacyDataRun(Authentication auth, @RequestBody ThingDTO data ) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			importBServ.importLegacyDataRun(data, user); 
			data=thingServ.loadThing(data, user);
			return data;
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
	}
	
	@PostMapping("/api/admin/import/legacydata/reload")
	public ThingDTO importLegacyDataReLoad(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data=importBServ.importLegacyDataReload(data,user); 
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	@PostMapping("/api/admin/actuator/load")
	public ActuatorAdmDTO actuatorLoad(Authentication auth, @RequestBody ActuatorAdmDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		data=actuatorService.loadData(data);
		return data;
	}
	
	@PostMapping("/api/admin/metric/test")
	public ActuatorAdmDTO metricTest(Authentication auth, @RequestBody ActuatorAdmDTO data) throws DataNotFoundException {
		metricServ.collectMetricTTR();
		return data;
	}
	
	@PostMapping("/api/admin/report/renewexternal")
	public ReportConfigDTO reportsRenewExternal(Authentication auth, @RequestBody ReportConfigDTO data) throws DataNotFoundException, SQLException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		dwhServ.upload();
		return data;
	}
	
	/**
	 * Load data by change password from admin
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/changepass/load")
	public ThingDTO changePassAdminLoad(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = accessControlService.changePassAdminLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Save data by change password from admin
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/changepass/save")
	public ThingDTO changePassAdminSave(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = accessControlService.changePassAdminSave(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Load import admin units feature
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/admin/import/atccodes/load")
	public ThingDTO importATCcodesLoad(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importATCcodesService.importLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	@PostMapping("/api/admin/import/atccodes/run")
	public ThingDTO importATCcodesRun(Authentication auth, @RequestBody ThingDTO data ) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importATCcodesService.importStart(data, user);
			importATCcodesService.importRun(data, user);
			//data = thingServ.loadThing(data, user);
			return data;
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
	}
	
	@PostMapping("/api/admin/import/atccodes/reload")
	public ThingDTO importATCcodesReLoad(Authentication auth,@RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userService.userData(auth, new UserDetailsDTO());
		try {
			data = importATCcodesService.importReLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
}
