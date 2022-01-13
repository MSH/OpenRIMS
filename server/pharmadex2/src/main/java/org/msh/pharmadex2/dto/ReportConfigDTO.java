package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Report configuration
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportConfigDTO extends AllowValidation{
	private long nodeId=0l;
	private String title="";
	private String description="";
	private String dataUrl="";
	private String dictStageUrl="";
	public String applicantUrl="";
	private String ownerUrl="";
	private String addressUrl="";
	private String inspectAppUrl ="";
	private String renewAppUrl ="";
	private String registerAppUrl="";
	private boolean applicantRestriction=false;		//applicant should see only own
	private boolean registered=true;
	//configurations related
	private TableQtb table=new TableQtb();
	private ThingDTO report=new ThingDTO();
	//screen logic related
	private boolean form=false;
	
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDataUrl() {
		return dataUrl;
	}
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}
	
	public String getApplicantUrl() {
		return applicantUrl;
	}
	public void setApplicantUrl(String applicantUrl) {
		this.applicantUrl = applicantUrl;
	}
	public String getOwnerUrl() {
		return ownerUrl;
	}
	public void setOwnerUrl(String ownerUrl) {
		this.ownerUrl = ownerUrl;
	}
	public String getAddressUrl() {
		return addressUrl;
	}
	public void setAddressUrl(String addressUrl) {
		this.addressUrl = addressUrl;
	}
	public boolean isRegistered() {
		return registered;
	}
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	public String getDictStageUrl() {
		return dictStageUrl;
	}
	public void setDictStageUrl(String dictStageUrl) {
		this.dictStageUrl = dictStageUrl;
	}
	public String getInspectAppUrl() {
		return inspectAppUrl;
	}
	public void setInspectAppUrl(String inspectAppUrl) {
		this.inspectAppUrl = inspectAppUrl;
	}
	public String getRenewAppUrl() {
		return renewAppUrl;
	}
	public void setRenewAppUrl(String renewAppUrl) {
		this.renewAppUrl = renewAppUrl;
	}
	public String getRegisterAppUrl() {
		return registerAppUrl;
	}
	public void setRegisterAppUrl(String registerAppUrl) {
		this.registerAppUrl = registerAppUrl;
	}
	public boolean isApplicantRestriction() {
		return applicantRestriction;
	}
	public void setApplicantRestriction(boolean applicantRestriction) {
		this.applicantRestriction = applicantRestriction;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}

	public ThingDTO getReport() {
		return report;
	}
	public void setReport(ThingDTO report) {
		this.report = report;
	}
	public boolean isForm() {
		return form;
	}
	public void setForm(boolean form) {
		this.form = form;
	}

}
