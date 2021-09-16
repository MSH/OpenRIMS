package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.controller.common.DocxView;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ClosureService;
import org.msh.pharmadex2.service.r2.ResolverService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test variable resolve
 * Test here depends on Ids, therefore cannot be used as a "pure" unit tests
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class ResolverServiceTest {
	@Autowired
	ResolverService resolverServ;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	ResourceService resServ;
	@Autowired
	ClosureService closureServ;
	
	//@Test
	public void topConcept() throws ObjectNotFoundException {
		Concept conc = resolverServ.topConcept("pharmacy.site", 322);
		assertEquals(20005, conc.getID());
		conc=resolverServ.topConcept("pharmacy.site.inspection.reports", 322);
		assertEquals(20298, conc.getID());
	}
	//@Test
	@Transactional
	public void nextConcept() throws ObjectNotFoundException {
		Concept top = resolverServ.topConcept("pharmacy.site", 322);
		Concept conc = resolverServ.nextConcept("classifiers", top);
		Thing claThi = boilerServ.loadThingByNode(conc);
		assertEquals(4, claThi.getDictionaries().size());
	}
	
	//@Test
	@Transactional
	public void readVariable() throws ObjectNotFoundException {
		Concept top = resolverServ.topConcept("pharmacy.site", 322);
		Map<String,Object> val = resolverServ.readVariable("prefLabel", top, new LinkedHashMap<String, Object>());
		assertNotNull(val.get("literal"));
		val = resolverServ.readVariable("wadano", top, new LinkedHashMap<String, Object>());
		assertNotNull(val.get("number"));
		Concept payment = resolverServ.nextConcept("payment", top);
		val=resolverServ.readVariable("dateofpayment", payment, new LinkedHashMap<String, Object>());
		assertNotNull(val.get("date"));
	}
	/**
	 * Prepare a model from MS word file
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	//@Test
	public void prepareModel() throws ObjectNotFoundException, IOException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream("Invoice2000-1.docx");
		assertTrue(stream.available()>0);
		DocxView dx = new DocxView(stream);
		Map<String,Object> model = dx.initModel();
		ResourceDTO td = new ResourceDTO();
		td.setHistoryId(322);
		model = resolverServ.resolveModel(model,td);
		for(String key :model.keySet()) {
			System.out.println(key+"=>"+model.get(key));
		}
	}
}
