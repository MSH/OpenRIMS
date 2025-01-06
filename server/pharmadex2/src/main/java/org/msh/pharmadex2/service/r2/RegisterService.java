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
import org.msh.pdex2.model.r2.Register;
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
	private static final Logger logger = LoggerFactory.getLogger(RegisterService.class);
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
		if(registers.size()>0) {
			// try to load the existing register
			//determine the data node concept
			Concept node = new Concept();
			if(data.getNodeId()>0) {
				node=closureServ.loadConceptById(data.getNodeId());
			}
			if(node.getID()==0) {
				if(data.getHistoryId()>0) {
					History h = boilerServ.historyById(data.getHistoryId());
					node=h.getApplicationData();	//for new elements in the existing data sets or workflows
				}
			}
			//determine the application data
			Concept ia = boilerServ.initialApplicationNode(node);
			//clear old registers
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
				if(ia != null) {
					//load existing
					Long regId = jdbcRepo.registerApplication(ia.getID(), dto.getUrl());
					if(regId != null) {
						Register register = boilerServ.registerById(regId);
						dto.setNodeID(register.getConcept().getID());
						dto.setReg_number(FormFieldDTO.of(register.getRegister()));
						LocalDate regDate = boilerServ.localDateFromDate(register.getRegisteredAt());
						dto.setRegistration_date(FormFieldDTO.of(regDate));
						if(ad.isMult()) {
							LocalDate expDate = boilerServ.localDateFromDate(register.getValidTo());
							dto.setExpirable(true);
							dto.setExpiry_date(FormFieldDTO.of(expDate));
						}
					}
				}
			}
		}
		return data;
	}
	
	/**
	 * ASk new register number for the register url given
	 * @param url
	 * @return
	 */
	@Transactional
	public RegisterDTO askNewNumber(RegisterDTO dto) {
		Long newNumber = jdbcRepo.register_number(dto.getUrl());
		String num = "000000" + newNumber;
		num=num.substring(num.length()-6, num.length());
		String regNumber=dto.getNumberPrefix()+num;
		dto.setReg_number(FormFieldDTO.of(regNumber));
		return dto;
	}
	/**
	 * load all registers for an application data given and 
	 * @param applicationData
	 * @return empty map if not found
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Map<String, RegisterDTO> registersLoadByApplicationData(Concept applicationData) throws ObjectNotFoundException {
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
			headers.getHeaders().add(TableHeader.instanceOf(
					"url",
					"url",
					true,
					false,
					false,
					TableHeader.COLUMN_STRING,
					0));
		}
		headers.getHeaders().get(0).setSort(true);
		boilerServ.translateHeaders(headers);
		return headers;
	}


}
