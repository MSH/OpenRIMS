package org.msh.pharmadex2.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.r2.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=Pharmadex2Application.class)
public class SystemServiceTest {

	@Autowired
	SystemService systemServ;
	@Test
	public void responsibility() throws ObjectNotFoundException {
		List<Concept> ret  = systemServ.guestWorkflows("application.amend.inn.manuf");
		System.out.println(ret);
	}

}
