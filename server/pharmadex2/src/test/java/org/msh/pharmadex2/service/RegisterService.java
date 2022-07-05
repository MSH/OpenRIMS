package org.msh.pharmadex2.service;

import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=Pharmadex2Application.class)
public class RegisterService {
	@Autowired
	RegisterServiceTest regServ;
	public void nexNumber() {
		RegisterDTO dto = new RegisterDTO();
		dto.setUrl("register.test");
		//String regNum = regServ.
	}
	
}
