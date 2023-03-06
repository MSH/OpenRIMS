package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Responsible for activity submit data
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActivitySubmitDTO extends AllowValidation{
	//a record related to this activity
		private long  historyId=0;
		private FormFieldDTO<String> notes = FormFieldDTO.of("");
		private TableQtb nextJob = new TableQtb();		//next activity in the current process
		private TableQtb execs = new TableQtb();		//executors of the next activity in the current process
		private TableQtb scheduled = new TableQtb();		//activities will be scheduled by approve 
		private boolean reloadExecs=false;		//the selection in nextJob has been changed
		private TableQtb actions = new TableQtb();	//set of actions
		private boolean supervisor=false;		//the action has been initiated by the supervisor
		private boolean applicant=false;			//the special behavior for an applicant
		private String applicantEmail="";			//applcant email
		private boolean reload=false;				//reload all unconditionally
		private boolean reassign=false;			//re-assign and cancel only
		private boolean reject=false;				//reject and return to user for clarification
		private boolean revokepermit = false;// revokepermit state
		private String colorAlert = "info"; // color Alert message
		
		public String getColorAlert() {
			return colorAlert;
		}
		public void setColorAlert(String colorAlert) {
			this.colorAlert = colorAlert;
		}
		public long getHistoryId() {
			return historyId;
		}
		public void setHistoryId(long historyId) {
			this.historyId = historyId;
		}
		public FormFieldDTO<String> getNotes() {
			return notes;
		}
		public void setNotes(FormFieldDTO<String> notes) {
			this.notes = notes;
		}
		public TableQtb getNextJob() {
			return nextJob;
		}
		public void setNextJob(TableQtb nextJob) {
			this.nextJob = nextJob;
		}
		public TableQtb getExecs() {
			return execs;
		}
		public void setExecs(TableQtb execs) {
			this.execs = execs;
		}
		
		public TableQtb getScheduled() {
			return scheduled;
		}
		public void setScheduled(TableQtb scheduled) {
			this.scheduled = scheduled;
		}
		public boolean isReloadExecs() {
			return reloadExecs;
		}
		public void setReloadExecs(boolean reloadExecs) {
			this.reloadExecs = reloadExecs;
		}
		public TableQtb getActions() {
			return actions;
		}
		public void setActions(TableQtb actions) {
			this.actions = actions;
		}
		public boolean isSupervisor() {
			return supervisor;
		}
		public void setSupervisor(boolean supervisor) {
			this.supervisor = supervisor;
		}
		public boolean isApplicant() {
			return applicant;
		}
		public void setApplicant(boolean applicant) {
			this.applicant = applicant;
		}
		
		public String getApplicantEmail() {
			return applicantEmail;
		}
		public void setApplicantEmail(String applicantEmail) {
			this.applicantEmail = applicantEmail;
		}
		public boolean isReload() {
			return reload;
		}
		public void setReload(boolean reload) {
			this.reload = reload;
		}
		
		public boolean isReassign() {
			return reassign;
		}
		public void setReassign(boolean reassign) {
			this.reassign = reassign;
		}
		/**
		 * Id of the next activity selected by a user
		 * @return 0 if next activity undetermined
		 */
		public long nextActivity() {
			for(TableRow row : getNextJob().getRows()) {
				if(row.getSelected()) {
					return row.getDbID();
				}
			}
			return 0;
		}
		
		/**
		 * List of executor's IDs
		 * @return empty list if not found
		 */
		public List<Long> executors() {
			List<Long> ret =new ArrayList<Long>();
			for(TableRow row : getExecs().getRows()) {
				if(row.getSelected()) {
					ret.add(row.getDbID());
				}
			}
			return ret;
		}
		/**
		 * Which action has been selected
		 * @return
		 */
		public int actionSelected() {
			for(TableRow row : getActions().getRows()) {
				if(row.getSelected()) {
					long retl=row.getDbID();
					return Math.toIntExact(retl);
				}
			}
			return -1;
		}
		public boolean isReject() {
			return reject;
		}
		public void setReject(boolean reject) {
			this.reject = reject;
		}
		public boolean isRevokepermit() {
			return revokepermit;
		}
		public void setRevokepermit(boolean revokepermit) {
			this.revokepermit = revokepermit;
		}
}
