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
	@Autowired
	JdbcRepository jdbcRepo;
	
	
	@Test
	public void createWorkflowParameters() throws JsonProcessingException {
		WorkflowParamDTO wpdto = new WorkflowParamDTO();
		wpdto.setChecklistUrl("dictionary.selfcheck.new.pharmacy");
		System.out.println(objectMapper.writeValueAsString(wpdto));
		
	}
	@Test
	public void executors_select() throws ObjectNotFoundException {
		Concept actConf = closureServ.loadConceptById(34316);
		History prevHis = boilerServ.historyById(3295);
		Concept applConc=null;
		Concept dictConc=closureServ.loadConceptById(34302);
		Concept configRoot=closureServ.loadConceptById(34316);
		History curHis=applServ.createHostHistorySample(prevHis, applConc, dictConc, configRoot);
		List<String> executors = applServ.executors_select(actConf, curHis);
		System.out.println(executors);
	}
	
	@Test
	@Transactional
	public void executors_select_jdbc() throws ObjectNotFoundException {
		History curHis = boilerServ.historyById(3385);
		Concept actConf = closureServ.loadConceptById(34445);
		TableQtb execTable = new TableQtb();
		execTable.setHeaders(applServ.headersExecutors(execTable.getHeaders()));
		TableQtb table = applServ.executorsTable(curHis, actConf, execTable, false);
		for(TableRow tr : table.getRows()) {
			System.out.println(tr.getRow().get(0));
		}
	}
}
