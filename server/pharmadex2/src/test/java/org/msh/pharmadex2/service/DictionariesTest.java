package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.ConceptRepo;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.annotation.Rollback;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test data dictionaries CRUD
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class DictionariesTest {
	@Autowired
	DictService dictServ;
	@Autowired
	ClosureService closureServ;
	@Autowired
	BoilerService boilerServ; 
	@Autowired
	JdbcRepository jdbcRepo;
	@Autowired
	private Messages messages;
	@Autowired
	private LiteralService literalServ;

	@Autowired
	private ConceptRepo conceptRepo;
	@Autowired
	private ObjectMapper objectMapper;



	//@Test
	public void createCheckListDict() throws ObjectNotFoundException {
		// by Review
		String[] values = {"1. Pharmacy details", "2. Owner details", "3. Pharmacist / MAnager details", 
				"4. Dispencer details", "5. Verify uploaded documents"};

		Concept root = closureServ.loadRoot("dictionary.checklists.newpharmacy.review");
		literalServ.createUpdatePrefLabel("Checklist review", root);
		literalServ.createUpdateDescription("", root);

		for(String v:values) {
			Concept it = new Concept();
			it = closureServ.save(it);
			it.setIdentifier(it.getID() + "");
			it = closureServ.saveToTree(root, it);
			literalServ.createUpdatePrefLabel(v, it);
			literalServ.createUpdateDescription("", it);
		}

		// by Inspector
		String[] valuesInsp = {"Fully complies", "Modifications are needed"};

		root = closureServ.loadRoot("dictionary.checklists.newpharmacy.inspect");
		literalServ.createUpdatePrefLabel("Checklist inspect", root);
		literalServ.createUpdateDescription("", root);

		for(String v:valuesInsp) {
			Concept it = new Concept();
			it = closureServ.save(it);
			it.setIdentifier(it.getID() + "");
			it = closureServ.saveToTree(root, it);
			literalServ.createUpdatePrefLabel(v, it);
			literalServ.createUpdateDescription("", it);
		}

		// by Moderator
		String[] valuesApprov = {"Fully complies", "Modifications are needed"};

		root = closureServ.loadRoot("dictionary.checklists.newpharmacy.approv");
		literalServ.createUpdatePrefLabel("Checklist approval", root);
		literalServ.createUpdateDescription("", root);

		for(String v:valuesApprov) {
			Concept it = new Concept();
			it = closureServ.save(it);
			it.setIdentifier(it.getID() + "");
			it = closureServ.saveToTree(root, it);
			literalServ.createUpdatePrefLabel(v, it);
			literalServ.createUpdateDescription("", it);
		}

		// by Accounter
		String[] valuesAccount = {"Fully complies", "Modifications are needed"};

		root = closureServ.loadRoot("dictionary.checklists.newpharmacy.account");
		literalServ.createUpdatePrefLabel("Checklist account", root);
		literalServ.createUpdateDescription("", root);

		for(String v:valuesAccount) {
			Concept it = new Concept();
			it = closureServ.save(it);
			it.setIdentifier(it.getID() + "");
			it = closureServ.saveToTree(root, it);
			literalServ.createUpdatePrefLabel(v, it);
			literalServ.createUpdateDescription("", it);
		}
	}

	//@Test
	public void createDict() throws ObjectNotFoundException{
		Concept root = closureServ.loadRoot("dictionary.persons.retail.pharmacy");

		String[] values = {"01. Owner.", "02. Pharmacist/Manager.", "03. Dispenser/Employee."};
		literalServ.createUpdatePrefLabel("Persons retail pharmacy", root);
		literalServ.createUpdateDescription("", root);

		for(String v:values) {
			Concept it = new Concept();
			it = closureServ.save(it);
			it.setIdentifier(it.getID() + "");
			it = closureServ.saveToTree(root, it);
			literalServ.createUpdatePrefLabel(v, it);
			literalServ.createUpdateDescription("", it);
		}
	}

	//@Test
	public void updateAdminUnitDict() throws ObjectNotFoundException{
		Map<String, String> mapCenters = buildMapCenters();
		Map<String, String> mapZooms = buildMapZooms();

		Concept root = closureServ.loadRoot("dictionary.admin.units");
		literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, "27.703430;85.318418", root);
		literalServ.createUpdateLiteral(LiteralService.ZOMM, "8", root);

		List<Concept> provinces = literalServ.loadOnlyChilds(root);
		if(provinces != null && provinces.size() > 0){
			for(Concept p:provinces) {
				DictNodeDTO node = dictServ.createNode(p,"dictionary.admin.units");
				String value = node.fetchPrefLabel().getValue();
				String center = mapCenters.get(value);
				if(center != null) {
					node.fetchPrefLabel().setValue(center);
				}
				String zoom = mapZooms.get(value);
				if(zoom != null) {
					FormFieldDTO<String> ff = new FormFieldDTO<String>();
					ff.setValue(zoom);
					node.getLiterals().put(LiteralService.ZOMM, ff);
				}
				node = dictServ.save(node);
			}
		}

	}

	private Map<String, String> buildMapCenters(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("Bagmati Province", "27.703430;85.318418");
		map.put("Gandaki Province", "28.208735;83.985706");
		map.put("Karnali Province", "29.1631;82.1100");
		return map;
	}

	private Map<String, String> buildMapZooms(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("Bagmati Province", "10");
		map.put("Gandaki Province", "10");
		map.put("Karnali Province", "10");
		return map;
	}
	
	@Test
	public void dictPath() throws ObjectNotFoundException {
		Concept node = closureServ.loadConceptById(21091);
		String ret = dictServ.dictPath("en_us", node);
		System.out.println(ret);
		assertTrue(ret.length()>0);
	}
	
	@Test
	@Rollback(false)
	public void storePath() throws ObjectNotFoundException {
		Concept node = closureServ.loadConceptById(68121);
		Concept dictNode=closureServ.loadConceptById(27097);
		node=dictServ.storePath(dictNode, node);
		
	}
	

}
