package org.msh.pdex2.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * Database related tests
 * @author alexk
 *
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class JdbcRepoTest {
	@Autowired
	JdbcRepository jdbcRepo;
	
	@Test
	public void getMetaData() {
		List<String> excl = new ArrayList<String>();
		excl.add("conceptID");
		excl.add("Discriminator");
		 List<TableHeader> headers =jdbcRepo.headersFromSelect("select * from assembly where false", excl);
		 for(TableHeader header : headers) {
			 System.out.println(header);
		 }
	}
	
	@Test
	public void catchSqlExceptions() {
		System.out.println("below will be a stack trace");
		String select="select * from not_existing_table";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", "", new Headers());
		System.out.println("above should be red execption");
		assertTrue(rows.isEmpty());
	}
}
