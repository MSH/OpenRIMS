package org.msh.pdex2.repository.r2;

import org.msh.pdex2.model.r2.EventLog;
import org.springframework.data.repository.CrudRepository;

public interface EventLogRepo extends CrudRepository<EventLog, Long> {

}
