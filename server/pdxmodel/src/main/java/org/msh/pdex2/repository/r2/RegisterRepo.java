package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Register;
import org.springframework.data.repository.CrudRepository;

public interface RegisterRepo extends CrudRepository<Register, Long> {

	Optional<Register> findByConcept(Concept concept);

	List<Register> findAllByRegister(String trim);

	List<Register> findAllByConceptAndRegister(Concept conc, String trim);


}
