package org.msh.pdex2.repository.r2;

import java.util.Optional;

import org.msh.pdex2.model.r2.PasswordsTemporary;
import org.springframework.data.repository.CrudRepository;

public interface PasswordTempoRepo extends CrudRepository<PasswordsTemporary, Long> {

	Optional<PasswordsTemporary> findByUseremail(String email);

}
