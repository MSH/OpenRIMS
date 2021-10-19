package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.ThingRegister;
import org.springframework.data.repository.CrudRepository;

public interface ThingRegisterRepo extends CrudRepository<ThingRegister, Long> {

	List<ThingRegister> findAllByConcept(Concept concept);

}
