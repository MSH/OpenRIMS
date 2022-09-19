package org.msh.pharmadex2.service;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.LinksDTO;
import org.msh.pharmadex2.service.r2.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=Pharmadex2Application.class)
public class LinkServiceTest {
	@Autowired
	LinkService linkServ;
	
	//@Test
	public void loadLinks() throws ObjectNotFoundException {
		LinksDTO dto = new LinksDTO();
		dto.setNodeID(208769);
		dto.setObjectUrl("site.owner.person");
		dto=linkServ.loadLinks(dto,"links");
	}
}
