package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * REsponsible to configure a data variable
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DataVariableDTO extends AllowValidation {
	//parent node - collection
	private long nodeId=0;
	//the key
	private long varNodeId=0;
	// available for public view
	private FormFieldDTO<OptionDTO> publicavailable = FormFieldDTO.of(new OptionDTO());
	// hide it from an applicant
	private FormFieldDTO<OptionDTO> hidefromapplicant = FormFieldDTO.of(new OptionDTO());
	//variable name
	private FormFieldDTO<String> varName = FormFieldDTO.of("");
	//variable name extension
	private FormFieldDTO<String> varNameExt = FormFieldDTO.of("");
	//Help text
	private FormFieldDTO<String> description=FormFieldDTO.of("");
	//Auxiliary data
	private FormFieldDTO<String> url = FormFieldDTO.of("");
	private FormFieldDTO<String> dictUrl = FormFieldDTO.of("");
	private FormFieldDTO<String> auxUrl = FormFieldDTO.of("");
	private FormFieldDTO<String> fileTypes = FormFieldDTO.of("");
	private FormFieldDTO<OptionDTO> readOnly = FormFieldDTO.of(new OptionDTO());
	//Literal, Dictionary, Date, Number, Logical
	private FormFieldDTO<OptionDTO> clazz = FormFieldDTO.of(new OptionDTO());
	//validators
	private FormFieldDTO<Long> maxLen = FormFieldDTO.of(0l);
	private FormFieldDTO<Long> minLen = FormFieldDTO.of(0l);
	private FormFieldDTO<OptionDTO> required = FormFieldDTO.of(new OptionDTO());
	private FormFieldDTO<OptionDTO> mult = FormFieldDTO.of(new OptionDTO());
	private FormFieldDTO<OptionDTO> unique = FormFieldDTO.of(new OptionDTO());
	private FormFieldDTO<OptionDTO> prefLabel = FormFieldDTO.of(new OptionDTO());
	//screen position
	private FormFieldDTO<Long> row = FormFieldDTO.of(0l);
	private FormFieldDTO<Long> col = FormFieldDTO.of(0l);
	private FormFieldDTO<Long> ord = FormFieldDTO.of(0l);
	
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public long getVarNodeId() {
		return varNodeId;
	}
	public void setVarNodeId(long varNodeId) {
		this.varNodeId = varNodeId;
	}
	public FormFieldDTO<String> getVarName() {
		return varName;
	}
	public void setVarName(FormFieldDTO<String> varName) {
		this.varName = varName;
	}
	
	public FormFieldDTO<OptionDTO> getPublicavailable() {
		return publicavailable;
	}
	public void setPublicavailable(FormFieldDTO<OptionDTO> publicavailable) {
		this.publicavailable = publicavailable;
	}
	public FormFieldDTO<OptionDTO> getHidefromapplicant() {
		return hidefromapplicant;
	}
	public void setHidefromapplicant(FormFieldDTO<OptionDTO> hidefromapplicant) {
		this.hidefromapplicant = hidefromapplicant;
	}
	public FormFieldDTO<String> getVarNameExt() {
		return varNameExt;
	}
	public void setVarNameExt(FormFieldDTO<String> varNameExt) {
		this.varNameExt = varNameExt;
	}
	public FormFieldDTO<String> getDescription() {
		return description;
	}
	public void setDescription(FormFieldDTO<String> description) {
		this.description = description;
	}
	public FormFieldDTO<OptionDTO> getClazz() {
		return clazz;
	}
	public void setClazz(FormFieldDTO<OptionDTO> clazz) {
		this.clazz = clazz;
	}
	public FormFieldDTO<Long> getMaxLen() {
		if(maxLen.getValue()==null) {
			maxLen.setValue(0l);
		}
		return maxLen;
	}
	public void setMaxLen(FormFieldDTO<Long> maxLen) {
		this.maxLen = maxLen;
	}
	public FormFieldDTO<Long> getMinLen() {
		if(minLen.getValue()==null) {
			minLen.setValue(0l);
		}
		return minLen;
	}
	public void setMinLen(FormFieldDTO<Long> minLen) {
		this.minLen = minLen;
	}
	public FormFieldDTO<OptionDTO> getRequired() {
		return required;
	}
	public void setRequired(FormFieldDTO<OptionDTO> required) {
		this.required = required;
	}
	public FormFieldDTO<OptionDTO> getMult() {
		return mult;
	}
	public void setMult(FormFieldDTO<OptionDTO> mult) {
		this.mult = mult;
	}
	
	public FormFieldDTO<OptionDTO> getUnique() {
		return unique;
	}
	public void setUnique(FormFieldDTO<OptionDTO> unique) {
		this.unique = unique;
	}
	
	
	public FormFieldDTO<OptionDTO> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(FormFieldDTO<OptionDTO> prefLabel) {
		this.prefLabel = prefLabel;
	}
	public FormFieldDTO<Long> getRow() {
		if(row.getValue()==null) {
			row.setValue(0l);
		}
		return row;
	}
	public void setRow(FormFieldDTO<Long> row) {
		this.row = row;
	}
	public FormFieldDTO<Long> getCol() {
		if(col.getValue()==null) {
			col.setValue(0l);
		}
		return col;
	}
	public void setCol(FormFieldDTO<Long> col) {
		this.col = col;
	}
	public FormFieldDTO<Long> getOrd() {
		if(ord.getValue()==null) {
			ord.setValue(0l);
		}
		return ord;
	}
	public void setOrd(FormFieldDTO<Long> ord) {
		this.ord = ord;
	}
	public FormFieldDTO<String> getUrl() {
		return url;
	}
	public void setUrl(FormFieldDTO<String> url) {
		this.url = url;
	}
	public FormFieldDTO<String> getDictUrl() {
		return dictUrl;
	}
	public void setDictUrl(FormFieldDTO<String> dictUrl) {
		this.dictUrl = dictUrl;
	}
	public FormFieldDTO<String> getFileTypes() {
		return fileTypes;
	}
	public void setFileTypes(FormFieldDTO<String> fileTypes) {
		this.fileTypes = fileTypes;
	}
	public FormFieldDTO<OptionDTO> getReadOnly() {
		return readOnly;
	}
	public void setReadOnly(FormFieldDTO<OptionDTO> readOnly) {
		this.readOnly = readOnly;
	}
	public FormFieldDTO<String> getAuxUrl() {
		return auxUrl;
	}
	public void setAuxUrl(FormFieldDTO<String> auxUrl) {
		this.auxUrl = auxUrl;
	}
	
}
