package org.msh.pharmadex2.service;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.r2.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



/**
 * Test Thing service
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class ThingServiceTest {
	@Autowired
	ThingService thingServ;
	@Autowired
	Messages messages;
	
	//@Test
	public void loadThing() throws ObjectNotFoundException {
		ThingDTO dto = new ThingDTO();
		dto.setNodeId(20005l);
		UserDetailsDTO user = new UserDetailsDTO();
		user.setEmail("oleksiik@unops.org");
		dto=thingServ.loadThing(dto, user);
		System.out.println(messages.get(dto.getUrl()));
		System.out.println(dto.getLiterals().get("prefLabel").getValue());
	}
	
}
