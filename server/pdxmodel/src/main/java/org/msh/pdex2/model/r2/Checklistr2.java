/**
 * "Visual Paradigm: DO NOT MODIFY THIS FILE!"
 * 
 * This is an automatic generated file. It will be regenerated every time 
 * you generate persistence class.
 * 
 * Modifying its content may cause the program not work, or your work may lost.
 */

/**
 * Licensee: DuKe TeAm
 * License Type: Purchased
 */
package org.msh.pdex2.model.r2;

import java.io.Serializable;
import javax.persistence.*;
/**
 * New implementation of checklists
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="checklistr2")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Checklistr2 implements Serializable {
	public Checklistr2() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418098A2C20603820")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418098A2C20603820", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="dictItemID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept dictItem;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="activityID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept activity;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="applicationID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept applicationData;
	
	@Column(name="Question", nullable=true, length=255)	
	private String question;
	
	@Column(name="Answer", nullable=false, length=11)	
	private int answer;
	
	@Column(name="Comment", nullable=true, length=255)	
	private String comment;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setQuestion(String value) {
		this.question = value;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void setAnswer(int value) {
		this.answer = value;
	}
	
	public int getAnswer() {
		return answer;
	}
	
	public void setComment(String value) {
		this.comment = value;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setApplicationData(org.msh.pdex2.model.r2.Concept value) {
		this.applicationData = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplicationData() {
		return applicationData;
	}
	
	public void setActivity(org.msh.pdex2.model.r2.Concept value) {
		this.activity = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getActivity() {
		return activity;
	}
	
	public void setDictItem(org.msh.pdex2.model.r2.Concept value) {
		this.dictItem = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getDictItem() {
		return dictItem;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
