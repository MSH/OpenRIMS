package org.msh.pdex2.repository.common;

import org.msh.pdex2.model.old.Query;
import org.springframework.data.repository.CrudRepository;

public interface QueryRepository extends CrudRepository<Query, Long> {
	//Optional<Query> findByKey(String key);
}
