package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.transaction.annotation.Transactional;
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
		data=loadResources(data);
		data=loadDictionaries(data);
		return data;
	}
	/**
	 * Dictionaries are:
	 * <ul>
	 * <li>check lists for application form and workflow forms
	 * <li>dictionaries defined in pages of the application form and workflow forms
	 * <li>dictionaries defined for "documents" 
	 * </ul>
	 * @param data
	 * @return
	 */
	private ProcessComponentsDTO loadDictionaries(ProcessComponentsDTO data) {
		//check lists
		jdbcRepo.importWF_activities(data.getDictNodeID());
		String select= "select distinct dicturl as 'url' from importwf_activities";
		List<TableRow> checkListRows=jdbcRepo.qtbGroupReport(select, "", "", headersToSelectUrls());
		//defined in the pages
		String selectPageDict = "SELECT distinct ClazzUrl as 'url' \r\n" + 
				"FROM page_clazz_url pcu\r\n" + 
				"where \r\n" + 
				"Clazz='dictionaries' and\r\n" + 
				"PageUrl in " +createInCriteriaFromRows(data.getDataConfigurations().getRows());
		List<TableRow> dictPagesRows=jdbcRepo.qtbGroupReport(selectPageDict, "", "", headersToSelectUrls());
		//defined for "documents"
		String selectDocDict = "SELECT distinct DictUrl as 'url' \r\n" + 
				"FROM page_clazz_url pcu\r\n" + 
				"where \r\n" + 
				"pcu.Clazz='documents' and\r\n" + 
				"PageUrl in " + createInCriteriaFromRows(data.getDataConfigurations().getRows());
		List<TableRow> docDictRows=jdbcRepo.qtbGroupReport(selectDocDict, "", "", headersToSelectUrls());
		// check definitions of dictionaries
		List<TableRow> dictUrls = new ArrayList<TableRow>();
		dictUrls.addAll(checkListRows);
		dictUrls.addAll(dictPagesRows);
		dictUrls.addAll(docDictRows);
		String definedDict="select c.Identifier as 'url'\r\n" + 
				"from concept c\r\n" + 
				"left join closure clo on clo.childID=c.ID and clo.`Level`=1\r\n" + 
				"join closure clo1 on clo1.parentID=c.ID and clo1.`Level`=1\r\n" + 
				"left join concept item on item.ID=clo1.childID and item.Identifier != '_LITERALS_' and item.Active\r\n" + 
				"where \r\n" + 
				"c.Active and\r\n" + 
				"c.Identifier like 'dictionary.%' \r\n" + 
				"group by c.Identifier\r\n" + 
				"having count(item.ID)>0";
		List<TableRow> definedDictRows=jdbcRepo.qtbGroupReport(definedDict, "", "", headersToSelectUrls());
		//collect urls of defined and required dictionaries
		Set<String> definedUrls=urlsFromRows(definedDictRows);
		Set<String> requiredUrls = urlsFromRows(dictUrls);
		//create output table
		if(data.getDictionaries().getHeaders().getHeaders().isEmpty()) {
			data.getDictionaries().setHeaders(headersOnScreenTable());
		}
		long i =1;
		List<TableRow> rows = new ArrayList<TableRow>();
		for(String url :requiredUrls) {
			TableRow row = TableRow.instanceOf(i);
			row.getRow().add(TableCell.instanceOf("url",url));
			row.getRow().add(TableCell.instanceOf("good",definedUrls.contains(url)));
			rows.add(row);
			i++;
		}
		Collections.sort(rows, new Comparator<TableRow>() {

			@Override
			public int compare(TableRow o1, TableRow o2) {
				return o1.getRow().get(0).getValue().compareTo(o2.getRow().get(0).getValue());
			}

		});
		TableQtb.tablePage(rows, data.getDictionaries());
		data.getDictionaries().setSelectable(false);
		return data;
	}

	/**
	 * Load resources existing and not existing
	 * The not existing is a resource with no files loaded
	 * @param data
	 * @return
	 */
	private ProcessComponentsDTO loadResources(ProcessComponentsDTO data) {
		String select = "SELECT distinct ClazzUrl as 'url', \r\n" + 
				"(min(attached)=max(attached) and min(attached!=0)) as 'good'\r\n" + 
				"FROM page_clazz_url p\r\n" + 
				"left join resources r on r.url=p.ClazzUrl\r\n";
		String where = "Clazz='resources' and pageUrl in "+createInCriteriaFromRows(data.getDataConfigurations().getRows());
		if(data.getResources().getHeaders().getHeaders().size()==0) {
			data.getResources().setHeaders(headersOnScreenTable());
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "Group By ClazzUrl", where, data.getResources().getHeaders());
		TableQtb.tablePage(rows, data.getResources());
		data.getResources().setSelectable(false);
		return data;
	}
	/**
	 * Load data pages
	 * @param data
	 * @return
	 */
	@Transactional
	public ProcessComponentsDTO loadPages(ProcessComponentsDTO data) {
		List<TableRow> rows = pagesRows(data);
		TableQtb.tablePage(rows, data.getDataConfigurations());
		data.getDataConfigurations().setSelectable(false);
		return data;
	}
	/**
	 * get rows for data configuration table
	 * @param data
	 * @return
	 */
	@Transactional
	public List<TableRow> pagesRows(ProcessComponentsDTO data) {
		//result rows
		List<TableRow> rows = new ArrayList<TableRow>();
		//get all data URLs
		jdbcRepo.importWF_activities(data.getDictNodeID());
		String select= "select distinct dataurl as url from importwf_activities";
		List<TableRow> mainPages=jdbcRepo.qtbGroupReport(select, "", "", headersToSelectUrls());
		if(!mainPages.isEmpty()) {
			// get all aux data urls (
			String where="mainUrl in "+createInCriteriaFromRows(mainPages);
			select="SELECT distinct auxUrl as 'url' \r\n" + 
					"FROM dataconfig_auxdata";
			List<TableRow> auxPages = jdbcRepo.qtbGroupReport(select, "", where, headersToSelectUrls());
			//collect main and aux data urls and get other pages
			List<TableRow> mainAndAuxPages = new ArrayList<TableRow>();
			mainAndAuxPages.addAll(mainPages);
			mainAndAuxPages.addAll(auxPages);
			where="mainUrl in "+createInCriteriaFromRows(mainAndAuxPages);
			select="SELECT distinct url\r\n" + 
					"FROM dataconfig_things";
			List<TableRow> otherPages = jdbcRepo.qtbGroupReport(select, "", where, headersToSelectUrls());
			//collect all pages need for the application and get pages for which configuration is defined
			List<TableRow> applicationPages=new ArrayList<TableRow>();
			applicationPages.addAll(mainAndAuxPages);
			applicationPages.addAll(otherPages);
			where="url in "+createInCriteriaFromRows(applicationPages);
			select="SELECT url \r\n" + 
					"FROM dataconfig_defined";
			List<TableRow> definedPages =jdbcRepo.qtbGroupReport(select, "", where, headersToSelectUrls());
			//collect urls of required and defined pages
			Set<String> applPageUrls=urlsFromRows(applicationPages);
			Set<String> definedPageUrls = urlsFromRows(definedPages);
			//create output table
			if(data.getDataConfigurations().getHeaders().getHeaders().isEmpty()) {
				data.getDataConfigurations().setHeaders(headersOnScreenTable());
			}
			long i =1;
			for(String url :applPageUrls) {
				TableRow row = TableRow.instanceOf(i);
				row.getRow().add(TableCell.instanceOf("url",url));
				row.getRow().add(TableCell.instanceOf("good",definedPageUrls.contains(url)));
				rows.add(row);
				i++;
			}
			Collections.sort(rows, new Comparator<TableRow>() {

				@Override
				public int compare(TableRow o1, TableRow o2) {
					return o1.getRow().get(0).getValue().compareTo(o2.getRow().get(0).getValue());
				}

			});
		}
		return rows;
	}
	/**
	 * Headers for on screen tables
	 * @return
	 */
	private Headers headersOnScreenTable() {
		Headers ret = headersToSelectUrls();
		ret.getHeaders().add(TableHeader.instanceOf(
				"good",
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
	 * Headers to select URLs
	 * @return
	 */
	private Headers headersToSelectUrls() {
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
