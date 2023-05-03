package org.msh.pharmadex2.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.model.dwh.ReportSession;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.dwh.ReportSessionRepo;
import org.msh.pdex2.repository.metric.MetricTTRRepo;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.dto.metric.TimeToReplyDTO;
import org.msh.pharmadex2.service.r2.DWHService;
import org.msh.pharmadex2.service.r2.MetricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes=Pharmadex2Application.class)
public class DWHServiceTest {
	@Autowired
	DWHService dwhServ;
	@Autowired
	MetricService metServ;
	@Autowired
	private MetricTTRRepo mttrRepo;
	@Autowired
	private JdbcRepository jdbcRepo;
	
	//@Test
	public void update() throws SQLException {
		dwhServ.upload();
	}
	
	//@Test
	public void downloadMetricttr() {
		mttrRepo.deleteAll();
		TimeToReplyDTO rec = new TimeToReplyDTO();
		LocalDateTime localNow=LocalDateTime.now();
		LocalDateTime startDate=localNow.minusMinutes(43200);
		int qt=0;
		int dt=0;
		//43200
		for(int i=0; i<=43200; i++) {
			rec.setMinute(startDate.plusMinutes(i));
			//0-180=Quantity 
			
			double a = (double)(Math.random()* 181);
			int aa=(int)a;
			AtomicInteger quant=new AtomicInteger(aa);
			rec.setQuantity(quant);
			qt=qt+aa;
			//100-600=duration (generator); duration*Quantity=DurationMills
			double a1 = (double) Math.random()*701;
			int aa1=(int)a1;
			if(aa1>100) {
				aa1=aa1-100;
			}
			AtomicInteger durationMills=new AtomicInteger(aa*aa1);
			rec.setDurationMils(durationMills);
			if(aa*aa1!=0) {
			dt=dt+aa*aa1;
			}
			//1500-5000=MaxMills (generator)
			double a2 =  (double) (Math.random()*6501);
			int aa2=(int)a2;
			if(aa2>1500) {
				aa2=aa2-1500;
			}
			if(aa==0) {
				rec.setMaxMils(new AtomicInteger(0));
			}else {
				AtomicInteger maxMills=new AtomicInteger(aa2);
			rec.setMaxMils(maxMills); 
			}
			//QuantityTotal= QuantityTotal+Quantity(i)
			rec.setQuantityTotal(metServ.doubleToLong(Double.valueOf(qt), 1.0));
		
			//DurationTotal=DurationTotal+durationMills(i)
			rec.setDurationMilsTotal(metServ.doubleToLong(Double.valueOf(dt), 1.0));
			metServ.storeMetricTTR(rec);
		}
		
	}
	
	/**
	 * Read actual reportpages in enless loop. It should not allow DWH session close 
	 */
	//@Test
	public void deadLock() {
		while (true)	jdbcRepo.reporting_objects("who.atc.human", "LEGACY",null);
	}
	
}
