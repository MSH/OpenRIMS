package org.msh.pharmadex2.service.r2;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DataSourceDTO;
import org.msh.pharmadex2.dto.DataSourceDetailsDTO;
import org.msh.pharmadex2.dto.DataSourcesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Creation of various data sources
 */
@Service
public class DataSourceService {
	private static final String HEADER_SAMPLES = "samples";
	private static final String HEADER_FIELD = "field";
	private static final String HEADER_FILTER_FIELD = "filterField";
	private static final String HEADER_DESCRIPTION = "description";
	private static final String HEADER_FILTER = "filter";
	private static final String LINE_FEED = "\r\n";
	private static final String DATA_SOURCES_ROOT = "system.data.sources";
	private static final String HEADER_FIELDS = "fields";
	private static final String HEADER_FILTERS = "filters";
	private static final String HEADER_DATATYPE = "datatype";
	private static final String APPLICATION_ID = "ApplicationID";
	private static final Logger logger = LoggerFactory.getLogger(DataSourceService.class);
	@Autowired
	private Messages messages;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private UrlAssistantService urlValidator;

	private static final List<String> views = new ArrayList<>(Arrays.asList(
			Messages.markAsKey("pv_applications"),
			Messages.markAsKey("pv_activities"),
			Messages.markAsKey("pv_addresses"),
			Messages.markAsKey("pv_classifiers"),
			Messages.markAsKey("pv_links"),
			Messages.markAsKey("pv_literals"),
			Messages.markAsKey("pv_events")
			));
	private static final Map<String, String> filterSqls = new HashMap<String, String>(){
		{
			put("pv_applications",
					"select * from (SELECT distinct Lang as 'Lang', 'ApplicationUrl' as 'filterField',  ApplicationUrl as 'filter', ApplicationDescription as 'description'\r\n"
							+ "FROM pv_applications\r\n"
							+ "union\r\n"
							+ "SELECT distinct Lang as 'Lang', 'State' as 'filterField',  State as 'filter', rm.message_value as 'description'\r\n"
							+ "FROM pv_applications pa\r\n"
							+ "join resource_bundle rb on rb.locale=pa.Lang\r\n"
							+ "left join resource_message rm on rm.key_bundle=rb.id and rm.message_key=pa.State) t");
			put("pv_activities",
					"select distinct * from (\r\n"
					+ "	SELECT distinct Lang as 'Lang', 'WorkflowGroupURL' as 'filterField',  WorkflowGroupURL as 'filter', WorkflowGroupName as 'description'\r\n"
					+ "	FROM pv_activities\r\n"
					+ "	union\r\n"
					+ "	SELECT distinct Lang as 'Lang', 'WorkflowURL' as 'filterField',  WorkflowURL as 'filter', WorkflowPrefLabel as 'description'\r\n"
					+ "	FROM pv_activities\r\n"
					+ "	union\r\n"
					+ "	SELECT distinct Lang as 'Lang', 'ActivityCompleted' as 'filterField',  ActivityCompleted as 'filter', '' as 'description'\r\n"
					+ "	FROM pv_activities\r\n"
					+ "	union\r\n"
					+ "	SELECT distinct Lang as 'Lang', 'ActivityURL' as 'filterField',  ActivityURL as 'filter', ActivityName as 'description' \r\n"
					+ "	FROM pv_activities\r\n"
					+ "	union\r\n"
					+ "	SELECT distinct Lang as 'Lang', 'ActivityOutcome' as 'filterField',  ActivityOutcome as 'filter', '' as 'description'\r\n"
					+ "	FROM pv_activities ) t");
			put("pv_addresses","select * from (\r\n"
					+ "SELECT distinct pa.`Lang` as 'Lang', pa.`AddressURL` as 'filter', 'AddressURL' as 'filterField', '' as 'description'\r\n"
					+ "FROM pv_addresses pa\r\n"
					+ "where pa.`AddressLevel`=0\r\n"
					+ "union\r\n"
					+ "SELECT distinct pa.`Lang` as 'Lang', pa.`PageURL` as 'filter', 'PageURL' as 'filterField', '' as 'description'\r\n"
					+ "FROM pv_addresses pa\r\n"
					+ "where pa.`AddressLevel`=0\r\n"
					+ "union\r\n"
					+ "SELECT distinct pa.Lang as 'Lang', pa.`Variable` as 'filter', 'Variable' as 'filterField',  rm.message_value as 'description'\r\n"
					+ "FROM pv_addresses pa\r\n"
					+ "join resource_bundle rb on rb.`locale`=pa.Lang\r\n"
					+ "left join resource_message rm on rm.message_key=pa.`Variable` and rm.key_bundle=rb.id\r\n"
					+ "where pa.`AddressLevel`=0\r\n"
					+ "union\r\n"
					+ "SELECT distinct pa.Lang as 'Lang', pa.`AddressLevel` as 'filter', 'AddressLevel' as 'filterField',  '' as 'description'\r\n"
					+ "FROM pv_addresses pa\r\n"
					+ ") t");
			put("pv_classifiers","select distinct * from (\r\n"
					+ "SELECT distinct pc.Lang as 'Lang', pc.`ClassifierURL` as 'filter', du.prefLabel as 'description', 'ClassifierURL' as 'filterField' \r\n"
					+ "FROM pv_classifiers pc\r\n"
					+ "join dict_urls du on du.`url`=pc.ClassifierURL and du.Lang=pc.Lang\r\n"
					+ "where pc.`ClassifierLevel`=0\r\n"
					+ "union\r\n"
					+ "SELECT distinct pc.Lang as 'Lang', pc.ClassifierVarPageURL as 'filter', '' as 'description', 'ClassifierVarPageURL' as 'filterField' \r\n"
					+ "FROM pv_classifiers pc\r\n"
					+ "where pc.`ClassifierLevel`=0\r\n"
					+ "union\r\n"
					+ "SELECT distinct pc.Lang as 'Lang', pc.ClassifierVar as 'filter', rm.message_value as 'description', 'ClassifierVar' as 'filterField' \r\n"
					+ "FROM pv_classifiers pc\r\n"
					+ "join resource_bundle rb on rb.`locale`=pc.Lang\r\n"
					+ "left join resource_message rm on rm.message_key=pc.ClassifierVar and rm.key_bundle=rb.id\r\n"
					+ "where pc.`ClassifierLevel`=0\r\n"
					+ "union\r\n"
					+ "SELECT distinct pc.Lang as 'Lang', pc.ClassifierLevel as 'filter', '' as 'description', 'ClassifierLevel' as 'filterField' \r\n"
					+ "FROM pv_classifiers pc\r\n"
					+ ") t");
			put("pv_links", "select distinct distinct * from (\r\n"
					+ "SELECT  distinct pl.`Lang` as 'Lang', pl.`LinkURL` as `filter`, du.prefLabel as 'description', 'LinkURL' as 'filterField'\r\n"
					+ "FROM pv_links pl\r\n"
					+ "join data_urls du on du.`url`=pl.`LinkURL` and du.Lang=pl.Lang\r\n"
					+ "union\r\n"
					+ "SELECT  distinct pl.`Lang` as 'Lang', pl.`LinkVariable` as `filter`, rm.message_value as 'description', 'LinkVariable' as 'filterField'\r\n"
					+ "FROM pv_links pl\r\n"
					+ "join resource_bundle rb on rb.`locale`=pl.`Lang`\r\n"
					+ "left join resource_message rm on rm.message_key=pl.LinkVariable and rm.key_bundle=rb.id\r\n"
					+ ") t");
			put("pv_events", "select distinct * from (\r\n"
					+ "SELECT  distinct pe.lang as 'Lang', pe.`EventURL` as `filter`, '' as 'description', 'EventURL' as 'filterField'\r\n"
					+ "FROM pv_events pe\r\n"
					+ "union\r\n"
					+ "select distinct pe.`Lang` as 'Lang', pe.EventVariable as 'filter', rm.message_value as 'description', 'EventVariable' as 'FilterField'\r\n"
					+ "FROM pv_events pe\r\n"
					+ "join resource_bundle rb on rb.`locale`=pe.`Lang`\r\n"
					+ "left join resource_message rm on rm.key_bundle=rb.id and rm.message_key=pe.EventVariable\r\n"
					+ ") t1");
			put("pv_literals","select * from (\r\n"
					+ "select distinct pl.`Lang` as 'Lang', pl.`Variable` as 'filter', rm.message_value as 'description', 'Variable' as 'FilterField'\r\n"
					+ "FROM pv_literals pl\r\n"
					+ "join resource_bundle rb on rb.`locale`=pl.`Lang`\r\n"
					+ "left join resource_message rm on rm.key_bundle=rb.id and rm.message_key=pl.`Variable`\r\n"
					+ "union\r\n"
					+ "SELECT distinct pl.Lang as `Lang`, pl.PageURL as `filter`, '' as 'description', 'PageURL' as 'filterField' \r\n"
					+ "FROM pv_literals pl) t");
		}
	};
	/**
	 * List of the data sources that have been stored for a selected data sources dictionary item 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DataSourcesDTO sources(DataSourcesDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		TableQtb table=data.getDataSources();
		if(!table.isLoaded()) {
			table.getHeaders().getHeaders().add(TableHeader.instanceOf(
					"ds_url",
					messages.get("url"),
					true,
					true,
					true,
					TableHeader.COLUMN_STRING, 0));
			table.getHeaders().getHeaders().add(TableHeader.instanceOf(
					HEADER_DESCRIPTION,
					messages.get(HEADER_DESCRIPTION),
					true,
					true,
					true,
					TableHeader.COLUMN_STRING, 0));
		}
		String select="select * from data_sources ds";
		List<TableRow> rows=jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		TableQtb.tablePageKeepSelection(rows, table, data.isSelectedOnly());
		return data;
	}



	/**
	 * Load a data source selected
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 * @throws DatabindException 
	 * @throws StreamReadException 
	 */
	@Transactional
	public DataSourceDTO load(DataSourceDTO data) throws ObjectNotFoundException, StreamReadException, DatabindException, IOException {
		data.clearErrors();
		if(data.getDataSourceID()>0) {
			Concept node = closureServ.loadConceptById(data.getDataSourceID());
			String dataSourceDTOJson = node.getLabel();
			data=objectMapper.readValue(dataSourceDTOJson.getBytes(), DataSourceDTO.class);
		}else {
			data=new DataSourceDTO();
			data=dataTypes(data);
		}
		data=counters(data);
		return data;
	}

	/**
	 * Create/reload data types table
	 * @param data
	 * @return
	 */
	private DataSourceDTO dataTypes(DataSourceDTO data) {
		if(!data.getDataTypes().isLoaded()) {
			data=createDataTypes(data);
		}else {
			data=reloadDataTypes(data);
		}
		return data;
	}
	/**
	 * Re-calculate quantities of filters and fields for data types 
	 * @param data
	 * @return
	 */
	private DataSourceDTO reloadDataTypes(DataSourceDTO data) {
		for(TableRow row : data.getDataTypes().getRows()) {
			String dataType=row.getRow().get(0).getKey();
			int size=0;
			// filters
			if(data.getFilters().get(dataType) != null) {
				size=data.getFilters().get(dataType).size();
			}else {
				size=0;
			}
			row.getCellByKey(HEADER_FILTERS).setValue(""+size);
			row.getCellByKey(HEADER_FILTERS).setOriginalValue(size);
			// fields
			size=0;
			if(data.getFields().get(dataType) != null) {
				size=data.getFields().get(dataType).size();
			}else {
				size=0;
			}
			row.getCellByKey(HEADER_FIELDS).setValue(""+size);
			row.getCellByKey(HEADER_FIELDS).setOriginalValue(size);
		}
		return data;
	}
	/**
	 * Create a table allows data type selection
	 * @param data
	 * @return
	 */
	private DataSourceDTO createDataTypes(DataSourceDTO data) {
		data.getDataTypes().getRows().clear();
		data.getDataTypes().getHeaders().getHeaders().clear();
		data.getDataTypes().getHeaders().getHeaders().add(TableHeader.instanceOf(HEADER_DATATYPE,
				messages.get("datasources"),
				0,
				TableHeader.COLUMN_LINK));
		data.getDataTypes().getHeaders().getHeaders().add(TableHeader.instanceOf(HEADER_FILTERS,
				messages.get("filters"),
				0,
				TableHeader.COLUMN_LONG));
		data.getDataTypes().getHeaders().getHeaders().add(TableHeader.instanceOf(HEADER_FIELDS,
				messages.get("fields"),
				0,
				TableHeader.COLUMN_LONG));

		// fill out the table, selected fields and filters
		for(String key : views) {
			data.getDataTypes().getRows().add(createDataType(key,
					views.indexOf(key)));
			data.getFields().put(key, new ArrayList<Long>());
			data.getFilters().put(key, new ArrayList<Long>());
		}

		data.getDataTypes().setSelectable(false);
		return data;
	}
	/**
	 * Create a row in the data types table
	 * @param key
	 * @param index 
	 * @return
	 */
	private TableRow createDataType(String key, int index) {
		TableRow row = TableRow.instanceOf(index);
		TableCell cell= TableCell.instanceOf(HEADER_DATATYPE);
		cell.setValue(messages.get(key));
		cell.setOriginalValue(key);
		row.getRow().add(cell);
		TableCell cell1= TableCell.instanceOf(HEADER_FILTERS);
		cell1.setValue("0");
		cell1.setOriginalValue(Long.valueOf(0l));
		row.getRow().add(cell1);
		TableCell cell2= TableCell.instanceOf(HEADER_FIELDS);
		cell2.setValue("0");
		cell2.setOriginalValue(Long.valueOf(0l));
		row.getRow().add(cell2);
		return row;
	}


	/**
	 * load/create filters and filelds for a data source given
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DataSourceDetailsDTO details(DataSourceDetailsDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data.setSourceLabel(messages.get(data.getSource()));
		String filterSql = filterSqls.get(data.getSource());
		if(filterSql != null) {
			data=pv_filters(filterSql,data);
			data=pv_fields(data);
		}else{
			data.addError(messages.get("invalid_data_source")+ ": '"+ data.getSource()+"'");
		}
		return data;
	}


	/**
	 * select a random row from a pv_* view
	 * @param data 
	 * @return
	 */
	private DataSourceDetailsDTO pv_fields(DataSourceDetailsDTO data) {
		TableQtb table = data.getFieldsTable();
		if(!table.isLoaded()) {
			table=createFieldsHeaders(table);
		}
		List<TableRow> rows = fieldsInView(data.getSource());
		TableQtb.tablePageKeepSelection(rows, table, data.isFieldsSelectedOnly());
		return data;
	}


	/**
	 * Get list of rows contains fields and data examples from a view given 
	 * @param viewName
	 * @return
	 */
	private List<TableRow> fieldsInView(String viewName) {
		List<TableRow> rows = new ArrayList<TableRow>();
		TableRow sample = randomRow(viewName, 1000);
		// pivot it!
		for(TableCell cell : sample.getRow()) {
			TableRow row = TableRow.instanceOf(rows.size()+1);
			row.getRow().add(TableCell.instanceOf(HEADER_FIELD, cell.getKey()));
			row.getRow().add(TableCell.instanceOf(HEADER_SAMPLES, cell.getValue()));
			rows.add(row);
		}
		return rows;
	}


	/**
	 * GEt a random row from a view given
	 * @param viewName
	 * @param limit
	 * @return
	 */
	private TableRow randomRow(String viewName, int limit) {
		String select = "select * from " + viewName; 
		List<TableHeader> headers = jdbcRepo.headersFromSelect(select + " LIMIT 1", new ArrayList<String>());
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select + " LIMIT "+limit, "", "", Headers.of(headers));
		Random rand= new Random(LocalTime.now().toNanoOfDay());
		int index=rand.nextInt(rows.size());
		TableRow row = rows.get(index);
		return row;
	}



	/**
	 * Create headers for fields table
	 * @param table
	 * @return
	 */
	private TableQtb createFieldsHeaders(TableQtb table) {
		table.getHeaders().getHeaders().clear();
		table.getHeaders().getHeaders().add(TableHeader.instanceOf(HEADER_FIELD, messages.get("global_field"),
				true, false, false, TableHeader.COLUMN_STRING, 0));
		table.getHeaders().getHeaders().add(TableHeader.instanceOf(HEADER_SAMPLES, messages.get(HEADER_SAMPLES),
				true, false, false, TableHeader.COLUMN_STRING, 0));
		return table;
	}



	/**
	 * All guest applications URLs and descriptions from pv_applications table
	 * @param filterSql 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws Exception 
	 */
	private DataSourceDetailsDTO pv_filters(String filterSql, DataSourceDetailsDTO data){
		TableQtb table = data.getFiltersTable();
		if(!table.isLoaded()) {
			table.setHeaders(createFilterHeaders());
		}
		List<TableRow> rows=filterRows(filterSql, table.getHeaders());
		TableQtb.tablePageKeepSelection(rows, table, data.isFiltersSelectedOnly());
		return data;
	}

	/**
	 * All rows for pv_applications filter
	 * @param headers 
	 * @param headers
	 * @return
	 */
	List<TableRow> filterRows(String filterSql, Headers headers){
		String select=filterSql;
		String where="Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, headers);
		for(TableRow row : rows) {
			String businessKey = row.getCellByKey(HEADER_FILTER).getValue()
					+row.getCellByKey(HEADER_FILTER_FIELD).getValue();
			row.setDbID(businessKey.hashCode());
		}
		return rows;
	}

	/**
	 * Create headers for filters table
	 * @return
	 */
	private Headers createFilterHeaders() {
		Headers headers = new Headers();
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				HEADER_FILTER, messages.get("url"),
				true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders().add(TableHeader.instanceOf(
				HEADER_DESCRIPTION, messages.get(HEADER_DESCRIPTION),
				true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders().add(TableHeader.instanceOf(
				HEADER_FILTER_FIELD, messages.get("global_field"),
				true, true, true, TableHeader.COLUMN_STRING, 0));
		return headers;
	}

	/**
	 * Add counters to the fields/filters table
	 * it is presumed taht the table is existing
	 * @param data
	 * @throws ObjectNotFoundException 
	 */
	public DataSourceDTO counters(DataSourceDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		if(!data.getDataTypes().isLoaded()) {
			data=createDataTypes(data);
		}
		for(TableRow row : data.getDataTypes().getRows()) {
			String key=(String) row.getRow().get(0).getOriginalValue();
			row.getCellByKey(HEADER_FILTERS).setValue(data.getFilters().get(key).size()+"");
			row.getCellByKey(HEADER_FILTERS).setOriginalValue(data.getFilters().get(key).size());
			row.getCellByKey(HEADER_FIELDS).setValue(data.getFields().get(key).size()+"");
			row.getCellByKey(HEADER_FIELDS).setOriginalValue(data.getFields().get(key).size());
		}
		return data;
	}

	/**
	 * Validate and save a data source
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws JsonProcessingException 
	 */
	@Transactional
	public DataSourceDTO save(DataSourceDTO data) throws ObjectNotFoundException, JsonProcessingException {
		data=validate(data);
		if(data.isValid()) {
			Concept root = closureServ.loadRoot(DATA_SOURCES_ROOT);
			Concept node =closureServ.saveToTree(root, data.getUrl().getValue());
			node = closureServ.save(node);
			data.setDataSourceID(node.getID());
			String dataSourceAsJson=objectMapper.writeValueAsString(data);
			node.setLabel(dataSourceAsJson);
		}
		return data;
	}


	/**
	 * Validate the data source
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DataSourceDTO validate(DataSourceDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data=validateURL(data);
		if(data.isValid()) {
			data=validateFiltersFields(data);
		}
		return data;
	}

	/**
	 * Should be defined at least one filter for "datasource_application" and/or "datasource_workflows"
	 * For uniformity we assume that are first two elements in the dataTypes
	 * @param data
	 * @return
	 */
	private DataSourceDTO validateFiltersFields(DataSourceDTO data) {
		int applFilters=data.getFilters().get("pv_applications").size();
		int applFields=data.getFields().get("pv_applications").size();
		if((applFilters+applFields)==0) {
			data.addError(messages.get("applicationsFiltersAreMandatory"));
		}
		return data;
	}



	/**
	 * URl for data source is mandatory, proper and unique
	 * @param data
	 * @return
	 */
	private DataSourceDTO validateURL(DataSourceDTO data) {
		String url=data.getUrl().getValue();
		if(urlValidator.isValidURL(url)) {
			data=isDataSourceUrlUnique(data);
		}else {
			data.addError(messages.get("error_url"));
		}
		return data;
	}


	/**
	 * Data source URL should be unique
	 * @param data
	 * @return
	 */
	private DataSourceDTO isDataSourceUrlUnique(DataSourceDTO data) {
		String select="SELECT ID\r\n"
				+ "FROM pdx2.data_sources";
		String where="ds_url='"+data.getUrl().getValue()+"'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, new Headers());
		if(rows.size()>0) {
			if(rows.size()!=1 || rows.get(0).getDbID()!=data.getDataSourceID()) {
				data.addError(messages.get("url_exists"));
			}
		}
		return data;
	}

	/**
	 * Create an SQL statement using data given
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DataSourceDTO sqlCreate(DataSourceDTO data) throws ObjectNotFoundException {
		String from="";
		String where="";
		List<String> select = new ArrayList<String>();
		select.add("`pv_applications`.`Lang`");
		List<String> joins = new ArrayList<String>();
		if(data.getFilters().get("pv_applications").size()>0) {
			//Collect SQL parameters
			from="FROM `pv_applications` pv_applications";
			where="WHERE "+ filters("pv_applications",data);
			select=fields("pv_applications", data,select);
			joins=joins("pv_applications",data, joins);
			//select from fields in joins
			select=fields("pv_activities", data,select);
			select=fields("pv_addresses", data,select);
			select=fields("pv_classifiers", data,select);
			select=fields("pv_links", data,select);
			select=fields("pv_events", data,select);
			select=fields("pv_literals", data,select);
			// Create SQL statement using the parameters
			if(data.isValid()) {
				String sql= "SELECT DISTINCT"+LINE_FEED + String.join(","+LINE_FEED, select) + LINE_FEED+ LINE_FEED;
				sql+=from+LINE_FEED + LINE_FEED;
				//TODO joins
				sql+=String.join(LINE_FEED,joins);
				sql+=LINE_FEED;
				sql+=where;
				data.setSql(sql);
			}else {
				data.setSql(data.getIdentifier());
			}
		}else {
			data.addError(messages.get("applicationsFiltersAreMandatory"));
		}
		return data;
	}

	/**
	 * Create SQL joins as a list of strings
	 * Join requires both filters and fields
	 * @param mainView
	 * @param data
	 * @param joins
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private List<String> joins(String mainView, DataSourceDTO data, List<String> joins) throws ObjectNotFoundException {
		for(String view : views) {
			if(!view.equalsIgnoreCase(mainView)) {
				List<Long> filterIDs =data.getFilters().get(view);
				List<Long> fieldIDs = data.getFields().get(view);
				if(!filterIDs.isEmpty() && !fieldIDs.isEmpty()) {
					joins.add(join(mainView, view, filterIDs, data));
				}else {
					if(!filterIDs.isEmpty() || !fieldIDs.isEmpty()) {
						data.addError(messages.get("fltersAndFieldsShouldBeDefined")+" - "+ view);
					}
				}
			}
		}
		return joins;
	}


	/**
	 * Create a view join for SQL
	 * @param mainView view in FROM
	 * @param view 
	 * @param viewIDs
	 * @param data 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String join(String mainView, String view, List<Long> viewIDs, DataSourceDTO data) throws ObjectNotFoundException {
		String ret= "";
		String joinLinkID=APPLICATION_ID;
		if(view.equalsIgnoreCase("pv_classifiers")) {
			joinLinkID="JoinID";
		}
		ret = "join "+ view +"`"+view+"`"+LINE_FEED +
				"ON `"+view+"`.`"+joinLinkID+"`=`"+mainView+"`.`"+APPLICATION_ID+"`" + LINE_FEED
				+"AND "+filters(view,data)
				+"AND `"+mainView+"`.`Lang`=`"+view+"`.`Lang`" + LINE_FEED; 
		return ret;
	}



	/**
	 * Create list of fields for selection for a view given 
	 * @param viewName
	 * @param data
	 * @param select - previous select
	 * @return
	 */
	private List<String> fields(String viewName, DataSourceDTO data, List<String> select) {
		List<TableRow> rows = fieldsInView(viewName);
		for(TableRow row : rows) {
			if(data.getFields().get(viewName).contains(row.getDbID())) {
				select.add("`"+viewName+"`.`"+row.getCellByKey(HEADER_FIELD).getValue()+"`");
			}
		}
		return select;
	}



	/**
	 * Create filters for where or join
	 * @param viewName - name of the pv_* view to get filters
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String filters(String viewName, DataSourceDTO data) throws ObjectNotFoundException {
		String ret="";
		Headers headers = createFilterHeaders();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<TableRow> rows = new ArrayList<TableRow>();
		String filterSql=filterSqls.get(viewName);
		if(filterSql != null) {
			rows=filterRows(filterSql,createFilterHeaders());
			for(TableRow row : rows) {
				if(data.getFilters().get(viewName).contains(row.getDbID())) {
					String field = (String) row.getOriginalCellValue(HEADER_FILTER_FIELD, headers);
					String filter=(String) row.getOriginalCellValue(HEADER_FILTER, headers);
					List<String> values = map.get(field);
					if(values == null) {
						values = new ArrayList<String>();
						map.put(field, values);
					}
					values.add("'"+filter+"'");
				}
			}
			List<String> criterias = new ArrayList<String>();
			for(String key : map.keySet()) {
				criterias.add("`"+viewName+"`.`"+key+ "` IN ("+String.join(",", map.get(key))+")"+LINE_FEED);
			}
			ret=String.join("AND ", criterias);
		}
		return ret;
	}

	/**
	 * Build and test data source SQL
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DataSourceDTO sqlTest(DataSourceDTO data) throws ObjectNotFoundException {
		data=sqlCreate(data);
		if(data.isValid()) {
			if(!data.getTestSql().isLoaded()) {
				String headersSelect = data.getSql() + " LIMIT 1";
				List<TableHeader> headers = jdbcRepo.headersFromSelect(headersSelect, new ArrayList<String>());
				data.getTestSql().getHeaders().getHeaders().addAll(headers);
				data.getTestSql().setSelectable(false);
				data.getTestSql().getHeaders().setPageSize(100);
			}
			String rowsSelect = data.getSql();
			List<TableRow> rows = jdbcRepo.qtbGroupReport(rowsSelect, "", "", data.getTestSql().getHeaders());
			TableQtb.tablePage(rows, data.getTestSql());
		}
		return data;
	}

}
