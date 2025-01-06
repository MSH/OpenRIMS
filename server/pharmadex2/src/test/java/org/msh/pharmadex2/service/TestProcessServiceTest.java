package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.User;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.IntervalDTO;
import org.msh.pharmadex2.dto.RunTestProcessDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.msh.pharmadex2.service.r2.TestProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest(classes=Pharmadex2Application.class)
public class TestProcessServiceTest {
	private static Logger logger = logger = LoggerFactory.getLogger(TestProcessServiceTest.class);
	@Autowired
	TestProcessService testProcessServ;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	ClosureService closureServ;
	@Autowired
	UserService userServ;
	

	/**
	 * Solve a problem when a dictionary contains paths with different length, e.g.
	 * <ul>
	 * <li> a,a1,a2
	 * <li> b
	 * <li> c,c1
	 * </ul>
	 * @throws ObjectNotFoundException 
	 */
	@Test
	public void selectDictionary() throws ObjectNotFoundException {
		//mult-level symmetrical 5 probes
		Long selected = testProcessServ.selectDictionary(SystemService.DICTIONARY_ADMIN_UNITS);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary(SystemService.DICTIONARY_ADMIN_UNITS);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary(SystemService.DICTIONARY_ADMIN_UNITS);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary(SystemService.DICTIONARY_ADMIN_UNITS);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary(SystemService.DICTIONARY_ADMIN_UNITS);
		assertTrue(selected>0);
		System.out.println(selected);
		//mult-level asymmetrical 5 probes
		selected = testProcessServ.selectDictionary("dictionary.product.system.category.h");
		System.out.println(selected);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary("dictionary.product.system.category.h");
		System.out.println(selected);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary("dictionary.product.system.category.h");
		System.out.println(selected);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary("dictionary.product.system.category.h");
		System.out.println(selected);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary("dictionary.product.system.category.h");
		System.out.println(selected);
		assertTrue(selected>0);
		//single level 5 probes
		selected = testProcessServ.selectDictionary("dictionary.system.roles");
		System.out.println(selected);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary("dictionary.system.roles");
		System.out.println(selected);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary("dictionary.system.roles");
		System.out.println(selected);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary("dictionary.system.roles");
		System.out.println(selected);
		assertTrue(selected>0);
		selected = testProcessServ.selectDictionary("dictionary.system.roles");
		System.out.println(selected);
		assertTrue(selected>0);
	}
	
	@Test
	public void testDates() {
		ThingDTO dto= new ThingDTO();
		dto.getDates().put("date", FormFieldDTO.of(LocalDate.now()));
		Assembly assm = new Assembly();
		Concept c=new Concept();
		c.setIdentifier("date");
		assm.setPropertyName(c);
		assm.setRequired(true);
		//both are negative
		assm.setMin(100*12*-1);
		assm.setMax(18*12*-1);
		LocalDate minDate=LocalDate.now().plusMonths(assm.getMin()).minusDays(1);
		LocalDate maxDate=LocalDate.now().plusMonths(assm.getMax()).plusDays(1);
		dto=testProcessServ.resolveDates(assm, dto);
		LocalDate val=dto.getDates().get("date").getValue();
		System.out.println(assm.getMin()+"-"+assm.getMax()+":"+val);
		assertTrue(val.isAfter(minDate) && val.isBefore(maxDate));
		//between negative and positive
		assm.setMin(1*-1);
		assm.setMax(1);
		minDate=LocalDate.now().plusMonths(assm.getMin()).minusDays(1);
		maxDate=LocalDate.now().plusMonths(assm.getMax()).plusDays(1);
		dto=testProcessServ.resolveDates(assm, dto);
		val=dto.getDates().get("date").getValue();
		System.out.println(assm.getMin()+"-"+assm.getMax()+":"+val);
		assertTrue(val.isAfter(minDate) && val.isBefore(maxDate));
		//both are positive
		assm.setMin(1*12);
		assm.setMax(1*12);
		minDate=LocalDate.now().plusMonths(assm.getMin()).minusDays(1);
		maxDate=LocalDate.now().plusMonths(assm.getMax()).plusDays(1);
		dto=testProcessServ.resolveDates(assm, dto);
		val=dto.getDates().get("date").getValue();
		System.out.println(assm.getMin()+"-"+assm.getMax()+":"+val);
		assertTrue(val.isAfter(minDate) && val.isBefore(maxDate));
		//between zero, means today only
		assm.setMin(0);
		assm.setMax(0);
		dto=testProcessServ.resolveDates(assm, dto);
		val=dto.getDates().get("date").getValue();
		System.out.println(assm.getMin()+"-"+assm.getMax()+":"+val);
		assertEquals(LocalDate.now(), val);
		//not mandatory, means today only
		assm.setRequired(false);
		assm.setMin(1*12);
		assm.setMax(2*12);
		dto=testProcessServ.resolveDates(assm, dto);
		val=dto.getDates().get("date").getValue();
		System.out.println(val);
		assertEquals(LocalDate.now(), val);
	}
	
	@Test
	public void testNumbers() {
		ThingDTO dto= new ThingDTO();
		dto.getNumbers().put("numbers", FormFieldDTO.of(100l));
		Assembly assm = new Assembly();
		Concept c=new Concept();
		c.setIdentifier("numbers");
		assm.setPropertyName(c);
		assm.setRequired(true);
		
		logger.info("both negative");
		assm.setMin(-1000l);
		assm.setMax(-100);
		dto=testProcessServ.resolveNumbers(assm, dto);
		Long val=dto.getNumbers().get("numbers").getValue();
		assertTrue(val>=assm.getMin() && val<=assm.getMax());
		
		logger.info("negative to zero");
		assm.setMin(-1000l);
		assm.setMax(0);
		dto=testProcessServ.resolveNumbers(assm, dto);
		val=dto.getNumbers().get("numbers").getValue();
		assertTrue(val>=assm.getMin() && val<=assm.getMax());
		
		logger.info("zero to zero");
		assm.setMin(0);
		assm.setMax(0);
		dto=testProcessServ.resolveNumbers(assm, dto);
		val=dto.getNumbers().get("numbers").getValue();
		assertTrue(val>=assm.getMin() && val<=assm.getMax());
		
		logger.info("zero to positive");
		assm.setMin(0);
		assm.setMax(0);
		dto=testProcessServ.resolveNumbers(assm, dto);
		val=dto.getNumbers().get("numbers").getValue();
		assertTrue(val>=assm.getMin() && val<=assm.getMax());
		
		logger.info("positive to positive");
		assm.setMin(10);
		assm.setMax(100);
		dto=testProcessServ.resolveNumbers(assm, dto);
		val=dto.getNumbers().get("numbers").getValue();
		assertTrue(val>=assm.getMin() && val<=assm.getMax());
		
		logger.info("equals");
		assm.setMin(10);
		assm.setMax(10);
		dto=testProcessServ.resolveNumbers(assm, dto);
		val=dto.getNumbers().get("numbers").getValue();
		assertTrue(val>=assm.getMin() && val<=assm.getMax());
		
		logger.info("not mandatory");
		assm.setRequired(false);
		dto=testProcessServ.resolveNumbers(assm, dto);
		val=dto.getNumbers().get("numbers").getValue();
		assertEquals(100l,val);
	}
	
	@Test
	public void testIntervals() throws ObjectNotFoundException {
		ThingDTO dto= new ThingDTO();
		dto.getIntervals().put("intervals", new IntervalDTO());
		Assembly assm = new Assembly();
		Concept c=new Concept();
		c.setIdentifier("intervals");
		assm.setPropertyName(c);
		assm.setRequired(true);
		
		logger.info("in past");
		assm.setMax(-1);
		assm.setMin(12);
		dto=testProcessServ.resolveIntervals(assm, dto);
		LocalDate valFrom=dto.getIntervals().get("intervals").getFrom().getValue();
		LocalDate valTo=dto.getIntervals().get("intervals").getTo().getValue();
		assertTrue(valTo.isBefore(LocalDate.now().plusMonths(assm.getMax()).plusDays(1)));
		assertTrue(valFrom.isAfter(LocalDate.now().plusMonths(assm.getMax()).minusMonths(assm.getMin()).minusDays(1)));
		
		logger.info("up to today");
		assm.setMax(-1);
		assm.setMin(0);
		dto=testProcessServ.resolveIntervals(assm, dto);
		valFrom=dto.getIntervals().get("intervals").getFrom().getValue();
		valTo=dto.getIntervals().get("intervals").getTo().getValue();
		assertTrue(valTo.isBefore(LocalDate.now().plusMonths(assm.getMax()).plusDays(1)));
		assertTrue(valFrom.isAfter(LocalDate.now().plusMonths(assm.getMax()).minusMonths(assm.getMin()).minusDays(1)));
		
		logger.info("in future");
		assm.setMax(0);
		assm.setMin(10);
		dto=testProcessServ.resolveIntervals(assm, dto);
		valFrom=dto.getIntervals().get("intervals").getFrom().getValue();
		valTo=dto.getIntervals().get("intervals").getTo().getValue();
		assertTrue(valTo.isBefore(LocalDate.now().plusMonths(assm.getMax()).plusDays(1)));
		assertTrue(valFrom.isAfter(LocalDate.now().plusMonths(assm.getMax()).minusMonths(assm.getMin()).minusDays(1)));
		
		logger.info("exactly today");
		assm.setMax(0);
		assm.setMin(0);
		dto=testProcessServ.resolveIntervals(assm, dto);
		valFrom=dto.getIntervals().get("intervals").getFrom().getValue();
		valTo=dto.getIntervals().get("intervals").getTo().getValue();
		assertTrue(valTo.isBefore(LocalDate.now().plusMonths(assm.getMax()).plusDays(1)));
		assertTrue(valFrom.isAfter(LocalDate.now().plusMonths(assm.getMax()).minusMonths(assm.getMin()).minusDays(1)));
	}
	
	//@Test //COMMENT OUT IT!!!!
	public void testCheckList() throws ObjectNotFoundException {
		ThingDTO applDTO = new ThingDTO();
		applDTO.setHistoryId(9329);
		UserDetailsDTO applicant= testProcessServ.createApplicant("epharmadex@gmail.com");
		CheckListDTO checkListDto=testProcessServ.checklist(applicant, applDTO);
	}
	/**
	 * pass a completed draft application to approve
	 * @throws ObjectNotFoundException
	 * @throws IOException 
	 */
	//@Test //COMMENT OUT IT!!!!
	public void testApprove() throws ObjectNotFoundException, IOException {
		History his= boilerServ.historyById(9362l);	//CHANGECHANGECHANGE
		UserDetailsDTO currentUserDto = userServ.loadUserByEmail("alex.kurasoff@gmail.com",true);
		ThingDTO mainApplPage=new ThingDTO();
		mainApplPage.setNodeId(his.getApplicationData().getID());
		RunTestProcessDTO data=testProcessServ.reviewApplication("3",currentUserDto,
				new RunTestProcessDTO(),mainApplPage, SystemService.FINAL_ACCEPT);
	}
	//@Test //COMMENT OUT IT!!!!
	public void testPathFinalActivity() throws ObjectNotFoundException {
		History firstActivity=boilerServ.historyById(9580);
		List<Long> pathToApprove=testProcessServ.pathToFinalActivity(firstActivity, SystemService.FINAL_ACCEPT);
		assertTrue(pathToApprove.size()>1);
		int last=pathToApprove.size()-1;
		Concept finalConfig=closureServ.loadConceptById(pathToApprove.get(last));
		assertEquals("ACCEPT",testProcessServ.activityOutCome(finalConfig));
		
	}
	//@Test //COMMENT OUT IT!!!!
	public void testNextActivity() throws ObjectNotFoundException, JsonProcessingException {
		History firstActivity=boilerServ.historyById(9455);
		List<Long> pathToApprove=testProcessServ.pathToFinalActivity(firstActivity, SystemService.FINAL_ACCEPT);
		Long nextActivityCfgId=pathToApprove.get(0);
		UserDetailsDTO currentUserDto = userServ.loadUserByEmail("alex.kurasoff@gmail.com",true);
		History nextActivity=testProcessServ.nextActivity(firstActivity, currentUserDto, nextActivityCfgId);
		assertEquals(nextActivityCfgId, nextActivity.getActConfig().getID());
	}
	//@Test //COMMENT OUT IT!!!!
	public void testCompleteActivity() throws ObjectNotFoundException, IOException {
		History history=boilerServ.historyById(9455);
		UserDetailsDTO currentUserDto = userServ.loadUserByEmail("alex.kurasoff@gmail.com",true);
		testProcessServ.completeActivity(currentUserDto, history);
	}
	//@Test //COMMENT OUT IT!!!!
	public void testApproveApplication() throws ObjectNotFoundException, IOException {
		History history=boilerServ.historyById(9455);
		UserDetailsDTO currentUserDto = userServ.loadUserByEmail("alex.kurasoff@gmail.com",true);
		RunTestProcessDTO runTestProcessDto = new RunTestProcessDTO(); //only to error transfer, maybe should be removed
		runTestProcessDto=testProcessServ.stages(runTestProcessDto);
		ThingDTO mainApplPage=new ThingDTO();
		mainApplPage.setNodeId(history.getApplicationData().getID());
		testProcessServ.reviewApplication("03",currentUserDto, 
				runTestProcessDto, mainApplPage, SystemService.FINAL_ACCEPT);
	}
	
	@Test //it does not depend on the history id, etc.
	public void testSelectStages() {
		RunTestProcessDTO dto = new RunTestProcessDTO();
		dto=testProcessServ.stages(dto);
		for(long i=1;i<=6;i++) {
			for(TableRow row :dto.getStages().getRows()) {
				if(row.getDbID()==i) {
					row.setSelected(true);
				}else {
					row.setSelected(false);
				}
			}
			TableRow selectedRow=dto.getStages().getSelectedRow();
			TableRow declineRow=dto.getStages().getRows().get(3);
			List<TableRow> stages=testProcessServ.selectedStages(dto.getStages());
			if(selectedRow.getDbID()==4) {
				assertTrue(stages.contains(declineRow));
				assertEquals(3, stages.size());
			}else {
				assertFalse(stages.contains(declineRow));
			}
		}
	}

}
