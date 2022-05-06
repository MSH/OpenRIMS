package org.msh.pharmadex2.service;

import org.junit.jupiter.api.Test;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.service.r2.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=Pharmadex2Application.class)
public class RegisterServiceTest {
	@Autowired
	RegisterService regServ;
	@Test
	public void nexNumber() {
		RegisterDTO dto = new RegisterDTO();
		dto.setUrl("register.test");
		dto = regServ.askNewNumber(dto);
		System.out.println(dto.getReg_number());
	}
	
}
