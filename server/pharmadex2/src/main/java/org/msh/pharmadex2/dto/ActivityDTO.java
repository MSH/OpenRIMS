package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * All data necessary to work in an activity
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActivityDTO extends AllowValidation{
	//amendment related path from amended unit to application
	List<DataUnitDTO> modiPath = new ArrayList<DataUnitDTO>();
	//a record related to this activity
	private long  historyId=0;
	//guest or not guest
	private boolean guest=false;
	//is this application host?
	private boolean host=false;
	//application as a set of data blocks
	private List<ThingDTO> application = new ArrayList<ThingDTO>();
	//all completed activities. The last activity is the current!
	private List<ThingDTO> path = new ArrayList<ThingDTO>();
	//activity data
	private List<ThingDTO> data=new ArrayList<ThingDTO>();
	//notes from the previous step
	private List<String> notes = new ArrayList<String>();
	private List<Boolean> cancelled = new ArrayList<Boolean>();
	//Current activity is background 
	private boolean background=false;
	//current activity has been done successfully
	private boolean done=false;
	
	private boolean attention = false;
	
	public List<DataUnitDTO> getModiPath() {
		return modiPath;
	}
	public void setModiPath(List<DataUnitDTO> modiPath) {
		this.modiPath = modiPath;
	}
	public long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	
	public boolean isGuest() {
		return guest;
	}
	public void setGuest(boolean guest) {
		this.guest = guest;
	}
	
	public boolean isHost() {
		return host;
	}
	public void setHost(boolean host) {
		this.host = host;
	}
	public List<ThingDTO> getApplication() {
		return application;
	}
	public void setApplication(List<ThingDTO> application) {
		this.application = application;
	}
	public List<ThingDTO> getPath() {
		return path;
	}
	public void setPath(List<ThingDTO> path) {
		this.path = path;
	}
	public List<ThingDTO> getData() {
		return data;
	}
	public void setData(List<ThingDTO> data) {
		this.data = data;
	}
	public List<String> getNotes() {
		return notes;
	}
	public void setNotes(List<String> notes) {
		this.notes = notes;
	}
	public List<Boolean> getCancelled() {
		return cancelled;
	}
	public void setCancelled(List<Boolean> cancelled) {
		this.cancelled = cancelled;
	}
	public boolean isBackground() {
		return background;
	}
	public void setBackground(boolean background) {
		this.background = background;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public boolean isAttention() {
		return attention;
	}
	public void setAttention(boolean attention) {
		this.attention = attention;
	}
	
}
