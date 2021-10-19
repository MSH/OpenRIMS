package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.dto.AtcDTO;
import org.msh.pharmadex2.dto.ExcipientsDTO;
import org.msh.pharmadex2.dto.InnsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtcService {
	@Autowired
	BoilerService boilerServ;
	@Autowired
	JdbcRepository jdbcRepo;
	
	
	
	public AtcDTO loadTable(AtcDTO data) throws ObjectNotFoundException {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(createHeaders(table.getHeaders(), data.isReadOnly()));
		}
		if(data.getSelectedtable().getHeaders().getHeaders().size() == 0) {
			data.getSelectedtable().setHeaders(createHeaders(data.getSelectedtable().getHeaders(), data.isReadOnly()));
		}
		String select = "SELECT a.AtcCode as atccode, a.AtcName as atcname from atc a";
		List<TableRow> rows= jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
		data.getSelectedtable().setSelectable(false);
		return data;
	}
	
	
	public ExcipientsDTO loadTable(ExcipientsDTO data) throws ObjectNotFoundException {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(createHeadersExc(table.getHeaders(), data.isReadOnly(), true));
		}
		if(data.getSelectedtable().getHeaders().getHeaders().size() == 0) {
			data.getSelectedtable().setHeaders(createHeadersExc(data.getSelectedtable().getHeaders(), data.isReadOnly(), false));
		}
		String select = "SELECT exc.id as ID, exc.Name as expname from excipient exc";
		List<TableRow> rows= jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
		data.getSelectedtable().setSelectable(false);
		return data;
	}
	
	public ExcipientsDTO addExcipient(ExcipientsDTO data) throws ObjectNotFoundException {
		if(data.getId() > 0) {
			TableQtb table = data.getSelectedtable();
			TableRow row = new TableRow();
			row.setDbID(data.getId());
			TableCell cell = new TableCell();
			cell.setKey("expname");
			cell.setOriginalValue(data.getExcipient_name().getValue());
			cell.setValue(data.getExcipient_name().getValue());
			row.getRow().add(cell);
			
			cell = new TableCell();
			cell.setKey("dosstr");
			cell.setOriginalValue(data.getDos_strength().getValue());
			cell.setValue(data.getDos_strength().getValue());
			row.getRow().add(cell);
			
			cell = new TableCell();
			cell.setKey("dosunit");
			cell.setOriginalValue(data.getDos_unit().getValue());
			cell.setValue(data.getDos_unit().getValue());
			row.getRow().add(cell);
			
			table.getRows().add(row);
			
			data.setId(0);
			data.getExcipient_name().setValue("");
			data.getDos_strength().setValue("");
			data.getDos_unit().setValue("");
		}
		return data;
	}
	
	public InnsDTO loadTable(InnsDTO data) throws ObjectNotFoundException {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(createHeadersInn(table.getHeaders(), data.isReadOnly(), true));
		}
		if(data.getSelectedtable().getHeaders().getHeaders().size() == 0) {
			data.getSelectedtable().setHeaders(createHeadersInn(data.getSelectedtable().getHeaders(), data.isReadOnly(), false));
		}
		String select = "SELECT inn.id as ID, inn.Name as innname from inn inn";
		List<TableRow> rows= jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
		data.getSelectedtable().setSelectable(false);
		return data;
	}
	
	public InnsDTO addInn(InnsDTO data) throws ObjectNotFoundException {
		if(data.getId() > 0) {
			TableQtb table = data.getSelectedtable();
			TableRow row = new TableRow();
			row.setDbID(data.getId());
			TableCell cell = new TableCell();
			cell.setKey("innname");
			cell.setOriginalValue(data.getProduct_innname().getValue());
			cell.setValue(data.getProduct_innname().getValue());
			row.getRow().add(cell);
			
			cell = new TableCell();
			cell.setKey("dosstr");
			cell.setOriginalValue(data.getDos_strength().getValue());
			cell.setValue(data.getDos_strength().getValue());
			row.getRow().add(cell);
			
			cell = new TableCell();
			cell.setKey("dosunit");
			cell.setOriginalValue(data.getDos_unit().getValue());
			cell.setValue(data.getDos_unit().getValue());
			row.getRow().add(cell);
			
			table.getRows().add(row);
			
			data.setId(0);
			data.getProduct_innname().setValue("");
			data.getDos_strength().setValue("");
			data.getDos_unit().setValue("");
		}
		return data;
	}
	
	
	
	/**
	 * Create dictionary table headers
	 * @param ret 
	 * @param readOnly 
	 * @return
	 */
	private Headers createHeaders(Headers ret, boolean readOnly) {
		ret.getHeaders().clear();
		ret.setPageSize(4);
		int firstHeader=TableHeader.COLUMN_LINK;
		if(readOnly) {
			firstHeader=TableHeader.COLUMN_STRING;
		}
		ret.getHeaders().add(TableHeader.instanceOf(
				"atccode", 
				"atc_code",
				true,
				true,
				true,
				firstHeader,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"atcname", 
				"atc_name",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		//ret.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}
	
	private Headers createHeadersExc(Headers ret, boolean readOnly, boolean shortTable) {
		ret.getHeaders().clear();
		ret.setPageSize(4);
		int firstHeader=TableHeader.COLUMN_LINK;
		if(readOnly) {
			firstHeader=TableHeader.COLUMN_STRING;
		}
		if(shortTable) {
			ret.getHeaders().add(TableHeader.instanceOf(
					"expname", 
					"excipient_name",
					true,
					true,
					true,
					firstHeader,
					0));
		}else {
			ret.getHeaders().add(TableHeader.instanceOf(
					"expname", 
					"excipient_name",
					true,
					true,
					true,
					firstHeader,
					0));
			ret.getHeaders().add(TableHeader.instanceOf(
					"dosstr", 
					"dos_strength",
					true,
					true,
					true,
					firstHeader,
					0));
			ret.getHeaders().add(TableHeader.instanceOf(
					"dosunit", 
					"dos_unit",
					true,
					true,
					true,
					firstHeader,
					0));
		}
		
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}
	
	private Headers createHeadersInn(Headers ret, boolean readOnly, boolean shortTable) {
		ret.getHeaders().clear();
		ret.setPageSize(4);
		int firstHeader=TableHeader.COLUMN_LINK;
		if(readOnly) {
			firstHeader=TableHeader.COLUMN_STRING;
		}
		if(shortTable) {
			ret.getHeaders().add(TableHeader.instanceOf(
					"innname", 
					"product_innname",
					true,
					true,
					true,
					firstHeader,
					0));
		}else {
			ret.getHeaders().add(TableHeader.instanceOf(
					"innname", 
					"product_innname",
					true,
					true,
					true,
					firstHeader,
					0));
			ret.getHeaders().add(TableHeader.instanceOf(
					"dosstr", 
					"dos_strength",
					true,
					true,
					true,
					firstHeader,
					0));
			ret.getHeaders().add(TableHeader.instanceOf(
					"dosunit", 
					"dos_unit",
					true,
					true,
					true,
					firstHeader,
					0));
		}
		
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}
}
