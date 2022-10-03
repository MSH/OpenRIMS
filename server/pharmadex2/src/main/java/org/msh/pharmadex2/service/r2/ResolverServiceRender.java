package org.msh.pharmadex2.service.r2;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.ThingLink;
import org.msh.pdex2.model.r2.ThingPerson;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.LinkDTO;
import org.msh.pharmadex2.dto.LinksDTO;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The helper class responsible for render values for the ResolverService
 * @author alexk
 *
 */
@Service
public class ResolverServiceRender {
	public static final String CHANGES = "changes";
	public static final String FORM = "form";
	public static final String ERROR_TAG = "_ERROR_";
	@Autowired
	private Messages mess;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private AssemblyService assemblyServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private AmendmentService amendServ;
	@Autowired
	private RegisterService regServ;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private LinkService linkServ;

	/**
	 * Render a whole object as a table
	 * @param var a root of the object
	 * @param dataUrl the assembly or data url
	 * @param assemblies known assemblies
	 * @param varName for the main header
	 * @param value map (EL, value) 
	 * @param deepDive - add all related things as well
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, Object> objectAsTable(Concept var, String dataUrl, String varName, Map<String, 
			List<AssemblyDTO>> assemblies, Map<String, Object> value, boolean deepDive) throws ObjectNotFoundException {
		//get all assemblies
		List<Assembly> assms = assemblyServ.loadDataConfiguration(dataUrl); 
		if(value.get(FORM)==null) {
			value.put(FORM,initTable(varName,"",value));
		}
		TableQtb table = (TableQtb) value.get(FORM);
		//first row should be a prefLabel, in case the prefLabel is not under the var
		for(Assembly asm :assms) {
			table=renderClazz(asm, var, table, deepDive);
		}
		return value;
	}


	/**
	 * Create or get the table
	 * @param description 
	 * @param value
	 * @return
	 */
	public TableQtb initTable(String varName, String description, Map<String, Object> value) {
		if(value.get(FORM)==null) {
			TableQtb table = new TableQtb();
			value.put(FORM,table);
		}
		TableQtb table = (TableQtb) value.get(FORM);
		table.setPaintBorders(false);
		Headers headers=table.getHeaders();
		headers.getHeaders().add(TableHeader.instanceOf("varname", mess.get(varName), 40, TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("description",description , 80, TableHeader.COLUMN_STRING));
		return table;
	}


	/**
	 * Render a particular class from the assembly
	 * @param asm
	 * @param var
	 * @param table
	 * @param deepDive deep dive to the related things
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public TableQtb renderClazz(Assembly asm, Concept var, TableQtb table, boolean deepDive) throws ObjectNotFoundException {
		String clazz = asm.getClazz();
		String varName=asm.getPropertyName().getIdentifier();
		if(clazz.equalsIgnoreCase("literals") || clazz.equalsIgnoreCase("strings")) {
			String valStr = literalServ.readValue(varName, var);
			if(valStr.length()>0) {
				table=tableRow(asm.getID(), varName, valStr, false, table);
			}
		}
		if(clazz.equalsIgnoreCase("numbers")) {
			String valStr = literalServ.readValue(varName, var);
			if(valStr.length()>0) {
				try {
					Long valNum=Long.parseLong(valStr);
					valStr=DecimalFormat.getInstance().format(valNum);
				} catch (NumberFormatException e) {
					valStr=mess.get("number_format_error");
				}
				table=tableRow(asm.getID(), varName, valStr,  false, table);
			}
		}
		if(clazz.equalsIgnoreCase("dates")) {
			String valStr = literalServ.readValue(varName, var);
			if(valStr.length()>0) {
				try {
					LocalDate ld = LocalDate.parse(valStr, DateTimeFormatter.ISO_DATE);
					valStr=localDateToString(ld);
				} catch (Exception e) {
					valStr=mess.get("date_format_error");
				}
				table=tableRow(asm.getID(), varName, valStr, false, table);
			}
		}
		if(clazz.equalsIgnoreCase("dictionaries")) {
			Thing thing = boilerServ.thingByNode(var);
			List<Concept> selected = new ArrayList<Concept>();
			for(ThingDict td : thing.getDictionaries()) {
				if(td.getVarname().equalsIgnoreCase(varName)) {
					selected.add(td.getConcept());
				}
			}
			Map<String,Object> vals = new HashMap<String, Object>();
			vals=dictionaryValues(vals, selected);
			String choice =(String) vals.get("choice");
			table=tableRow(asm.getID(), varName, choice, false, table);
		}
		if(clazz.equalsIgnoreCase("intervals")) {
			Map<String,Object> vals = new HashMap<String, Object>();
			vals=interval(varName, var, vals);
			String fromStr="*";
			if(vals.get("from") instanceof LocalDate) {
				LocalDate fromLd = (LocalDate) vals.get("from");
				fromStr=localDateToString(fromLd);
			}
			String toStr="*";
			if(vals.get("to") instanceof LocalDate) {
				LocalDate toLd = (LocalDate) vals.get("to");
				toStr=" / "+localDateToString(toLd);
			}
			table=tableRow(asm.getID(), mess.get(varName),fromStr+ toStr , false, table);
		}

		if(clazz.equalsIgnoreCase("addresses")) {
			Map<String,Object> vals = new HashMap<String, Object>();
			vals=address(varName, var, vals);
			table=tableRow(asm.getID(), mess.get(varName),"" , false, table);
			String choice=(String) vals.get("choice");
			String gis = (String) vals.get("gis");
			table=tableRow(asm.getID(), mess.get("choice_addr"), choice, false, table);
			table=tableRow(asm.getID(), mess.get("gis_addr"), gis, false, table);
		}

		if(clazz.equalsIgnoreCase("persons")) {
			if(deepDive) {
				table=personDetails(asm,var,table);
			}else {
				table = presonsTable(asm, var, table);
			}
		}
		
		if(clazz.equalsIgnoreCase("links")) {
			table = linksTable(asm, var, table);
		}

		if(clazz.equalsIgnoreCase("things") && deepDive) {
			Thing thing = boilerServ.thingByNode(var);
			for(ThingThing tt : thing.getThings()) {
				if(tt.getVarname().equalsIgnoreCase(varName)) {
					List<Concept> roots = closureServ.loadParents(tt.getConcept());
					if(roots.size()>1) {
						table=tableRow(tt.getID(), "", "", false, table);
						table=tableRow(tt.getID(), "*  " +mess.get(tt.getVarname()), "", true, table);
						table=tableRow(tt.getID(), "", "", false, table);
						int rows=table.getRows().size();
						List<Assembly> assms1 = assemblyServ.loadDataConfiguration(roots.get(0).getIdentifier());
						for(Assembly asm1 :assms1) {
							table=renderClazz(asm1, tt.getConcept(), table,deepDive);
						}
						if(table.getRows().size()==rows) {
							//no add, remove the header
							table.setRows(table.getRows().subList(0, rows-1));
						}
					}
				}
			}
		}
		return table;
	}


	/**
	 * Person details for all persons 
	 * @param asm
	 * @param var
	 * @param table
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private TableQtb personDetails(Assembly asm, Concept var, TableQtb table) throws ObjectNotFoundException {
		Thing thing = new Thing();
		thing=boilerServ.thingByNode(var,thing);
		if(thing.getID()>0 && thing.getUrl() != null && thing.getUrl().length()>0) {
			int num=1;
			for(ThingPerson tp : thing.getPersons()) {
				if(tp.getConcept().getActive()) {
					String label=literalServ.readPrefLabel(tp.getConcept());
					table=tableRow(0l,num+")" , label, true, table);
					//get all assemblies
					if(tp.getPersonUrl()!=null && tp.getPersonUrl().length()>0) {
						List<Assembly> assms = assemblyServ.loadDataConfiguration(tp.getPersonUrl()); 
						for(Assembly assm : assms ) {
							table=renderClazz(assm, tp.getConcept(), table,false);
						}
					}
					num++;
				}
			}
		}
		return table;
	}


	/**
	 * Represent persons component as a table
	 * @param asm
	 * @param var
	 * @param table
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public TableQtb presonsTable(Assembly asm, Concept var, TableQtb table) throws ObjectNotFoundException {
		List<String> names = personNameList(var,true);
		List<String> removed=personNameList(var,false);
		List<String> addNames = new ArrayList<String>();
		Thing thing = new Thing();
		thing=boilerServ.thingByNode(var,thing);
		if(thing.getID()>0) {
			if(thing.getAmendments().iterator().hasNext()) {
				//all persons from amended
				addNames= personNameList(thing.getAmendments().iterator().next().getConcept(),true);
			}
		}
		for(String name : addNames) {
			if(!names.contains(name)) {
				names.add(name);
			}
		}
		names.removeAll(removed);
		table=tableRow(asm.getID(), mess.get("prefLabel"), String.join(", ", names), false, table);
		return table;
	}
	/**
	 * Get list of person's names
	 * @param persons - concept with the person list
	 * @param all - true- all, false - only removed
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public List<String> personNameList(Concept persons, boolean all) throws ObjectNotFoundException {
		List<Concept> pers = personList(persons,all);
		List<String> names = new ArrayList<String>();
		if(pers!=null) {
			for(Concept p : pers) {
				String pref = literalServ.readPrefLabel(p);
				names.add(pref);
			}
		}
		Collections.sort(names);
		return names;
	}
	/**
	 * Represent links component as a table
	 * @param asm
	 * @param var
	 * @param table
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public TableQtb linksTable(Assembly asm, Concept var, TableQtb table) throws ObjectNotFoundException {
		table=tableRow(0, mess.get(asm.getPropertyName().getIdentifier()), "", true, table);
		LinksDTO links = new LinksDTO();
		links.setNodeID(var.getID());
		links = linkServ.loadLinks(links, asm.getPropertyName().getIdentifier());
		for(LinkDTO link : links.getLinks()) {
			table=tableRow(link.getID(), link.getObjectLabel(), link.getDictLabel(), false, table);
		}
		table=tableRow(0,"", "", false, table);
		return table;
	}
	/**
	 * Read an address and represent it as a map
	 * @param varName 
	 * @param var
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public Map<String, Object> address(String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		value.clear();
		Thing thing = boilerServ.thingByNode(var);
		for(ThingThing tt : thing.getThings()) {
			if(tt.getVarname().equalsIgnoreCase(varName)) {
				Concept addr = tt.getConcept();
				value.put("gis", addr.getLabel());
				Thing addrThing = boilerServ.thingByNode(addr);
				Concept au = new Concept();
				for(ThingDict td : addrThing.getDictionaries()) {
					au=td.getConcept();
				}
				if(au.getID()>0) {
					value=addressLevels(value, au);
				}
			}
		}
		return value;
	}
	/**
	 * create address levels map from admin unit dict node 
	 * @param value map
	 * @param au admin unit dict node 
	 * @throws ObjectNotFoundException
	 */
	public Map<String, Object> addressLevels(Map<String, Object> value, Concept au) throws ObjectNotFoundException {
		List<Concept> all = closureServ.loadParents(au);
		List<String> choices= new ArrayList<String>();
		for(int i=1; i<all.size();i++) {
			String item = literalServ.readPrefLabel(all.get(i));
			value.put("level"+i, item);
			choices.add(item);
		}
		value.put("choice", String.join(", ", choices));
		//for some reason it will be nice to reserve up to 10 levels
		for(int i=all.size();i<10;i++) {
			value.put("level"+i, "");
		}
		return value;
	}


	/**
	 * Local Date to String using TableCell
	 * @param ld
	 * @return
	 */
	public String localDateToString(LocalDate ld) {
		TableCell cell = TableCell.instanceOf("first", ld, LocaleContextHolder.getLocale());
		String valStr=cell.getValue();
		return valStr;
	}

	/**
	 * Add a row to the table
	 * @param rowId any long value, doesn't matter
	 * @param label the first column
	 * @param value the second column
	 * @param selected true - highlight by background color
	 * @param table the table
	 * @return
	 */
	public TableQtb tableRow(long rowId, String label, String value, boolean selected, TableQtb table) {
		TableRow row= TableRow.instanceOf(rowId);
		row.setSelected(selected);	//color!!!
		row.getRow().add(TableCell.instanceOf("first", mess.get(label)));
		row.getRow().add(TableCell.instanceOf("second", value));
		table.getRows().add(row);
		return table;
	}

	/**
	 * Add a row to the table for changes
	 * @param rowId any long value, doesn't matter
	 * @param label the first column
	 * @param value the second column
	 * @param selected true - highlight by background color
	 * @param table the table
	 * @return
	 */
	public TableQtb tableChangeRow(long rowId, String label, String oldValue, String newValue, boolean selected, TableQtb table) {
		TableRow row= TableRow.instanceOf(rowId);
		row.setSelected(selected);	//color!!!
		row.getRow().add(TableCell.instanceOf("1", mess.get(label)));
		row.getRow().add(TableCell.instanceOf("2", oldValue));
		row.getRow().add(TableCell.instanceOf("2", newValue));
		table.getRows().add(row);
		return table;
	}

	/**
	 * Create values from the dictionary
	 * For single choice - all levels
	 * For mult choice a String that represents a list
	 * @param ad
	 * @param value
	 * @param selected
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public Map<String, Object> dictionaryValues(Map<String, Object> value, List<Concept> selected)
			throws ObjectNotFoundException {
		List<String> choices= new ArrayList<String>();
		if(selected.size()>0) {
			List<Concept> all = closureServ.loadParents(selected.get(0));
			for(int i=1; i<all.size()-1;i++) {
				String item = literalServ.readPrefLabel(all.get(i));
				value.put("level"+i, item);
				choices.add(item);
			}
			if(selected.size()==1) {
				String item = literalServ.readPrefLabel(selected.get(0));
				value.put("level"+(all.size()-1), item);
				choices.add(item);
			}else {
				List<String> mChoice = new ArrayList<String>();
				for(Concept dictNode : selected) {
					mChoice.add(literalServ.readPrefLabel(dictNode));
				}
				String item = String.join(",", mChoice);
				value.put("level"+(all.size()-1), item);
				choices.add("/"+item);
			}
			value.put("choice", String.join(", ", choices));		
		}
		return value;
	}

	/**
	 * Resolve an interval
	 * @param ival interval's assembly
	 * @param varName
	 * @param var the node
	 * @param value the result
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public Map<String, Object> interval(String varName, Concept var, Map<String, Object> value) throws ObjectNotFoundException {
		LocalDate from=dtoServ.readDate(var,varName+"_from");
		LocalDate to=dtoServ.readDate(var, varName+"_to");
		value.put("from",from);					//to locale string!!!!
		value.put("fromBS", boilerServ.localDateToNepali(from, false));
		value.put("fromBS1",boilerServ.localDateToNepali(from, true));
		value.put("to", to);
		value.put("toBS", boilerServ.localDateToNepali(to, false));
		value.put("toBS1",boilerServ.localDateToNepali(to, true));
		return value;
	}

	/**
	 * Render whole application from the root
	 * @param fres
	 * @param historyId 
	 * @param assemblies 
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, Object> root(ResourceDTO fres, long historyId, Map<String, List<AssemblyDTO>> assemblies, 
			Map<String, Object> value) throws ObjectNotFoundException {
		History his=boilerServ.historyById(historyId);
		if(validServ.isAmendmentWorkflow(his)) {
			value=rootAmendment(fres, his, assemblies, value);
		}else {
			value = rootApplication(fres, historyId, assemblies, value);
		}
		return value;
	}
	/**
	 * Amendment application data
	 * @param fres
	 * @param his
	 * @param assemblies
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Map<String, Object> rootAmendment(ResourceDTO fres, History his,
			Map<String, List<AssemblyDTO>> assemblies, Map<String, Object> value) throws ObjectNotFoundException {
		//calculate values
		Concept amended = amendServ.amendedConcept(his.getApplicationData());
		Concept amendment = amendServ.amendmentConcept(his.getApplicationData(), amended);
		//amendment may be already implemented, thus amended values are in old
		Thing amendedThing= boilerServ.thingByNode(amendment);
		if(amendedThing.getOldValue() != null) {
			amended=amendedThing.getOldValue().getConcept();
		}
		//initialize the table
		String header = literalServ.readPrefLabel(his.getApplDict());
		String prefLabel=boilerServ.prefLabelCheck(amendment);
		if(prefLabel.length()==0) {
			prefLabel=boilerServ.prefLabelCheck(amended);
		}
		if(value.get(CHANGES)==null) {
			value.put(CHANGES,initChangesTable(header,prefLabel));
		}
		TableQtb table = (TableQtb) value.get(CHANGES);
		table=amendmentRows(assemblies, table, amended, amendment);
		return value;
	}
	/**
	 * Add to the table rows with previous and amended values
	 * @param assemblies
	 * @param table
	 * @param amended
	 * @param amendment
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public TableQtb amendmentRows(Map<String, List<AssemblyDTO>> assemblies, TableQtb table, Concept amended,
			Concept amendment) throws ObjectNotFoundException {
		Map<String,Object> amendedValues=new HashMap<String, Object>();
		Map<String,Object> amendmentValues=new HashMap<String, Object>();
		amendedValues = objectAsTable(amended, closureServ.getUrlByNode(amended), "", assemblies, amendedValues,false);
		amendmentValues = objectAsTable(amendment, closureServ.getUrlByNode(amendment), "", assemblies, amendmentValues,false);
		TableQtb amendedTable = (TableQtb) amendedValues.get(FORM);
		TableQtb amendmentTable = (TableQtb) amendmentValues.get(FORM);
		for(TableRow row : amendedTable.getRows()) {
			String label = row.getRow().get(0).getValue();
			String oldValue=row.getRow().get(1).getValue();
			String newValue="";
			for(TableRow row1 :amendmentTable.getRows()) {
				if(row1.getRow().get(0).getValue().equalsIgnoreCase(label)) {
					newValue=row1.getRow().get(1).getValue();
					table = tableChangeRow(0l, label, oldValue, newValue, false, table);
				}
			}
		}
		return table;
	}
	/**
	 * Init changes table - header and sub
	 * @param header
	 * @param prefLabel 
	 * @return
	 */
	private TableQtb initChangesTable(String header, String prefLabel) {
		TableQtb table = new TableQtb();
		Headers headers =table.getHeaders();
		table.setPaintBorders(false);
		headers.getHeaders().add(TableHeader.instanceOf("1", header, 20, TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("2",prefLabel , 40, TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("3","" , 40, TableHeader.COLUMN_STRING));
		TableRow row=TableRow.instanceOf(0l);
		row.getRow().add(TableCell.instanceOf("1", ""));
		row.getRow().add(TableCell.instanceOf("2", mess.get("prev")));
		row.getRow().add(TableCell.instanceOf("3", mess.get("amendment")));
		row.setSelected(true);
		table.getRows().add(row);
		return table;
	}

	/**
	 * Init changes table - for a list of changes
	 * @param header
	 * @return
	 */
	private TableQtb initListChangesTable() {
		TableQtb table = new TableQtb();
		Headers headers =table.getHeaders();
		table.setPaintBorders(false);
		headers.getHeaders().add(TableHeader.instanceOf("1", "", 20, TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("2","" , 40, TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("3","" , 40, TableHeader.COLUMN_STRING));
		return table;
	}

	/**
	 * Resolve root of the application, i.e. application data
	 * @param fres
	 * @param historyId
	 * @param assemblies
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public Map<String, Object> rootApplication(ResourceDTO fres, long historyId,
			Map<String, List<AssemblyDTO>> assemblies, Map<String, Object> value) throws ObjectNotFoundException {
		long nodeId = fres.getData().getNodeId();
		if(nodeId==0) {
			nodeId=fres.getData().getParentId();
		}
		Concept topConcept = topConcept("", nodeId, historyId,value);
		if(topConcept!=null) {
			List<Concept> parents = closureServ.loadParents(topConcept);
			if(parents.size()>0) {
				String pref=literalServ.readPrefLabel(topConcept);
				value=registers(topConcept, pref,value);
				value=listOfChanges(topConcept, assemblies, value);
				value=objectAsTable(topConcept, parents.get(0).getIdentifier(), "", assemblies, value,true);
			}
		}
		return value;
	}
	/**
	 * Create a list of changes under @changes
	 * @param topConcept
	 * @param assemblies 
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Map<String, Object> listOfChanges(Concept topConcept, Map<String, List<AssemblyDTO>> assemblies, Map<String, Object> value) throws ObjectNotFoundException {
		jdbcRepo.application_events(topConcept.getID());
		Headers headers=new Headers();
		headers.setPageSize(Integer.MAX_VALUE);
		headers.getHeaders().add(TableHeader.instanceOf("eventdate", TableHeader.COLUMN_LOCALDATE));
		headers.getHeaders().add(TableHeader.instanceOf("pref", TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("newdata", TableHeader.COLUMN_LONG));
		headers.getHeaders().get(0).setSort(true);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from application_events", "", "", headers);
		TableQtb resTable = initListChangesTable();
		for(TableRow row : rows) {
			Concept prev=closureServ.loadConceptById(row.getDbID());
			Concept modi=closureServ.loadConceptById(row.getRow().get(2).getIntValue());
			String prefLabel=boilerServ.prefLabelCheck(modi);
			if(prefLabel.length()==0) {
				prefLabel=boilerServ.prefLabelCheck(prev);
			}
			resTable=tableChangeRow(0, row.getRow().get(0).getValue(), row.getRow().get(1).getValue(), prefLabel, true, resTable);
			resTable=tableChangeRow(0, "", mess.get("prev"), mess.get("amendment"), true, resTable);
			resTable=amendmentRows(assemblies, resTable, prev, modi);
		}
		value.put("changes",resTable);
		return value;
	}


	/**
	 * Get and put to 
	 * @param topConcept
	 * @param pref 
	 * @param value
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private Map<String, Object> registers(Concept topConcept, String pref, Map<String, Object> value) throws ObjectNotFoundException {
		if(value.get(FORM)==null) {
			value.put(FORM, initTable(pref,"",value));
		}
		TableQtb table=(TableQtb) value.get(FORM);
		Concept objectData=boilerServ.initialApplicationNode(topConcept);
		Map<String,RegisterDTO> allRegisters = regServ.registersLoadByApplicationData(objectData);
		for(String key : allRegisters.keySet()) {
			RegisterDTO rDto = allRegisters.get(key);
			String expiry="";
			if(rDto.hasExpired()) {
				expiry="/"+ localDateToString(rDto.getExpiry_date().getValue());
			}
			String register= rDto.getReg_number().getValue() +"/"+rDto.getRegistration_date().getValue()+expiry;
			table=tableRow(0l, mess.get(rDto.getVarName()), register, true, table);
		}
		return value;
	}


	/**
	 * Search for top concept in data, then in history
	 * @param url top url
	 * @param historyId - id of the current history
	 * @param nodeId 
	 * @return null if not found
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept topConcept(String url,  long nodeId, long historyId, Map<String, Object> ret) throws ObjectNotFoundException {
		History his = boilerServ.historyById(historyId);
		//try in application data from the root of one
		List<Concept> all = closureServ.loadParents(his.getApplicationData());
		if(url.length()==0 || all.get(0).getIdentifier().equalsIgnoreCase(url)) {
			return amendServ.initialApplicationData(his.getApplicationData());
			/*Concept amended = amendServ.amendedConcept(his.getApplicationData());
			if(amended.getID()>0) {
				return amendServ.amendedApplication(amended);
			}else {
				return his.getApplicationData();
			}*/
		}
		if(url.equals("~")) {					//home node of this object. Assume two level hierarchy. Any ThingThing point to it
			if(nodeId>0) {
				Concept node = closureServ.loadConceptById(nodeId);
				Concept home = boilerServ.homeNode(node);
				return home;
			}else {
				ret=error("topConcept ~", "Node is not saved yet",ret);
			}
		}
		//try in activity data
		if(his.getApplication()!=null) {
			List<History> allHis = boilerServ.historyAllByApplication(his.getApplication());
			for(History history :allHis) {
				if(history.getDataUrl()!=null
						&& (url.length()==0 || history.getDataUrl().equalsIgnoreCase(url))
						&& history.getActivityData()!=null) {
					return history.getActivityData();
				}
			}
		}
		ret = error("url/historyId is " +url+"/"+historyId, "topConcept.Top concept not found", ret);
		return null;
	}
	/**
	 * Add an error message to the map
	 * @param message
	 * @param expr string for which error occurs
	 * @param ret map of the resolved variables
	 * @return
	 */
	public Map<String, Object> error(String expr, String message, Map<String, Object> ret) {
		String errMess = "["+ expr + ": " +message + "]";
		Object err = ret.get(ERROR_TAG);
		if(err!=null && err instanceof String) {
			String errStr=(String) err;
			errMess = errStr+", "+errMess;
		}else {
			ret.put(ERROR_TAG, errMess);
		}
		return ret;
	}

	/**
	 * Get list of persons
	 * @param var
	 * @param all - true - all, false - removed only
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public List<Concept> personList(Concept var, boolean all) throws ObjectNotFoundException {
		Thing thing = boilerServ.thingByNode(var);
		List<Concept> pers = new ArrayList<Concept>();
		for(ThingPerson tp : thing.getPersons()) {
			if(tp.getConcept().getActive()) {
				if(all) {
					pers.add(tp.getConcept());
				}else {
					if(tp.getVarName().equalsIgnoreCase(AmendmentService.REMOVE_PERSON)) {
						pers.add(tp.getConcept());
					}
				}
			}
		}
		Collections.sort(pers, new Comparator<Concept>(){
			@Override
			public int compare(Concept o1, Concept o2) {
				long o1L = o1.getID();
				long o2L = o2.getID();
				if(o1L>o2L) {
					return 1;
				}
				if(o1L<o2L) {
					return -1;
				}
				return 0;
			}
		});

		return pers;
	}
	/**
	 * Render a thing bound to the parent
	 * @param varName 
	 * @param var
	 * @param assemblies
	 * @param value
	 * @param deepDive - and relatd thongs as well
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, Object> thing(String varName, Concept var, Map<String, List<AssemblyDTO>> assemblies,
			Map<String, Object> value, boolean deepDive) throws ObjectNotFoundException {
		if(var != null) {
			if(value.get("from")==null) {
				value.put("from",initTable(varName,"",value));
			}
			TableQtb table = (TableQtb) value.get("from");
			value.put(FORM, table);
			Thing thing = boilerServ.thingByNode(var);
			for(ThingThing tt : thing.getThings()) {
				if(tt.getVarname().equalsIgnoreCase(varName)) {
					value=objectAsTable(tt.getConcept(), tt.getUrl(), tt.getVarname(), assemblies, value, deepDive);
				}
			}
		}
		return value;
	}

	/**
	 * Get list of links
	 * @param var concept on which links are
	 * @param varName - variable name of "links" component
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<ThingLink> linkList(Concept var, String varName) throws ObjectNotFoundException {
		List<ThingLink> ret = linkServ.list(var, varName);
		return ret;
	}




}
