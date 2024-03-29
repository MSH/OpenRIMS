package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ProcessComponentsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Responsible for process components collection
 * The front end is ProcessValidator.js
 * @author alexk
 *
 */
@Service
public class ProcessComponentsService {
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private BoilerService boilerServ;
	/**
	 * Load components for a process given in dictNodeID 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ProcessComponentsDTO load(ProcessComponentsDTO data) throws ObjectNotFoundException {
		data=loadDetails(data);
		data=loadPages(data);
		return data;
	}
	/**
	 * Load data pages
	 * @param data
	 * @return
	 */
	@Transactional
	private ProcessComponentsDTO loadPages(ProcessComponentsDTO data) {
		//get all data URLs
		jdbcRepo.importWF_activities(data.getDictNodeID());
		String select= "select distinct dataurl as url from importwf_activities";
		List<TableRow> mainPages=jdbcRepo.qtbGroupReport(select, "", "", headersUrls());
		if(!mainPages.isEmpty()) {
			// get all aux data urls (
			String where="mainUrl in "+createInCriteriaFromRows(mainPages);
			select="SELECT distinct auxUrl as 'url' \r\n" + 
					"FROM pdx2.dataconfig_auxdata";
			List<TableRow> auxPages = jdbcRepo.qtbGroupReport(select, "", where, headersUrls());
			//collect main and aux data urls and get other pages
			List<TableRow> mainAndAuxPages = new ArrayList<TableRow>();
			mainAndAuxPages.addAll(mainPages);
			mainAndAuxPages.addAll(auxPages);
			where="mainUrl in "+createInCriteriaFromRows(mainAndAuxPages);
			select="SELECT distinct url\r\n" + 
					"FROM pdx2.dataconfig_things";
			List<TableRow> otherPages = jdbcRepo.qtbGroupReport(select, "", where, headersUrls());
			//collect all pages need for the application and get pages for which configuration is defined
			List<TableRow> applicationPages=new ArrayList<TableRow>();
			applicationPages.addAll(mainAndAuxPages);
			applicationPages.addAll(otherPages);
			where="url in "+createInCriteriaFromRows(applicationPages);
			select="SELECT url \r\n" + 
					"FROM pdx2.dataconfig_defined";
			List<TableRow> definedPages =jdbcRepo.qtbGroupReport(select, "", where, headersUrls());
			//collect urls of required and defined pages
			Set<String> applPageUrls=urlsFromRows(applicationPages);
			Set<String> definedPageUrls = urlsFromRows(definedPages);
			//create output table
			if(data.getDataConfigurations().getHeaders().getHeaders().isEmpty()) {
				data.getDataConfigurations().setHeaders(headersTable());
			}
			long i =1;
			List<TableRow> rows = new ArrayList<TableRow>();
			for(String url :applPageUrls) {
				TableRow row = TableRow.instanceOf(i);
				row.getRow().add(TableCell.instanceOf("url",url));
				row.getRow().add(TableCell.instanceOf("dataconfigurator",definedPageUrls.contains(url)));
				rows.add(row);
				i++;
			}
			Collections.sort(rows, new Comparator<TableRow>() {

				@Override
				public int compare(TableRow o1, TableRow o2) {
					return o1.getRow().get(0).getValue().compareTo(o2.getRow().get(0).getValue());
				}
				
			});
			TableQtb.tablePage(rows, data.getDataConfigurations());
			data.getDataConfigurations().setSelectable(false);
		}
		return data;
	}
	/**
	 * Headers for any table
	 * @return
	 */
	private Headers headersTable() {
		Headers ret = headersUrls();
		ret.getHeaders().add(TableHeader.instanceOf(
				"'dataconfigurator'",
				"",
				true,
				false,
				false,
				TableHeader.COLUMN_TRUE_FALSE,
				0));
		boilerServ.translateHeaders(ret);
		return ret;
	}
	/**
	 * Get URLs from table rows
	 * URL columns presumed as the first
	 * @param rows
	 */
	public Set<String> urlsFromRows(List<TableRow> rows) {
		if(!rows.isEmpty()) {
			Set<String> ret = rows.stream()
					.filter(el->{
						return !el.getRow().get(0).getValue().isEmpty();
					})
					.map(el->{
						return el.getRow().get(0).getValue();
					})
					.collect(Collectors.toSet());
			return ret;
		}else {
			return new HashSet<String>();
		}
	}
	/**
	 * Create IN creteria from list of rows
	 * @param rows
	 */
	public String createInCriteriaFromRows(List<TableRow> rows) {
		if(!rows.isEmpty()) {
			List<String> urls=rows.stream()
					.filter(element->{
						return !element.getRow().get(0).getValue().isEmpty();
					})
					.map(element->{
						return "'"+element.getRow().get(0).getValue()+"'"; 
					})
					.collect(Collectors.toList());
			return "("+String.join(",", urls)+")";
		}else {
			return "()";
		}
	}
	/**
	 * Headers 
	 * @return
	 */
	private Headers headersUrls() {
		Headers ret = new Headers();
		ret.getHeaders().add(TableHeader.instanceOf(
				"url",
				"url",
				true,
				false,
				false,
				TableHeader.COLUMN_LINK,
				0));
		return ret;
	}
	/**
	 * Load proccess meta data from the process dictionary
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ProcessComponentsDTO loadDetails(ProcessComponentsDTO data) throws ObjectNotFoundException {
		Concept item=closureServ.loadConceptById(data.getDictNodeID());
		Concept dictionary = closureServ.getParent(item);
		data.getDictURL().setValue(dictionary.getIdentifier());
		data.getDictName().setValue(literalServ.readPrefLabel(dictionary));
		data.getDictDescr().setValue(literalServ.readDescription(dictionary));
		data.getApplURL().setValue(literalServ.readValue("applicationurl", item));
		data.getApplName().setValue(literalServ.readPrefLabel(item));
		data.getApplDescr().setValue(literalServ.readDescription(item));
		return data;
	}

}
