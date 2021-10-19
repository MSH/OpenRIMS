package org.msh.pdex2.repository.r2;

import java.util.List;

import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepo extends CrudRepository<History, Long> {

	List<History> findAllByActivityOrderByCome(Concept activityNode);

	List<History> findAllByApplicationOrderByCome(Concept application);

	List<History> findByActivityData(Concept conc);

}
