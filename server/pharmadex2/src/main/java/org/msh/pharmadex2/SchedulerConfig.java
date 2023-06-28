package org.msh.pharmadex2;

import java.sql.SQLException;

import org.msh.pharmadex2.service.r2.DWHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
/**
 * Run scheduled processes
 * @author alexk
 *
 */
public class SchedulerConfig {
	
	@Autowired
	private DWHService dwhService;
	
/**
 * Schedule of DWH update by default daily at 1:00AM
 */
	@Scheduled(cron = "${data.renew.schedule:0 0 1 * * *}")
	private void runDWHupload() {
		try {
			dwhService.upload();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
