package org.msh.pharmadex2.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.AboutDTO;
import org.msh.pharmadex2.dto.ActivityDTO;
import org.msh.pharmadex2.dto.ActivityToRun;
import org.msh.pharmadex2.dto.WorkflowParamDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pdex2.services.r2.ClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test application service
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class ApplicationServiceTest {
	@Autowired
	ClosureService closureServ;
	@Autowired
	ApplicationService applServ;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	ObjectMapper objectMapper;
	
	
	@Test
	public void createWorkflowParameters() throws JsonProcessingException {
		WorkflowParamDTO wpdto = new WorkflowParamDTO();
		wpdto.setChecklistUrl("dictionary.selfcheck.new.pharmacy");
		System.out.println(objectMapper.writeValueAsString(wpdto));
		
	}
	/**
	 * Search for any not submitted test application and try to build a list on activities to run
	 * Print this list to check manually 
	 * @throws ObjectNotFoundException
	 */
	@Test
	public void activitiesAndExcutors() throws ObjectNotFoundException {
		//get all activities
		Concept configRoot = closureServ.loadRoot("configuration.application.workflow.junit");
		List<Concept> nextActs = applServ.loadActivities(configRoot);
		//search for a first not submitted application
		Concept root = closureServ.loadRoot("workflow.junit.test");	
		List<Concept> emails=closureServ.loadLevel(root);
		for(Concept email :emails) {
			List<Concept> appls= closureServ.loadLevel(email);
			for(Concept applData : appls) {
				List<History> hlist = boilerServ.historyAll(applData);
				if(hlist.size()==1) {
					History curHis = hlist.get(0);
					AboutDTO data = new AboutDTO();		//may be any that extends AllowValidation 
					List<ActivityToRun> acts=applServ.activitiesToRun(data, "application.workflow.junit", curHis, nextActs);
					for(ActivityToRun act : acts) {
						System.out.println(act);
					}
					return;
				}
			}
		}
	}
}
