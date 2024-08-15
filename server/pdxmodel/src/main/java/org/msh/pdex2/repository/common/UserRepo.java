package org.msh.pdex2.repository.common;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.User;
import org.msh.pdex2.model.r2.UserDict;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Long> {

	Optional<User> findByUsername(String userName);
	Optional<User> findByEmail(String email);
	List<User> findByOrganization(User user);
	List<User> findAllByDictionaries(UserDict dicts);
}
