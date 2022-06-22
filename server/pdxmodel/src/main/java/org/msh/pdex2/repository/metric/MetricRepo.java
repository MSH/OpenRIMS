package org.msh.pdex2.repository.metric;

import org.msh.pdex2.model.actuator.Metric;
import org.springframework.data.repository.CrudRepository;

public interface MetricRepo extends CrudRepository<Metric, Long> {

}
