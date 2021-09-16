package org.msh.pharmadex2.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ClosureService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Import Dictionary from file .json from path=src/test/resources
 * 
 * @author dudchenko
 *
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class DictionaryImportTest {
	@Autowired
	DictService dictServ;
	@Autowired
	ClosureService closureServ;
	@Autowired
	BoilerService boilerServ; 
	@Autowired
	JdbcRepository jdbcRepo;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private ObjectMapper objectMapper;

	//@Test
	public void loadDictionaryFromFile() throws JsonParseException, JsonMappingException, IOException, ObjectNotFoundException {
		Path pathFile = Paths.get("src","test","resources", DictionaryExportTest.FILE_NAME);
		File file = pathFile.toFile();
		OptionDTO rootDTO = objectMapper.readValue(file, OptionDTO.class);
		if(rootDTO != null) {
			//String dictUrl = rootDTO.getCode() + ".TEST";
			String dictUrl = DictionaryExportTest.DICTIONARY_URL_BY_IMPORT;
			//String dictUrl = rootDTO.getCode();
			Concept root = closureServ.loadRoot(dictUrl);

			List<OptionDTO> list = rootDTO.getOptions();
			if(list != null && list.size() > 0) {
				OptionDTO mainDTO = null;
				/* получаем карту variable - список значений которіе нужно записать (на всех языках)*/
				Map<String, List<OptionDTO>> mapVars = new HashMap<String, List<OptionDTO>>();
				for(OptionDTO dto:list) {
					if(dto.getDescription().equals(DictionaryExportTest.KEY_CHILDREN)) {
						mainDTO = dto;
					}else {
						if(mapVars.get(dto.getDescription()) == null) {
							mapVars.put(dto.getDescription(), new ArrayList<OptionDTO>());
						}
						mapVars.get(dto.getDescription()).add(dto);
					}
				}
				Iterator<String> it = mapVars.keySet().iterator();
				while(it.hasNext()) {
					String variable = it.next();
					Map<String, String> values = new HashMap<String, String>();
					for(OptionDTO dto:mapVars.get(variable)) {
						values.put(dto.getOriginalCode(), dto.getOriginalDescription());
					}
					root = literalServ.createUpdateLiteral(variable, root, values);
				}

				if(mainDTO != null)
					createLevel(mainDTO, root);

			}
		}
	}

	private void createLevel(OptionDTO mainDTO, Concept par) throws ObjectNotFoundException {
		List<OptionDTO> levelList = mainDTO.getOptions();
		if(levelList != null && levelList.size() > 0) {
			OptionDTO mainDTO_item = null;
			/*строим карту ключ(ид из старого словаря) - список значений, а потом уже списко будем разбирать */
			Map<String, List<OptionDTO>> mapItems = new HashMap<String, List<OptionDTO>>();
			for(OptionDTO dto:levelList	) {
				if(mapItems.get(dto.getCode()) == null) {
					mapItems.put(dto.getCode(), new ArrayList<OptionDTO>());
				}
				mapItems.get(dto.getCode()).add(dto);
			}

			Iterator<String> it = mapItems.keySet().iterator();
			while(it.hasNext()) {
				String key = it.next();
				/* получаем карту variable - список значений которіе нужно записать (на всех языках)*/
				Map<String, List<OptionDTO>> mapVars = new HashMap<String, List<OptionDTO>>();
				for(OptionDTO dto:mapItems.get(key)	) {
					if(dto.getDescription().equals(DictionaryExportTest.KEY_CHILDREN)) {
						mainDTO_item = dto;
					}else {
						if(mapVars.get(dto.getDescription()) == null) {
							mapVars.put(dto.getDescription(), new ArrayList<OptionDTO>());
						}
						mapVars.get(dto.getDescription()).add(dto);
					}
				}

				Concept parent = new Concept();
				parent = closureServ.save(parent);
				parent.setIdentifier(parent.getID() + "");
				parent = closureServ.saveToTree(par, parent);

				Iterator<String> itParent = mapVars.keySet().iterator();
				while(itParent.hasNext()) {
					String variable = itParent.next();
					Map<String, String> values = new HashMap<String, String>();
					for(OptionDTO dto:mapVars.get(variable)) {
						values.put(dto.getOriginalCode(), dto.getOriginalDescription());
					}
					parent = literalServ.createUpdateLiteral(variable, parent, values);
				}
				if(mainDTO_item != null)
					createLevel(mainDTO_item, parent);

				mainDTO_item = null;
			}

		}
	}
}
