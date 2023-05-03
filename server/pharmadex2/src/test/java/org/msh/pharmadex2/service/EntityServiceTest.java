package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.common.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test methods in the EntityService
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class EntityServiceTest {
	@Autowired
	EntityService entityServ;
	@Test
	public void testTableQtbRender() throws ObjectNotFoundException {
			Assembly assm = new Assembly();
			TableQtb table = new TableQtb();
			table.getHeaders().getHeaders().add(TableHeader.instanceOf("Row", TableHeader.COLUMN_LONG));
			TableRow row = TableRow.instanceOf(0);
			row.getRow().add(TableCell.instanceOf("Row", 12l, Locale.ENGLISH));
			table.getRows().add(row);
			assm=(Assembly) entityServ.renderFromQtbTable(table.getRows().get(0), assm);
			assertEquals(12l, assm.getRow());
	}
}
