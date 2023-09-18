package org.msh.pharmadex2.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.util.SystemOutLogger;
import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.ExchangeConfigDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.r2.ExchangeConfigMainService;
import org.msh.pharmadex2.service.r2.ExchangeConfigurationService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test methods in the ExchangeConfigurationService
 * @author khomenska
 *
 */
@ActiveProfiles({"test"})
@SpringBootTest(classes = Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ExchangeConfigTest {

	@Autowired
	ExchangeConfigurationService exchangeServ;
	@Autowired
	ExchangeConfigMainService exchangeMainServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private Messages messages;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	//@Test
	public void testTableQtbRender() {
		ExchangeConfigDTO dto = new ExchangeConfigDTO();
		try {
			Concept dictByCopy = closureServ.loadRoot("dictionary.admin.units");
			OptionDTO opt = new OptionDTO();
			opt.setId(dictByCopy.getID());// id dictionary on MainServer by copy on UserService
			dto.setDictByCopy(opt);

			//dto.setCurLang("uk_UA");
			dto.setCurLang("en_US");
			dto.setOtherLang("NE_NP");

			String url = "http://localhost:" + port + "/api/public/exchange/config/importdict";
			dto = restTemplate.postForObject(url, dto, ExchangeConfigDTO.class);

			if(dto.getUrlByCopy() != null && dto.getUrlByCopy().length() > 0) {

				String curLang = dto.getCurLang(); // current lang on clientServer
				String lang = dto.getOtherLang();

				OptionDTO rootDTO = dto.getDictByCopy();
				Concept root = closureServ.loadRoot(dto.getUrlByCopy() + ".test12");

				Map<String, String> values = new HashMap<String, String>();// одно из значений всегда заполнено
				String curV = !rootDTO.getOriginalCode().isEmpty()?rootDTO.getOriginalCode():rootDTO.getCode();
				String v = !rootDTO.getCode().isEmpty()?rootDTO.getCode():rootDTO.getOriginalCode();
				values.put(curLang, curV);
				values.put(lang, v);
				root = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, root, values);

				values = new HashMap<String, String>();
				curV = !rootDTO.getOriginalDescription().isEmpty()?rootDTO.getOriginalDescription():rootDTO.getDescription();
				v = !rootDTO.getDescription().isEmpty()?rootDTO.getDescription():rootDTO.getOriginalDescription();
				values.put(curLang, curV);
				values.put(lang, v);
				root = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, root, values);
				root.setActive(rootDTO.isActive());

				root = closureServ.saveToTree(null, root);

				exchangeServ.recursionCreateDictionary(rootDTO.getOptions(), root, curLang, lang);

				System.out.println(root.getIdentifier());

			}
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}
	
	@Test
	public void test() throws ObjectNotFoundException {
		ExchangeConfigDTO dto = new ExchangeConfigDTO();
		dto.setItProcessID(66058l);
		dto.setCurLang(messages.getDefLocaleFromBundle());
		
		//dto = exchangeMainServ.importWorkflows(dto);
		dto.setValid(true);
		//dto = exchangeServ.byTestImport(dto);
		
		dto.getItProcessID();

	}
	
}
