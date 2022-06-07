package org.msh.pharmadex2.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.r2.ImportAService;
import org.msh.pharmadex2.service.r2.LegacyDataService;
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
	@Autowired
	ImportAService importAService;
	
	
	//@Test
	public void importPharmacies() throws IOException, ObjectNotFoundException {
		Path pathFile = Paths.get("src","test","resources", "all branch retail pharmacies.xlsx");
		byte[] bytes = Files.readAllBytes(pathFile);
		if(bytes.length > 0) {
			XSSFWorkbook wb = legacyService.importLegacyData(bytes);
			Path pathFileOut = Paths.get("src","test","resources", "all branch retail pharmacies.xlsxOut.xlsx");
			File fileout = pathFileOut.toFile();
			FileOutputStream fos = new FileOutputStream(fileout);
			wb.write(fos);
			fos.flush();
			fos.close();
		}
	}
}
