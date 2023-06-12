package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.repository.r2.HistoryRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serves boot and JUNIT tests
 * @author alexk
 *
 */
@Service
public class AutoTestingService {
	private static final Logger logger = LoggerFactory.getLogger(AutoTestingService.class);
	@Autowired
	private SubmitService submitServ;
	@Autowired
	private HistoryRepo historyRepo;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private AmendmentService amendServ;
	@Autowired
	private ValidationService validServ;
	
	/**
	 * Rule 1. For any particular permit, a new application should be forbidden, 
	 * if modification or deregistration or revocation is processing for the permit
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean singletonConditionRule1() throws ObjectNotFoundException {
		boolean ret = true;
		// collect ID's of all permits in the state of modifications, de-registrations and revocations
		Set<Long> set1 = permitsIn(SystemService.DICTIONARY_GUEST_AMENDMENTS);
		Set<Long> set2=permitsIn(SystemService.DICTIONARY_GUEST_DEREGISTRATION);
		Set <Long> set3=permitsIn(SystemService.DICTIONARY_SHUTDOWN_APPLICATIONS);
		//collect ID's of all active permits
		Set<Long> set = permitsIn(SystemService.DICTIONARY_HOST_APPLICATIONS);
		//if a permit in mod, dreg or amend - submit is impossible, otherwise, possible
		for(Long id : set) {
		//TODO
			
		}
		return ret;
	}
	
	/**
	 * GEt list of all permit IDs for a dictionary given
	 * @param applicationDictionaryUrl
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Set<Long> permitsIn(String applicationDictionaryUrl) throws ObjectNotFoundException {
		Set<Long> ret = new LinkedHashSet<Long>();
		DictionaryDTO dict = new DictionaryDTO();
		dict.setUrl(applicationDictionaryUrl);
		dict = dictServ.createDictionary(dict);
		List<History> hlist = new ArrayList<History>();
		for(TableRow row : dict.getTable().getRows()){
			hlist.addAll(historyRepo.findAllByApplDictIDAndGo(row.getDbID(), null));
		}
		for(History h : hlist) {
			if(h.getActConfig() != null && h.getGo()==null) {
				ret.add(amendServ.initialApplicationData(h.getApplicationData()).getID());
			}
		}
		logger.debug("permitsIn " +applicationDictionaryUrl + " " + ret.size());
		return ret;
	}



}
