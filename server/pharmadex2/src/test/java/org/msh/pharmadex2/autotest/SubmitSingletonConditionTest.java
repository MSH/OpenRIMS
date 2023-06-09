package org.msh.pharmadex2.autotest;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.r2.AutoTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Is an application submit allowed?
 * Check the running processes 
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class)
public class SubmitSingletonConditionTest {
	@Autowired
	private AutoTestingService autoTest;
	
	@Test
	/**
	 * Rule 1. For any particular permit, a new application should be forbidden, 
	 * if modification or deregistration or revocation is processing for the permit
	 * @throws ObjectNotFoundException
	 */
	public void rule1() throws ObjectNotFoundException {
		boolean ret = autoTest.singletonConditionRule1();
	}
}
