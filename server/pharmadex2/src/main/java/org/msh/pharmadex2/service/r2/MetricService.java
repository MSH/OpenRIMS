package org.msh.pharmadex2.service.r2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.msh.pdex2.model.actuator.MetricTTR;
import org.msh.pdex2.repository.metric.MetricTTRRepo;
import org.msh.pharmadex2.dto.metric.TimeToReplyDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.Sample;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.core.instrument.Statistic;

/**
 * The Spring Boot Actuator provides Actuator Endpoint API to get necessary metrics. 
 * @author alexk
 *
 */
@Service
public class MetricService {
	private static final Logger logger = LoggerFactory.getLogger(MetricService.class);
	@Autowired
	private MetricsEndpoint metricsEndpoint;
	@Autowired
	private MetricTTRRepo mttrRepo;
	@Autowired
	private BoilerService boilerServ;

	private TimeToReplyDTO prevTTR = new TimeToReplyDTO();  

	public TimeToReplyDTO getPrevTTR() {
		return prevTTR;
	}
	public void setPrevTTR(TimeToReplyDTO prevTTR) {
		this.prevTTR = prevTTR;
	}
	/**
	 * Collect time to reply every minute
	 */
	//@Scheduled(fixedRate = 1000*60)
	public void collectMetricTTR() {
		TimeToReplyDTO rec = new TimeToReplyDTO();
		rec.setMinute(LocalDateTime.now());
		List<String> tags = new ArrayList<String>();
		tags.add("method:POST");
		MetricResponse mResp = metricsEndpoint.metric("http.server.requests", tags);
		if(mResp!=null) {
			double mult=1;
			if(mResp.getBaseUnit().equalsIgnoreCase("SECONDS")) {
				mult=1000;
			}
			for(Sample sample :mResp.getMeasurements()) {
				if(sample.getStatistic().equals(Statistic.COUNT)){
					rec.setQuantityTotal(doubleToLong(sample.getValue(), 1.0));
				}
				if(sample.getStatistic().equals(Statistic.TOTAL_TIME)){
					rec.setDurationMilsTotal(doubleToLong(sample.getValue(), mult));
				}
				if(sample.getStatistic().equals(Statistic.MAX)){
					rec.setMaxMils(doubleToInt(sample.getValue(), mult));
				}
			}
			Long durDiff=rec.getDurationMilsTotal().get()-getPrevTTR().getDurationMilsTotal().get();
			Long quantDiff=rec.getQuantityTotal().get()-getPrevTTR().getQuantityTotal().get();
			rec.setDurationMils(new AtomicInteger(durDiff.intValue()));
			rec.setQuantity(new AtomicInteger(quantDiff.intValue()));
			if(rec.getQuantity().get()==0) {
				rec.setMaxMils(new AtomicInteger(0));
			}
		}
		logger.trace(rec.toString());
		storeMetricTTR(rec);
		setPrevTTR(rec);
	}
	@Transactional
	public void storeMetricTTR(TimeToReplyDTO rec) {
		MetricTTR mttr = new MetricTTR();
		mttr.setMinute(boilerServ.localDateTimeToDate(rec.getMinute()));
		mttr.setQuantity(rec.getQuantity().get());
		mttr.setDurationMills(rec.getDurationMils().get());
		mttr.setMaxMills(rec.getMaxMils().get());
		mttr.setQuantityTotal(rec.getQuantityTotal().get());
		mttr.setDurationTotal(rec.getDurationMilsTotal().get());
		mttr=mttrRepo.save(mttr);
	}
	/**
	 * Multiply double, and, then convert the result to integer 
	 * @param value
	 * @param mult
	 * @return
	 */
	private AtomicInteger doubleToInt(Double value, double mult) {
		Double dm = value * mult;
		return new AtomicInteger(dm.intValue());
	}
	/**
	 * Multiply double, and, then convert the result to integer 
	 * @param value
	 * @param mult
	 * @return
	 */
	public AtomicLong doubleToLong(Double value, double mult) {
		Double dm = value * mult;
		return new AtomicLong(dm.longValue());
	}
}
