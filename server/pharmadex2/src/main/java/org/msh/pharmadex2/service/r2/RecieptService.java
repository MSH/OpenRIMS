package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.User;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.repository.common.UserRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.SubmitRecieptDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible for receipts after submit
 * @author alexk
 *
 */
@Service
public class RecieptService {
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private RegisterService registerServ;
	@Autowired
	private AccessControlService accessControl;
	@Autowired
	private Messages messages;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private UserRepo userRepo;

	/**
	 * Submit receipt data
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public SubmitRecieptDTO submitReciept(UserDetailsDTO user, SubmitRecieptDTO data) throws ObjectNotFoundException {
		if(data.getHistoryId()>0) {
			History his=boilerServ.historyById(data.getHistoryId());
			if(accessControl.isActivityExecutor(his.getActivity(), user)) {
				data.getSubmitted_date().setValue(BoilerService.dateToLocalDate(his.getGo()));
				data.getPrefLabel().setValue(literalServ.readPrefLabel(his.getApplicationData()));
				data.getReg_processing().setValue(literalServ.readPrefLabel(his.getApplDict()));
				data.getDescription().setValue(literalServ.readDescription(his.getApplDict()));
				data.getReferences().setValue(referencesLoad(his.getApplicationData()));
				data.getOffice().setValue(sentToOffices(his.getApplicationData()));
			}
		}
		return data;
	}

	/**
	 * Load 
	 * @param applicationData
	 * @return unknown if not found, comma separated list of offices otherwise
	 * @throws ObjectNotFoundException 
	 */
	private String sentToOffices(Concept applicationData) throws ObjectNotFoundException {
		String ret=messages.get("unknown");
		Set<String> officies = new LinkedHashSet<String>();
		List<History> allHis=boilerServ.historyAll(applicationData);
		for(History his : allHis) {
			if(his.getActConfig()!=null && his.getGo()==null) {
				String office = officeByActivity(his.getActivity());
				if(office.length()>0) {
					officies.add(office);
				}
			}
		}
		if(officies.size()>0) {
			ret=String.join(", ", officies);
		}
		return ret;
	}
	/**
	 * Find the office that serves the activity 
	 * @param activity
	 * @return empty string if no office
	 * @throws ObjectNotFoundException 
	 */
	private String officeByActivity(Concept activity) throws ObjectNotFoundException {
		String ret="";
		Concept worker=closureServ.getParent(activity);
		Optional<User> usero=userRepo.findByEmail(worker.getIdentifier());
		if(usero.isPresent()) {
			Concept org=usero.get().getOrganization();
			if(org!=null) {
				ret=literalServ.readPrefLabel(org);
			}
		}
		return ret;
	}

	/**
	 * Load all registers numbers as a references for a receipt
	 * @param applicationData
	 * @return "not assigned" if none, comma separated list of register numbers otherwise
	 * @throws ObjectNotFoundException 
	 */
	private String referencesLoad(Concept applicationData) throws ObjectNotFoundException {
		String ret= messages.get("ReviewStatus.NOT_ASSIGNED");
		Map<String, RegisterDTO> registers= registerServ.registersLoadByApplicationData(applicationData);
		List<String> reply = new ArrayList<String>();
		for(String key : registers.keySet()) {
			RegisterDTO dto = registers.get(key);
			reply.add(dto.getNumberPrefix()+dto.getReg_number().getValue());
		}
		if(reply.size()>0) {
			ret=String.join(", ", reply);
		}
		return ret;
	}



}
