package org.msh.pharmadex2.service.r2;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.dwh.ReportSession;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.dwh.ReportSessionRepo;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Responsible to provide data for external reports and DWH
 * @author alexk
 *
 */
@Service
public class DWHService {
	private static final Logger logger = LoggerFactory.getLogger(DWHService.class);
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ReportSessionRepo sessionRepo;
	@Autowired
	private BoilerService boilerServ;

	/**
	 * Upload DWH data
	 * @throws SQLException 
	 */
	public void upload() throws SQLException {
		long newSessionID = sessionOpen();
		if(newSessionID>0) {
			updateAux(newSessionID);
			updatePage(newSessionID);
			//uploadReportObjects(newSessionID);
			uploadClassifiers(newSessionID);
			uploadLiterals(newSessionID);
			uploadEvents(newSessionID);
			sessionClose(newSessionID);
		}else {
			logger.error("can't start upload");
		}
		return;
	}
	
	/**
	 * Update ReportPage (form structure)
	 * @param newSessionID
	 * @param dwhRepo
	 * @return 
	 */
	private int updatePage(long newSessionID) {
		int ret=0;
		String sql="insert into reportpage (`reportsessionID`,`RootId`,`RootUrl`,`Lang`,`PrefLabel`,`PageId`,`PageUrl`,`PageVar`,`Owner`)\r\n" + 
				"SELECT "+newSessionID+", `rootId`,`rootUrl`,`lang`,`prefLabel`,`pageId`,`pageUrl`,`pageVar`,`owner` FROM reportpages;";
		ret = jdbcRepo.update(sql);
		return ret;
		
	}
	/**
	 * Update ReportAux (persons, warehouses, etc)
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int updateAux(long newSessionID) {
		int ret=0;
		String sql="insert into reportaux (`reportsessionID`,`ParentId`,`ParentUrl`,`AuxId`,`AuxUrl`,`Lang`,`PrefLabel`,`Owner`)\r\n" + 
				"select "+newSessionID+",`parentId`,`parentUrl`,`auxId`,`auxUrl`,`lang`,`prefLabel`,`owner` from reportaux";
		ret = jdbcRepo.update(sql);
		return ret;
	}

	/**
	 * Upload application events
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int uploadEvents(long newSessionID) {
		int ret=0;
		String sql="insert into reportevent (`ObjectConceptId`,`Url`,`Come`,`Go`,`First`,`Last`,`Appldictid`, `ActivityConfigId`, `reportsessionID`)\r\n" + 
				"select objectid,stage,come,go,isStart,isFinish,appldictid, actConfigId,"+newSessionID+" from reportactivities";
		ret = jdbcRepo.update(sql);
		return ret;
	}
	/**
	 * Upload all literals on all languages
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int uploadLiterals(long newSessionID) {
		int ret=0;
		String sql="insert into reportliteral (`ConceptID`,`Variable`,`Language`,`ValueStr`, `reportsessionID`)\r\n" + 
				"select conceptid, varname,lang,varvalue, "+newSessionID+" from pdx2.reportliterals";
		ret = jdbcRepo.update(sql);
		return ret;
		
	}

	/**
	 * Upload classifiers
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int uploadClassifiers(long newSessionID) {
		int ret=0;
		String sql="insert into reportclassifier (`ObjectConceptId`,`Level`,`ItemConceptId`,`DictUrl`,`DictRootId`,`Variable`,`PageUrl`,`prefLabel`, `Lang`,`reportsessionID`)\r\n" + 
				"select `objectid`, `level`, `dictitemid`,`dictUrl`, `dictRootId`,`variable`,`pageurl`, `pref`,`lang`,"+ newSessionID + " from pdx2.reportclassifiers";
		ret = jdbcRepo.update(sql);
		return ret;
	}
	/**
	 * Upload report objects to the DWH database
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int uploadReportObjects(long newSessionID) {
		int ret=0;
		String sql="insert into reportobject (`ObjectConceptID`,`Url`, `Email`,`reportsessionID`)\r\n" + 
				"SELECT objectid, objecturl,authemail,"+newSessionID +" FROM reportobjects;";
		ret = jdbcRepo.update(sql);
		return ret;
	}

	/**
	 * Close the current session and perform the housekeeping
	 * The two session should be left - previous marked as inactive and the current, marked as active
	 * @param newSessionID 
	 */
	@Transactional
	private void sessionClose(long newSessionID) {
		Optional<ReportSession> currento = sessionRepo.findById(newSessionID);
		if(currento.isPresent()) {
			ReportSession current = currento.get();
			if(!current.getActual()) {
				removeExtraSessions();
				current.setActual(true);
				current.setCompletedAt(new Date());
				current=sessionRepo.save(current);
			}else {
				logger.error("Cannot close the session. Already closed. ID is "+newSessionID);
			}
		}else {
			logger.error("Cannot close the session, not found. ID is "+newSessionID);
		}
	}

	/**
	 * Only one previous session should be left and marked as inactive
	 */
	private void removeExtraSessions() {
		Iterable<ReportSession> rss = sessionRepo.findAll();
		LocalDateTime min = LocalDateTime.now().minusYears(100);
		List<ReportSession> toRemove = new ArrayList<ReportSession>();
		ReportSession prev = new ReportSession();
		for(ReportSession rs : rss) {
			if(rs.getActual()) {
				LocalDateTime come = boilerServ.localDateTimeServer(rs.getStartedAt());	//it is near impossible, but can be more then one actual
				if(come.isAfter(min)) {
					prev=rs;
					min=come;
				}else {
					toRemove.add(rs);
				}
			}else {
				if(rs.getCompletedAt()!=null) {
					toRemove.add(rs);
				}
			}
		}
		if(toRemove.size()>0) {
			sessionRepo.deleteAll(toRemove);
		}
		if(prev.getID()>0) {
			prev.setActual(false);
			sessionRepo.save(prev);
		}
	}

	@Transactional
	private long sessionOpen() {
		long ret=0l;
		ReportSession rs = sessionProcessOpened();
		if(rs.getID()==0) {			//there is no opened session
			rs.setStartedAt(new Date());
			rs.setActual(false);
			rs=sessionRepo.save(rs);
			ret=rs.getID();
		}else {
			logger.warn("Update has been suspended, because of active session");
		}
		return ret;
	}

	/**
	 * House keeping opened sessions
	 * Removes opened session that have been opened a hour ago
	 * Left only one opened session with the maximal come date
	 * @return
	 */
	@Transactional
	private ReportSession sessionProcessOpened() {
		ReportSession ret = new ReportSession();
		List<ReportSession> rsol=sessionRepo.findAllByActual(false);
		List<ReportSession> toRemove= new ArrayList<ReportSession>();
		LocalDateTime actual = LocalDateTime.now().minusHours(1);
		for(ReportSession rs :rsol) {
			if(rs.getStartedAt()!=null && rs.getCompletedAt() == null) {
				LocalDateTime come = boilerServ.localDateTimeServer(rs.getStartedAt());
				if(come.isBefore(actual)) {
					toRemove.add(rs);
				}else {
					ret=rs;
					actual=come;
				}
			}else {
				toRemove.add(rs);
			}
		}
		if(toRemove.size()>0) {
			sessionRepo.deleteAll(toRemove);
		}
		return ret;
	}

}
