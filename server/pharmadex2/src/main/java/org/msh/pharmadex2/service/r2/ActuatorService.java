package org.msh.pharmadex2.service.r2;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.msh.pharmadex2.dto.ActuatorAdmDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.cache.CachesEndpoint;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.MetricResponse;
import org.springframework.boot.actuate.metrics.MetricsEndpoint.Sample;
import org.springframework.boot.actuate.session.SessionsEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
	@Autowired
	private BeansEndpoint beansEndpoint;
	@Autowired
	private CachesEndpoint cachesEndpoint;
	@Autowired
	private EnvironmentEndpoint environmentEndpoint;
	@Autowired
	private InfoEndpoint infoEndpoint;
	@Autowired
	private MetricsEndpoint metricsEndpoint;
	
	@Autowired
	private SessionsEndpoint sessionsEndpoint;
	
	@Autowired
	private PathMappedEndpoints pathMappedEndpoints;

	public ActuatorAdmDTO loadData(ActuatorAdmDTO data) {
		data.getKeys().clear();
		data.getLiterals().clear();
		
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
			if(!ms.getStatistic().equals("COUNT")) {
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
			if(!ms.getStatistic().equals("COUNT")) {
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
}
