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
/* отключить планирование во время выполнения тестов. "scheduler.enabled" включить его по умолчанию. 
 * Для этого устанавливаем значение matchIfMissing в котором true означает, 
 * что мы не должны установить это свойство для того, чтобы планировать, 
 * но должны установить это свойство явно отключить планировщик.*/
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SchedulerConfig {
	
	@Autowired
	private DWHService dwhService;
	
	/** запускаем через 15 минут после старта приложения
	 * далее каждіе 2 часа*/
	@Scheduled(initialDelay=15*60*1000, fixedRate = 24*60*60*1000)
	//@Scheduled(initialDelay=5*60*1000, fixedRate=10*60*1000)
	private void runDWHupload() {
		try {
			dwhService.upload();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
