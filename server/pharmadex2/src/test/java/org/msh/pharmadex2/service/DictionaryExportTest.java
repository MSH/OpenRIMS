package org.msh.pharmadex2.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Export Dictionary to file .json to path=src/test/resources
 * 
 * @author dudchenko
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class DictionaryExportTest {
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

	/** URL словаря который сохраняем */
	public static final String DICTIONARY_URL_BY_EXPORT = "dictionary.admin.units.new";
	/** URL нового словаря */
	public static final String DICTIONARY_URL_BY_IMPORT = "dictionary.admin.units.new";
	/** URL словаря который уже есть в БД*/
	public static final String DICTIONARY_URL_IN_DB = "dictionary.admin.units.new";
	public static final String KEY_CHILDREN = "CHILDREN";
	/** имя файла будет как URL словаря который сохраняем */
	public static final String FILE_NAME = DICTIONARY_URL_BY_EXPORT + ".json";
	
	//@Test
	public void saveDictionaryToFile() throws ObjectNotFoundException, JsonGenerationException, JsonMappingException, IOException {
		Concept root = closureServ.loadRoot(DICTIONARY_URL_BY_EXPORT);
		OptionDTO rootDTO = new OptionDTO();
		rootDTO.setId(0);
		rootDTO.setCode(DICTIONARY_URL_BY_EXPORT);

		Concept literals = literalServ.loadLiterals(root);
		List<Concept> variables = closureServ.loadLevel(literals);
		for(Concept variable : variables) {
			List<Concept> langItems = literalServ.loadOnlyChilds(variable);
			for(Concept lang: langItems) {
				OptionDTO childDTO = new OptionDTO();
				childDTO.setCode("0");
				childDTO.setDescription(variable.getIdentifier());
				childDTO.setOriginalCode(lang.getIdentifier());
				childDTO.setOriginalDescription(lang.getLabel());
				rootDTO.getOptions().add(childDTO);
			}
		}

		OptionDTO childDTO_main = new OptionDTO();
		childDTO_main.setCode("0");
		childDTO_main.setDescription(KEY_CHILDREN);

		rootDTO.getOptions().add(childDTO_main);
		loadLevel(root, childDTO_main);

		Path pathFile = Paths.get("src","test","resources", FILE_NAME);
		objectMapper.writeValue(pathFile.toFile(), rootDTO);
	}

	private void loadLevel(Concept root, OptionDTO childrenDTO) throws ObjectNotFoundException {
		List<Concept> levelList = literalServ.loadOnlyChilds(root);
		for(Concept l:levelList) {
			OptionDTO mainDTO = new OptionDTO();
			mainDTO.setCode(l.getIdentifier());
			mainDTO.setDescription(KEY_CHILDREN);
			childrenDTO.getOptions().add(mainDTO);

			Concept literals = literalServ.loadLiterals(l);
			List<Concept> variables = closureServ.loadLevel(literals);
			for(Concept variable : variables) {
				List<Concept> langItems = literalServ.loadOnlyChilds(variable);
				for(Concept lang: langItems) {
					OptionDTO childDTO = new OptionDTO();
					childDTO.setCode(l.getIdentifier());
					childDTO.setDescription(variable.getIdentifier());
					childDTO.setOriginalCode(lang.getIdentifier());
					childDTO.setOriginalDescription(lang.getLabel());

					childrenDTO.getOptions().add(childDTO);
				}
			}
			loadLevel(l, mainDTO);
		}
	}
}
