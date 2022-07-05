package org.msh.pharmadex2.controller.r2;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
/**
 * Contains scheduled background calls
 * @author alexk
 *
 */
@Controller
public class Scheduler {
	/**
	 * Store metrics to the database
	 */
	@Scheduled(fixedRate=60*60*1000)
	public void metrics() {
		
	}
}
