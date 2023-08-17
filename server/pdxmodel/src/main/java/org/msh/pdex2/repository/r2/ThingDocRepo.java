package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.ThingDoc;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ThingDocRepo extends CrudRepository<ThingDoc, Long> {
	List<ThingDoc> findByConcept(Concept concept);
	List<ThingDoc> findByDocUrl(String url);
	/**
	 * Load thing node ID by file concept ID
	 * First usage is for access control - read access
	 * @param fileConceptID
	 * @return
	 */
	@Query(
			value = "SELECT t.conceptID\r\n" + 
					"FROM thingdoc td\r\n" + 
					"join  thing t on t.ID=td.thingID\r\n" + 
					"where td.conceptID=:fileConceptID",
			nativeQuery = true
			)
	Optional<Long> findThingNodeByFileConcept(@Param("fileConceptID") Long fileConceptID );
}
