package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents all dictionaries defined in the system
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DictionariesDTO  extends AllowValidation{
		//select from here
		private TableQtb table = new TableQtb();
		private boolean editor = false;
		private long selectId = -1;
		private DictionaryDTO select = null;

		public TableQtb getTable() {
			return table;
		}

		public void setTable(TableQtb table) {
			this.table = table;
		}

		public boolean isEditor() {
			return editor;
		}

		public void setEditor(boolean editor) {
			this.editor = editor;
		}

		public long getSelectId() {
			return selectId;
		}

		public void setSelectId(long selectId) {
			this.selectId = selectId;
		}

		public DictionaryDTO getSelect() {
			return select;
		}

		public void setSelect(DictionaryDTO select) {
			this.select = select;
		}
}
