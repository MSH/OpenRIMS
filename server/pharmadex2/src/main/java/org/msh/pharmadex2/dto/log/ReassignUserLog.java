package org.msh.pharmadex2.dto.log;

import org.msh.pharmadex2.dto.form.AllowValidation;

/**
 * A log record JSON packed into a concept.label field
 * @author alexk
 *
 */
public class ReassignUserLog extends AllowValidation {
	private String emailFrom="";
	private String emailTo="";
	private long reassigned=0l;	//how many records reassigned to emailTo
	private long left=0l;				//how many records left for emailFrom
	private String description="";
	public String getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public long getReassigned() {
		return reassigned;
	}
	public void setReassigned(long reassigned) {
		this.reassigned = reassigned;
	}
	public long getLeft() {
		return left;
	}
	public void setLeft(long left) {
		this.left = left;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
