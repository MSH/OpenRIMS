	package org.msh.pdex2.repository.r2;

	import java.util.Optional;

	import org.msh.pdex2.model.r2.Concept;
	import org.msh.pdex2.model.r2.PublicOrganization;
	import org.springframework.data.repository.CrudRepository;

	public interface PubOrgRepo extends CrudRepository<PublicOrganization, Long> {
		/**
		 * Find by node in hierarchy
		 * @param concept
		 * @return
		 */
		Optional<PublicOrganization> findByConcept(Concept concept);

	}

