package org.msh.pharmadex2.dto.log;

import java.time.LocalDate;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class ImportLanguageDTO extends AllowValidation {
	private String oldLanguages="";
	private String newLanguage="";
	private int totalMessages=0;
	private int imported=0;
	private int updated=0;
	public String getOldLanguages() {
		return oldLanguages;
	}
	public void setOldLanguages(String oldLanguages) {
		this.oldLanguages = oldLanguages;
	}
	public String getNewLanguage() {
		return newLanguage;
	}
	public void setNewLanguage(String newLanguage) {
		this.newLanguage = newLanguage;
	}
	public int getTotalMessages() {
		return totalMessages;
	}
	public void setTotalMessages(int totalMessages) {
		this.totalMessages = totalMessages;
	}
	public int getImported() {
		return imported;
	}
	public void setImported(int imported) {
		this.imported = imported;
	}
	public int getUpdated() {
		return updated;
	}
	public void setUpdated(int updated) {
		this.updated = updated;
	}
	/**
	 * cleanup counters
	 */
	public void cleanUp() {
		setImported(0);
		setTotalMessages(0);
		setUpdated(0);
	}

}
