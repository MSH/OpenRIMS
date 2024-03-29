package org.msh.pharmadex2.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.ExchangeConfigDTO;
import org.msh.pharmadex2.dto.ImportWorkflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.ImportWorkflowMainService;
import org.msh.pharmadex2.service.r2.ImportWorkflowService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Test methods in the ExchangeConfigurationService
 * @author khomenska
 *
 */
@ActiveProfiles({"test"})
@SpringBootTest(classes = Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImportWorkflowTest {

	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private Messages messages;
	@Autowired
	private ImportWorkflowService importServ;
	@Autowired
	private ImportWorkflowMainService importMainServ;
	@Autowired
	private UserService userService;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void test() {
		String url = "dfdf";
		long parID = 89160;
		String answer = recursionLoadDictionary(url, parID);
		if(answer != null) {
			System.out.println(answer);
		}else {
			System.out.println("!!!");
		}
	}
	
	private String recursionLoadDictionary(String url, long parID){
		DictNodeDTO dictDTO = new DictNodeDTO();
		try {
			// разбираем словарь по уровням
			Concept levelNode = closureServ.loadConceptById(parID);
			List<Concept> child = literalServ.loadOnlyChilds(levelNode);
			if(child != null && child.size() > 0) {
				for(Concept c:child) {
					String r = recursionLoadDictionary(url, 10);
					if(r != null) {
						return r;
					}
				}
			}
		} catch (ObjectNotFoundException e) {
			return e.getMessage();
		}
		return null;
	}

	public String readPrefLabel(Concept node) throws ObjectNotFoundException {
		throw new ObjectNotFoundException("loadConceptById. Parent concept not found, id=");
	}
	
	//@Test
	public void testActivitiesconfigs() throws JsonProcessingException, ObjectNotFoundException {
		ImportWorkflowDTO dto = new ImportWorkflowDTO();
		//try {
			dto.setProcessIDselect(33520l);
			dto.setProcessURL("dictionary.guest.applications");
			dto.setWfIDselect(66058l);
			dto.setWfURL("retail.site.owned.persons");

			String url = "http://localhost:" + port + "/api/public/importwf/wf";
			dto = restTemplate.postForObject(url, dto, ImportWorkflowDTO.class);
			if(dto.isValid()) {
				//dto = importServ.runimport(dto);
				
			}
			
			//dto = importServ.createActivitiesConfigsOnLocal(dto, user);
		//}catch (ObjectNotFoundException e) {
		//	e.printStackTrace();
		//}
	}
	
	//@Test
	public void testResources() throws JsonProcessingException, ObjectNotFoundException {
		ImportWorkflowDTO dto = new ImportWorkflowDTO();
		//try {
			dto.setProcessIDselect(4035l);//33595l);//33520l);//66589l);//
			dto.setProcessURL("dictionary.guest.applications");//dictionary.host.applications");//dictionary.guest.applications");//dictionary.guest.deregistration");//
			dto.setWfIDselect(181217l);//34339l);//34302l);//66058l);
			dto.setWfURL("retail.site.owned.persons");//application.pharmacy.renew");//application.pharmacy.inspection");//retail.site.owned.persons");
			//UserDetailsDTO user = userService.loadByEmail("dudchenkoirina90@gmail.com");
			
			
			/*dto = importMainServ.validateWF(dto);
			if(dto.isValid()) {
				System.out.println(dto.isValid());
			}*/
			
			/*String url = "";
			Concept dictRoot = closureServ.loadConceptByIdentifierActive(url);
			if(dictRoot != null && dictRoot.getID() > 0) {
				data = buildDict(url, dictRoot, data);
			}
			
			Concept root = closureServ.loadConceptByIdentifierActive(urldict);
			if(root == null) {
				Map<Long, List<DictNodeDTO>> dict = dicts.get(urldict);
				buildDict(urldict, dict, root);
			}
			*/
			
			/*String url = "http://localhost:" + port + "/api/public/importwf/wf";//dictionaries";//dataconfigs
			//String url = "https://pharmadex.irka.in.ua/api/public/importwf/dataconfigs";
			dto = restTemplate.postForObject(url, dto, ImportWorkflowDTO.class);
			if(dto.isValid()) {
				dto = importServ.createWFOnLocal(dto, user);
				
				System.out.println(dto.isValid());
			}*/
			
			/*String url = "dictionary.product.category";
			Concept root = closureServ.loadConceptByIdentifierActive(url);
			if(root != null && root.getID() > 0) {
				dto = importMainServ.buildDict(url, root, dto);
				
				url += ".dev";
				Concept d = closureServ.loadConceptByIdentifierActive(url);
				if(d == null) {
					Map<Long, List<DictNodeDTO>> dict = dto.getDictsImport().get("dictionary.product.category");
					importServ.buildDict(url, dict, d);
				}
				
				System.out.println(dto.isValid());
			}*/
			
			/*Map<String, Long> map = new HashMap<String, Long>();
			map.put("pharmacy.site.inspection.reports", 68358l);
			map.put("retail.site.owned.persons", 66058l);
			map.put("pharmacy.retail.site.solution", 68436l);
			map.put("pharmacy.site.certificate", 68410l);
			map.put("pharmacy.name.check", 68384l);
			map.put("extern.links", 68306l);
			map.put("extern.links", 68332l);*/
			
			/*dto = importMainServ.validateWF(dto);
			if(dto.isValid()) {
				System.out.println(dto.isValid());
			}*/
			//"https://pharmadex.irka.in.ua/ "http://localhost:" + port
			/*String url = "https://pharmadex.irka.in.ua/api/public/importwf/resources";
			dto = restTemplate.postForObject(url, dto, ImportWorkflowDTO.class);
			if(dto.isValid()) {
				//dto = importServ.createResourcesOnLocal(dto);
				
				System.out.println(dto.isValid());
			}*/
			
			//dto = importServ.createActivitiesConfigsOnLocal(dto, user);
		//}catch (ObjectNotFoundException e) {
		//	e.printStackTrace();
		//}
	}
	
}
