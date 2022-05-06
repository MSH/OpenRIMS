package org.msh.pharmadex2;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.Test;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.controller.common.CustomXWPFDocument;
import org.msh.pharmadex2.controller.common.DocxView;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ResolverService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
		DocxView dx = new DocxView(stream,boilerServ);
		Map<String,Object> model = dx.initModel();
		ResourceDTO td = new ResourceDTO();
		td.setHistoryId(318);
		model = resolverServ.resolveModel(model,td);
		for(String key :model.keySet()) {
			System.out.println(key+"=>"+model.get(key));
		}
	}

	@Test
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

	/**
	 * Insert the INCLUDEPICTURE field to the document
	 * Doesn't work
	 * @throws IOException 
	 */
	@Test
	public void insertField() throws IOException {
		Path pathFile = Paths.get("src","test","resources", "InsertField.docx");
		File file = pathFile.toFile();
		FileInputStream stream = new FileInputStream(file);
		XWPFDocument doc = new XWPFDocument(stream);
		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun r1=paragraph.createRun();
		r1.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
		XWPFRun r2=paragraph.createRun();
		CTText ctText = r2.getCTR().addNewInstrText();
		ctText.setStringValue("preserve");
		XWPFRun r3=paragraph.createRun();
		ctText = r3.getCTR().addNewInstrText();
		ctText.setStringValue("INCLUDEPICTURE \"http://storage.pigua.info/uploads/Anastasia/2019/August/28_Saw.jpg\" \\* MERGEFORMATINET");
		XWPFRun r4=paragraph.createRun();
		ctText = r4.getCTR().addNewInstrText();
		ctText.setStringValue("preserve");
		XWPFRun r5=paragraph.createRun();
		r5.getCTR().addNewFldChar().setFldCharType(STFldCharType.SEPARATE);
		XWPFRun r6=paragraph.createRun();
		r6.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
		Path pathFileout = Paths.get("src","test","resources", "myFile.docx");
		File fileout = pathFileout.toFile();
		FileOutputStream fos = new FileOutputStream(fileout);
		doc.write(fos);
		fos.flush();
		fos.close();
		doc.close();
	}

	/**
	 * Insert a plain image 
	 * @throws IOException
	 * @throws InvalidFormatException 
	 * @throws ObjectNotFoundException 
	 */
	@Test
	public void insertPlainImage() throws IOException, InvalidFormatException, ObjectNotFoundException {
		//doc
		Path pathFile = Paths.get("src","test","resources", "InsertField.docx");
		File file = pathFile.toFile();
		FileInputStream stream = new FileInputStream(file);
		DocxView dx = new DocxView(stream,null);
		dx.initModel();
		//pic
		Path pathPic = Paths.get("src","test","resources", "swin.png");
		File pic = pathPic.toFile();
		FileInputStream picStream = new FileInputStream(pic);

		XWPFDocument doc = dx.getDoc();
		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun run =paragraph.createRun();
		run=dx.insertPicture(IOUtils.toByteArray(picStream), "image/png", run);
		Path pathFileout = Paths.get("src","test","resources", "myFile.docx");
		File fileout = pathFileout.toFile();
		FileOutputStream fos = new FileOutputStream(fileout);
		doc.write(fos);
		fos.flush();
		fos.close();
		doc.close();
	}

	@Test
	public void insertSimpleImage() throws ObjectNotFoundException, IOException, InvalidFormatException {
		Path pathFile = Paths.get("src","test","resources", "InsertField.docx");
		File file = pathFile.toFile();
		//DocxView dx = new DocxView(new FileInputStream(file));
		//dx.resolveDocumentTset();
		CustomXWPFDocument document = new CustomXWPFDocument(new FileInputStream(file));

		Path pathFileout = Paths.get("src","test","resources", "myFile.docx");
		File fileout = pathFileout.toFile();
		FileOutputStream fos = new FileOutputStream(fileout);

		Path pathPic = Paths.get("src","test","resources", "swin.jpg");
		File pic = pathPic.toFile();
		FileInputStream picStream = new FileInputStream(pic);
		byte[] picPig = IOUtils.toByteArray(picStream);
		ByteArrayInputStream istream =new ByteArrayInputStream(picPig);
		BufferedImage img = ImageIO.read(istream);
		int height= img.getHeight();
		int width = img.getWidth();
		String id = document.addPictureData(picPig, Document.PICTURE_TYPE_PNG);
		int bId=document.getNextPicNameNumber(Document.PICTURE_TYPE_PNG);
		document.createPicture(id, bId, width, height);
		document.write(fos);
		fos.flush();
		fos.close();	
	}

	/**
	 * Test @form convertor. Abstract tables
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	@Test
	public void testTableEL() throws ObjectNotFoundException, IOException {
		//init view
		Path pathFile = Paths.get("src","test","resources", "Components_EL.docx");
		File file = pathFile.toFile();
		FileInputStream stream = new FileInputStream(file);
		DocxView dx = new DocxView(stream,boilerServ);
		//init model
		Map<String, Object> model = dx.initModel();
		model.put("/pharmacists/pharmacists/0/prefLabel@literal", "name of a Pharmacist");
		TableQtb table = new TableQtb();
		table.setPaintBorders(false);
		Headers headers=table.getHeaders();
		headers.getHeaders().add(TableHeader.instanceOf("varname", "varname", 20, TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("description", "", 80, TableHeader.COLUMN_STRING));
		TableRow row = TableRow.instanceOf(1l);
		row.getRow().add(TableCell.instanceOf("first", "First Row, First Col"));
		row.getRow().add(TableCell.instanceOf("second", "First Row, Second Col"));
		table.getRows().add(row);
		model.put("/pharmacists/pharmacists/0@form", table);
		model.put("/pharmacists/pharmacists/0/qualification@form", table);
		//fill out EL
		stream = new FileInputStream(file);
		dx = new DocxView(stream,boilerServ);
		dx.resolveDocument(model, true);
		//save result
		XWPFDocument document = dx.getDoc();
		Path pathFileout = Paths.get("src","test","resources", "Components_EL_out.docx");
		File fileout = pathFileout.toFile();
		FileOutputStream fos = new FileOutputStream(fileout);
		document.write(fos);
		fos.flush();
		fos.close();
	}
	
	/**
	 * Test @form convertor. Real object. May not work on the some databases
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	@Test
	public void testRealTableELForm() throws ObjectNotFoundException, IOException {
		//init view
		Path pathFile = Paths.get("src","test","resources", "Components_EL.docx");
		File file = pathFile.toFile();
		FileInputStream stream = new FileInputStream(file);
		DocxView dx = new DocxView(stream,boilerServ);
		//init model
		Map<String, Object> model = dx.initModel();
		ResourceDTO fres = new ResourceDTO();
		fres.setHistoryId(2889);
		model=resolverServ.resolveModel(model, fres);
		//fill out EL
		stream = new FileInputStream(file);
		dx = new DocxView(stream,boilerServ);
		dx.resolveDocument(model, true);
		//save result
		XWPFDocument document = dx.getDoc();
		Path pathFileout = Paths.get("src","test","resources", "Components_EL_out.docx");
		File fileout = pathFileout.toFile();
		FileOutputStream fos = new FileOutputStream(fileout);
		document.write(fos);
		fos.flush();
		fos.close();
	}
	
	/**
	 * Test @changes convertor. Real object. May not work on the some databases
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	@Test
	public void testRealTableELChanges() throws ObjectNotFoundException, IOException {
		//init view
		Path pathFile = Paths.get("src","test","resources", "Components_EL_Changes.docx");
		File file = pathFile.toFile();
		FileInputStream stream = new FileInputStream(file);
		DocxView dx = new DocxView(stream,boilerServ);
		//init model
		Map<String, Object> model = dx.initModel();
		ResourceDTO fres = new ResourceDTO();
		//fres.setHistoryId(5280);		//qualif
		//fres.setHistoryId(5281);		//owner
		fres.setHistoryId(5283);   //pharmacist
		//fres.setHistoryId(5284);       //address
		//fres.setHistoryId(5285);          //capital
		//fres.setHistoryId(5286);             //the name
		//fres.setHistoryId(5217);			//implemented persons
		//fres.setHistoryId(3603);           //implemented capital
		//fres.setHistoryId(5288);			//implemented address
		//fres.setHistoryId(3603);			//implemented capital
		//fres.setHistoryId(5293);			//implemented name
		//fres.setHistoryId(5296);				//implement qualif
		model=resolverServ.resolveModel(model, fres);
		//fill out EL
		stream = new FileInputStream(file);
		dx = new DocxView(stream,boilerServ);
		dx.resolveDocument(model, true);
		//save result
		XWPFDocument document = dx.getDoc();
		Path pathFileout = Paths.get("src","test","resources", "Components_EL_Changes_out.docx");
		File fileout = pathFileout.toFile();
		FileOutputStream fos = new FileOutputStream(fileout);
		document.write(fos);
		fos.flush();
		fos.close();
	}
	
	/**
	 * Test @changes convertor. Real object. May not work on the some databases
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	@Test
	public void testRealTableELChangesList() throws ObjectNotFoundException, IOException {
		//init view
		Path pathFile = Paths.get("src","test","resources", "Components_EL_ChangesList.docx");
		File file = pathFile.toFile();
		FileInputStream stream = new FileInputStream(file);
		DocxView dx = new DocxView(stream,boilerServ);
		//init model
		Map<String, Object> model = dx.initModel();
		ResourceDTO fres = new ResourceDTO();
		fres.setHistoryId(2889); //pharmacist
		//fres.setHistoryId(3847);     //pharmacist qualif
		model=resolverServ.resolveModel(model, fres);
		//fill out EL
		stream = new FileInputStream(file);
		dx = new DocxView(stream,boilerServ);
		dx.resolveDocument(model, true);
		//save result
		XWPFDocument document = dx.getDoc();
		Path pathFileout = Paths.get("src","test","resources", "Components_EL_ChangesList_out.docx");
		File fileout = pathFileout.toFile();
		FileOutputStream fos = new FileOutputStream(fileout);
		document.write(fos);
		fos.flush();
		fos.close();
	}
}
