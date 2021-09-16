package org.msh.pdex2.repository.r2;

import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Scheduler;
import org.springframework.data.repository.CrudRepository;

public interface SchedulerRepo extends CrudRepository<Scheduler, Long> {

	Optional<Scheduler> findByConcept(Concept concept);

}
