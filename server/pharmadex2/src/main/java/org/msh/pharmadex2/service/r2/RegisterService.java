package org.msh.pharmadex2.service.r2;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serve operations with the filng system registers
 * @author alexk
 *
 */
@Service
public class RegisterService {
	private static final Logger logger = LoggerFactory.getLogger(ThingService.class);
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ClosureService closureServ;

	/**
	 * create empty and load existing registers
	 * @param registers
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO createRegisters(List<AssemblyDTO> registers, ThingDTO data) throws ObjectNotFoundException {
		data.getRegisters().clear();
		//get empty registers from the configurations
		for(AssemblyDTO ad : registers) {
			RegisterDTO dto = new RegisterDTO();
			dto.setReadOnly(ad.isReadOnly());
			dto.setUrl(ad.getUrl());
			dto.setVarName(ad.getPropertyName());
			LocalDate expLd = LocalDate.now().plusMonths(ad.getMax().intValue());
			FormFieldDTO<LocalDate> expiry = FormFieldDTO.of(expLd);
			dto.setExpiry_date(expiry);
			dto.setExpirable(ad.isMult());
			dto.setNumberPrefix(ad.getFileTypes());
			data.getRegisters().put(dto.getVarName(),dto);
		}
		//determine the node
		Concept node = new Concept();
		if(data.getNodeId()>0) {
			node=closureServ.loadConceptById(data.getNodeId());
		}
		if(node.getID()==0 && data.getHistoryId()>0) {
			History history = boilerServ.historyById(data.getHistoryId());
			node=history.getApplicationData();
		}
		//try to load values by the URLs from all registers related to the initial application object
		if(data.getRegisters().size()>0 && data.getHistoryId()>0) {
			History history = boilerServ.historyById(data.getHistoryId());
			//determine the initial application object
			Concept objectData=boilerServ.objectData(node);
			//load all registers that belong to the object
			Map<String,RegisterDTO> allRegisters = registersLoadByApplicationData(objectData);
			//fill out all registers belong to this thing
			Set<String> keySet = data.getRegisters().keySet();
			for(String  key : keySet) {
				RegisterDTO dto = data.getRegisters().get(key);
				RegisterDTO dtol = allRegisters.get(dto.getUrl());
				if(dtol!=null) {
					dto.setRegistration_date(dtol.getRegistration_date());
					dto.setExpiry_date(dtol.getExpiry_date());
					dto.setReg_number(dtol.getReg_number());
				}else {
					//create new one
					dto.getReg_number().setValue(dto.getNumberPrefix());
					dto.getRegistration_date().setValue(LocalDate.now());
				}
			}
		}
		return data;
	}
	/**
	 * load all registers for an application data given and 
	 * @param applicationData
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private Map<String, RegisterDTO> registersLoadByApplicationData(Concept applicationData) throws ObjectNotFoundException {
		Map<String, RegisterDTO> ret = new HashMap<String, RegisterDTO>();
		if(applicationData !=null) {
			TableQtb regTable = applicationRegistersTable(new TableQtb(), applicationData.getID(), true);
			List<TableRow> rows = regTable.getRows();
			for(TableRow row : rows) {
				Object active=row.getCellByKey("active").getOriginalValue();
				if(active instanceof Long) {
					Long activeL = (Long) active;
					if(activeL>0){
						RegisterDTO dto = new RegisterDTO();
						Object regD = row.getCellByKey("registered").getOriginalValue();
						Object regN = row.getCellByKey("register").getOriginalValue();
						Object regE = row.getCellByKey("validto").getOriginalValue();
						Object nodeid=row.getCellByKey("nodeid").getOriginalValue();
						Object varname=row.getCellByKey("varname").getOriginalValue();
						Object url=row.getCellByKey("url").getOriginalValue();
						//fill out the DTO
						dto.setAppDataID(row.getDbID());
						if(regD != null && regD instanceof LocalDate) {
							dto.getRegistration_date().setValue((LocalDate) regD);
						}
						if(regN!=null && regN instanceof String) {
							dto.getReg_number().setValue((String) regN);
						}
						if(regE !=null && regE instanceof LocalDate) {
							dto.getExpiry_date().setValue((LocalDate) regE);
						}
						if(varname !=null && url instanceof String) {
							dto.setVarName((String) varname);
						}
						if(nodeid !=null && url instanceof Long) {
							dto.setVarName((String) nodeid);
						}
						if(url !=null && url instanceof String) {
							dto.setUrl((String) url);
							ret.put((String) url, dto);
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Get registers for an application given
	 * @param regTable table with all registers
	 * @param nodeId application data node id
	 * @param includeUrl include url in a table row
	 * @return
	 */
	public TableQtb applicationRegistersTable(TableQtb regTable, long nodeId, boolean includeUrl) {
		if(regTable.getHeaders().getHeaders().size()==0) {
			regTable.setHeaders(headersRegTable(regTable.getHeaders(), includeUrl));
		}
		String select = "SELECT * from application_registers";

		List<TableRow> rows= jdbcRepo.qtbGroupReport(select, "order by registered", "ID='"+nodeId+"'", regTable.getHeaders());
		TableQtb.tablePage(rows, regTable);
		regTable.setSelectable(false);
		boilerServ.translateRows(regTable);
		return regTable;
	}

	/**
	 * Records from application_registers
	 * @param headers
	 * @param includeUrl 
	 * @return
	 */
	private Headers headersRegTable(Headers headers, boolean includeUrl) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"registered",
				"registration_date",
				true,
				false,
				false,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"validto",
				"expiry_date",
				true,
				false,
				false,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"register",
				"reg_number",
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		if(includeUrl) {
			//url and varname for ThingService
			headers.getHeaders().add(TableHeader.instanceOf(
					"varname",
					"register_applicant",
					true,
					false,
					false,
					TableHeader.COLUMN_STRING,
					0));
			headers.getHeaders().add(TableHeader.instanceOf(
					"url",
					"url",
					true,
					false,
					false,
					TableHeader.COLUMN_STRING,
					0));
			headers.getHeaders().add(TableHeader.instanceOf(
					"nodeid",
					"nodeid",
					true,
					false,
					false,
					TableHeader.COLUMN_LONG,
					0));
			headers.getHeaders().add(TableHeader.instanceOf(
					"active",
					"active",
					true,
					false,
					false,
					TableHeader.COLUMN_LONG,
					0));
		}else {
			//url and varname for screen
			headers.getHeaders().add(TableHeader.instanceOf(
					"varname",
					"register_applicant",
					true,
					false,
					false,
					TableHeader.COLUMN_I18,
					0));
		}
		headers.getHeaders().get(0).setSort(true);
		boilerServ.translateHeaders(headers);
		return headers;
	}

}
