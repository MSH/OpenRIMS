package org.msh.pharmadex2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.controller.common.CustomXWPFDocument;
import org.msh.pharmadex2.controller.common.DocxView;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ClosureService;
import org.msh.pharmadex2.service.r2.ResolverService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test variable resolve
 * Test here depends on Ids, therefore cannot be used as a "pure" unit tests
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class DoxViewTest {
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
	 * @throws InvalidFormatException 
	 */
	//@Test
	public void prepareModel() throws ObjectNotFoundException, IOException, InvalidFormatException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream("Invoice2000-1.docx");
		assertTrue(stream.available()>0);
		DocxView dx = new DocxView(stream);
		Map<String,Object> model = dx.initModel();
		ResourceDTO td = new ResourceDTO();
		td.setHistoryId(318);
		model = resolverServ.resolveModel(model,td);
		for(String key :model.keySet()) {
			System.out.println(key+"=>"+model.get(key));
		}
	}
	
	//@Test
	public void insertImageOther() throws ObjectNotFoundException, IOException, InvalidFormatException {
		Path pathFile = Paths.get("src","test","resources", "Invoice2000-1.docx");
		File file = pathFile.toFile();
		CustomXWPFDocument document = new CustomXWPFDocument(new FileInputStream(file));
		
		Path pathFileout = Paths.get("src","test","resources", "myFile.docx");
		File fileout = pathFileout.toFile();
        FileOutputStream fos = new FileOutputStream(fileout);
        
        Path pathFilejpg = Paths.get("src","test","resources", "Gov-logo.jpg");
		File filejpg = pathFilejpg.toFile();
        String id = document.addPictureData(new FileInputStream(filejpg), Document.PICTURE_TYPE_JPEG);
        document.createPicture(id,document.getNextPicNameNumber(Document.PICTURE_TYPE_JPEG), 64, 64);
        document.write(fos);
        fos.flush();
        fos.close();	
	}
	
	@Test
	public void insertImage() throws ObjectNotFoundException, IOException, InvalidFormatException {
		Path pathFile = Paths.get("src","test","resources", "Invoice2000-1.docx");
		File file = pathFile.toFile();
		//DocxView dx = new DocxView(new FileInputStream(file));
		//dx.resolveDocumentTset();
		CustomXWPFDocument document = new CustomXWPFDocument(new FileInputStream(file));
		
		Path pathFileout = Paths.get("src","test","resources", "myFile.docx");
		File fileout = pathFileout.toFile();
        FileOutputStream fos = new FileOutputStream(fileout);
        
        Path pathFilejpg = Paths.get("src","test","resources", "Gov-logo.jpg");
		File filejpg = pathFilejpg.toFile();
		
		String id = document.addPictureData(IOUtils.toByteArray(new FileInputStream(filejpg)), Document.PICTURE_TYPE_JPEG);
		document.createPicture(id, document.getNextPicNameNumber(Document.PICTURE_TYPE_JPEG), 200, 200);
		document.write(fos);
        fos.flush();
        fos.close();	
	}
}
