package org.msh.pharmadex2.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.WorkflowParamDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pdex2.services.r2.ClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
	//@Test
	public void executors_select() throws ObjectNotFoundException {
		Concept actConf = closureServ.loadConceptById(34445);
		History curHis = boilerServ.historyById(2759);
		List<String> executors = applServ.executors_select(actConf, curHis);
		System.out.println(executors);
	}
	
}
