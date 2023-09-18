package org.msh.pharmadex2.service.r2;

import java.util.List;
import java.util.Optional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.dwh.ReportPage;
import org.msh.pdex2.model.dwh.ReportSession;
import org.msh.pdex2.model.enums.YesNoNA;
import org.msh.pdex2.model.old.User;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.PublicOrganization;
import org.msh.pdex2.model.r2.UserDict;
import org.msh.pdex2.repository.common.UserRepo;
import org.msh.pdex2.repository.dwh.ReportPageRepo;
import org.msh.pdex2.repository.dwh.ReportSessionRepo;
import org.msh.pdex2.repository.r2.HistoryRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
/**
 * Responsible for determine access rights to things
 * @author alexk
 *
 */
@Service
public class AccessControlService {
	private static final Logger logger = LoggerFactory.getLogger(AccessControlService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private ReportPageRepo rpgRepo;
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ReportSessionRepo reportSessionRepo;

	/**
	 * Can this user create the thing
	 * @param data - the thing
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean createAllowed(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//TODO some more sophiscated ??
		return true;
	}

	/**
	 * Compare eMails
	 * @param email1
	 * @param email2
	 * @return true if the address is same
	 */
	public boolean sameEmail(String email1, String email2) {
		try {
			InternetAddress addr1 = new InternetAddress(email1,true);
			InternetAddress addr2 = new InternetAddress(email2,true);
			if(addr1.equals(addr2)) {
				return true;
			}else {
				return false;
			}
		} catch (AddressException e) {
			return false;
		}
	}


	/**
	 * Is this user supervisor?
	 * @param user
	 * @return
	 */
	public boolean isSupervisor(UserDetailsDTO user) {
		if(user.getGranted().size()==0) {
			return false;
		}
		if(user.getGranted().get(0).getAuthority().toUpperCase().contains("ADMIN")) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * SECRETARY-?
	 */
	public boolean isSecretary(UserDetailsDTO user) {
		if(user.getGranted().size()==0) {
			return false;
		}
		if(user.getGranted().get(0).getAuthority().toUpperCase().contains("SECRETARY")) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * Is this user currently acted as a moderator
	 * @param user
	 * @return
	 */
	public boolean isModerator(UserDetailsDTO user) {
		if(user.getGranted().size()==0) {
			return false;
		}
		if(user.getGranted().get(0).getAuthority().toUpperCase().contains("MODERATOR")) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 * Executor of the current activity in this application
	 * @param activityNode a node of any activity
	 * @param user current user
	 * @return
	 */
	@Transactional
	public boolean isActivityExecutor(Concept activityNode, UserDetailsDTO user) {
		String email = user.getEmail();
		if(email!=null) {
			Concept executor = closureServ.getParent(activityNode);
			if(sameEmail(executor.getIdentifier(), email)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is this user an owner of the thing
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isOwner(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(data.getNodeId()>0) {
			Concept thing = closureServ.loadConceptById(data.getNodeId());
			Concept owner=closureServ.getParent(thing);
			return sameEmail(owner.getIdentifier(), user.getEmail());
		}else {
			return false;
		}
	}

	/**
	 * Write allowed only to thing owner
	 * If thing has not owner - only to sysadmin
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public boolean writeAllowed(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(user==null) {
			return false;
		}
		if(data.getNodeId()>0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			List<Concept> parents = closureServ.loadParents(node);
			if(isSupervisor(user)) {
				return true;
			}else {
				if(parents.size()>1) {
					String email=parents.get(1).getIdentifier();
					if(email!=null) {
						if(!email.contains("@")) {
							return true;
						}
						if(sameEmail(email, user.getEmail())) {
							return true;
						}else {
							return isSupervisor(user);
						}
					}else {
						return isSupervisor(user);
					}
				}else {
					return false;
				}
			}
		}else {
			return createAllowed(data, user);
		}
	}

	/**
	 * Is this application allowed for the user
	 * It allowed if at least one activity has been assigned for this user
	 * @param allHis
	 * @param user 
	 * @return
	 */
	@Transactional
	public boolean applicationAllowed(List<History> allHis, UserDetailsDTO user) {
		for(History his : allHis) {
			Concept activity = his.getActivity();
			if(isMyActivity(activity,user)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Am I an executor of this activity?
	 * @param activity
	 * @param user
	 * @return
	 */
	@Transactional
	public boolean isMyActivity(Concept activity, UserDetailsDTO user) {
		Concept exec = closureServ.getParent(activity);
		String eMail = exec.getIdentifier();
		if(sameEmail(eMail, user.getEmail())) {
			return true;
		}else {
			return false;
		}
	}
	/**
	 * Applicant email by application concept
	 * @param application
	 * @return empty string if not found
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String applicantEmailByApplication(Concept appl) throws ObjectNotFoundException {
		Concept owner = closureServ.getParent(appl);
		String eMail = owner.getIdentifier();
		try {
			new InternetAddress(eMail,true);	//check only
			return eMail;
		} catch (AddressException e) {
			throw new ObjectNotFoundException("User's email not found by application. Application ID="+appl.getID(),logger);
		}
	}
	/**
	 * User is applicant if no one role
	 * @param user
	 * @return
	 */
	public boolean isApplicant(UserDetailsDTO user) {
		return user.getAllRoles().size()==0;
	}
	/**
	 * Person data read allowed to person owner and any NMRA employee
	 * @param personId
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public boolean personAllowed(long personId, UserDetailsDTO user) throws ObjectNotFoundException {
		if(isSupervisor(user)) {
			return true;
		}
		ThingDTO pers = new ThingDTO();
		pers.setNodeId(personId);
		if(isOwner(pers,user)) {
			return true;
		}
		if(isEmployee(user)) {
			return true;
		}
		return false;
	}
	/**
	 * Is this user an NMRA employee
	 * @param user
	 * @return
	 */
	@Transactional
	public boolean isEmployee(UserDetailsDTO user) {
		if(user.getGranted().size()>0) {
			if(user.getGranted().get(0).getAuthority().toUpperCase().contains("GUEST")) {
				return false;
			}else {
				return true;
			}
		}else {
			return false;
		}
	}
	/**
	 * Is office of this user restricted by territory?
	 * @param user
	 * @return true - territorial User. false - mainOrganization User
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isTerritoryUser(UserDetailsDTO user) throws ObjectNotFoundException {
		User u = boilerServ.findByEmail(user.getEmail());
		if(u!= null) {
			if(u.getConcept() != null) {
				if(u.getConcept().getActive()) {
					if(u.getOrganization() != null) {
						PublicOrganization porg=boilerServ.findPubOrgByConcept(u.getOrganization());
						if(porg.getAdminUnits().size()>0) {
							return true;
						}else {
							return false;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Is the role is an applicant?
	 * @param role
	 * @return
	 */
	@Transactional
	public boolean isApplicantRole(Concept role) {
		return role.getIdentifier().equalsIgnoreCase(SystemService.ROLE_APPLICANT);
	}
	/**
	 * Is this checklist allowed for a user given?
	 * @param actConf
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean checklistAllowed(Concept actConf, UserDetailsDTO user) throws ObjectNotFoundException {
		if(isApplicant(user)) {
			YesNoNA value = dtoServ.readLogicalLiteral(AssemblyService.HIDECHECKLIST_FLD, actConf);
			return !value.equals(YesNoNA.YES);
		}else {
			return true;
		}
	}
	/**
	 * Check hide from applicant and public available
	 * @param assm
	 * @param user
	 * @return
	 */
	public boolean allowAssembly(Assembly assm, UserDetailsDTO user) {
		if(isPublic(user)) {
			return assm.getPublicavailable();
		}else {
			if(isApplicant(user)) {
				return !assm.getHidefromapplicant();
			}
		}
		return true;
	}
	/**
	 * Same method, but for AssemblyDTO
	 * @param asmDTO
	 * @param user
	 * @return
	 */
	public boolean allowAssembly(AssemblyDTO asmDTO, UserDetailsDTO user) {
		if(isPublic(user)) {
			return asmDTO.isPublicAvailable();
		}else {
			if(isApplicant(user)) {
				return !asmDTO.isHideFromApplicant();
			}
		}
		return true;
	}
	/**
	 * Is this not-logged-in
	 * @param user
	 * @return
	 */
	public boolean isPublic(UserDetailsDTO user) {
		return user.getEmail().isEmpty() && user.getLogin().isEmpty();
	}

	/**
	 * Allow only authenticated actors - guest + nmra
	 * We do need this, because it is impossible to use something except * in the request mapping annotations :( 
	 * @param auth
	 * @param uri
	 * @throws DataNotFoundException 
	 */
	public void allowAuthenticated(Authentication auth, UriComponentsBuilder uri) throws DataNotFoundException {
		if(auth.isAuthenticated()) {
			return;
		}else {
			throw new DataNotFoundException("Page not found");
		}
	}
	/**
	 * User's read allowance for a permit data given
	 * @param permitData
	 * @param user
	 * @return true if allowed
	 */
	@Transactional
	public boolean readAllowed(Concept permitData, UserDetailsDTO user) {
		if(isPublic(user)) {
			return readAllowedNotAuth(permitData);
		}
		if(isApplicant(user)) {
			return readAllowedApplicant(permitData, user.getEmail());
		}
		return readAllowedNRA(permitData, user);
	}
	/**
	 * NRA staff can access public available data and applications allowed to access in the user configuration
	 * @param permitData
	 * @param user
	 * @return
	 */
	private boolean readAllowedNRA(Concept permitData, UserDetailsDTO user) {
		boolean ret = readAllowedNotAuth(permitData);
		if(!ret) {
			Optional<User> usero= userRepo.findByEmail(user.getEmail());
			if(usero.isPresent()) {
				if(usero.get().getEnabled()) {
					List<History> hlist = boilerServ.historyByActivitydata(permitData);
					if(hlist.isEmpty()) {
						return isSupervisor(user);		//only supervisor can see configurations
					}else {
						boolean accessible=false;
						for(UserDict ud : usero.get().getDictionaries()) {
							accessible = ud.getConcept().getID()==hlist.get(0).getApplDict().getID();
							if(accessible) {
								break;
							}
						}
						return accessible;
					}
				}else {
					logger.warn("NRA user is not enabled. Email is "+user.getEmail() );
					ret=false;
				}
			}else {
				logger.warn("NRA user not found. Email is "+user.getEmail());
				ret=false;
			}
		}
		return ret;
	}

	/**
	 * Applicant can read public available data and all own applications
	 * @param permitData
	 * @param email
	 * @return
	 */
	private boolean readAllowedApplicant(Concept permitData, String email) {
		boolean ret = readAllowedNotAuth(permitData);
		if(!ret) {
			Concept owner=closureServ.getParent(permitData);
			ret=sameEmail(owner.getIdentifier(), email);
		}
		return ret;
	}

	/**
	 * Search in Data Warehouse for a state
	 * Allow only for ACTIVE and DEREGISTERED
	 * @param permitData
	 * @return
	 */
	private boolean readAllowedNotAuth(Concept permitData) {
		List<ReportSession> rpsl = reportSessionRepo.findAllByActual(true);
		if(!rpsl.isEmpty()) {
			List<ReportPage> rpgl = rpgRepo.findAllByDataModuleIdAndReportSession(permitData.getID(),rpsl.get(rpsl.size()-1));
			if(!rpgl.isEmpty()) {
				String state = rpgl.get(rpgl.size()-1).getState();
				return state.equalsIgnoreCase("ACTIVE") || state.equalsIgnoreCase("DEREGISTERED");
			}
		}
		return false;
	}




}
