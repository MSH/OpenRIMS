package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.SchedulerDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.service.r2.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes=Pharmadex2Application.class)
public class FollowUpServiceTest {
	@Autowired
	FollowUpService service;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	ClosureService closureServ;
	
	@Test
	@Transactional
	public void schedulerFromThing() throws ObjectNotFoundException {
		SchedulerDTO data = new SchedulerDTO();
		data.setVarName("renewdate");
		Concept node= closureServ.loadConceptById(74365);
		Thing thing = boilerServ.thingByNode(node);
		data = service.schedulerFromThing(thing, data);
		assertTrue(!service.isEmpty(data));
	}
	
	@Test
	public void schedulerFromSchedulers() throws ObjectNotFoundException {
		SchedulerDTO data = new SchedulerDTO();
		Concept applData= closureServ.loadConceptById(68936);
		data=service.schedulerFromSchedulers(applData, "pharmacy.site.inspection.schedule", data);
		assertTrue(!service.isEmpty(data));
	}

}
