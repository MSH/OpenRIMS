package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;
/**
 * Responsible for "pure" dictionary values.
 * @author alexk
 *
 */
public class DictValuesDTO extends AllowValidation {
		//url of the dictionary
		private String url="";
		//all selected nodes
		private List<Long> selected = new ArrayList<Long>();
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public List<Long> getSelected() {
			return selected;
		}
		public void setSelected(List<Long> selected) {
			this.selected = selected;
		}
		@Override
		public String toString() {
			return "DictValuesDTO [url=" + url + ", selected=" + selected + ", getUrl()=" + getUrl()
					+ ", getSelected()=" + getSelected() + ", isValid()=" + isValid() + ", isStrict()=" + isStrict()
					+ ", getIdentifier()=" + getIdentifier() + ", getClass()=" + getClass() + ", hashCode()="
					+ hashCode() + ", toString()=" + super.toString() + "]";
		}
		
}
