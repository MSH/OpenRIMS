package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.controller.common.DocxView;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.service.r2.ResolverService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
	
	
	/**
	 * Prepare a model from MS word file
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	//@Test
	public void prepareModel() throws ObjectNotFoundException, IOException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream("Invoice2000-1.docx");
		assertTrue(stream.available()>0);
		DocxView dx = new DocxView(stream, boilerServ);
		Map<String,Object> model = dx.initModel();
		ResourceDTO td = new ResourceDTO();
		td.setHistoryId(322);
		model = resolverServ.resolveModel(model,td);
		for(String key :model.keySet()) {
			System.out.println(key+"=>"+model.get(key));
		}
	}
	//@Test
	public void character() throws ObjectNotFoundException {
		ResourceDTO fres = new ResourceDTO();
		fres.setHistoryId(1868);
		Map<String, List<AssemblyDTO>> assemblies = new HashMap<String, List<AssemblyDTO>>();
		Map<String, Object> ret = resolverServ.resolve("character/pharmacist/prefLabel", fres,assemblies);
		System.out.println(ret);
	}
	//@Test
	public void persons() throws ObjectNotFoundException {
		ResourceDTO fres = new ResourceDTO();
		fres.setHistoryId(2058);
		Map<String, List<AssemblyDTO>> assemblies = new HashMap<String, List<AssemblyDTO>>();
		Map<String, Object> ret = resolverServ.resolve("/pharmacists/pharmacists/0/pharmacist_qualification/person_academic", fres,assemblies);
		System.out.println(ret);
	}
	
	@Test
	public void resolveNepaliToString() {
		Locale locale = new Locale("ne","NP'");
		String str = String.format(locale,"%,d", 1234567890);
		str=boilerServ.numberToNepali(str);
		System.out.println(str);
		str=boilerServ.localDateToNepali(LocalDate.now(), false);
		System.out.println(str);
		str=boilerServ.localDateToNepali(LocalDate.now(), true);
		System.out.println(str);
		LocalDate ld = LocalDate.now().minusYears(2);
		int fullNep = boilerServ.fullYearsNepali(ld);
		System.out.println("minus two years - "+ fullNep);
		ld=ld.minusDays(1);
		fullNep = boilerServ.fullYearsNepali(ld);
		System.out.println("minus two years and a day - "+ fullNep);
		ld=ld.plusYears(4);
		fullNep = boilerServ.fullYearsNepali(ld);
		System.out.println("plus 4 years - "+ fullNep);
		ld=ld.plusDays(2);
		fullNep = boilerServ.fullYearsNepali(ld);
		System.out.println("and plus two days - "+ fullNep);
		
	}
}
