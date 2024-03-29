package org.msh.pdex2.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.model.r2.Closure;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.r2.ClosureRepo;
import org.msh.pdex2.repository.r2.ConceptRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ClosureRepoTest {
	@Autowired
	ClosureRepo closureRepo;
	@Autowired
	ConceptRepo conceptRepo;
	
	@Test
	public void conceptByIdentifierInBranchFast() {
		Concept root =conceptRepo.findByIdentifierIgnoreCase("who.atc.human").get(0);
		String identifier="R03CC04/fenoterol, 10mg, R";
		List<Long> concIDs = closureRepo.findInBranchActiveByConceptIdentifierFast(root.getID(), identifier);
		List<Closure> childs1 = closureRepo.findInBranchByConceptIdentifier(root, identifier);
		Optional<Concept> conco=conceptRepo.findById(concIDs.get(0));
		assertEquals(childs1.get(0).getChild(), conco.get());
	}
}
