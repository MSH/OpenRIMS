package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.ThingThing;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Link Thing to Thing, e.g., Owner to Activity
 * @author alexk
 *
 */
public interface ThingThingRepo extends CrudRepository<ThingThing,Long>{
	List<ThingThing> findByConcept(Concept concept);
	/**
	 * Find main data module page by the any page
	 * @param anyPageID
	 * @return main data module page ID or not present if concept under anyPageID is not thing node
	 */
	@Query(
			value = "select ifnull(t1.conceptID,:anyPageID) as mainPageId\r\n" + 
					"from concept c\r\n" + 
					"join thing t on t.conceptID=c.ID\r\n" + 
					"left join thingthing tt on tt.conceptID=c.ID\r\n" + 
					"left join thing t1 on t1.ID=tt.thingID\r\n" + 
					"where c.ID=:anyPageID",
			nativeQuery = true
			)
	Optional<Long> dataModulePageId(@Param("anyPageID") Long anyPageID);
	
	//********** for JUNIT testing purpose *********************************************
	@Query(
			value = "select c.ID from concept c\r\n" + 
					"left join thing t on t.conceptID=c.ID\r\n" + 
					"where t.ID is null\r\n" + 
					"LIMIT 1",
			nativeQuery = true
			)
	Long testNotPageID();
	/**
	 * Main thing with many pages
	 * One of the page is connected to thingperson
	 * @return
	 */
	@Query(
			value = "select mainPage.ID\r\n" + 
					"from concept mainPage\r\n" + 
					"join thing t on t.conceptID=mainPage.ID\r\n" + 
					"join thingthing tt on tt.thingID=t.ID\r\n" + 
					"join thing t1 on t1.ID=tt.thingID\r\n" + 
					"join thingperson tp on tp.thingID=t1.ID\r\n" + 
					"left join thingthing tt1 on tt.conceptID=mainPage.ID\r\n" + 
					"where tt1.ID is null\r\n" + 
					"LIMIT 1",
			nativeQuery = true
			)
	Long testMainPage();
	@Query(
			value = "select tt.conceptID\r\n" + 
					"from concept mainPage\r\n" + 
					"join thing t on t.conceptID=mainPage.ID\r\n" + 
					"join thingthing tt on tt.thingID=t.ID\r\n" + 
					"where mainPage.ID=:mainPageID  \r\n" + 
					"LIMIT 1",
			nativeQuery = true
			)
	Long testOtherPage(@Param("mainPageID")Long mainPageID);
	
}
