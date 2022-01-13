package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.i18n.LocaleContextHolder;

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

	/**
	 * CRUD a fiction dictionary
	 * @throws ObjectNotFoundException 
	 */
	//@Test
	public void testCRUDDictionary() throws ObjectNotFoundException {

		//remove the old dictionary if one
		Concept root = closureServ.loadRoot("dict.fiction.test");
		closureServ.removeNode(root);

		//create an empty dictionary
		root = closureServ.loadRoot("dict.fiction.test");

		//add two items on the root
		//fill out data on the client
		DictNodeDTO firstOpt = new DictNodeDTO();
		firstOpt.fetchPrefLabel().setValue("irka");
		firstOpt.fetchDescription().setValue("svinka");
		firstOpt.setNodeId(0);
		firstOpt.setParentId(root.getID());
		//add to the he dictionary
		firstOpt = dictServ.save(firstOpt);

		//fill out data on the client
		DictNodeDTO secOpt= new DictNodeDTO();
		secOpt.fetchPrefLabel().setValue("korova");
		secOpt.fetchDescription().setValue("zdorova");
		secOpt.setNodeId(0);
		secOpt.setParentId(root.getID());
		//add to the he dictionary
		firstOpt = dictServ.save(secOpt);

		//test it
		DictNodeDTO level = new DictNodeDTO();
		level.setUrl("dict.fiction.test");
		level = dictServ.loadLevel(level);
		assertEquals(2, level.getTable().getRows().size());
		DictNodeDTO dictEl = dictServ.loadNodeById(level.getTable().getRows().get(0).getDbID());
		assertEquals("irka", dictEl.fetchPrefLabel().getValue());

		//another language
		//switch locale
		Locale oldLocale = LocaleContextHolder.getLocale();
		LocaleContextHolder.setLocale(new Locale("pt"));
		//load options
		//fill out data on the client and add to the server
		for(TableRow row : level.getTable().getRows()) {
			DictNodeDTO opt = new DictNodeDTO();
			opt.setNodeId(row.getDbID());
			opt.setParentId(root.getID());
			opt.fetchPrefLabel().setValue(row.getRow().get(0)+"_pt");
			opt.fetchDescription().setValue(row.getRow().get(1)+"_pt");
			dictServ.save(opt);
		}
		//test it
		assertEquals(2, level.getTable().getRows().size());
		//restore locale
		LocaleContextHolder.setLocale(oldLocale);


		//select an item on the first level and load next level
		//selection was on a client
		DictNodeDTO nextLevel = new DictNodeDTO();
		nextLevel.setParentId(level.getTable().getRows().get(0).getDbID());
		nextLevel = dictServ.loadLevel(nextLevel);
		//should be empty yet
		assertEquals(0, nextLevel.getTable().getRows().size());

		//add an item to the second level
		DictNodeDTO opt2 = new DictNodeDTO();
		opt2.fetchPrefLabel().setValue("second");
		opt2.fetchDescription().setValue("secondary");
		opt2.setParentId(level.getTable().getRows().get(0).getDbID());
		opt2.setNodeId(0);

		Concept selectedNode = closureServ.loadConceptById(nextLevel.getParentId());
		opt2 = dictServ.save(opt2);
		DictNodeDTO thirdLevel = new DictNodeDTO();
		thirdLevel.setParentId(selectedNode.getID());
		thirdLevel = dictServ.loadLevel(thirdLevel);
		//should be only just added
		assertEquals(1, thirdLevel.getTable().getRows().size());

		//remove the test dictionary if one
		root = closureServ.loadRoot("dict.fiction.test");
		closureServ.removeNode(root);
	}

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
				DictNodeDTO node = dictServ.createNode(p);
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
}
