package org.msh.pdex2.repository.r2;

import java.util.List;

import org.msh.pdex2.model.r2.Concept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;

public interface ConceptRepo extends CrudRepository<Concept, Long> {
	List<Concept> findByIdentifierIgnoreCase(String identifier);
	 @Query(nativeQuery = true,value = "call onlyRoot")
	List<Concept> findAllRoots();
	 
	 List<Concept> findByLabel(String label);
	List<Concept> findAllByIdentifierAndLabel(String identifier, String label);
	List<Concept> findAllByIdentifier(String identifier);
	List<Concept> findAllByIdentifierAndActive(String identifier, boolean b);
}
