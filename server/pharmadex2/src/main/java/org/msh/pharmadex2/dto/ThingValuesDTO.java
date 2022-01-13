package org.msh.pharmadex2.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This DTO is responsible for Thing values
 * Motivation:<br>
 * For resource download we have to use get request with a parameter ResourceDTO.<br>
 * This parameter should contain all values from ThingDTO.<br> 
 * However, there is a limitation of get string, most common to 2048.<br>
 * Therefore, we need to put to ResourceDTO only values from ThingDTO, without tables, etc.
 * 
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ThingValuesDTO  extends AllowValidation{
	private String url="";
	private long nodeId=0;
	private long parentId=0;
	private Map<String, String> strings = new LinkedHashMap<String, String>();
	private Map<String, String> literals = new LinkedHashMap<String, String>();
	private Map<String, Long> numbers = new LinkedHashMap<String, Long>();
	private Map<String, LocalDate> dates = new LinkedHashMap<String, LocalDate>();
	private Map<String, AddressValuesDTO> addresses = new LinkedHashMap<String, AddressValuesDTO>();
	private Map<String, DictValuesDTO> dictionaries = new LinkedHashMap<String, DictValuesDTO>();
	private Map<String,Long> personselection = new LinkedHashMap<String, Long>();
	private Map<String,SchedulerDTO> schedulers = new LinkedHashMap<String, SchedulerDTO>();
	private Map<String,RegisterDTO> registers = new LinkedHashMap<String, RegisterDTO>();
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public Map<String, String> getStrings() {
		return strings;
	}
	public void setStrings(Map<String, String> strings) {
		this.strings = strings;
	}
	public Map<String, String> getLiterals() {
		return literals;
	}
	public void setLiterals(Map<String, String> literals) {
		this.literals = literals;
	}
	public Map<String, Long> getNumbers() {
		return numbers;
	}
	public void setNumbers(Map<String, Long> numbers) {
		this.numbers = numbers;
	}
	public Map<String, LocalDate> getDates() {
		return dates;
	}
	public void setDates(Map<String, LocalDate> dates) {
		this.dates = dates;
	}
	public Map<String, AddressValuesDTO> getAddresses() {
		return addresses;
	}
	public void setAddresses(Map<String, AddressValuesDTO> addresses) {
		this.addresses = addresses;
	}
	public Map<String, DictValuesDTO> getDictionaries() {
		return dictionaries;
	}
	public void setDictionaries(Map<String, DictValuesDTO> dictionaries) {
		this.dictionaries = dictionaries;
	}
	public Map<String, Long> getPersonselection() {
		return personselection;
	}
	public void setPersonselection(Map<String, Long> personselection) {
		this.personselection = personselection;
	}
	public Map<String, SchedulerDTO> getSchedulers() {
		return schedulers;
	}
	public void setSchedulers(Map<String, SchedulerDTO> schedulers) {
		this.schedulers = schedulers;
	}
	public Map<String, RegisterDTO> getRegisters() {
		return registers;
	}
	public void setRegisters(Map<String, RegisterDTO> registers) {
		this.registers = registers;
	}
	
}
