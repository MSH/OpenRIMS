package org.msh.pharmadex2.service.r2;

import java.util.List;
import java.util.Set;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.dto.ReportConfigDTO;
import org.msh.pharmadex2.dto.ReportDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Responsible for Report creation
 * @author alexk
 *
 */
@Service
public class ReportService {
	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

	@Autowired
	private DictService dictServ;
	@Autowired
	private SystemService systemServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private AssemblyService assmServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private AccessControlService accessControl;
	/**
	 * Load dictionary, table or thing 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ReportDTO load(UserDetailsDTO user, ReportDTO data) throws ObjectNotFoundException {
		if(data.getDict().getUrl().length()==0) {
			data.setDict(systemServ.reportDictionary(user));
		}
		data = table(user,data);
		return data;
	}
	/**
	 * Build and load a report
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ReportDTO table(UserDetailsDTO user, ReportDTO data) throws ObjectNotFoundException {
		Set<Long> selected = dictServ.selectedItems(data.getDict());
		if(selected.size()==1) {
			Concept conc = closureServ.loadConceptById(selected.iterator().next());
			String url=literalServ.readValue("url", conc);
			if(url.length()>0) {
				ReportConfigDTO repConf = assmServ.reportConfig(url);
				if(repConf.getAddressUrl().length()>0) {
					data = siteReport(data, repConf);
				}else {
					data = productReport(user, data, repConf);
				}
			}else {
				data = cleanData(data);
			}
		}else {
			data = cleanData(data);
		}
		return data;
	}
	/**
	 * Very Simple product report
	 * @param user
	 * @param data
	 * @param repConf
	 * @return
	 */
	private ReportDTO productReport(UserDetailsDTO user, ReportDTO data, ReportConfigDTO repConf) {
		//get headers
		TableQtb table = data.getTable();
		Headers headers= headersProduct(table.getHeaders());
		if(table.getHeaders().getHeaders().size()!=headers.getHeaders().size()) {
			table.setHeaders(headers);
		}else {
			if(table.getHeaders().getHeaders().get(0).getKey().equals(headers.getHeaders().get(0).getKey())) {
				table.setHeaders(headers);
			}
		}
		//get data
		jdbcRepo.productReport(repConf.getDataUrl(), repConf.getRegisterAppUrl());
		//applicant may see only own or not?
		String where = "";
		if(repConf.isApplicantRestriction()) {
			if(accessControl.isApplicant(user)) {
				where = "email='"+user.getEmail()+"'";
			}
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from report_products", "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		return data;
	}
	/**
	 * Create headers for simple product reports
	 * @param headers
	 * @return
	 */
	private Headers headersProduct(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"product",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				40));
		headers.getHeaders().add(TableHeader.instanceOf(
				"register",
				"reg_number",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"registered",
				"registration_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers.getHeaders().add(TableHeader.instanceOf(
				"valid",
				"valid_to",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers=boilerServ.translateHeaders(headers);
		headers.setPageSize(20);
		return headers;
	}
	/**
	 * Simple site report
	 * @param data
	 * @param repConf
	 * @return
	 */
	public ReportDTO siteReport(ReportDTO data, ReportConfigDTO repConf) {
		long nodeId=0;	//Thing node ID
		for(TableRow row : data.getTable().getRows()) {
			if(row.getSelected()) {
				nodeId=row.getDbID();
				break;
			}
		}
		if(nodeId==0) {
			data=reportTable(data,repConf);
		}else {
			//TODO data=loadReportThing(data);
		}
		return data;
	}

	/**
	 * Data table for report may be build
	 * @param data
	 * @param repConf
	 * @return
	 */
	private ReportDTO reportTable(ReportDTO data, ReportConfigDTO repConf) {
		//there are two categories of workflow - products and facilities
		if(repConf.getAddressUrl().length()>0) {
			//address exists, so facility
			if(repConf.isRegistered()) {
				//registered and expired
				data=registeredFacilityTable(data, repConf);
			}else {
				//in process of registration
				//TODO data=inProcessFacilityTable(data, repConf);
			}
		}else {
			//no address - product
			//TODO products
		}
		return data;
	}
	/**
	 * Registered facility
	 * @param data
	 * @param repConf
	 * @return
	 */
	private ReportDTO registeredFacilityTable(ReportDTO data, ReportConfigDTO repConf) {
		TableQtb table = data.getTable();
		Headers headers= headersRegisteredFacility(table.getHeaders());
		if(table.getHeaders().getHeaders().size()!=headers.getHeaders().size()) {
			table.setHeaders(headers);
		}else {
			if(table.getHeaders().getHeaders().get(0).getKey().equals(headers.getHeaders().get(0).getKey())) {
				table.setHeaders(headers);
			}
		}
		jdbcRepo.report_sites(repConf.getDataUrl(), repConf.getDictStageUrl(), repConf.getAddressUrl(), repConf.getOwnerUrl(),
				repConf.getInspectAppUrl(), repConf.getRenewAppUrl(), repConf.getRegisterAppUrl());
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from report_sites", "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		return data;
	}

	/**
	 * Report table headers for registered facility 
	 * @param headers
	 * @return
	 */
	private Headers headersRegisteredFacility(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				40));
		headers.getHeaders().add(TableHeader.instanceOf(
				"address",
				"address",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				60));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regno",
				"reg_number",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"owners",
				"owners",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regdate",
				"registration_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));

		headers.getHeaders().add(TableHeader.instanceOf(
				"inspdate",
				"inspectiondate",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers.getHeaders().add(TableHeader.instanceOf(
				"renewvaldate",
				"ProdAppType.RENEW",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers.getHeaders().add(TableHeader.instanceOf(
				"expdate",
				"expiry_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers=boilerServ.translateHeaders(headers);
		headers.setPageSize(20);
		return headers;
	}
	/**
	 * Clean table and thing
	 * @param data
	 * @return
	 */
	private ReportDTO cleanData(ReportDTO data) {
		data.getTable().getHeaders().getHeaders().clear();
		data.getTable().getRows().clear();
		data.setThing(new ThingDTO());
		return data;
	}
	/**
	 * Reset report screen to the root state
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ReportDTO resetRoot(UserDetailsDTO user, ReportDTO data) throws ObjectNotFoundException {
		data=cleanData(data);
		data.setDict(systemServ.reportDictionary(user));
		return data;
	}
	/**
	 * Reset the report scren to the dictionary path
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ReportDTO resetPath(UserDetailsDTO user, ReportDTO data) throws ObjectNotFoundException {
		data=cleanData(data);
		data.getDict().getPrevSelected().clear();
		data.setDict(dictServ.loadPath(data.getDict()));
		return data;
	}
	/**
	 * ASk for all records in all journals
	 * @param data
	 * @return
	 */
	public ThingDTO regTable(ThingDTO data) {
		TableQtb regTable = data.getRegTable();
		if(regTable.getHeaders().getHeaders().size()==0) {
			regTable.setHeaders(headersRegTable(regTable.getHeaders()));
		}
		String select = "SELECT r.RegisteredAt, r.Register,r.ValidTo,tr.VarName "
				+ "FROM register r join thingregister tr on tr.conceptID=r.conceptID";

		List<TableRow> rows= jdbcRepo.qtbGroupReport(select, "", "r.appdataID='"+data.getNodeId()+"'", regTable.getHeaders());
		TableQtb.tablePage(rows, regTable);
		regTable.setSelectable(false);
		boilerServ.translateRows(regTable);
		return data;
	}

	/**
	 * Records from register
	 * @param headers
	 * @return
	 */
	private Headers headersRegTable(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"RegisteredAt",
				"registration_date",
				true,
				false,
				false,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"ValidTo",
				"expiry_date",
				true,
				false,
				false,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"Register",
				"reg_number",
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"VarName",
				"register_applicant",
				true,
				false,
				false,
				TableHeader.COLUMN_I18,
				0));
		headers.getHeaders().get(0).setSort(true);
		boilerServ.translateHeaders(headers);
		return headers;
	}

}
