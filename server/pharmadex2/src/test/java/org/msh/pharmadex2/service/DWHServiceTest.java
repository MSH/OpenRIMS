package org.msh.pharmadex2.service;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.r2.DWHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=Pharmadex2Application.class)
public class DWHServiceTest {
	@Autowired
	DWHService dwhServ;
	
	@Test
	public void update() throws SQLException {
		dwhServ.upload();
	}
	
}
