package org.msh.pdex2.repository.common;

import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.dto.table.HasRow;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.dwh.ReportSession;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.dwh.ReportSessionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedCaseInsensitiveMap;


/**
 * Sometimes DTO should be created directly from result of JDBC query,
 * because traditional JPA repository became complex and/or inefficient
 * @author Alex Kurasoff
 *
 */
@Repository
@Transactional
public class JdbcRepository {
	private static final Logger logger = LoggerFactory.getLogger(JdbcRepository.class);
	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * For manual initialization
	 * @param schemaDb
	 * @param jdbcTemplate
	 * @param messages
	 * @return 
	 * @throws SQLException 
	 */
	public static JdbcRepository instanceOf(String schemaDb, JdbcTemplate jdbcTemplate,
			String userName, String password) throws SQLException{
		JdbcRepository ret = new JdbcRepository();
		if(jdbcTemplate.getDataSource().getConnection().getMetaData().getURL().toUpperCase().contains(schemaDb.toUpperCase())) {
			ret.setJdbcTemplate(jdbcTemplate);
		}else {
			//TODO create a new JDBC template with a new simple data source
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
			dataSource.setUrl("jdbc:mysql://localhost/"+schemaDb+"?useSSL=false");
			dataSource.setUsername(userName);
			dataSource.setPassword(password);
			JdbcTemplate template = new JdbcTemplate();
			template.setDataSource(dataSource);
			ret.setJdbcTemplate(template);
		}
		return ret;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Select rows with GroupBy phrase
	 * @param select - select, from, joins phrases
	 * @param groupByHaving groupBy and having phrases
	 * @param mainWhere expression that must been in where
	 * @param headers for additional where filters and 
	 * @return
	 */
	@Transactional
	public List<TableRow> qtbGroupReport(String select, String groupByHaving, String mainWhere, Headers headers){
		String sql=createFullSelect(select, groupByHaving, mainWhere, headers);
		return selectQuery(sql, headers);
	}

	/**
	 * create SELECT for future use
	 * @param select main select expression, typically loaded from "query" table of the database. Should not be empty or null
	 * @param groupByHaving aux group by and having phrase.
	 * @param mainWhere should be added to "where" phrase unconditionally, e.g. "id="+ myObj.getId()
	 * @param headers table headers that also may contain search and sort conditions
	 * @return string with SQL operator
	 */
	public String createFullSelect(String select, String groupByHaving, String mainWhere, Headers headers){
		return createQtbSQL(select, mainWhere, headers.createGeneralWhere(), headers.createWhere(), groupByHaving, headers.createOrderBy());
	}

	/**
	 * Issue an SQL SELECT operator and then put result to TableRows for future use
	 * @param select
	 * @param headers
	 * @return
	 */
	@Transactional
	public List<TableRow> selectQuery(String select, Headers headers){
		List<TableRow> ret = new ArrayList<TableRow>();
		ret = jdbcTemplate.query(select, new QtbRowMapper(headers));
		return ret;
	}


	/**
	 * Create SQL operator for qtb query - select, filters, order by
	 * @param select
	 * @param mainWhere 
	 * @param generalWhere general search field uses "or" condition
	 * @param where column's filters use "and" condition
	 * @param groupByHaving 
	 * @param orderBy
	 * @return
	 */
	private String createQtbSQL(String select, String mainWhere, List<String> generalWhere, List<String> where, String groupByHaving, List<String> orderBy) {
		String sql = select;
		String generalWherePhrase="";
		String wherePhrase = "";
		for(String s: generalWhere){
			if(generalWherePhrase.length()==0){
				generalWherePhrase="("+s+")";
			}else{
				generalWherePhrase = generalWherePhrase +" or (" + s + ")";
			}
		}
		for(String s : where){
			if(wherePhrase.length()==0){
				wherePhrase="("+s+")";
			}else{
				wherePhrase = wherePhrase +" and (" + s + ")";
			}
		}
		String orderByPhrase = "";
		for(String s : orderBy){
			if(orderByPhrase.length() == 0){
				orderByPhrase=s;
			}else{
				orderByPhrase = orderByPhrase + ", " + s;
			}
		}

		String commonWhere="";
		if(generalWherePhrase.length()>0){
			commonWhere = "(" + generalWherePhrase + ")";
		}
		if(wherePhrase.length()>0){
			if(commonWhere.length()>0){
				commonWhere = commonWhere + " and ("+wherePhrase +")";
			}else{
				commonWhere= wherePhrase;
			}
		}
		if(mainWhere.length()>0){
			if(commonWhere.length()>0){
				commonWhere = commonWhere + " and ("+mainWhere+")";
			}else{
				commonWhere = mainWhere;
			}
		}
		if(commonWhere.length()>0){
			sql = sql + " WHERE " + commonWhere;
		}

		if(groupByHaving.length()>0){
			sql = sql + " " + groupByHaving;
		}

		if(orderByPhrase.length()>0){
			sql = sql + " ORDER BY " + orderByPhrase;
		}
		return sql;
	}

	/**
	 * Drop and create table
	 * @param table
	 * @param createSQL
	 * @throws SQLException 
	 * @returns list of column names of just created table
	 */
	public List<TableHeader> dropAndCreateTable(String table, String createSQL) throws SQLException {
		List<TableHeader> ret = new ArrayList<>();
		dropTable(table);
		jdbcTemplate.execute(createSQL);
		DatabaseMetaData meta = jdbcTemplate.getDataSource().getConnection().getMetaData();
		ResultSet columns = meta.getColumns(null,null, table, null);
		while(columns.next())
		{
			String columnName = columns.getString("COLUMN_NAME");
			int dataType = columns.getInt("DATA_TYPE");
			int columnType=TableHeader.COLUMN_STRING;
			switch (dataType){
			case java.sql.Types.DATE:
				columnType=TableHeader.COLUMN_LOCALDATE;
				break;
			case java.sql.Types.BOOLEAN:
				columnType=TableHeader.COLUMN_BOOLEAN_CHECKBOX;
				break;
			case java.sql.Types.INTEGER:
			case java.sql.Types.BIGINT:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.TINYINT:
				columnType=TableHeader.COLUMN_LONG;
				break;
			default:
				//String
			}	
			TableHeader header = TableHeader.instanceOf(columnName,
					columnName,
					false,
					false,
					false,
					columnType,
					0);
			ret.add(header);
		}
		return ret;
	}

	/**
	 * Insert batch of rows to the table
	 * @param table name of table
	 * @param headers names of columns along with types
	 * @param rows array of TableCell
	 */
	public void insertBatchRows(String table, List<TableHeader> headers, List<HasRow> rows) {
		String insert="insert into "+table;
		String columns = "";
		String data="";
		for(TableHeader header : headers){
			if(columns.length()==0){
				columns=header.getKey();
			}else{
				columns=columns+","+header.getKey();
			}
			if(data.length()==0){
				data="?";
			}else{
				data=data+", ?";
			}
		}
		insert = insert+ "("+columns+") values("+data+")";
		final int INSERT_BATCH_SIZE= 200;
		for (int i = 0; i < rows.size(); i += INSERT_BATCH_SIZE){
			final List<HasRow> batchList = rows.subList(i, i
					+ INSERT_BATCH_SIZE > rows.size() ? rows.size() : i
							+ INSERT_BATCH_SIZE);
			jdbcTemplate.batchUpdate(insert,
					new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					for(int i=0;i<headers.size();i++){
						Object value = batchList.get(index).getManagedRow().getRow().get(i).getOriginalValue();
						if(value!=null){
							switch(headers.get(i).getColumnType()){
							case TableHeader.COLUMN_LOCALDATE:
								if(value instanceof String) {
									logger.error("wrong raw data " + batchList.get(index).getManagedRow().getRow().get(i).getOriginalValue() );
								}
								LocalDate dt = (LocalDate) value;
								ps.setDate(i+1, Date.valueOf(dt));/// new java.sql.Date(dt.toDate().getTime()));
								break;
							case TableHeader.COLUMN_BOOLEAN_CHECKBOX:
								boolean val = (boolean) value;
								ps.setBoolean(i+1, val);
								break;
							case TableHeader.COLUMN_LONG:
								Long lval = (Long) value;
								ps.setLong(i+1, lval);
								break;
							default:
								if(value instanceof String){
									ps.setString(i+1, (String) value);
								}
							}
						}else{
							ps.setNull(i+1,0);
						}
					}
				}
				@Override
				public int getBatchSize() {
					return batchList.size();
				}
			});
		}
	}

	/**
	 * Drop a table
	 * @param table
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void dropTable(String table) {
		jdbcTemplate.execute("drop table if exists "+table);
	}

	/**
	 * Reset AUTO_INCREMENT in forecast result tables
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void resetAutoIncrement() {
		jdbcTemplate.execute("ALTER TABLE `stockforecast` AUTO_INCREMENT = 0");
		jdbcTemplate.execute("ALTER TABLE `dailyforecast` AUTO_INCREMENT = 0");
		jdbcTemplate.execute("ALTER TABLE `regimen` AUTO_INCREMENT = 0");
		jdbcTemplate.execute("ALTER TABLE `phase` AUTO_INCREMENT = 0");
		jdbcTemplate.execute("ALTER TABLE `regimenforecast` AUTO_INCREMENT = 0");
		jdbcTemplate.execute("ALTER TABLE `parameters` AUTO_INCREMENT = 0");
		jdbcTemplate.execute("ALTER TABLE `medication` AUTO_INCREMENT = 0");
		jdbcTemplate.execute("ALTER TABLE `demandforecast` AUTO_INCREMENT = 0");
	}

	/**
	 * Remove forecast and regimen for all quantification
	 * 
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void deleteResultTabs(){
		jdbcTemplate.execute("DELETE FROM `collector`.`stockforecast` ");
		jdbcTemplate.execute("DELETE FROM `collector`.`dailyforecast` ");
		jdbcTemplate.execute("DELETE FROM `collector`.`regimen` ");
		jdbcTemplate.execute("DELETE FROM `collector`.`phase` ");
		jdbcTemplate.execute("DELETE FROM `collector`.`regimenforecast` ");
		jdbcTemplate.execute("DELETE FROM `collector`.`parameters` ");
		jdbcTemplate.execute("DELETE FROM `collector`.`medication` ");
		jdbcTemplate.execute("DELETE FROM `collector`.`demandforecast` ");
		resetAutoIncrement();
	}

	/**
	 * Remove/update data related to a collection
	 * E.g. remove forecast results
	 * @param sql sql operator for update, contains parameter ?
	 * @param iD
	 * @returns number of removed rows
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateColectionData(String sql, long iD){
		return jdbcTemplate.update(sql, iD);
	}
	/**
	 * Remove/update data related to a quantification
	 * E.g. remove forecast results
	 * @param sql
	 * @param id
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateQuantificationData(String sql, long id) {
		return jdbcTemplate.update(sql, id);
	}
	/**
	 * Prepare temporary table _dictlevel with the data of the required  dictionary level 
	 * @param parentID id of the immediate parent
	 */
	public void prepareDictionaryLevel(long parentID) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("dictlevel");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("parent", parentID);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Prepare temporary table  _prefdescription contains pref labels and descriptions
	 */
	@Deprecated
	public void preparePrefDescription() {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("prefdescription");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}

	public void userByOrganization(long orgID) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("users_org");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("orgID", orgID);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}

	/**
	 * Call stored procedure to prepare the file list
	 * @param dictRootId root od the dictionary
	 * @param thingId id of the current thing, 0 for the new thing
	 * @param docUrl url of the current document
	 * @param varName name of variable
	 * @param email user's email
	 */
	public void filelist(long dictRootId, long thingId, String docUrl, String varName, String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("filelist");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("dictrootid", dictRootId);
		params.addValue("thingid", thingId);
		params.addValue("docurl", docUrl);
		params.addValue("varname", varName);
		params.addValue("email", email);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Persons related to the node to temporary table _persons
	 * @param nodeId
	 */
	public void persons(long nodeId) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("persons");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("parent", nodeId);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Load list of activities from the database to table workflow_activities
	 * @param id
	 */
	public void workflowActivities(long id) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("workflow_activities");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("rootid", id);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * All possible executors for activity
	 * @param actConfigId
	 */
	public void executorsActivity(long actConfigId) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("executors_activity");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("actconfigid", actConfigId);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);

	}
	/**
	 * Read register's records by regester's url
	 * @param url
	 */
	public void readRegisterByUrl(String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("site_registers");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		proc.execute(params);
	}
	/**
	 * Get all opened activities by user's email
	 * @param email
	 */
	public void activities(String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("activities");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("appl", null);
		params.addValue("go", false);
		params.addValue("exec", email);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);

	}
	/**
	 * Get all opened monitoring activities by user's email
	 * @param email
	 */
	public void myMonitoring(String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("monitoring");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("exec", email);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}

	/**
	 * Remove concept using stored proc
	 * @param node
	 */
	@Transactional
	public void removeConcept(Concept node) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("remove_branch");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("root", node.getID());
		proc.execute(params);

	}
	/**
	 * Report sites
	 * @param dataUrl, site data url, i.e. pharmacy.site
	 * @param dictStageUrl life cycle stage dictionary - guest, host, shutdown (dictionary.guest.applications, dictionary.host.applications, dictionary.shutdown.applications)
	 * @param addressUrl - under which addresses of the site are stored, i.e. 'pharamcy.site.address'
	 * @param ownerUrl - under which owners of the site are stored, i.e. pharmacy.site.owners
	 * @param inspectAppUrl under which inspection applications are stored, i.e. application.pharmacy.inspection
	 * @param renewAppUrl under which renewal applications are stored, i.e. application.pharmacy.renew
	 */
	public void report_sites(String dataUrl, String dictStageUrl, String addressUrl, String ownerUrl,
			String inspectAppUrl, String renewAppUrl, String certAppUrl) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("report_sites");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("site_url", dataUrl);
		params.addValue("dict_stage_url", dictStageUrl);
		params.addValue("addr_url", addressUrl);
		params.addValue("owner_url", ownerUrl);
		params.addValue("appl_inspection_url", inspectAppUrl);
		params.addValue("appl_renew_url", renewAppUrl);
		params.addValue("appl_cert_url", certAppUrl);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);

	}
	/**
	 * load amendments
	 * @param email user's email, if null - all users all amendments
	 */
	public void amendments(String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("amendments");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Move root of some tree to a new root
	 * @param root
	 * @param newRoot
	 */
	@Transactional
	public void moveSubTree(Concept root, Concept newRoot) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("moveSubTree");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("rootNode", root.getID());
		params.addValue("newParent", newRoot.getID());
		proc.execute(params);
	}
	/**
	 * Get the history of an application data
	 * @param applDataId
	 */
	@Transactional
	public void application_history(long applDataId) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("application_history");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("applDataId", applDataId);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Load list of persons for the application data
	 * @param nodeId
	 */
	@Transactional
	public void persons_application(long applDataId) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("persons_application");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("applDataId", applDataId);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Simple product report
	 * @param dataUrl
	 * @param register_url
	 * @param string2 
	 * @param string 
	 */
	public void productReport(String data_url, String stage_url, String applicant_url, String register_url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("report_products");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("data_url", data_url);
		params.addValue("state_url", stage_url);
		params.addValue("applicant_url", applicant_url);
		params.addValue("register_url", register_url);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}

	/**
	 * depth of the longest branch in the dictionary
	 * Starts with 0
	 * @param url
	 * @return
	 */
	public Integer dict_depth(String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("dict_depth");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		Map<String, Object> retMap = proc.execute(params);
		return (Integer) retMap.get("dict_depth");
	}
	/**
	 * Get next registration number for register's URL
	 * @param url register's URL
	 * @return
	 */
	@Transactional
	public Long register_number(String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("register_number");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		Map<String, Object> retMap = proc.execute(params);
		return (Long) retMap.get("regno");
	}
	/**
	 * Selected ATC codes
	 * @param nodeId
	 */
	public void atc_selected(long nodeId) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("atc_selected");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("nodeid", nodeId);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);

	}
	/**
	 * All configurations for reports
	 */
	public void report_configurations() {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("report_configurations");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Reports available for applicant (any user)
	 */
	public void report_applicant() {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("report_applicant");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Reports available to NMRA user
	 * @param email
	 */
	public void report_user(String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("report_user");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	
	public void report_open_nmra(long nodeid, String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("report_open_nmra");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("nodeid", nodeid);
		params.addValue("email", email);
		proc.execute(params);
	}
	
	/**
	 * Get all values of the literal given
	 * @param rootId
	 * @param varName
	 */
	public void literal_values(Long rootId, String varName) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("literal_values");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("rootid", rootId);
		params.addValue("varname", varName);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}

	/**
	 * List of all inactive hosted applications
	 * @param url application data url
	 * @param email applicant's email
	 * @param stable it means that no applications is running
	 */
	public void applications_hosted_inactive(String url, String email, boolean stable) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("applications_hosted_inactive");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("dataurl", url);
		params.addValue("email", email);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);	
	}
	/**
	 * Select variables in modiUrl not covered by dataUrl 
	 * @param modiUrl
	 * @param dataUrl
	 */
	public void modification_check(String modiUrl, String dataUrl) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("modification_check");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("dataurl", dataUrl);
		params.addValue("modiUrl", modiUrl);
		proc.execute(params);
	}
	/**
	 * Select registers by URL
	 * @param regurl
	 * if regurl is null - select all
	 */
	public void report_register(String regurl) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("report_register");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("regurl", regurl);
		proc.execute(params);

	}
	/**
	 * List of schedulers to the temporary table
	 * @param dataUrl
	 * @param id
	 */
	public void scheduler_list(String url, long appldataid) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("scheduler_list");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		params.addValue("appldataid", appldataid);
		proc.execute(params);
	}
	/**
	 * List of activities in application defined by URL and application data id
	 * @param url
	 * @param appldataid
	 */
	public void application_list(String url, long appldataid) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("application_list");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		params.addValue("appldataid", appldataid);
		proc.execute(params);

	}
	/**
	 * Get all data units in the application data given 
	 * @param id
	 */
	public void data_units(long appldataid) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("data_units");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("appldataid", appldataid);
		proc.execute(params);
	}
	/**
	 * Application events for an application report
	 * @param appldataid
	 */
	public void application_events(long appldataid) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("application_events");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("appldataid", appldataid);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}

	/**
	 * Plain list of admin units up to level 2 (usually districts)
	 */
	public void admin_units2() {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("admin_units2");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);

	}
	
	/**
	 * get  office by adminunitID
	 */
	public List<TableRow> service_office(long admUnitID) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("service_office");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("admUnitId", admUnitID);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
		TableQtb table = new TableQtb();
		table.getHeaders().getHeaders().add(TableHeader.instanceOf("nodeid", TableHeader.COLUMN_LONG));
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("ID", TableHeader.COLUMN_LONG));
		headers.getHeaders().add(TableHeader.instanceOf("orgname", TableHeader.COLUMN_STRING));
		List<TableRow> selectQuery = selectQuery("select * from service_office", headers);
		
		return selectQuery;
	}
	
	/**
	 * Select executors by the criteria given
	 * @param id
	 * @param id2
	 * @param admUnitID
	 */
	public void executors_select(long roleID, long applDictID) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("executors_select");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("roleID", roleID);
		params.addValue("applDictID", applDictID);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Get legacy data by url
	 * @param url
	 */
	public void legacy_data(String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("legacy_data");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);

	}
	/**
	 * Load all known open activities to the temporary table in_activities for the future querying 
	 */
	public void in_activities() {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("in_activities");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Get assembly variables be configuration URL
	 * @param url
	 */
	public void assembly_variables(String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("assembly_variables");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		proc.execute(params);
	}

	/**
	 * Get de-registered sites
	 * @param addressUrl
	 * @param registerAppUrl
	 */
	public void report_deregister(String addressUrl, String registerUrl) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("report_deregister");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("registerurl", registerUrl);
		params.addValue("addrurl", addressUrl);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Get ID of the initial application by id of any related data of it
	 * @param applicationID data id of main application or null if unknown
	 * @param applicationDataID data id of any data related to the main application, or null if we want all of them 
	 * @return null if not found
	 */
	@Transactional
	public Long application_data(Long applicationID, Long  applicationDataID) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("application_data");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("nodeid", applicationID);
		params.addValue("dataid", applicationDataID);
		proc.execute(params);
		TableQtb table = new TableQtb();
		table.getHeaders().getHeaders().add(TableHeader.instanceOf("nodeid", TableHeader.COLUMN_LONG));
		List<TableRow> rows= qtbGroupReport("select * from application_data", "", "", table.getHeaders());
		if(rows.size()>0) {
			Long retl = (Long)rows.get(0).getRow().get(0).getOriginalValue();
			return retl;
		}else {
			return null;
		}
	}
	/**
	 * Get a register by register URL for application with initial node nodeId
	 * @param nodeId
	 * @param url
	 * @return
	 */
	public Long registerApplication(long nodeId, String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("registers_application");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("nodeid", nodeId);
		proc.execute(params);
		TableQtb table = new TableQtb();
		String where = "url='"+url+"'";
		table.getHeaders().getHeaders().add(TableHeader.instanceOf("regid", TableHeader.COLUMN_LONG));
		List<TableRow> rows= qtbGroupReport("select * from registers_application", "", where, table.getHeaders());
		if(rows.size()>0) {
			Long retl = (Long)rows.get(0).getRow().get(0).getOriginalValue();
			return retl;
		}else {
			return null;
		}
	}
	/**
	 * Create and execute a single insert
	 * @param tableName
	 * @param row
	 */
	@Transactional
	public boolean insert(String tableName, TableRow row) {
		if(row.getDbID()==0) {
			String sql = "INSERT INTO `"+tableName+"` " + col_Names(row) + " VALUES " + col_Values(row);
			return getJdbcTemplate().update(sql)==1;
		}else {
			return update(tableName, row);
		}
	}

	/**
	 * Values of columns
	 * @param headers
	 * @param row
	 * @return empty string if no values
	 */
	private String col_Values(TableRow row) {
		String ret="";
		List<String> cols = new ArrayList<String>();
		for(TableCell cell : row.getRow()) {
			cols.add(cell.getDbInsertValue());
		}
		if(cols.size()>0) {
			ret="("+String.join(",", cols) + ")";
		}
		return ret;
	}

	/**
	 * String with colun names
	 * @param headers
	 * @param row
	 * @return string like (colname1, colname2..) or empty string
	 */
	private String col_Names(TableRow row) {
		String ret="";
		List<String> cols = new ArrayList<String>();
		for(TableCell cell : row.getRow()) {
			cols.add("`"+cell.getKey()+"`");
		}
		if(cols.size()>0) {
			ret="("+String.join(",", cols) + ")";
		}
		return ret;
	}
	/**
	 * Execute SQL update on the table, using row
	 * @param string
	 * @param tableRow
	 */
	public boolean update(String tableName, TableRow row) {
		if(row.getDbID()>0) {
			String sql = "UPDATE `"+tableName+"` SET " +  col_NamesValues(row) + " where ID="+row.getDbID();
			return getJdbcTemplate().update(sql)==1;
		}else {
			return insert(tableName, row);
		}
	}
	/**
	 * Col names and values for SQL UPDATE
	 * @param row
	 * @return
	 */
	private String col_NamesValues(TableRow row) {
		String ret="";
		List<String> cols = new ArrayList<String>();
		for(TableCell cell : row.getRow()) {
			String s = "`"+cell.getKey()+"`=" + cell.getDbInsertValue();
			cols.add(s);
		}
		if(cols.size()>0) {
			ret=String.join(",", cols);
		}
		return ret;
	}
	/**
	 * Run insert/update SQL
	 * @param sql
	 * @return
	 */
	public int update(String sql) {
		return getJdbcTemplate().update(sql);
	}
	/**
	 * Update all DWH tables
	 * @param newSessionID
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void dwh_update(long newSessionID) throws ObjectNotFoundException {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("dwh_update");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("sessionId", newSessionID);
		try {
			proc.execute(params);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ObjectNotFoundException(e, logger); 
		}

	}
	/**
	 * Read resource to upload
	 * @param dictRootId - the dictionary root the resource built upon
	 */
	public void resource_read(long dictRootId) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("resource_read");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("dictRootId", dictRootId);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);

	}
	/**
	 * Get list of headers based on the resultset columns
	 * @param select - select statement, typically where=false
	 * @param except - which columns should be excluded - case sensitive!
	 * @return
	 */
	public List<TableHeader> headersFromSelect(String select, List<String> except) {
		List<TableHeader> ret = new ArrayList<TableHeader>();
		final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(select);
		for(int i=0;i<rs.getMetaData().getColumnCount();i++) {
			String key=rs.getMetaData().getColumnNames()[i];
			if(!except.contains(key) && !key.equalsIgnoreCase("ID")) {
				String cType=rs.getMetaData().getColumnTypeName(i+1);
				int columnType=TableHeader.COLUMN_STRING;
				switch(cType) {
				case "BIGINT":
				case "INT":
					columnType=TableHeader.COLUMN_LONG;
					break;
				case "DATE":
					columnType=TableHeader.COLUMN_LOCALDATE;
					break;
				case "DATETIME":
					columnType=TableHeader.COLUMN_LOCALDATETIME;
					break;
				case "DECIMAL":
					columnType=TableHeader.COLUMN_DECIMAL;
					break;
				case "TINYINT":
				case "BIT":
					columnType=TableHeader.COLUMN_BOOLEAN_CHECKBOX;
					break;
				}
				ret.add(TableHeader.instanceOf(
						key,
						key,
						true,
						true,
						true,
						columnType,
						0));
			}
		}
		return ret;
	}

	public void data_config_vars(String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("data_config_vars");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);

	}

	public void dict_level_ext(long nodeID) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("dict_level_ext");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("nodeID", nodeID);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Query two column table, and, then, pivot it
	 * @param select - select phrase
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public TableQtb queryAndPivot(String select) throws ObjectNotFoundException {
		List<TableHeader> headers= headersFromSelect(select + " LIMIT 1", new ArrayList<String>());
		if(headers.size()==2) {
			List<TableRow> rows = qtbGroupReport(select, "", "", Headers.of(headers));
			if(rows.size()>0) {
				long id=0;
				List<TableHeader> pHeaders= new ArrayList<TableHeader>();
				TableRow pRow = TableRow.instanceOf(id);
				List<TableRow> pRows= new ArrayList<TableRow>();
				//determine headers from the first column
				Set<String> keys = new LinkedHashSet<String>();
				for(TableRow row: rows) {
					keys.add(row.getRow().get(0).getValue());
				}
				// in the two column table the type of headers is a type of the second column
				for(String key : keys) {
					pHeaders.add(TableHeader.instanceOf(key, headers.get(1).getColumnType()));
				}
				//pivot rows
				for(TableRow row: rows) {
					if(row.getDbID()!=id) {
						//create a new row with all columns
						
						id=row.getDbID();
						pRow = TableRow.instanceOf(id);
						pRows.add(pRow);
						for(TableHeader ph : pHeaders) {
							pRow.getRow().add(TableCell.instanceOf(ph.getKey()));
						}
					}
					//fill-out cells in the row
					String key = row.getRow().get(0).getValue();
					TableHeader h = TableHeader.instanceOf(key, headers.get(1).getColumnType());
					int index= pHeaders.indexOf(h);
					if(index>-1) {
						pRow.getRow().get(index).setOriginalValue(row.getRow().get(1).getOriginalValue());
						pRow.getRow().get(index).setValue(row.getRow().get(1).getValue());
					}
				}
				//success
				TableQtb ret = new TableQtb();
				ret.getHeaders().setPageSize(Integer.MAX_VALUE);
				ret.getHeaders().getHeaders().addAll(pHeaders);
				ret.getRows().addAll(pRows);
				return ret;
			}else {
				//pivoting is impossible
				return new TableQtb();
			}
		}else {
			throw new ObjectNotFoundException("The source table should contain two columns for pivoting. Select is "+select,logger);
		}
	}
	/**
	 * select files uploaded by user that do not link to any thing
	* it is possible when a user upload files, however did not save a thing
	* known usage ThingService.removeOrphans
	 * @param url
	 * @param email
	 */
	public void orphan_files(String fileUrl, String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("orphan_files");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fileUrl", fileUrl);
		params.addValue("email", email);
		proc.execute(params);

	}
	/**
	 * Links for a node and varName on it
	 * @param nodeID
	 * @param varName
	 */
	public void links(long nodeID, String varName) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("links");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("nodeID", nodeID);
		params.addValue("varName", varName);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Get objects from reportpage table in form of a dictionary
	 * @param objectUrl
	 * @param state - ACTIVE
	 							OTHER
	 							ATC
	 							LEGACY
								NOTSUBMITTED
								ONAPPROVAL
								DEREGISTERED
								LOST
	 The parameters objectUrl and state allows comma separated lists
	 * @param owner - email of an owner or null
	 */
	public void reporting_objects(String objectUrl, String state, String owner) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("reporting_objects");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", objectUrl);
		params.addValue("state", state);
		params.addValue("email", owner);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
		
	}
	public void atc_codes(String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("atc_codes");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Get all references for a data URL given
	 * A data URL is a URL from the data configurations
	 * Known usage - data configurator, to provide strict rules to edit a data structure
	 * @param url
	 */
	public void data_url_references(String url) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("data_url_references");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("url", url);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
/**
 * Known usage - Monitoring all feature for Supervisor, Moderator, Applicant, Secretary
 * @param 0 - lang
 * @param 1, 2, 3 - APPLICANT (mail applicant, null, null)
 * MODERATOR COUNTRY (null, mail moderator, null)
 * MODERATOR TERRITORY (null, mail moderator, mail moderator)
 * SUPERVISOR||SECRETARY COUNTRY (null, null, null)
 * SUPERVISOR||SECRETARY TERRITORY (null, null, mail supervisor||secretary)
 */
	public void monitoring_all(String app, String mod, String loc) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("monitoring_all");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		params.addValue("ownerEmail", app);
		params.addValue("moderEmail", mod);
		params.addValue("localEmail", loc);
		proc.execute(params);
	}

	/**
	 * Simple product report
	 * @param dataUrl
	 * @param register_url
	 * @param string2 
	 * @param string 
	 */
	public List<LinkedCaseInsensitiveMap<Object>> reportpagesQraphql(String lang, String url, String district, String pharmtype) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("reportpages_graphql");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lang", lang);
		params.addValue("url", url);
		params.addValue("district", district);
		params.addValue("pharmtype", pharmtype);
		Map<String, Object> map = proc.execute(params);
		Object result = null;
		String key = "";
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String k = it.next();
			if(k.contains("result")) {
				key = k;
				break;
			}
		}
		if(!key.isEmpty()) {
			result = map.get(key);
		}
		List<Object> resList = (ArrayList<Object>)result;
		List<LinkedCaseInsensitiveMap<Object>> list = new ArrayList<LinkedCaseInsensitiveMap<Object>>();
		for(Object obj:resList) {
			list.add((LinkedCaseInsensitiveMap<Object>)obj);
		}
		return list;
	}
	/**
	 * Users of active registered companies
	 */
	public void company_users() {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("company_users");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
	}
	/**
	 * Applications list for applicant
	 * @param dataUrl
	 * @param email 
	 */
	public void applications_applicant(String dataUrl, String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("applications_applicant");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
		params.addValue("url", dataUrl);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
		
	}

	/**
	 * Amendments or De-registration list for applicant
	 * @param dataUrl
	 * @param email 
	 */
	public void amendments_applicant(String dataUrl, String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("amendments_applicant");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
		params.addValue("url", dataUrl);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
		
	}
	/**
	 * Host processes schedule
	 * @param dictURL
	 * @param email
	 */
	public void host_schedule(String dictURL, String email) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("host_schedule");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
		params.addValue("dictURL", dictURL);
		params.addValue("lang", LocaleContextHolder.getLocale().toString().toUpperCase());
		proc.execute(params);
		
	}
	/**
	Has applData open activities directly or indirectly
	First usage is ApplicationService.singletonCondition
	@usage
	call has_activities('dictionary.host.applications','epharmadex@gmail.com',20005);
	or
	call has_activities('dictionary.host.applications',null,null);
	or
	call has_activities(null,null,null);
	select * from has_activities;
	**/
	public void has_activities(String dictURL, String email, Long applDataID) {
		SimpleJdbcCall proc = new SimpleJdbcCall(jdbcTemplate);
		proc.withProcedureName("has_activities");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
		params.addValue("dictURL", dictURL);
		params.addValue("applDataID",applDataID);
		proc.execute(params);
	}

}
