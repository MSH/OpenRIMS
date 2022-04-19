package org.msh.pdex2.model.old;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.model.i18n.ResourceBundle;
import org.msh.pdex2.repository.common.UserRepo;
import org.msh.pdex2.repository.i18n.ResourceBundleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RepoModelTests {
	@Autowired
	UserRepo userRepo;
	@Autowired
	ResourceBundleRepo bundleRepo;

	@Test
	void loadUser(){
		Iterable<User> users = userRepo.findAll();
		assertTrue(users.iterator().hasNext());
	}
	@Test
	public void loadBundle() {
		List<ResourceBundle> bundles = bundleRepo.findAllByOrderBySortOrder();
		assertEquals(2, bundles.size());
		System.out.println(bundles.get(0).getMessages().size());
	}
	
}
