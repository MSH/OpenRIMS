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
 * Not used yet
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="webresource")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class WebResource implements Serializable {
	public WebResource() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418098A2C1F603815")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418098A2C1F603815", strategy="native")	
	private long ID;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url = "";
	
	@Column(name="File", nullable=true)	
	private byte[] file;
	
	@Column(name="MediaType", nullable=true, length=255)	
	private String mediaType = "";
	
	@Column(name="FileName", nullable=true, length=255)	
	private String fileName = "";
	
	@Column(name="FileSize", nullable=false, length=20)	
	private long fileSize = 0;
	
	@Column(name="ApiUpload", nullable=true, length=255)	
	private String apiUpload = "";
	
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
	 * URL of resource in case this resource is external
	 */
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	 * URL of resource in case this resource is external
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Binary repr. of the file
	 */
	public void setFile(byte[] value) {
		this.file = value;
	}
	
	/**
	 * Binary repr. of the file
	 */
	public byte[] getFile() {
		return file;
	}
	
	/**
	 * The content type, i.e., image/svg+xml"
	 */
	public void setMediaType(String value) {
		this.mediaType = value;
	}
	
	/**
	 * The content type, i.e., image/svg+xml"
	 */
	public String getMediaType() {
		return mediaType;
	}
	
	/**
	 * The name of a file
	 */
	public void setFileName(String value) {
		this.fileName = value;
	}
	
	/**
	 * The name of a file
	 */
	public String getFileName() {
		return fileName;
	}
	
	public void setFileSize(long value) {
		this.fileSize = value;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	/**
	 * URL of API to upload this resource
	 */
	public void setApiUpload(String value) {
		this.apiUpload = value;
	}
	
	/**
	 * URL of API to upload this resource
	 */
	public String getApiUpload() {
		return apiUpload;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
