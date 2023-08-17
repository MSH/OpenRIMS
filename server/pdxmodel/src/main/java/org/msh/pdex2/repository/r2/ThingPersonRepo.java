package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.ThingPerson;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ThingPersonRepo extends CrudRepository<ThingPerson, Long> {

	List<ThingPerson> findByConcept(Concept concept);
	/**
	 * Get a permit page on which persons component can be found
	 * @param personsMainPage main page of the persons data
	 * @return page ID in the permit or !present if the param is not persons main page
	 */
	@Query(
			value = "select t.conceptID\r\n" + 
					"from concept c\r\n" + 
					"join thingperson tp on tp.conceptID=c.ID\r\n" + 
					"join thing t on t.ID=tp.thingID\r\n" + 
					"where c.ID=:personsMainPage \r\n" + 
					"LIMIT 1",
			nativeQuery = true
			)
	Optional<Long> permitPageByPersonsMainPage(@Param("personsMainPage")Long personsMainPage);
	
	
	//**************************************** FOR JUNIT TESTING *******************************************************//
	
	/**
	 * Get any main "persons" page in the permit data with the main page given
	 * @param mainPageID
	 * @return
	 */
	@Query(
			value = "select tp.conceptID\r\n" + 
					"from concept c\r\n" + 
					"join thing t on t.conceptID=c.ID\r\n" + 
					"join thingthing tt on tt.thingID=t.ID\r\n" + 
					"join thing t1 on t1.conceptID=tt.conceptID\r\n" + 
					"join thingperson tp on tp.thingID=t1.ID\r\n" + 
					"where c.ID=:mainPageID \r\n" + 
					"LIMIT 1",
			nativeQuery = true
			)
	Long testMainPersonsPage(@Param("mainPageID")Long mainPageID);


}
