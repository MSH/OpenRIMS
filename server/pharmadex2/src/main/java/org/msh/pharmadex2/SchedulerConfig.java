package org.msh.pharmadex2;

import java.util.Date;

import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.service.r2.AsyncService;
import org.msh.pharmadex2.service.r2.DWHService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
/**
 * Run scheduled processes
 * For current - only DWH Update
 * @author alexk
 *
 */
public class SchedulerConfig {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
	@Autowired
	private AsyncService asyncService;
	@Autowired
	private Messages messages;
	
/**
 * Schedule of DWH update by default daily at 1:00AM
 */
	@Scheduled(cron = "${data.renew.schedule:0 0 1 * * *}")
	public void runDWHupload() {
			if(!AsyncService.hasDataImportThread()) {
				asyncService.dwhUploadRun();
			}else {
				logger.info(messages.get("rescheduleddwnupdate"));
				reschedulerDWH().schedule(new Runnable() {
					@Override
					public void run() {
						runDWHupload();
					}
					
				}, new Date(System.currentTimeMillis()+1000*60*10));	//schedule in a ten minutes
			}
	}
	/**
	 * RE-scheduler
	 * @return
	 */
	@Bean("reschedulerDWH")
    public ThreadPoolTaskScheduler reschedulerDWH(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
          = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix(
          "reschedulerDWH");
        return threadPoolTaskScheduler;
    }

}
