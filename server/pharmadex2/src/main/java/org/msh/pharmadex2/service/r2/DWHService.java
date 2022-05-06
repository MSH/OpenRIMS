package org.msh.pharmadex2.service.r2;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
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
	JdbcRepository jdbcRepo;
	@Value("${dwh.schema}")
	private String dwhSchema;
	@Value("${dwh.username}")
	private String dwhUsername;
	@Value("${dwh.password}")
	private String dwhPassword;
	/**
	 * Upload DWH data
	 * @throws SQLException 
	 */
	public void upload() throws SQLException {
		JdbcRepository  dwhRepo=JdbcRepository.instanceOf(dwhSchema, jdbcRepo.getJdbcTemplate(), dwhUsername, dwhPassword);
		long newSessionID = sessionOpen(dwhRepo);
		if(newSessionID>0) {
			updateAux(newSessionID, dwhRepo);
			updatePage(newSessionID, dwhRepo);
			uploadReportObjects(newSessionID, dwhRepo);
			uploadClassifiers(newSessionID, dwhRepo);
			uploadLiterals(newSessionID, dwhRepo);
			uploadEvents(newSessionID, dwhRepo);
			long activeSessionId=sessionActualize(dwhRepo, newSessionID);
			if(activeSessionId==0) {
				logger.error("can't finish upload");
			}
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
	private int updatePage(long newSessionID, JdbcRepository dwhRepo) {
		int ret=0;
		String sql="";
		ret = jdbcRepo.update(sql);
		return ret;
		
	}
	/**
	 * Update ReportAux (persons, warehouses, etc)
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int updateAux(long newSessionID, JdbcRepository dwhRepo) {
		int ret=0;
		String sql="insert into "+dwhSchema+".reportaux (`sessionID`,`ParentId`,`ParentUrl`,`AuxId`,`AuxUrl`,`Lang`,`PrefLabel`,`Owner`)\r\n" + 
				"select "+newSessionID+",`parentId`,`parentUrl`,`auxId`,`auxUrl`,`lang`,`prefLabel`,`owner` from reportaux";
		ret = jdbcRepo.update(sql);
		return ret;
	}

	/**
	 * Upload application events
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int uploadEvents(long newSessionID, JdbcRepository dwhRepo) {
		int ret=0;
		String sql="insert into "+dwhSchema+".reportevent (`ObjectConceptId`,`Url`,`Come`,`Go`,`First`,`Last`,`Appldictid`, `ActivityConfigId`, `sessionID`)\r\n" + 
				"select objectid,stage,come,go,isStart,isFinish,appldictid, actConfigId,"+newSessionID+" from reportactivities";
		ret = jdbcRepo.update(sql);
		return ret;
	}
	/**
	 * Upload all literals on all languages
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int uploadLiterals(long newSessionID, JdbcRepository dwhRepo) {
		int ret=0;
		String sql="insert into "+dwhSchema+".reportliteral (`ConceptID`,`Variable`,`Language`,`ValueStr`, `sessionID`)\r\n" + 
				"select conceptid, varname,lang,varvalue, "+newSessionID+" from pdx2.reportliterals";
		ret = jdbcRepo.update(sql);
		return ret;
		
	}

	/**
	 * Upload classifiers
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int uploadClassifiers(long newSessionID, JdbcRepository dwhRepo) {
		int ret=0;
		String sql="insert into "+dwhSchema+".reportclassifier (`ObjectConceptId`,`Level`,`ItemConceptId`,`DictUrl`,`DictRootId`,`Variable`,`PageUrl`,`prefLabel`, `Lang`,`sessionID`)\r\n" + 
				"select `objectid`, `level`, `dictitemid`,`dictUrl`, `dictRootId`,`variable`,`pageurl`, `pref`,`lang`,"+ newSessionID + " from pdx2.reportclassifiers";
		ret = jdbcRepo.update(sql);
		return ret;
	}
	/**
	 * Upload report objects to the DWH database
	 * @param newSessionID
	 * @param dwhRepo
	 */
	private int uploadReportObjects(long newSessionID, JdbcRepository dwhRepo) {
		int ret=0;
		String sql="insert into "+ dwhSchema +".reportobject (`ObjectConceptID`,`Url`, `Email`,`sessionID`)\r\n" + 
				"SELECT objectid, objecturl,authemail,"+newSessionID +" FROM reportobjects;";
		ret = jdbcRepo.update(sql);
		return ret;
	}

	/**
	 * actualize the non actual session.
	 * The non actual session should be only one
	 * @param dwhRepo
	 * @param newSessionID 
	 */
	@Transactional
	private long sessionActualize(JdbcRepository dwhRepo, long newSessionID) {
		long ret=0;
		sessionsCloseAll(dwhRepo);													//make all session non-actual
		//make the current session actual
		TableRow row = TableRow.instanceOf(newSessionID);
		row.getRow().add(TableCell.instanceOf("CompletedAt", LocalDateTime.now(), LocaleContextHolder.getLocale()));
		row.getRow().add(TableCell.instanceOf("Actual", true));
		if(dwhRepo.update("session",row)) {
			List<TableRow> ra= session(dwhRepo,true, true);
			if(ra.size()==1) {
				ret=ra.get(0).getDbID();
			}
		}
		return ret;
	}
	/**
	 * make all session not active
	 * @param dwhRepo 
	 */
	@Transactional
	private int sessionsCloseAll(JdbcRepository dwhRepo) {
		int ret=0;
		String sql = "update `session` set `Actual`=0";
		ret = dwhRepo.update(sql);
		return ret;
	}

	/**
	 * Open a new upload session.
	 * It is possible only if one active session is in use or it is a first session
	 * @param dwhRepo
	 * @return
	 */
	@Transactional
	private long sessionOpen(JdbcRepository dwhRepo) {
		long ret=0;
		List<TableRow> arows = session(dwhRepo, true,true);
		List<TableRow> narows=session(dwhRepo,false,true);
		if(arows.size()==1 || narows.size()==0) {
			TableRow row = TableRow.instanceOf(0l);
			row.getRow().add(TableCell.instanceOf("StartedAt", LocalDateTime.now(), LocaleContextHolder.getLocale()));
			row.getRow().add(TableCell.instanceOf("Actual", false));
			if(dwhRepo.insert("session", row)) {
				List<TableRow> ra = session(dwhRepo, false, false);
				if(ra.size()==1) {
					return ra.get(0).getDbID();
				}
			}
		}
		return ret;
	}

	/**
	 * Get a list of sessions
	 * @param dwhRepo repository to get
	 * @param actual actual or not
	 * @param closed - has CompletedAt date
	 * @return
	 */
	@Transactional
	public List<TableRow> session(JdbcRepository dwhRepo, boolean actual, boolean closed) {
		Headers headers = sessionHeaders();
		String where="Actual=false";
		if(actual) {
			where="Actual=true";
		}
		if(closed) {
			where=where+ " and CompletedAt is not null";
		}else {
			where=where+ " and CompletedAt is null";
		}
		List<TableRow> rows= dwhRepo.qtbGroupReport("select * from `session`", "", where, headers);
		return rows;
	}
	/**
	 * Session table structure
	 * @return
	 */
	private Headers sessionHeaders() {
		Headers ret =new Headers();
		ret.getHeaders().add(TableHeader.instanceOf("StartedAt", TableHeader.COLUMN_LOCALDATETIME));
		ret.getHeaders().add(TableHeader.instanceOf("CompletedAt", TableHeader.COLUMN_LOCALDATETIME));
		ret.getHeaders().add(TableHeader.instanceOf("Actual", TableHeader.COLUMN_STRING));
		return ret;
	}

}
