package org.msh.pdex2.dto.i18n;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * all languages and default one
 * @author Alex Kurasoff
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Languages {
	private Language selected=new Language();
	private Language byDefault=new Language();
	private List<Language> langs = new ArrayList<Language>();
	
	/**
	 * Selected language
	 * @return
	 */
	public Language getSelected() {
		return selected;
	}
	/**
	 * Selected language
	 */
	public void setSelected(Language selected) {
		this.selected = selected;
	}
	/**
	 * Default language
	 */
	public Language getByDefault() {
		return byDefault;
	}
	/**
	 * Default language
	 * @param byDefault
	 */
	public void setByDefault(Language byDefault) {
		this.byDefault = byDefault;
	}
	/**
	 * All languages
	 * @return
	 */
	public List<Language> getLangs() {
		return langs;
	}
	/**
	 * All languages
	 */
	public void setLangs(List<Language> langs) {
		this.langs.clear();
		if(langs != null){
			this.langs.addAll(langs);
		}
	}



}
