package org.msh.pdex2.dto.table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A column header for a table
 * @author Alex Kurasoff
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TableHeader {
	/**
	 * java.lang.String
	 */
	public static final int COLUMN_STRING=0;
	/**
	 * not editable boolean,i.e. yes/no, 0/1 +/- etc
	 */
	public static final int COLUMN_YESNO=1;
	/**
	 * editable boolean - checkbox
	 */
	public static final int COLUMN_BOOLEAN_CHECKBOX = 2;
	/**
	 * Editable boolean radio button
	 */
	public static final int COLUMN_BOOLEAN_RADIO = 3;
	/**
	 * java.time.LocalDate
	 */
	public static final int COLUMN_LOCALDATE=4;
	/**
	 * link, for a cell - key is url, value is label 
	 */
	public static final int COLUMN_LINK=5;

	/**
	 * Date and time
	 */
	public static final int COLUMN_LOCALDATETIME=6;

	/**
	 * Long number
	 */
	public static final int COLUMN_LONG=7;
	
	/**
	 * Decimal numbers
	 */
	public static final int COLUMN_DECIMAL=8;
	
	/**
	 * Column with localized original value
	 */
	public static final int COLUMN_I18=9;
	
	/**
	 * Column with localized original value and contains link
	 */
	public static final int COLUMN_I18LINK=10;
	
	/**
	 * Column true/false
	 */
	public static final int COLUMN_TRUE_FALSE=11;

	/**
	 * Sort is off
	 */
	public static final int SORT_OFF=0;
	/**
	 * Sort it ascending
	 */
	public static final int SORT_ASC=1;
	/**
	 * Sort it descending
	 */
	public static final int SORT_DESC=2;

	private boolean important=true;
	private String key=""; 
	private String valueKey="";  //key for displayValue in messages
	private String displayValue="";
	private boolean sort=false;
	private boolean filterAllowed=false;
	private boolean filterActive=false;
	private int columnType=COLUMN_STRING;
	private int excelWidth=0; // 0 means default

	//Sort
	private int sortValue=SORT_OFF;

	//Filter's
	private boolean conditionB=false;
	private String conditionS=""; //condition for this column only
	private String generalCondition=""; //general condition only for text columns (field Search)
	//parse("1970-01-01", fmt);//private DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
	private LocalDate from=LocalDate.of(1970,1,1);
	private LocalDate to = LocalDate.of(3970,1,1);
	private LocalDateTime fromDt=LocalDateTime.of(1970,1,1,0,0,0);
	private LocalDateTime toDt = LocalDateTime.of(3970,1,1,0,0,0);
	private long fromLong=Long.MIN_VALUE/2;
	private long toLong=Long.MAX_VALUE/2;

	public boolean isImportant() {
		return important;
	}
	public void setImportant(boolean important) {
		this.important = important;
	}
	public String getKey() {
		return key;
	}
	/**
	 * Get a key enclosed in quotas for select statements
	 * @return
	 */
	public String getQuotedKey() {
		return "`"+key+"`";
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	
	
	public String getValueKey() {
		return valueKey;
	}
	public void setValueKey(String valueKey) {
		this.valueKey = valueKey;
	}
	public String getDisplayValue() {
		return displayValue;
	}
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
	public boolean isSort() {
		return sort;
	}
	public void setSort(boolean sort) {
		this.sort = sort;
	}
	public boolean isFilterAllowed() {
		return filterAllowed;
	}
	public void setFilterAllowed(boolean filter) {
		this.filterAllowed = filter;
	}

	public boolean isFilterActive() {
		return filterActive;
	}
	public void setFilterActive(boolean filterActive) {
		this.filterActive = filterActive;
	}
	public int getColumnType() {
		return columnType;
	}
	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}
	public int getExcelWidth() {
		return excelWidth;
	}
	public void setExcelWidth(int excelWidth) {
		this.excelWidth = excelWidth;
	}
	public int getSortValue() {
		return sortValue;
	}
	public void setSortValue(int sortValue) {
		this.sortValue = sortValue;
	}
	public boolean isConditionB() {
		return conditionB;
	}
	public void setConditionB(boolean conditionB) {
		this.conditionB = conditionB;
	}
	public String getConditionS() {
		if(conditionS==null) {
			conditionS="";
		}
		return conditionS;
	}
	public void setConditionS(String conditionS) {
		this.conditionS = conditionS;
	}

	public String getGeneralCondition() {
		if(generalCondition==null) {
			generalCondition="";
		}
		return generalCondition;
	}
	public void setGeneralCondition(String generalCondition) {
		this.generalCondition = generalCondition;
	}
	public LocalDate getFrom() {
		return from;
	}
	public void setFrom(LocalDate from) {
		this.from = from;
	}
	public LocalDate getTo() {
		return to;
	}
	public void setTo(LocalDate to) {
		this.to = to;
	}


	public LocalDateTime getFromDt() {
		return fromDt;
	}
	public void setFromDt(LocalDateTime fromDt) {
		this.fromDt = fromDt;
	}
	public LocalDateTime getToDt() {
		return toDt;
	}
	public void setToDt(LocalDateTime toDt) {
		this.toDt = toDt;
	}

	public long getFromLong() {
		return fromLong;
	}
	public void setFromLong(long fromLong) {
		this.fromLong = fromLong;
	}
	public long getToLong() {
		return toLong;
	}
	public void setToLong(long toLong) {
		this.toLong = toLong;
	}
	/**
	 * Private constructor to avoid create this object directly
	 */
	private TableHeader(){
		super();
		DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
	}
	/**
	 * 
	 * @param key name of column in sql query result, for all types, except COLUMN_LINK
	 * @param keyValue key for a text of a header in i18N
	 * @param important show this column on small screens
	 * @param sort add sort capability
	 * @param filter add filter capability
	 * @param columType COLUMN_... const
	 * @param excelWidth whide this column in Excel view. Any impact to ftl view 
	 * @return
	 */
	public static TableHeader instanceOf(
			String key,
			String valueKey,
			boolean important,
			boolean sort,
			boolean filter,
			int columnType,
			int excelWidth) {
		TableHeader ret = new TableHeader();
		ret.key = key;
		ret.valueKey = valueKey;
		ret.displayValue=valueKey;
		ret.important = important;
		ret.sort = sort;
		ret.filterAllowed = filter;
		ret.columnType = columnType;
		ret.excelWidth = excelWidth;

		return ret;
	}

	/**
	 * Short form for database operations
	 * @param key name of column in SQL select
	 * @param columnType one of TableHeader.COLUMN_*
	 * @return
	 */
	public static TableHeader instanceOf(String key,int columnType){
		return TableHeader.instanceOf(
				key,
				"",
				false,
				false,
				false,
				columnType,
				0);
	}
	/**
	 * Short form for non-database oerations - free hand created tables
	 * @param key
	 * @param header
	 * @param width in chars
	 * @param columnString
	 * @return
	 */
	public static TableHeader instanceOf(String key, String header, int width,  int columnType) {
		return TableHeader.instanceOf(
				key,
				header,
				true,
				false,
				false,
				columnType,
				width);
	}
	/**
	 * Create SQL where phrase's expression for this header
	 * @return
	 */
	public String createWhere() {
		String ret = "";
		if(isFilterAllowed() && isFilterActive()){
			switch(getColumnType()){
			case COLUMN_STRING:
				if(isFilterAllowed() && getConditionS().length()>0){
					ret = getQuotedKey()+" like '%" + getConditionS() + "%'";
				}
				break;
			case COLUMN_LONG:
				ret= "("+getQuotedKey() + ">=" + getFromLong() + " and " + getQuotedKey() + "<=" + getToLong() + ")";
				break;
			case COLUMN_YESNO:
			case COLUMN_BOOLEAN_CHECKBOX:
			case COLUMN_BOOLEAN_RADIO:
				if(isConditionB()){
					ret=getQuotedKey()+ " IS TRUE";
				}else{
					ret = getQuotedKey() + " IS NOT TRUE";
				}
				break;
			case COLUMN_LOCALDATE:
				/* в дате ДО установим последний день месяца*/
				setTo(LocalDate.of(getTo().getYear(), getTo().getMonthValue(), getTo().lengthOfMonth()));
				
				DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
				ret = getQuotedKey()+ " BETWEEN '" +  getFrom().format(fmt) + "' AND '" + getTo().format(fmt)+"'";
				break;
			case COLUMN_LOCALDATETIME:  //Please, put your attention!!!!! dates without times
				//DateTimeFormatter fmt1 = ISODateTimeFormat.dateHourMinuteSecond();
				DateTimeFormatter fmt1=DateTimeFormatter.ISO_LOCAL_DATE_TIME;
				
				/* на клиенте выбор даты идет через поля  From To
				поэтому копируем значения в даты с временем
				Так же граничные даты устанавливаем след.образом
				01.08  начнаеться 31.07 23:50, а 31.08 заканчивается 01.09 в 00:10
				*/
				LocalDate dd = getFrom();
				dd = dd.minusDays(1);
				setFromDt(LocalDateTime.of(dd.getYear(), dd.getMonthValue(), dd.getDayOfMonth(),23,50,0));
				
				dd = getTo();
				dd = dd.plusMonths(1);// надо взять первое число следующего месяца
				setToDt(LocalDateTime.of(dd.getYear(), dd.getMonthValue(), 1,0,10,0));
				
				ret = getQuotedKey()+ " BETWEEN '" +  getFromDt().format(fmt1).replace("T", " ") + "' AND '" + getToDt().format(fmt1).replace("T", " ")+"'";
				break;
			case COLUMN_LINK:
				if(isFilterAllowed() && getConditionS().length()>0){
					ret = getQuotedKey()+" like '%" + getConditionS() + "%'";
				}
				break;
			default:
				break; // nothing to do

			}
		}
		return ret;
	}
	/**
	 * Create query condition for general search field value, i.e. for all text columns with condition or
	 * @return
	 */
	public String createGeneralWhere(){
		String ret="";
		if(isFilterAllowed()){
			switch(getColumnType()){
			case COLUMN_STRING:
				if(getGeneralCondition().length()>0){
					ret =getQuotedKey()+" like '%" + getGeneralCondition() + "%'";
				}
				break;
			case COLUMN_LINK:
				if(getGeneralCondition().length()>0){
					ret = getQuotedKey()+" like '%" + getGeneralCondition() + "%'";
				}
				break;
			default:
				break;
			}
		}
		return ret;
	}

	/**
	 * Create SQL Order By phrase's expression for this header
	 * @return
	 */
	public String createOrderBy() {
		String ret="";
		if(isSort()){
			switch(getSortValue()){
			case SORT_OFF:
				break; //nothing to do
			case SORT_ASC:
				ret = getQuotedKey();
				break;
			case SORT_DESC:
				ret = getQuotedKey() + " DESC";
			default:
				break; //nothing to do
			}
		}
		return ret;
	}

	/**
	 * Place a current page to a table from all rows
	 * @param rows
	 * @param table
	 */
	public static void tablePage(List<TableRow> rows, TableQtb table) {
		table.getRows().clear();
		table.getHeaders().setPages(TableHeader.calcPages(table.getHeaders().getPageSize(),rows.size()));
		table.getRows().addAll(TableHeader.fetchPage(rows,table.getHeaders().getPage(),
				table.getHeaders().getPageSize()));
	}

	/**
	 * Calc pages
	 * @param pageSize
	 * @param totalSize
	 * @return
	 */
	public static int calcPages(int pageSize, int totalSize) {
		int ret = (int)Math.ceil((float)totalSize/pageSize);
		return ret;
	}
	/**
	 * fetch a page by number
	 * @param rows all rows
	 * @param page from 1
	 * @param pageSize 
	 * @return
	 */
	public static List<TableRow> fetchPage(List<TableRow> rows, 
			int page, int pageSize) {
		int start = (page-1) * pageSize;
		int end = start+pageSize;
		if(end>rows.size()){
			end=rows.size();
		}
		if(start>end){
			start=0;
		}
		return rows.subList(start, end);
	}

	public String toString() {
		return(getKey() + "[type=" + getColumnType()+"]");
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableHeader other = (TableHeader) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	
}
