package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.LayoutCellDTO;
import org.msh.pharmadex2.dto.LayoutRowDTO;

/**
 * Simple utility to create a layout
 * @author alexk
 *
 */
public class LayoutCalculator {
	private List<LayoutRowDTO> rows = new ArrayList<LayoutRowDTO>();
	private int rowNo=-1;
	private int colNo=-1;
	public List<LayoutRowDTO> getRows() {
		return rows;
	}

	public void setRows(List<LayoutRowDTO> rows) {
		this.rows = rows;
	}
	/**
	 * WE supposed that assemblies are sorted row-col-ord
	 * @param assm
	 */
	public void add(int row, int col, String varName) {
		if(row!=rowNo) {
			LayoutRowDTO rowDto = new LayoutRowDTO();
			getRows().add(rowDto);
			rowNo=row;
			colNo=-1;
		}
		int rowIndex= getRows().size()-1;
		if(col!=colNo) {
			LayoutCellDTO cell = new LayoutCellDTO();
			getRows().get(rowIndex).getCells().add(cell);
			colNo=col;
		}
		int cellIndex =  getRows().get(rowIndex).getCells().size()-1;
		//add variable
		getRows().get(rowIndex).getCells().get(cellIndex).getVariables().add(varName);	
	}

	
}
