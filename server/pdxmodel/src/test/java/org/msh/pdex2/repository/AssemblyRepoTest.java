package org.msh.pdex2.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.repository.r2.AssemblyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AssemblyRepoTest {
	@Autowired
	AssemblyRepo assmRepo;
	
	@Test
	public void testfindAllByByThingNodeUrlAndVarName() {
		List<Long> assmIds = assmRepo.findAllByByThingNodeUrlAndVarName("retail.site.owned.persons",
				"docs_tenancy");
		if(!assmIds.isEmpty()) {
			System.out.println(assmIds.get(0));
		}
	}

}
