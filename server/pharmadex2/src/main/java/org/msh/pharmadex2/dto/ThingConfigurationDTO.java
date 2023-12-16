package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Thing configuration to store in the field "Label" of the concept  
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ThingConfigurationDTO {
	private List<AssemblyDTO> assemblies = new ArrayList<AssemblyDTO>();
	private List<LayoutRowDTO> layout = new ArrayList<LayoutRowDTO>();
	public List<AssemblyDTO> getAssemblies() {
		return assemblies;
	}
	public void setAssemblies(List<AssemblyDTO> assemblies) {
		this.assemblies = assemblies;
	}
	public List<LayoutRowDTO> getLayout() {
		return layout;
	}
	public void setLayout(List<LayoutRowDTO> layout) {
		this.layout = layout;
	}	
	
}
