package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.controller.r2.ExcelViewMult;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.service.common.DtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Export ThingDTO into Excel table
 * Import ThingDTO from the data exported to the Excel table
 * @author alexk
 *
 */
@Service
public class ImportExportWorkflowService {
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private Messages mess;
	@Autowired
	private ObjectMapper objectMapper;
	/**
	 * Export workflow configuration to MS Excel
	 * @param data
	 * @return
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Resource workflowExportExcel(WorkflowDTO dto) throws IOException, ObjectNotFoundException {
		// load all workflow configuration things
		for(ThingDTO tdto : dto.getPath()) {
			Concept node=closureServ.loadConceptById(tdto.getNodeId());
			tdto.setStrings(dtoServ.readAllStrings(tdto.getStrings(),node));
			tdto.setLiterals(dtoServ.readAllLiterals(tdto.getLiterals(), node));
			tdto.setDates(dtoServ.readAllDates(tdto.getDates(),node));
			tdto.setNumbers(dtoServ.readAllNumbers(tdto.getNumbers(),node));
			tdto.setLogical(dtoServ.readAllLogical(tdto.getLogical(), node));
			Set<String> dicts = tdto.getDictionaries().keySet();
			if(dicts!=null) {
				for(String key : dicts) {
					DictionaryDTO ddto = tdto.getDictionaries().get(key);
					ddto = dictServ.createCurrentSelections(ddto);
				}
			}
		}
		ExcelViewMult excel = new ExcelViewMult();
		XSSFWorkbook workbook=excel.workbookForWorkflowConfiguration(dto, mess);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		byte[] arr = out.toByteArray();
		workbook.close();
		out.close();
		return new ByteArrayResource(arr);
	}

}
