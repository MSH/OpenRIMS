package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface AssemblyRepo extends CrudRepository<Assembly, Long> {
	Optional<Assembly> findByPropertyName(Concept var);

	List<Assembly> findAllByPropertyNameIn(List<Concept> vars, Sort sort);

}
