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
 * Responsible for data configuration
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="assembly")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Discriminator", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("Assembly")
public class Assembly implements Serializable {
	public Assembly() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217E2154D03603FCE")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217E2154D03603FCE", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept propertyName;
	
	@Column(name="Required", nullable=false, length=1)	
	private boolean required;
	
	@Column(name="Mult", nullable=false, length=1)	
	private boolean mult;
	
	@Column(name="ReadOnly", nullable=false, length=1)	
	private boolean readOnly;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@Column(name="DictUrl", nullable=true, length=255)	
	private String dictUrl;
	
	@Column(name="Min", nullable=false, length=20)	
	private long min;
	
	@Column(name="Max", nullable=false, length=20)	
	private long max;
	
	@Column(name="FileTypes", nullable=true, length=255)	
	private String fileTypes;
	
	@Column(name="Row", nullable=false, length=11)	
	private int row;
	
	@Column(name="Col", nullable=false, length=11)	
	private int col;
	
	@Column(name="Ord", nullable=false, length=11)	
	private int ord;
	
	@Column(name="Clazz", nullable=true, length=255)	
	private String clazz;
	
	@Column(name="AuxDataUrl", nullable=true, length=255)	
	private String auxDataUrl;
	
	@Column(name="`Unique`", nullable=false, length=1)	
	private boolean unique = false;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	/**
	 * The class name of a variable
	 */
	public void setClazz(String value) {
		this.clazz = value;
	}
	
	/**
	 * The class name of a variable
	 */
	public String getClazz() {
		return clazz;
	}
	
	/**
	 * Text area or allow multiply choices
	 */
	public void setMult(boolean value) {
		this.mult = value;
	}
	
	/**
	 * Text area or allow multiply choices
	 */
	public boolean getMult() {
		return mult;
	}
	
	public void setReadOnly(boolean value) {
		this.readOnly = value;
	}
	
	public boolean getReadOnly() {
		return readOnly;
	}
	
	/**
	 * Url for dictionary, thing, etc
	 */
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	 * Url for dictionary, thing, etc
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * additional URL for dictionary
	 */
	public void setDictUrl(String value) {
		this.dictUrl = value;
	}
	
	/**
	 * additional URL for dictionary
	 */
	public String getDictUrl() {
		return dictUrl;
	}
	
	/**
	 * min value or days before the current date, or min length of string
	 */
	public void setMin(long value) {
		this.min = value;
	}
	
	/**
	 * min value or days before the current date, or min length of string
	 */
	public long getMin() {
		return min;
	}
	
	/**
	 * max days from the current date, max number, max length of string
	 */
	public void setMax(long value) {
		this.max = value;
	}
	
	/**
	 * max days from the current date, max number, max length of string
	 */
	public long getMax() {
		return max;
	}
	
	/**
	 * This element should be not empty (see min and max)
	 */
	public void setRequired(boolean value) {
		this.required = value;
	}
	
	/**
	 * This element should be not empty (see min and max)
	 */
	public boolean getRequired() {
		return required;
	}
	
	/**
	 * see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept
	 */
	public void setFileTypes(String value) {
		this.fileTypes = value;
	}
	
	/**
	 * see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept
	 */
	public String getFileTypes() {
		return fileTypes;
	}
	
	/**
	 * Row on the screen
	 */
	public void setRow(int value) {
		this.row = value;
	}
	
	/**
	 * Row on the screen
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * Column inside the row in the screen
	 */
	public void setCol(int value) {
		this.col = value;
	}
	
	/**
	 * Column inside the row in the screen
	 */
	public int getCol() {
		return col;
	}
	
	/**
	 * Order inside the column that is in a row
	 */
	public void setOrd(int value) {
		this.ord = value;
	}
	
	/**
	 * Order inside the column that is in a row
	 */
	public int getOrd() {
		return ord;
	}
	
	public void setAuxDataUrl(String value) {
		this.auxDataUrl = value;
	}
	
	public String getAuxDataUrl() {
		return auxDataUrl;
	}
	
	public void setUnique(boolean value) {
		this.unique = value;
	}
	
	public boolean getUnique() {
		return unique;
	}
	
	public void setPropertyName(org.msh.pdex2.model.r2.Concept value) {
		this.propertyName = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getPropertyName() {
		return propertyName;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
