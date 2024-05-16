package org.msh.pdex2.repository.common;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableRow;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.RowMapper;

/**
 * Maps rows for any table that:
 * <ul>
 * <li> 
 * </ul>
 * @author Alex Kurasoff
 *
 */
public class QtbRowMapper implements RowMapper<TableRow> {

	private Headers headers;

	/**
	 * Set localize string values
	 * @param headers 
	 * @param link
	 */
	public QtbRowMapper(Headers headers) {
		super();
		this.headers = headers;
	}


	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	@Override
	public TableRow mapRow(ResultSet rs, int rowNum) throws SQLException {
		TableRow ret = TableRow.instanceOf(fetchID(rs,rowNum));
		for(TableHeader header: getHeaders().getHeaders()){
			switch(header.getColumnType()){
			case TableHeader.COLUMN_STRING:
				ret.getRow().add(TableCell.instanceOf(header.getKey(),rs.getString(header.getKey())));
				break;
			case TableHeader.COLUMN_LONG:
				ret.getRow().add(TableCell.instanceOf(header.getKey(),rs.getLong(header.getKey()),LocaleContextHolder.getLocale()));
				break;
			case TableHeader.COLUMN_BOOLEAN_CHECKBOX:
			case TableHeader.COLUMN_BOOLEAN_RADIO:
			case TableHeader.COLUMN_TRUE_FALSE:
			case TableHeader.COLUMN_YESNO:
				ret.getRow().add(TableCell.instanceOf(header.getKey(),rs.getBoolean(header.getKey())));
				break;
			case TableHeader.COLUMN_LOCALDATE:
				Date dt = rs.getDate(header.getKey());
				LocalDate ld = null;
				if(dt != null){
					ld= toLocalDate(dt);
				}
				ret.getRow().add(TableCell.instanceOf(header.getKey(),
						ld, 
						LocaleContextHolder.getLocale()));
				break;
			case TableHeader.COLUMN_LOCALDATETIME:
				Date dt1 = rs.getTimestamp(header.getKey());
				LocalDateTime dTime = null;
				if(dt1 != null){
					dTime= toLocalDateTime(dt1);
				}
				ret.getRow().add(TableCell.instanceOf(header.getKey(),
						dTime, 
						LocaleContextHolder.getLocale()));
				break;
			case TableHeader.COLUMN_LINK:
			case TableHeader.COLUMN_I18LINK:
				String displayValue = "link";
				try {
					String s = rs.getString(header.getKey());
					if(s==null || s.length()==0){
						s="";
					}
					displayValue = s;
				} catch (Exception e) {
					//nothing to do
				}
				ret.getRow().add(TableCell.instanceOfLink(header.getKey(), displayValue));
				break;
			case TableHeader.COLUMN_DECIMAL:
				BigDecimal res = rs.getBigDecimal(header.getKey());
				if(res==null){
					res = BigDecimal.ZERO;
				}
				ret.getRow().add(TableCell.instanceOf(header.getKey(),res,LocaleContextHolder.getLocale()));
				break;
			case TableHeader.COLUMN_I18:
				ret.getRow().add(TableCell.instanceOf(header.getKey(),rs.getString(header.getKey())));
				break;
			default:
				//nothing to do
			}
		}
		return ret;
	}
	/**
	 * Convert java.util.Date to java.time.LocalDateTime
	 * Time zone will be kept
	 * @param dateToConvert
	 * @return
	 */
	public LocalDateTime toLocalDateTime(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime())
			      .atZone(ZoneId.systemDefault())
			      .toLocalDateTime();
	}
	
	/**
	 * Convert java.util.Date to java.time.LocalDateTime
	 * Time zone will be kept
	 * @param dateToConvert
	 * @return
	 */
	public LocalDate toLocalDate(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime())
			      .atZone(ZoneId.systemDefault())
			      .toLocalDate();
	}

	/**
	 * ID if exists, otherwise rowNum
	 * @param rs
	 * @param rowNum
	 * @return
	 */
	private long fetchID(ResultSet rs, int rowNum) {
		long ret = 0;
		try {
			ret = rs.getLong("ID");
		} catch (SQLException e) {
			ret=rowNum;
		}
		return ret;
	}

}
