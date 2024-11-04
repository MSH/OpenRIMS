package org.msh.pharmadex2.service.r2;

import java.util.List;

import org.msh.pdex2.dto.i18n.Language;
import org.msh.pdex2.dto.i18n.Languages;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.MessageDTO;
import org.msh.pharmadex2.dto.VariableAssistantDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Responsible for Variable Name assistant
 */
@Service
public class VariableAssitantService {
	private static final Logger logger = LoggerFactory.getLogger(VariableAssitantService.class);
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private Messages messages;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private ValidationService validation;
	@Autowired
	private SupervisorService superVisorServ;


	/**
	 * load table and select variable definition if one
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public VariableAssistantDTO load(VariableAssistantDTO data) throws ObjectNotFoundException {
		if(!data.getTable().hasHeaders()) {
			data.getTable().setHeaders(headers(data.getTable().getHeaders()));
		}
		String select="SELECT distinct varname,labels\r\n"
				+ "FROM variable_assist";
		String where="";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, data.getTable().getHeaders());
		for(TableRow row : rows) {
			if(row.getCellByKey("varname").getValue().equalsIgnoreCase(data.getCurrentName())) {
				row.setSelected(!row.getSelected());
				if(!row.getSelected()) {
					data.setCurrentName("");
				}
			}else {
				row.setSelected(false);
			}
		}
		TableQtb.tablePage(rows, data.getTable());
		return data;
	}
	private Headers headers(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"varname",
				messages.get("prefLabel"), 
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"labels",
				messages.get("description"), 
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		return headers;
	}

	/**
	 * Prepare fields for edtit variable name and labels
	 * @param data
	 * @return
	 */
	@Transactional
	public VariableAssistantDTO prepareEdit(VariableAssistantDTO data) {
		data=createEmptyLabels(data);
		data=createFields(data);
		data=calcUsage(data);
		return data;
	}
	/**
	 * Calculate the quantity of usage of this name as a variable name
	 * @param data
	 * @return
	 */
	@Transactional
	private VariableAssistantDTO calcUsage(VariableAssistantDTO data) {
		TableQtb table = new TableQtb();
		table.getHeaders().getHeaders().add(TableHeader.instanceOf("occurs", TableHeader.COLUMN_LONG));
		String select="SELECT propertyName, count(AssemblyID) as 'occurs' \r\n"
				+ "FROM assm_var";
		String where="propertyName='"+data.getVarName().getValue()+"'";
		String groupBy="Group By propertyName";
		List<TableRow> rows=jdbcRepo.qtbGroupReport(select, groupBy, where, table.getHeaders());
		if(rows.size()==1) {
			data.setUsageCount(rows.get(0).getRow().get(0).getIntValue());
		}else {
			data.setUsageCount(0);
		}
		return data;
	}
	/**
	 * Create editable fields
	 * @param data
	 */
	private VariableAssistantDTO createFields(VariableAssistantDTO data) {
		TableQtb table = new TableQtb();
		table.getHeaders().getHeaders().add(TableHeader.instanceOf("locale", TableHeader.COLUMN_STRING));
		table.getHeaders().getHeaders().add(TableHeader.instanceOf("message_value", TableHeader.COLUMN_STRING));
		String select="SELECT rb.locale,rm.message_value \r\n"
				+ "FROM resource_message rm\r\n"
				+ "join resource_bundle rb on rb.ID=rm.key_bundle";
		String where="message_key='"+data.getCurrentName()+"'";
		List<TableRow> rows=jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		for(TableRow row : rows) {
			String key=row.getCellByKey("locale").getValue();
			FormFieldDTO<String> fld=data.getLabels().get(key);
			fld.setValue(row.getCellByKey("message_value").getValue());
		}
		data.getVarName().setValue(data.getCurrentName());
		return data;
	}
	/**
	 * Create empty labels
	 * @param data
	 * @return
	 */
	@Transactional
	private VariableAssistantDTO createEmptyLabels(VariableAssistantDTO data) {
		data.getLabels().clear();
		Languages langs=messages.getLanguages();
		for(Language lang : langs.getLangs()) {
			String key=lang.getLocaleAsString();
			data.getLabels().put(key, FormFieldDTO.of(""));
		}
		return data;
	}
	/**
	 * Save variable name and labels to messages. Reload messages
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public VariableAssistantDTO editSave(VariableAssistantDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data=validation.variableNameAndLabels(data);
		if(data.isValid()) {
			MessageDTO mDto=new MessageDTO();
			mDto.getRes_key().setValue(data.getVarName().getValue());
			for(String key : data.getLabels().keySet()) {
				mDto.getValues().put(key.toUpperCase(), FormFieldDTO.of(data.getLabels().get(key).getValue()));
			}
			mDto=superVisorServ.messagesSave(mDto);
			if(mDto.isValid()) {
				messages.getMessages().clear();
				messages.loadLanguages();
			}else {
				data.addError(mDto.getIdentifier());
			}
		}
		return data;
	}

}
