package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.r2.LegacyDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test legacy data import, etc
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class LegacyDataServiceTest {
	
	@Autowired
	LegacyDataService legacyService;
	
	@Test
	public void importPharmacies() throws IOException, ObjectNotFoundException {
		Path pathFile = Paths.get("src","test","resources", "LegacyDataSample.xlsx");
		byte[] bytes = Files.readAllBytes(pathFile);
		if(bytes.length > 0) {
			String mess = legacyService.importData(bytes,"legacy.pharmacies");
			assertEquals("", mess);
		}
	}
}
