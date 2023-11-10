package org.msh.pharmadex2.service.r2;

import java.util.List;
import java.util.Map;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AsyncInformDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Async part of Reassign User Service
 * Self-invocation — calling the async method from within the same class — won’t work. (see https://www.baeldung.com/spring-async) 
 * @author alexk
 *
 */
@Service
public class ReassignUserServiceAsync {
	private static final Logger logger = LoggerFactory.getLogger(ReassignUserServiceAsync.class);
	@Autowired
	private Messages messages;
	@Autowired
	private LoggerEventService eventLog;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private JdbcRepository jdbcRepo;

	// states of background processes
	private static volatile  AsyncInformDTO applicantReassignInformer = new AsyncInformDTO();

	public static synchronized AsyncInformDTO getApplicantReassignInformer() {
		//System.out.println(Thread.currentThread().getName() +"  get  "+applicantReassignInformer);
		return applicantReassignInformer;
	}
	public static synchronized void setApplicantReassignInformer(AsyncInformDTO applicantReassignInformer) {
		ReassignUserServiceAsync.applicantReassignInformer = applicantReassignInformer;
	}

	/**
	 * load applicant reassigning progress data
	 * @param data
	 * @return
	 */
	public AsyncInformDTO applicantProgressLoad(AsyncInformDTO data) {
		return getApplicantReassignInformer();
	}
	/**
	 * Is reassign applicant process is alredy running?
	 * @return
	 */
	public boolean isApplicantReassignRunning() {
		AsyncInformDTO info = getApplicantReassignInformer();
		return !(info.isCompleted() || info.isCancelled());
	}
	/**
	 * Run applicant reassign in background
	 * Each data will moved in the separate transaction
	 * @param emailFrom 
	 * @param emailTo 
	 * @param emailExec TODO
	 * @param data
	 */
	@Async
	public void applicantReassignRunAsync(String emailFrom, String emailTo
			, Map<String, List<Long>> toReassign, String emailExec) {
		AsyncInformDTO info = clearData();
		info.setComplOf(totalToReassign(toReassign));
		info.setTitle(String.format(messages.get("toReassignApplicantTitle"), emailFrom,emailTo));
		try {
			info.setCompleted(false);
			info.setCancelled(false);
			for(String key :toReassign.keySet()) {
				Concept root = closureServ.loadRoot(key);
				Concept emailToC=closureServ.saveToTree(root, emailTo);
				for(Long id : toReassign.get(key)) {
					if(!info.isCancelled()) {
						Concept conc=closureServ.loadConceptById(id);
						jdbcRepo.moveSubTree(conc, emailToC);
						info.setCompl(info.getCompl()+1);
						Float percentF = (info.getCompl() * 100.0f) / info.getComplOf();
						info.setComplPercent(percentF.intValue());
						info.setProgressMessage(String.format(messages.get("toReassignTotal")
								, info.getCompl(), info.getComplOf()));
					}else {
						break;
					}
				}
			}
		} catch (Exception e) {
			info.addError(e.getMessage());
		}
		if(info.isCancelled()) {
			info.setCompleted(false);
		}else {
			info.setCompleted(true);
		}
		try {
			String mess = info.getProgressMessage();
			if(!info.isValid()) {
				mess=info.getIdentifier();
			}
			eventLog.applicantReassign(emailExec, emailFrom, emailTo
					,info.getCompl(), info.getComplOf(), mess);
		} catch (ObjectNotFoundException e) {
			logger.error("applicantReassignRunAsync "+e.getMessage());
		}
	}
	/**
	 * Total to reassign
	 * @param toReassign
	 * @return
	 */
	private long totalToReassign(Map<String, List<Long>> toReassign) {
		long ret=0l;
		for(String key :toReassign.keySet()) {
			ret+=toReassign.get(key).size();
		}
		return ret;
	}
	/**
	 * Clean up results of the previous execution
	 */
	public AsyncInformDTO clearData() {
		AsyncInformDTO ret = getApplicantReassignInformer();
		ret.setCompl(0);
		ret.setCompleted(true);
		ret.setCancelled(false);
		ret.setComplOf(0);
		ret.setComplPercent(0);
		ret.setProgressMessage(String.format(messages.get("toReassignTotal"), 0,0));
		return ret;
	}
	/**
	 * Stop the process
	 * @param data
	 * @return
	 */
	public AsyncInformDTO applicantProgressCancel() {
		AsyncInformDTO ret = getApplicantReassignInformer();
		ret.setCancelled(true);
		return ret;
	}

}
