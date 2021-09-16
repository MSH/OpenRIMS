package org.msh.pdex2.repository.r2;

import java.util.Optional;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.springframework.data.repository.CrudRepository;

public interface FileResourceRepo extends CrudRepository<FileResource, Long> {

	Optional<FileResource> findByConcept(Concept concept);
}
