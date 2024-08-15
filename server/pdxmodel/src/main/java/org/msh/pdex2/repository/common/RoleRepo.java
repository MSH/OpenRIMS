package org.msh.pdex2.repository.common;

import java.util.Optional;

import org.msh.pdex2.model.old.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepo extends CrudRepository<Role, Integer> {
	Optional<Role> findByrolename(String roleName);
}
