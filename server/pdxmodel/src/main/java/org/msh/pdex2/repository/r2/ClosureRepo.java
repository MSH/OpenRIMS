package org.msh.pdex2.repository.r2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.msh.pdex2.model.r2.Closure;
import org.msh.pdex2.model.r2.Concept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ClosureRepo extends CrudRepository<Closure, Long> {
	/**
	 * Typically using to find parent of parent include parent itself
	 * @param closure
	 * @return
	 */
	List<Closure> findByChild(Concept parent);
	List<Closure> findByChildOrderByLevelAsc(Concept parent);
	/**
	 * Find all children of the parent that belongs to the level given 
	 * @param parent
	 * @param level
	 * @return
	 */
	List<Closure> findByParentAndLevel(Concept parent, int level);
	List<Closure> findByParent(Concept parent);
	List<Closure> findByChildOrderByLevelDesc(Concept node);
	@Procedure
	void moveSubTree(long rootNodeID, long newParentId);

	@Query(value="call dictvariables(?1,?2)", nativeQuery = true)
	List<String> dictvariables(long rootid, String varname);
	/**
	 * Seems as slow and should be deprecated
	 * @param parent
	 * @param identifier
	 * @return
	 */
	@Query(value="select clo from Closure clo inner join clo.child child where clo.level=1 and child.identifier=:identifier and clo.parent=:parent")
	List<Closure> findInBranchByConceptIdentifier(@Param("parent") Concept parent, @Param("identifier") String identifier);
	
	/**
	 * The fast release of above, selects only IDs of concepts found
	 * @param root
	 * @param identifier
	 * @return
	 */
	//@Query(value="select clo from Closure clo where clo.level=1 and clo.child.active=true and clo.child.identifier=:identifier and clo.parent=:parent")
	@Query(value="select conc.ID\r\n" + 
			"from closure clo\r\n" + 
			"join concept conc on conc.ID=clo.childID\r\n" + 
			"where  conc.Active\r\n" + 
			"and clo.`Level`=1\r\n" + 
			"and clo.parentID=:parentID\r\n" + 
			"and conc.Identifier=:identifier",
			nativeQuery = true)
	List<Long> findInBranchActiveByConceptIdentifierFast(@Param("parentID") Long parentID, @Param("identifier") String identifier);
	
	/**
	 * Get all identifiers of active nodes under the parent concept
	 * @param identifier
	 * @return
	 */
	@Query( value="select distinct node.Identifier\r\n" + 
			"from concept node\r\n" + 
			"join closure clo on clo.childID=node.ID and clo.`Level`=1\r\n" + 
			"join concept parent on parent.ID=clo.parentID\r\n" + 
			"where\r\n" + 
			"node.Active\r\n" + 
			"and parent.identifier=:identifier",
			nativeQuery = true)
	Set<String> findAllIdentifiersUnderParent(@Param("identifier")String identifier);
	

}
