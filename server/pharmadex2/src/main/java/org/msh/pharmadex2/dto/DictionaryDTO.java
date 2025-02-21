package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The dictionary.<br>
 * Allows single or multiply selections.<br>
 * Allows multi level<br>
 * <b>Selected IDs will be in prevSelected property. These are really selected items, not levels</b><br>
 * Many properties, like a varName, are related to integration a dictionary into electronic forms (things)
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DictionaryDTO extends AllowValidation{
	//recognized as single level
	private boolean singleLevel=false;
	//for amendments etc
	private boolean changed;		
	//url of the dictionary
	private String url="";
	//id of url concept, used to keep dictionary related literals
	private long urlId=0;
	//NAme of the variable in a thing
	private String varName = ""; 
	//name of a dictionary, for use as "home" link in the breadcrumb
	private String home="";
	private List<OptionDTO> path = new ArrayList<OptionDTO>();
	// max depth for this dictionary
	private int maxDepth=0;
	//selected in all dictionaries
	private FormFieldDTO<OptionDTO> selection=FormFieldDTO.of(new OptionDTO());
	//select from here
	private TableQtb table = new TableQtb();
	//is this dictionary system?
	private boolean system=false;
	// is multi-value choice allowed?
	private boolean mult=false;
	//is this dictionary required to fill up?
	private boolean required=true;
	//all selected rows at the final level
	private List<Long> prevSelected = new ArrayList<Long>();
	//selection from path
	private OptionDTO pathSelected = new OptionDTO();
	//show only selected rows
	private boolean selectedOnly=false;
	//this dictionary should be in read-only mode
	private boolean readOnly=false;
	// currently selected item
	private List<OptionDTO> currentSelections = new ArrayList<OptionDTO>();
	private List<TableRow> allrows = new ArrayList<TableRow>();
	
	public DictionaryDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean isSingleLevel() {
		return singleLevel;
	}

	public void setSingleLevel(boolean singleLevel) {
		this.singleLevel = singleLevel;
	}

	public List<TableRow> getAllrows() {
		return allrows;
	}
	public void setAllrows(List<TableRow> allrows) {
		this.allrows = allrows;
	}
	/*private FileDTO file = new FileDTO();
	private ThingDTO thing = new ThingDTO();
	

	public ThingDTO getThing() {
		return thing;
	}
	public void setThing(ThingDTO thing) {
		this.thing = thing;
	}
	public FileDTO getFile() {
		return file;
	}
	public void setFile(FileDTO file) {
		this.file = file;
	}*/
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	public long getUrlId() {
		return urlId;
	}
	public void setUrlId(long urlId) {
		this.urlId = urlId;
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getHome() {
		return home;
	}
	public void setHome(String home) {
		this.home = home;
	}
	public List<OptionDTO> getPath() {
		return path;
	}
	public void setPath(List<OptionDTO> path) {
		this.path = path;
	}
	public FormFieldDTO<OptionDTO> getSelection() {
		return selection;
	}
	public void setSelection(FormFieldDTO<OptionDTO> selection) {
		this.selection = selection;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
	public boolean isSystem() {
		return system;
	}
	public void setSystem(boolean system) {
		this.system = system;
	}
	public boolean isMult() {
		return mult;
	}
	public void setMult(boolean mult) {
		this.mult = mult;
	}

	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	/**
	 * Also remove zero values
	 * @return
	 */
	public List<Long> getPrevSelected() {
		List<Long> tmp = new ArrayList<Long>();
		tmp.addAll(this.prevSelected);
		this.prevSelected.clear();
		for(Long n :tmp) {
			if(n>0) {
				this.prevSelected.add(n);
			}
		}
		return prevSelected;
	}
	public void setPrevSelected(List<Long> prevSelected) {
		this.prevSelected = prevSelected;
	}
	public OptionDTO getPathSelected() {
		return pathSelected;
	}
	public void setPathSelected(OptionDTO pathSelected) {
		this.pathSelected = pathSelected;
	}

	public int getMaxDepth() {
		return maxDepth;
	}
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	public boolean isSelectedOnly() {
		return selectedOnly;
	}
	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public List<OptionDTO> getCurrentSelections() {
		return currentSelections;
	}
	public void setCurrentSelections(List<OptionDTO> currentSelections) {
		this.currentSelections = currentSelections;
	}

	/**
	 * Create a new dictionary based on data provided
	 * Except calculated parameters
	 * @return
	 */
	public DictionaryDTO cloneImportant() {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setVarName(getVarName());
		ret.setHome(getHome());
		ret.setIdentifier(getIdentifier());
		ret.setMult(isMult());
		ret.setPrevSelected(getPrevSelected());
		ret.setRequired(isRequired());
		ret.setUrl(getUrl());
		ret.setValid(isValid());
		ret.setPathSelected(getPathSelected());
		ret.setSelectedOnly(isSelectedOnly());
		ret.setMaxDepth(getMaxDepth());
		ret.setSystem(isSystem());
		return ret;
	}
	
	/**
	 * Clear all dictionary selection
	 */
	public void clear() {
		this.getPrevSelected().clear();
		this.getSelection().setValue(new OptionDTO());
	}
	/*@Override
	public String toString() {
		List<String> ret = new ArrayList<String>();
		for(OptionDTO opt :getCurrentSelections()) {
			ret.add(opt.toString());
		}
		return String.join(";",ret);
	}*/
	@Override
	public String toString() {
		return "DictionaryDTO [changed=" + changed + ", url=" + url + ", urlId=" + urlId + ", varName=" + varName
				+ ", home=" + home + ", path=" + path + ", maxDepth=" + maxDepth + ", selection=" + selection
				+ ", table=" + table + ", system=" + system + ", mult=" + mult + ", required=" + required
				+ ", prevSelected=" + prevSelected + ", pathSelected=" + pathSelected + ", selectedOnly=" + selectedOnly
				+ ", readOnly=" + readOnly + ", currentSelections=" + currentSelections + ", allrows=" + allrows + "]";
	}
	/**
	 * Is this dictionary already loaded
	 * @return
	 */
	public boolean isLoaded() {
		return getTable().getHeaders().getHeaders().size()>0;
	}
	
}
