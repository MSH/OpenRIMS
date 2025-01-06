package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.PublicOrganization;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.PublicOrgDTO;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.PubOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Test public orgalnization CRUD
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class PubOrgServiceTest {
	
	@Autowired
	PubOrgService pubOrgServ;
	@Autowired
	DtoService dtoServ;
	@Autowired
	DictService dictServ;
	
	/**
	 * Requires test organization
	 * @throws ObjectNotFoundException 
	 */
	@Test
	public void testLoadRoot() throws ObjectNotFoundException {
		PublicOrganization org = pubOrgServ.loadById(2);
		DictNodeDTO node = dictServ.createNode(org.getConcept(),null);
		PublicOrgDTO dto = pubOrgServ.loadByConcept(node); 
		assertEquals(2L, dto.getId());
	}
	
	
}
