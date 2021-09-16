package org.msh.pdex2.repository.r2;

import java.util.List;

import org.msh.pdex2.model.r2.Checklistr2;
import org.msh.pdex2.model.r2.Concept;
import org.springframework.data.repository.CrudRepository;

public interface Checklistr2Repo extends CrudRepository<Checklistr2, Long> {
	List<Checklistr2> findAllByActivity(Concept activity);
}
