package org.msh.pharmadex2.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.controller.r2.ExcelViewMult;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.service.r2.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test report related serices
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class ReportServiceTest {
	@Autowired
	ReportService reportServ;
	
	@Test
	public void dataConfiguration() throws ObjectNotFoundException {
		Map<String, DataCollectionDTO> ret=new LinkedHashMap<String, DataCollectionDTO>();
		ret = reportServ.dataConfigurations("root","retail.site.owned.persons", ret);
		ret.get("retail.site.owned.persons");
	}
	@Test
	public void dictConfiguration() throws ObjectNotFoundException {
		Map<String, DataCollectionDTO> ret=new LinkedHashMap<String, DataCollectionDTO>();
		ret = reportServ.dictConfigurations("root","dictionary.pharmacy.registration.fees", ret);
		ret.get("dictionary.pharmacy.registration.fees");
	}
	@Test
	public void excelReport() throws ObjectNotFoundException, IOException {
		Map<String, DataCollectionDTO> data =new LinkedHashMap<String, DataCollectionDTO>();
		data = reportServ.dataConfigurations("root","retail.site.owned.persons", data);
		Map<String,Map<String,DataCollectionDTO>> model= new LinkedHashMap<String, Map<String,DataCollectionDTO>>();
		model.put("data",data);
		XSSFWorkbook workbook = new XSSFWorkbook();
		ExcelViewMult excel = new ExcelViewMult();
		excel.buildWorkbook(model, workbook);
		Path pathFileout = Paths.get("src","test","resources", "reportDataStructure.xlsx");
		File fileout = pathFileout.toFile();
		FileOutputStream fos = new FileOutputStream(fileout);
		workbook.write(fos);
		fos.flush();
		fos.close();	
	}
}
