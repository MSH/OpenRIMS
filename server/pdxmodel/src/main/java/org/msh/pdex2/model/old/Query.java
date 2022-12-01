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
package org.msh.pdex2.model.old;

import java.io.Serializable;
import javax.persistence.*;
/**
 * REsponsiblr to store SQL query text. Only for queries taht is not a part of stored procedures
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="query")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Query implements Serializable {
	public Query() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274184AF102B940E7FE")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274184AF102B940E7FE", strategy="native")	
	private long ID;
	
	@Column(name="`Key`", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="Key")	
	private String key;
	
	@Column(name="Sql", nullable=true, length=8198)	
	private String sql;
	
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
	
	public void setKey(String value) {
		this.key = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setSql(String value) {
		this.sql = value;
	}
	
	public String getSql() {
		return sql;
	}
	
	public void setComment(String value) {
		this.comment = value;
	}
	
	public String getComment() {
		return comment;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
