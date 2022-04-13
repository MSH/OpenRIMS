package org.msh.pharmadex2.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.ImportFromExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Import Data for Dictionary admin unit from .xlsx file
 * 
 * province: identifier = "pr" + номер по порядку провинции;
 * district: identifier = identifier Province + "." + номер по порядку внутри провинции
 * municipality: identifier = колонка SN из файла импорта (номер по порядку)
 * 
 * @author dudchenko
 *
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImportAdmBangl {
	@Autowired
	DictService dictServ;
	
	@Autowired
	private ImportFromExcel importFromExcel;
	
	/** получаем все провинции 
	 * 
	 * перемещаем их все в корзину*/
	//@Test
	public void loadAllProvinces() throws ObjectNotFoundException {
		dictServ.loadAllProvinces();
	}
	
	@Test
	public void importAdmUnit() throws JsonParseException, JsonMappingException, IOException, ObjectNotFoundException {
		String fname = "Bangladesh-GIS coordinatesTest.xlsx";
		Path pathFile = Paths.get("src","test","resources", fname);
		byte[] bytes = Files.readAllBytes(pathFile);
		
		if(bytes.length > 0) {
			importFromExcel.importAdmUnit(bytes);
		}
	}

	
	
}
