package org.msh.pharmadex2.dto;
/**
 * Data necessary to run an activity
 * @author alexk
 *
 */

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pharmadex2.dto.form.AllowValidation;

public class ActivityToRun extends AllowValidation {
	private Concept config = new Concept();
	private String prefLabel="";
	private boolean background = false;
	private List<String> executors = new ArrayList<String>();
	private List<String> fallBack = new ArrayList<String>();
	public Concept getConfig() {
		return config;
	}
	public void setConfig(Concept config) {
		this.config = config;
	}
	public String getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}
	public boolean isBackground() {
		return background;
	}
	public void setBackground(boolean background) {
		this.background = background;
	}
	public List<String> getExecutors() {
		return executors;
	}
	public void setExecutors(List<String> executors) {
		this.executors = executors;
	}
	public List<String> getFallBack() {
		return fallBack;
	}
	public void setFallBack(List<String> fallBack) {
		this.fallBack = fallBack;
	}
	public static ActivityToRun of(Concept config, String prefLabel, boolean background) {
		ActivityToRun ret = new ActivityToRun();
		ret.setConfig(config);
		ret.setPrefLabel(prefLabel);
		ret.setBackground(background);
		return ret;
	}
	@Override
	public String toString() {
		return "ActivityToRun [config=" + config + ", prefLabel=" + prefLabel + ", background=" + background
				+ ", executors=" + executors + ", fallBack=" + fallBack + "]";
	}
	
	
}
