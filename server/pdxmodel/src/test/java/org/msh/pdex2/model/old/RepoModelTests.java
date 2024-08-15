package org.msh.pdex2.model.old;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.junit.jupiter.api.Test;
import org.msh.pdex2.model.i18n.ResourceBundle;
import org.msh.pdex2.model.r2.User;
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
	@PersistenceContext
	EntityManager entityManager;

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
	@Test
	public void getAllTables() {
		MetamodelImplementor metaModelImpl = (MetamodelImplementor)entityManager.getMetamodel();
		Map<String, EntityPersister> entityPersisters = metaModelImpl.entityPersisters();
		Collection<EntityPersister> val = entityPersisters.values();               
		List<String> tables = new ArrayList<String>();
		for (EntityPersister ep : val) {
		        AbstractEntityPersister aep = (AbstractEntityPersister)ep;
		        tables.add("'"+aep.getTableName().replaceAll("`", "")+"',");
		 }
		Collections.sort(tables);
		for(String s : tables) {
			System.out.println(s);
		}
	}
	
}
