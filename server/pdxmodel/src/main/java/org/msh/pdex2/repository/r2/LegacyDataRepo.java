package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.LegacyData;
import org.springframework.data.repository.CrudRepository;

public interface LegacyDataRepo extends CrudRepository<LegacyData, Long> {

	List<LegacyData> findByRegisterAndUrl(String register, String url);

	Optional<LegacyData> findByConcept(Concept conc);

}
