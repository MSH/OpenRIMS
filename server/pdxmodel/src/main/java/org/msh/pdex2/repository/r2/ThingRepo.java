package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDoc;
import org.msh.pdex2.model.r2.ThingPerson;
import org.msh.pdex2.model.r2.ThingThing;
import org.springframework.data.repository.CrudRepository;

public interface ThingRepo extends CrudRepository<Thing, Long> {
	List<Thing> findByConcept(Concept concept);								//some strange may be 

	List<Thing> findByThings(ThingThing thingThing);

	Optional<Thing> findByDocuments(ThingDoc td);

	List<Thing> findByPersons(ThingPerson tp);



}
