package org.msh.pdex2.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.repository.r2.ThingPersonRepo;
import org.msh.pdex2.repository.r2.ThingThingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class GetPermitDataUnitTest {
	@Autowired
	ThingThingRepo thingThingRepo;
	@Autowired
	ThingPersonRepo thingPersonRepo;
	
	@Test
	public void dataModulePageId() {
		//no page
		Long pageID=thingThingRepo.testNotPageID();
		Optional<Long> ido = thingThingRepo.dataModulePageId(pageID);
		assertFalse(ido.isPresent());
		//main page
		pageID=thingThingRepo.testMainPage();
		ido = thingThingRepo.dataModulePageId(pageID);
		assertEquals(pageID, ido.get());
		//other page
		Long otherPageID=thingThingRepo.testOtherPage(pageID);
		ido = thingThingRepo.dataModulePageId(otherPageID);
		assertEquals(pageID, ido.get());
		//Persons
		//no "persons" page
		ido=thingPersonRepo.permitPageByPersonsMainPage(pageID);
		assertFalse(ido.isPresent());
		//Existing persons page
		Long personsPageID=thingPersonRepo.testMainPersonsPage(pageID);	//one "persons" data module linked to some permit page
		ido=thingPersonRepo.permitPageByPersonsMainPage(personsPageID);							//page in the permit on which "persons" can be found
		ido = thingThingRepo.dataModulePageId(ido.get());							//main page of the permit
		assertEquals(pageID, ido.get());
		
	}
}
