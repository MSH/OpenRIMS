package org.msh.pdex2.repository.r2;

import java.util.List;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.ThingThing;
import org.springframework.data.repository.CrudRepository;

/**
 * Link Thing to Thing, e.g., Owner to Activity
 * @author alexk
 *
 */
public interface ThingThingRepo extends CrudRepository<ThingThing,Long>{
	List<ThingThing> findByConcept(Concept concept);
}
