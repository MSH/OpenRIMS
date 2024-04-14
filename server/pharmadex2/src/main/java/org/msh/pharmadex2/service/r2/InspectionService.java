package org.msh.pharmadex2.service.r2;

import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.HostScheduleDTO;
import org.msh.pharmadex2.dto.PermitsDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inspections related business logic
 * @author alexk
 *
 */
@Service
public class InspectionService {
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	DeregistrationService dataFilter;	//we use the same data filtering logic
	/**
	 * Load actual and going to be actual permits by a type and a user given 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public PermitsDTO permits(UserDetailsDTO user, PermitsDTO data) throws ObjectNotFoundException {
		if(data.getDictItemId()>0) {
			//set permit type name
			Concept item = closureServ.loadConceptById(data.getDictItemId());
			data.setPermitType(literalServ.readPrefLabel(item));
			data.setCanAdd(item.getActive());
			//create a table with permits
			String url=literalServ.readValue("url", item);
			String applDataUrl = literalServ.readValue("dataurl", item);
			jdbcRepo.applications_applicant(url, user.getEmail());
			String select = "select ID, prefLabel,tcategory from applications_applicant"; 
			String where=  "category in ('NOTSUBMITTED', 'ACTIVE')";
			if(data.getTable().getHeaders().getHeaders().size()==0) {
				data.getTable().setHeaders(permitTableHeaders(data.getTable().getHeaders()));
			}
			List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, data.getTable().getHeaders());
			rows=dataFilter.excludeProcessing(applDataUrl, user.getEmail(),rows);
			TableQtb.tablePage(rows, data.getTable());
			boilerServ.translateRows(data.getTable());
			data.getTable().setSelectable(false);
		}
		return data;
	}
	/**
	 * Permit table headers
	 * @param headers
	 * @return
	 */
	private Headers permitTableHeaders(Headers headers) {
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"tcategory",
				"category",
				true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_DESC);
		headers = boilerServ.translateHeaders(headers);
		headers.setPageSize(20);
		return headers;
	}
	
	/**
	 * Load a host schedule
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public HostScheduleDTO hostSchedule(UserDetailsDTO user, HostScheduleDTO data) throws ObjectNotFoundException {
		if(data.getTable().getHeaders().getHeaders().size()==0) {
			data.getTable().setHeaders(hostScheduleTableHeaders(data.getTable().getHeaders()));
		}
		if(data.getHostDictionary().length()==0) {
			Concept dict=closureServ.loadConceptByIdentifier(data.getDictURL());
			data.setHostDictionary(literalServ.readPrefLabel(dict));
		}
		jdbcRepo.host_schedule(data.getDictURL(),user.getEmail());
		String select= "select * from host_schedule";
		List<TableRow> rows= jdbcRepo.qtbGroupReport(select, "", "", data.getTable().getHeaders());
		data.setCount(rows.size());
		TableQtb.tablePage(rows, data.getTable());
		data.getTable().setSelectable(false);
		return data;
	}
	/**
	 * Host processes scheduler
	 * @param headers
	 * @return
	 */
	private Headers hostScheduleTableHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"come",
				"scheduled",
				true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		//headersheaders.getHeaders().add(TableHeader.instanceOf(
				//"done",
				//"done",
			//	true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"workflow",
				"prod_app_type",
				true, true, true, TableHeader.COLUMN_STRING, 0));
		boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSort(true);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_DESC);
		return headers;
	}

}
