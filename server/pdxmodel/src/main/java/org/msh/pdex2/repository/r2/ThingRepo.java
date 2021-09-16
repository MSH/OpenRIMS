package org.msh.pdex2.repository.r2;

import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.springframework.data.repository.CrudRepository;

public interface ThingRepo extends CrudRepository<Thing, Long> {
	Optional<Thing> findByConcept(Concept concept);

}
