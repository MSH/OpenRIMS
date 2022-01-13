package org.msh.pdex2.repository.r2;

import java.util.List;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.ThingDoc;
import org.springframework.data.repository.CrudRepository;

public interface ThingDocRepo extends CrudRepository<ThingDoc, Long> {
	List<ThingDoc> findByConcept(Concept concept);
	List<ThingDoc> findByDocUrl(String url);
}
