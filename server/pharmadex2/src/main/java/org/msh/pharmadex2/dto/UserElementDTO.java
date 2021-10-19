package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserElementDTO extends AllowValidation{
	//name of an organization if applicable
	private String organization="";
	//literals in the tree
	private DictNodeDTO node = new DictNodeDTO();
	//email for this person
	private FormFieldDTO<String> user_email = FormFieldDTO.of(""); 
	//enabled to login or disabled
	private FormFieldDTO<OptionDTO> global_enable = new FormFieldDTO<OptionDTO>();
	//Id of the selected concept,i.e., NMRA, Applicant 
	private long conceptId=0;
	// user ID
	private long id=0;
	//select from suspended persons
	private FormFieldDTO<OptionDTO> addPerson=FormFieldDTO.of(new OptionDTO());
	//dictionaries
	private DictionaryDTO roles = new DictionaryDTO();
	private DictionaryDTO applications = new DictionaryDTO();
	private DictionaryDTO amendments = new DictionaryDTO();
	private DictionaryDTO renewal = new DictionaryDTO();
	private DictionaryDTO deregistration = new DictionaryDTO();
	private DictionaryDTO areas = new DictionaryDTO();

	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public DictNodeDTO getNode() {
		return node;
	}
	public void setNode(DictNodeDTO node) {
		this.node = node;
	}
	
	public FormFieldDTO<String> getUser_email() {
		return user_email;
	}
	public void setUser_email(FormFieldDTO<String> user_email) {
		this.user_email = user_email;
	}
	
	public FormFieldDTO<OptionDTO> getGlobal_enable() {
		return global_enable;
	}
	public void setGlobal_enable(FormFieldDTO<OptionDTO> global_enable) {
		this.global_enable = global_enable;
	}
	public long getConceptId() {
		return conceptId;
	}
	public void setConceptId(long conceptId) {
		this.conceptId = conceptId;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public FormFieldDTO<OptionDTO> getAddPerson() {
		return addPerson;
	}
	public void setAddPerson(FormFieldDTO<OptionDTO> addPerson) {
		this.addPerson = addPerson;
	}
	public DictionaryDTO getRoles() {
		return roles;
	}
	public void setRoles(DictionaryDTO roles) {
		this.roles = roles;
	}
	public DictionaryDTO getApplications() {
		return applications;
	}
	public void setApplications(DictionaryDTO applications) {
		this.applications = applications;
	}
	public DictionaryDTO getAmendments() {
		return amendments;
	}
	public void setAmendments(DictionaryDTO amendments) {
		this.amendments = amendments;
	}
	public DictionaryDTO getRenewal() {
		return renewal;
	}
	public void setRenewal(DictionaryDTO renewal) {
		this.renewal = renewal;
	}
	public DictionaryDTO getDeregistration() {
		return deregistration;
	}
	public void setDeregistration(DictionaryDTO deregistration) {
		this.deregistration = deregistration;
	}
	public DictionaryDTO getAreas() {
		return areas;
	}
	public void setAreas(DictionaryDTO areas) {
		this.areas = areas;
	}
	
}