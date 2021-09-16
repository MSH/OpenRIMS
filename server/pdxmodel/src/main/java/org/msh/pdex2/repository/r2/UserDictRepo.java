package org.msh.pdex2.repository.r2;

import java.util.List;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.UserDict;
import org.springframework.data.repository.CrudRepository;

public interface UserDictRepo extends CrudRepository<UserDict, Long> {
	/**
	 * Find by node in hierarchy
	 * @param concept
	 * @return
	 */
	List<UserDict> findAllByConceptAndUrl(Concept concept, String url);

}
