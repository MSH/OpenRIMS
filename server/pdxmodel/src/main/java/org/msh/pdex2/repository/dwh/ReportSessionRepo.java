package org.msh.pdex2.repository.dwh;

import java.util.List;

import org.msh.pdex2.model.dwh.ReportSession;
import org.springframework.data.repository.CrudRepository;

public interface ReportSessionRepo extends CrudRepository<ReportSession, Long> {

	List<ReportSession> findAllByActual(boolean b);

}
