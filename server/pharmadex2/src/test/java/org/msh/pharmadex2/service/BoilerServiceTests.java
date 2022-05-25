package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
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
	@Test
	public void objectData() throws ObjectNotFoundException {
		List<Long> ids = Arrays.asList(
				83453l,
				83467l,
				83471l,
				83475l,
				83505l,
				83534l,
				83592l,
				83609l,
				83627l,
				83639l,
				87740l,
				87747l,
				87809l,
				87815l,
				87902l,
				88108l,
				88119l,
				96676l,
				96686l,
				101227l,
				101262l,
				101279l,
				101283l,
				106667l,
				106671l
			);
		for(Long id : ids) {
			Concept node = closureServ.loadConceptById(id);
			Concept root=boilerServ.initialApplicationNode(node);
			assertEquals(83453, root.getID());
		}
	}
	
	@Test
	public void testLocalTime() {
		LocalDate ld = LocalDate.now();
		Date dt = boilerServ.localDateToDate(ld);
		LocalDate ld1 = boilerServ.localDateFromDate(dt);
		assertEquals(ld, ld1);
		
	}
}
