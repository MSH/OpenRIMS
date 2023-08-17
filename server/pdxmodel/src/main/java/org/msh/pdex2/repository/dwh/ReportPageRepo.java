package org.msh.pdex2.repository.dwh;

import java.util.List;

import org.msh.pdex2.model.dwh.ReportPage;
import org.msh.pdex2.model.dwh.ReportSession;
import org.springframework.data.repository.CrudRepository;

public interface ReportPageRepo extends CrudRepository<ReportPage, Long> {

	List<ReportPage> findAllByDataModuleIdAndReportSession(long dataModuleId, ReportSession reportSession);

}
