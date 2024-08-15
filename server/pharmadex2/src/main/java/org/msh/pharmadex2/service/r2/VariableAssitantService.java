package org.msh.pharmadex2.service.r2;

import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.dto.VariableAssistantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Responsible for Variable Name assistant
 */
@Service
public class VariableAssitantService {
	private static final Logger logger = LoggerFactory.getLogger(VariableAssitantService.class);
	@Autowired
	private JdbcRepository jdbcRepo;
	
	/**
	 * load variable name and labels or propose selection
	 * @param data
	 * @return
	 */
	public VariableAssistantDTO load(VariableAssistantDTO data) {
		data=loadTable(data);
		//TODO data=loadSelection(data);
		return data;
	}
	/**
	 * load table and select variable definition is one
	 * @param data
	 * @return
	 */
	private VariableAssistantDTO loadTable(VariableAssistantDTO data) {
		if(!data.getTable().hasHeaders()) {
			//TODO data.getTable().getHeaders().getHeaders().addAll(headers());
		}
		String select="";
		String where="";
		if(data.getCurrentName().isEmpty()) {
			select="";//TODO
			data.getTable().getHeaders().setSelectedOnly(false);
		}else {
			select=""; //TODO
			where="";	//TODO
			data.getTable().getHeaders().setSelectedOnly(true);
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, data.getTable().getHeaders());
		TableQtb.currentTablePage(rows, data.getTable());
		return data;
	}


}
