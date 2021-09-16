package org.msh.pharmadex2.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.ClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
	
	//@Test
	public void findExecutors() throws ObjectNotFoundException {
		Concept actConf = closureServ.loadConceptById(14064);
		History his = boilerServ.historyById(66);
		List<String> execs = applServ.findExecutors(actConf, his);
		System.out.println(execs);
	}
	//@Test
	public void loadActivities() throws ObjectNotFoundException {
		Concept configRoot = closureServ.loadConceptById(14064);
		List<Concept> nextActs = applServ.loadActivities(configRoot);
		System.out.println(nextActs);
	}
	
}
