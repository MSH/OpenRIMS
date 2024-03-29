package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.msh.pharmadex2.dto.form.AllowValidation;
/**
 * NRA user's branch/department and allowed applications
 * @author alexk
 *
 */
public class UserAccessDTO extends AllowValidation {
	private String email="";
	private String name="";
	private long officeID=0l;
	private boolean mainOffice=false;
	private Set<String> roles=new HashSet<String>();
	private Set<Long> applDicts=new HashSet<Long>();
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getOfficeID() {
		return officeID;
	}
	public void setOfficeID(long officeID) {
		this.officeID = officeID;
	}
	public boolean isMainOffice() {
		return mainOffice;
	}
	public void setMainOffice(boolean mainOffice) {
		this.mainOffice = mainOffice;
	}
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	public Set<Long> getApplDicts() {
		return applDicts;
	}
	public void setApplDicts(Set<Long> applDicts) {
		this.applDicts = applDicts;
	}
	public static UserAccessDTO instanceOf(String userEmail) {
		UserAccessDTO ret = new UserAccessDTO();
		ret.setEmail(userEmail);
		return ret;
	}
	/**
	 * Return comma separated IDs of items in application's dictionaries
	 * Only these applications are accessible by the user 
	 * @return
	 */
	public String applAccess() {
		return StringUtils.join(getApplDicts(), ',');
	}
	
}
