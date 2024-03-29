package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.UserAccessDTO;
import org.msh.pharmadex2.service.common.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest(classes=Pharmadex2Application.class)
public class UserServiceTests {
	@Autowired
	UserService userServ; 
	
	@Test
	public void testRolesFromDict() throws ObjectNotFoundException {
		UserDetails ud = userServ.loadUserByUsername("admin");
		assertNotNull(ud);
	}
	
	@Test
	public void testNraUserAccess() throws ObjectNotFoundException {
		UserAccessDTO dto = userServ.nraUserAccess("alex.kurasoff@gmail.com");
		assertTrue(dto.isMainOffice());
		dto = userServ.nraUserAccess("Biratnagar.all@gmail.com");
		assertFalse(dto.isMainOffice());
	}
}
