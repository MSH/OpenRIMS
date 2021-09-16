package org.msh.pdex2.repository.r2;

import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.ThingDoc;
import org.springframework.data.repository.CrudRepository;

public interface ThingDocRepo extends CrudRepository<ThingDoc, Long> {
	Optional<ThingDoc> findByConcept(Concept concept);
}
