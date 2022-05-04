package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
/**
 * Test the boiler 
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class BoilerServiceTests {
	@Autowired
	BoilerService boilerServ;
	@Autowired
	ClosureService closureServ;
	//@Test
	public void objectData() throws ObjectNotFoundException {
		List<Long> ids = Arrays.asList(
				83453l,
				83592l,
				87754l,
				87794l,
				88108l,
				88119l,
				96676l,
				96686l,
				83627l,
				106671l
			);
		for(Long id : ids) {
			Concept node = closureServ.loadConceptById(id);
			Concept root=boilerServ.objectData(node);
			assertEquals(83453, root.getID());
		}
	}
}
