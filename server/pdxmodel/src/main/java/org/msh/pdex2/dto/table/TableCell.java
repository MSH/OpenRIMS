package org.msh.pdex2.dto.table;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.msh.pdex2.i18n.Messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Cell for on screen table
 * Provides also expandable set of instanceOf for different formats of the value
 * You can also add any bootstrap style to it
 * @author Alex Kurasoff
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TableCell {
	private String key=""; //value before i18, if not 18 - empty
	private Object originalValue; //header name
	private String value=""; //value after render and i18 convert
	private String toolTip=""; //never used
	private int render=0; //0 – display value as is, 1 – display as link – key – address, value is text, 2 – use logical value from header and value as unique key that will be pass to function switchIt
	private String styleClass=""; //Bootstrap class or classes for style this cell

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	public void setOriginalValue(Object originalValue) {
		this.originalValue = originalValue;
	}

	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public int getRender() {
		return render;
	}

	public void setRender(int render) {
		this.render = render;
	}


	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	/**
	 * Cell for a string
	 * @param key
	 * @param value
	 * @return
	 */
	public static TableCell instanceOf(String key, String value){
		TableCell ret = new TableCell();
		ret.setKey(key);
		if(value != null){
			ret.setValue(value);
		}else{
			value="";
		}
		ret.setOriginalValue(value);
		return ret;
	}
	/**
	 * Cell for a long value (if you want integer, please use it)
	 * @param key
	 * @param value
	 * @param locale
	 * @return
	 */
	public static TableCell instanceOf(String key, Long value, Locale locale){
		TableCell  ret = new TableCell();
		ret.setKey(key);
		NumberFormat format = NumberFormat.getNumberInstance(locale);
		ret.setValue(format.format(value));
		ret.setOriginalValue(value);
		return ret;
	}


	/**
	 * Cell for date
	 * @param key
	 * @param value
	 * @param locale
	 * @return
	 */
	public static TableCell instanceOf(String key, LocalDate value, Locale locale){
		TableCell ret = new TableCell();
		ret.setKey(key);
		if(value != null && value.getYear()>1970){
			LocalDate ld = (LocalDate) value;
			DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
			ret.setValue(ld.format(formatter));
			ret.setOriginalValue(value);
		}else{
			ret.setValue("***");
			ret.setOriginalValue(null);
		}
		return ret;
	}

	/**
	 * Cell for DateTime
	 * @param key
	 * @param value
	 * @param locale
	 * @return
	 */
	public static TableCell instanceOf(String key, LocalDateTime value, Locale locale){
		TableCell ret = new TableCell();
		ret.setKey(key);
		if(value != null && value.getYear()>1970){
			LocalDateTime ld = value;
			DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale);
			ret.setValue(ld.format(formatter));
			ret.setOriginalValue(value);
		}else{
			ret.setValue("***");
			ret.setOriginalValue("***");
		}
		return ret;
	}

	/**
	 * Cell to DISPLAY boolean value yes/no, +/-, 1/0 etc
	 * @param key
	 * @param value
	 * @param yes string representation of true
	 * @param no string representation of false
	 * @return
	 */
	public static TableCell instanceOf(String key, boolean value, String yes, String no){
		TableCell ret = new TableCell();
		ret.setKey(key);
		if(value){
			ret.setValue(yes);
		}else{
			ret.setValue(no);
		}
		ret.setOriginalValue(value);
		return ret;
	}
	/**
	 * Boolean value to edit in a row
	 * @param key
	 * @param value
	 * @return
	 */
	public static TableCell instanceOf(String key, boolean value){
		TableCell ret = new TableCell();
		ret.setKey(key);
		ret.setValue("");
		ret.setOriginalValue(value);
		return ret;
	}

	/**
	 * Place link to a cell
	 * @param url
	 * @param text
	 * @return
	 */
	public static TableCell instanceOfLink(String url, String text){
		TableCell ret = new TableCell();
		ret.setRender(1);
		ret.setKey(url);
		ret.setValue(text);
		ret.setOriginalValue(text);
		return ret;
	}
	/**
	 * Clone a cell
	 * @param tableCell
	 * @return
	 */
	public static TableCell instanceOf(TableCell tableCell) {
		TableCell ret = new TableCell();
		ret.setKey(tableCell.getKey());
		ret.setOriginalValue(tableCell.getOriginalValue());
		ret.setRender(tableCell.getRender());
		ret.setToolTip(tableCell.getToolTip());
		ret.setValue(tableCell.getValue());
		return ret;
	}
	/**
	 * get int value of this cell if value is Long
	 * @return
	 */
	public int getIntValue() {
		if(getOriginalValue() instanceof Long){
			Long val = (Long)getOriginalValue();
			return val.intValue();
		}else{
			return 0;
		}
	}
	/**
	 * Big Decimal instance of
	 * @param key
	 * @param res
	 * @return
	 */
	public static TableCell instanceOf(String key, BigDecimal res, Locale locale) {
		TableCell ret = new TableCell();
		ret.setKey(key);
		//ret.setValue(res.toPlainString());
		NumberFormat nf = NumberFormat.getNumberInstance(locale);
		ret.setValue(nf.format(res));
		ret.setOriginalValue(res);
		return ret;
	}

	/**
	 * Instance of localized string
	 * @param i18Key the exact key 
	 * @param messages current messages service. Should be passed as a parameter to avoid cycle references
	 * @return
	 */
	public static TableCell instanceOf(String i18Key, Messages messages) {
		TableCell ret = new TableCell();
		ret.setKey(i18Key);
		ret.setValue(messages.get(i18Key));
		ret.setOriginalValue(i18Key);
		return ret;
	}
	/**
	 * Instance of NULL cell
	 * @param key
	 * @return
	 */
	public static TableCell instanceOf(String key) {
		TableCell ret = new TableCell();
		ret.setKey(key);
		ret.setValue("");
		ret.setOriginalValue(null);
		return ret;
	}

	@Override
	public String toString() {
		return "TableCell [key=" + key + ", value=" + value + "]";
	}
	/**
	 * Get a value that suit for the database insert
	 * @return string value allowed for ANSI SQL insert
	 */
	public String getDbInsertValue() {
		String ret="null";
		Object val = getOriginalValue();
		if(val != null) {
			if(val instanceof String || val instanceof Long || val instanceof BigDecimal) {
				ret="'"+ getValue() + "'";
			}

			if(val instanceof LocalDate) {
				LocalDate ldval=(LocalDate) val;
				String iso = DateTimeFormatter.BASIC_ISO_DATE.format(ldval);
				ret="'"+iso+"'";
			}
			if(val instanceof LocalDateTime) {
				LocalDateTime ldtval=(LocalDateTime) val;
				String iso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ldtval);
				ret="'"+iso+"'";
			}

			if(val instanceof Boolean) {
				Boolean bval = (Boolean) val; 
				if(bval) {
					ret="1";
				}else {
					ret="0";
				}
			}
		}
		return ret;
	}

}
