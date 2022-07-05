package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.msh.pdex2.model.actuator.Metric;
import org.msh.pdex2.repository.metric.MetricRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.Sample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.micrometer.core.instrument.Statistic;

/**
 * The Spring Boot Actuator provides API to get necessary metrics. Metrics are long numbers.<br>
 * Spring Boot Actuator gathers metrics from Pharmadex 2 software start.<br>
 *  Metrics that should be used may be classified as:
<ul>
<li>1.	Counters, i.e. successful requests, errors, total time to process, etc.
<li>2.	Extrema, i.e. min and max values of something
</ul>
The metric collector service should gather metrics hourly and then store them to the database:
<ul>
<li>1.	The collection should start in a hour after Pharmadex 2 will start and repeats every hour
<li>2.	Counters to store should be calculated as current counter value minus previous counter value 
<li>3.	Extremum to store should be selected as extremum from the previous and the current
</ul>
 * @author alexk
 *
 */
@Service
public class MetricService {
	@Value("${app.buildTime}")
	private String buildTime;
	@Value("${app.release}")
	private String release;
	@Autowired
	private MetricsEndpoint metricsEndpoint;
	@Autowired
	private MetricRepo metricRepo;

	//stored values of the previous counters, initially zeros
	private AtomicLong q_200 = new AtomicLong(0);
	private AtomicLong q_404 = new AtomicLong(0);
	private AtomicLong q_500 = new AtomicLong(0);

	public AtomicLong getQ_200() {
		return q_200;
	}
	public void setQ_200(AtomicLong q_200) {
		this.q_200 = q_200;
	}
	public AtomicLong getQ_404() {
		return q_404;
	}
	public void setQ_404(AtomicLong q_404) {
		this.q_404 = q_404;
	}
	public AtomicLong getQ_500() {
		return q_500;
	}
	public void setQ_500(AtomicLong q_500) {
		this.q_500 = q_500;
	}

	/**
	 * Hourly scheduled service
	 */
	@Transactional
	public void collectMetrics() {
		String appl = "Pharmadex 2";
		String ver = "R"+release+"@"+buildTime;
		Metric metric = new Metric();
		metric.setApplication(appl);
		metric.setApplVersion(ver);
		metric.setCounter200(collectMetric("http.server.requests","status:200", 1, getQ_200()));
		metric.setCounter404(collectMetric("http.server.requests","status:404", 1, getQ_404()));
		metric.setCounter500(collectMetric("http.server.requests","status:500", 1, getQ_500()));
		metric.setHikariConnMax(collectMetric("hikaricp.connections.wait",null, 1,null));
		//TODO the rest
		metric=metricRepo.save(metric);
	}

	/**
	 * Counters to store should be calculated as current counter value minus previous counter value 
	 * Extremum should be stored “as is”. The unit of measurement should be aligned
	 * The previous counter value should be stored to the memory
	 * The previous metric value should not be stored to the memory
	 * @param metric counter metric, e.g. "status:200"
	 * @param tag what should be extracted from the metric
	 * @param multiplier - to  align unit of measurement (bytes, millisec, etc.)
	 * @return
	 */
	private long collectMetric(String metric, String tag,  double multiplier, AtomicLong previous) {
		long ret= 0l;
		ArrayList<String>tags = new ArrayList<String>();
		if(tag!=null) {
			tags.add(tag);
		}else {
			tags=null;
		}
		MetricResponse mr = metricsEndpoint.metric(metric, tags);
		if(mr!=null) {
			for(Sample sample :mr.getMeasurements()) {
				if(sample.getStatistic().equals(Statistic.COUNT)) {
					Double dret =sample.getValue() *multiplier;
					if(dret != null) {
						if(previous!=null) {								//counter
							ret=dret.longValue()-previous.get();
							previous.set(dret.longValue());
						}else {
							ret=dret.longValue();						//extremum
						}
					}
				}
			}
		}
		return ret;
	}

}
