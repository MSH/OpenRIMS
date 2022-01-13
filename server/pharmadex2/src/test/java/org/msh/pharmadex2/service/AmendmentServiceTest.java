package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.DataUnitDTO;
import org.msh.pharmadex2.service.r2.AmendmentService;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=Pharmadex2Application.class)
public class AmendmentServiceTest {
	@Autowired
	ClosureService closureServ;
	@Autowired
	AmendmentService amendServ;
	@Autowired
	LiteralService literalServ;
	
	//@Test
	public void dataUnits() throws ObjectNotFoundException {
		List<DataUnitDTO> nodes = new ArrayList<DataUnitDTO>();
		Concept rootNode = closureServ.loadConceptById(68936);
		String dataUrl = closureServ.getUrlByNode(rootNode);
		nodes=amendServ.dataNodes(rootNode, dataUrl,literalServ.readPrefLabel(rootNode),"", nodes);
		assertTrue(nodes.size()>0);
	}
	@Test
	public void dataUnitsNew() throws ObjectNotFoundException {
		List<DataUnitDTO> nodes = new ArrayList<DataUnitDTO>();
		Concept rootNode = closureServ.loadConceptById(68936);
		nodes=amendServ.dataNodes(rootNode);
		assertTrue(nodes.size()>0);
	}
	@Test
	public void reversePath() throws ObjectNotFoundException {
		//pure root
		Concept node = closureServ.loadConceptById(68105);
		List<DataUnitDTO> path = amendServ.reversePath(node);
		assertTrue(path.size()==1);
		//aux (classifiers)
		node = closureServ.loadConceptById(68129);
		path = amendServ.reversePath(node);
		assertTrue(path.size()==2);
		//person root
		node = closureServ.loadConceptById(68139);
		path = amendServ.reversePath(node);
		assertTrue(path.size()==3);
		//person aux
		node = closureServ.loadConceptById(68264);
		path = amendServ.reversePath(node);
		assertTrue(path.size()==4);
	}
}
