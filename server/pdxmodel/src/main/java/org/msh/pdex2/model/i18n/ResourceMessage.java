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
package org.msh.pdex2.model.i18n;

import java.io.Serializable;
import javax.persistence.*;
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="resource_message")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ResourceMessage implements Serializable {
	public ResourceMessage() {
	}
	
	@Column(name="Id", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222277187B38344250A5E7")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222277187B38344250A5E7", strategy="native")	
	private long id;
	
	@Column(name="Message_key", nullable=true, length=255)	
	private String message_key;
	
	@Column(name="Message_value", nullable=true, length=255)	
	private String message_value;
	
	private void setId(long value) {
		this.id = value;
	}
	
	public long getId() {
		return id;
	}
	
	public long getORMID() {
		return getId();
	}
	
	public void setMessage_key(String value) {
		this.message_key = value;
	}
	
	public String getMessage_key() {
		return message_key;
	}
	
	public void setMessage_value(String value) {
		this.message_value = value;
	}
	
	public String getMessage_value() {
		return message_value;
	}
	
	public String toString() {
		return String.valueOf(getId());
	}
	
}
