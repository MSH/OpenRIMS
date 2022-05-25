package org.msh.pdex2.repository.r2;

import java.util.ArrayList;
import java.util.List;

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
	
	@Query(value="select clo from Closure clo inner join clo.child child where clo.level=1 and child.identifier=:identifier and clo.parent=:parent")
	List<Closure> findInBranchByConceptIdentifier(@Param("parent") Concept parent, @Param("identifier") String identifier);
	

}
