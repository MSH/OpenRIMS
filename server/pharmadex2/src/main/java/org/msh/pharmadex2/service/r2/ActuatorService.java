package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.model.old.Workspace;
import org.msh.pdex2.repository.common.WorkspaceRepo;
import org.msh.pharmadex2.dto.ActuatorAdmDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.Sample;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Statistic;
/**
 * Сервис работает с дополнение Spring Boot Actuator
 * 
 * @author khomenskaya
 *
 */
@Service
public class ActuatorService {
	@Autowired
	private HealthEndpoint healthEndpoint;
	//@Autowired
	//private BeansEndpoint beansEndpoint;
	//@Autowired
	//private CachesEndpoint cachesEndpoint;
	//@Autowired
	//private EnvironmentEndpoint environmentEndpoint;
	@Autowired
	private InfoEndpoint infoEndpoint;
	@Autowired
	private MetricsEndpoint metricsEndpoint;
	
	@Autowired
	private WorkspaceRepo workspaceRepo;
	
	@Value( "${link.report.datastudio.average:}" )
	private String linkReportAverage;

	//@Autowired
	//private SessionsEndpoint sessionsEndpoint;

	//@Autowired
	//private PathMappedEndpoints pathMappedEndpoints;

	public ActuatorAdmDTO loadData(ActuatorAdmDTO data) {
		data.getKeys().clear();
		data.getLiterals().clear();

		loadSlaParams(data);
		
		Map<String, Object> map = infoEndpoint.info();
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String k = it.next();
			Object obj = map.get(k);
			if(obj instanceof Map) {
				LinkedHashMap<String, String> mapVls = (LinkedHashMap<String, String>)obj;
				Iterator<String> itM = mapVls.keySet().iterator();
				while(itM.hasNext()) {
					String kM = itM.next();
					data.getKeys().add(kM);
					data.getLiterals().put(kM, new FormFieldDTO<String>(mapVls.get(kM)));
				}
			}

		}

		Health h = (Health) healthEndpoint.healthForPath("db");
		map = h.getDetails();
		it = map.keySet().iterator();
		while(it.hasNext()) {
			String k = it.next();
			if(k.equals("database")) {
				Object obj = map.get(k);
				if(obj instanceof String) {
					String v = (String)obj;
					data.getKeys().add(k);
					data.getLiterals().put(k, new FormFieldDTO<String>(v));
				}
			}
		}

		h = (Health) healthEndpoint.healthForPath("diskSpace");
		map = h.getDetails();
		it = map.keySet().iterator();
		while(it.hasNext()) {
			String k = it.next();
			Object obj = map.get(k);
			if(obj instanceof String) {
				String v = (String)obj;
				data.getKeys().add(k + " diskSpace");
				data.getLiterals().put(k + " diskSpace", new FormFieldDTO<String>(v));
			}
			if(obj instanceof Long) {
				data.getKeys().add(k + " diskSpace");
				data.getLiterals().put(k + " diskSpace", new FormFieldDTO<String>(convertToMb(obj)));
			}

		}

		MetricResponse mr = metricsEndpoint.metric("hikaricp.connections", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(String.valueOf(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("hikaricp.connections.active", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(String.valueOf(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("hikaricp.connections.acquire", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(String.valueOf(mr.getMeasurements().get(0).getValue()) + " seconds"));

		mr = metricsEndpoint.metric("hikaricp.connections.creation", null);
		for(int i = 0; i < mr.getMeasurements().size(); i++) {
			Sample ms = mr.getMeasurements().get(i);
			String k = mr.getDescription() + " " + ms.getStatistic();

			data.getKeys().add(k);
			if(!ms.getStatistic().equals(Statistic.COUNT)) {
				data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(ms.getValue()) + " seconds"));
			}else
				data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(ms.getValue())));
		}

		mr = metricsEndpoint.metric("hikaricp.connections.idle", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(String.valueOf(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("hikaricp.connections.usage", null);
		for(int i = 0; i < mr.getMeasurements().size(); i++) {
			Sample ms = mr.getMeasurements().get(i);
			String k = mr.getDescription() + " " + ms.getStatistic();

			data.getKeys().add(k);
			if(!ms.getStatistic().equals(Statistic.COUNT)) {
				data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(ms.getValue()) + " seconds"));
			}else
				data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(ms.getValue())));
		}

		mr = metricsEndpoint.metric("system.cpu.count", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(String.valueOf(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("system.cpu.usage", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(String.valueOf(mr.getMeasurements().get(0).getValue())));


		mr = metricsEndpoint.metric("jvm.buffer.memory.used", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(convertToMb(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("jvm.gc.memory.allocated", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(convertToMb(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("jvm.gc.memory.promoted", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(convertToMb(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("jvm.memory.committed", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(convertToMb(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("jvm.memory.max", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(convertToMb(mr.getMeasurements().get(0).getValue())));

		mr = metricsEndpoint.metric("jvm.memory.used", null);
		data.getKeys().add(mr.getDescription());
		data.getLiterals().put(mr.getDescription(), new FormFieldDTO<String>(convertToMb(mr.getMeasurements().get(0).getValue())));

		List<String> tags = new ArrayList<String>();
		tags.add("method:POST");
		mr = metricsEndpoint.metric("http.server.requests", tags);//http://localhost:3001/actuator/metrics/http.server.requests?tag=method:POST
		String key = "http requests execution time (all POST) ";
		for(int i = 0; i < mr.getMeasurements().size(); i++) {
			Sample ms = mr.getMeasurements().get(i);
			String k = key + " " + ms.getStatistic();

			data.getKeys().add(k);
			if(!ms.getStatistic().equals(Statistic.COUNT)) {
				data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(Math.round(ms.getValue())) + " seconds"));
			}else
				data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(Math.round(ms.getValue()))));
		}

		tags = new ArrayList<String>();
		tags.add("status:200");
		mr = metricsEndpoint.metric("http.server.requests", tags);
		key = "http requests with 200 ";
		String k = key + " " + Statistic.COUNT;
		if(mr != null) {
			for(int i = 0; i < mr.getMeasurements().size(); i++) {
				Sample ms = mr.getMeasurements().get(i);
				if(ms.getStatistic().equals(Statistic.COUNT)) {
					data.getKeys().add(k);
					data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(Math.round(ms.getValue()))));
				}
			}
		}else {
			data.getKeys().add(k);
			data.getLiterals().put(k, new FormFieldDTO<String>("-"));
		}

		tags = new ArrayList<String>();
		tags.add("status:404");
		mr = metricsEndpoint.metric("http.server.requests", tags);
		key = "http requests with 404 ";
		k = key + " " + Statistic.COUNT;
		if(mr != null) {
			for(int i = 0; i < mr.getMeasurements().size(); i++) {
				Sample ms = mr.getMeasurements().get(i);
				if(ms.getStatistic().equals(Statistic.COUNT)) {
					data.getKeys().add(k);
					data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(Math.round(ms.getValue()))));
				}
			}
		}else {
			data.getKeys().add(k);
			data.getLiterals().put(k, new FormFieldDTO<String>("-"));
		}


		tags = new ArrayList<String>();
		tags.add("status:302");
		mr = metricsEndpoint.metric("http.server.requests", tags);
		key = "http requests with 302 ";
		k = key + " " + Statistic.COUNT;
		if(mr != null) {
			for(int i = 0; i < mr.getMeasurements().size(); i++) {
				Sample ms = mr.getMeasurements().get(i);
				if(ms.getStatistic().equals(Statistic.COUNT)) {
					data.getKeys().add(k);
					data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(Math.round(ms.getValue()))));
				}
			}
		}else {
			data.getKeys().add(k);
			data.getLiterals().put(k, new FormFieldDTO<String>("-"));
		}

		tags = new ArrayList<String>();
		tags.add("status:500");
		mr = metricsEndpoint.metric("http.server.requests", tags);
		key = "http requests with 500 ";
		k = key + " " + Statistic.COUNT;
		if(mr != null) {
			for(int i = 0; i < mr.getMeasurements().size(); i++) {
				Sample ms = mr.getMeasurements().get(i);
				if(ms.getStatistic().equals(Statistic.COUNT)) {
					data.getKeys().add(k);
					data.getLiterals().put(k, new FormFieldDTO<String>(String.valueOf(Math.round(ms.getValue()))));
				}
			}
		}else {
			data.getKeys().add(k);
			data.getLiterals().put(k, new FormFieldDTO<String>("-"));
		}

		return data;
	}

	private String convertToMb(Object val) {
		String res = "";
		if(val instanceof Long) {
			Long v = (Long)val;
			long l = v/1048576;

			res = String.valueOf(l) + " Mb";
		}else if(val instanceof Double) {
			Double d = (Double)val;
			double l = d/1048576;

			res = String.valueOf(Math.round(l)) + " Mb";
		}
		return res;
	}
	
	private void loadSlaParams(ActuatorAdmDTO data) {
		data.getKeysSLA().clear();
		data.getSla().clear();
		
		String link = linkReportAverage.replaceAll("\"", "");
		data.setLinkReport(link);
		
		Iterable<Workspace> collection = workspaceRepo.findAll();
		if(collection != null) {
			Workspace w = null;
			if(collection.iterator().hasNext()) {
				w = collection.iterator().next();
			}
			
			if(w != null) {
				String k = "slaquantity";
				data.getKeysSLA().add(k);
				data.getSla().put(k, new FormFieldDTO<Integer>(w.getSlaQuantity()));
				
				k = "sladuration";
				data.getKeysSLA().add(k);
				data.getSla().put(k, new FormFieldDTO<Integer>(w.getSlaDuration()));
				
				k = "slamax";
				data.getKeysSLA().add(k);
				data.getSla().put(k, new FormFieldDTO<Integer>(w.getSlaMax()));
			}
		}
	}
}
