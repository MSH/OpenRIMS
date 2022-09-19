package org.msh.pharmadex2.service.r2;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.old.User;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.PublicOrganization;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.model.r2.UserDict;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.auth.UserRoleDto;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	private LiteralService literalServ;
	@Autowired
	private UserService userServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private PubOrgService publicOrgServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private Messages messages;

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
	 * User is moderator for an url given when this user has at least one relation to a dictionary item contains this url and role "moderator"
	 * @param url
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isModerator(String url, UserDetailsDTO user) throws ObjectNotFoundException {
		User umodel = userServ.findByEmail(user.getEmail());
		if(umodel!=null) {
			List<UserRoleDto> roles=user.getGranted();
			if(roles.size()==0) {
				return false;
			}
			if(roles.get(0).getAuthority().equalsIgnoreCase("moderator")) {
				for(UserDict ud : umodel.getDictionaries()) {
					String urlAllowed = literalServ.readValue("url", ud.getConcept());
					if(urlAllowed.equalsIgnoreCase(url)) {
						return true;
					}
				}
				return false;
			}else {
				return false;
			}
		}else {
			return false;		//external unregistered user cannot be moderator!
		}
	}

	/**
	 * Is this user initiator of workflow?
	 * @param node
	 * @param user
	 * @return
	 */
	public boolean isInitiator(Concept node, UserDetailsDTO user) {
		List<Concept> parents = closureServ.loadParents(node);
		int url_index = parents.size()-1;
		if(url_index>2) {
			return parents.get(1).getIdentifier().equalsIgnoreCase(user.getEmail());
		}
		return false;
	}

	/**
	 * Read access allowed
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean readAllowed(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//user may be null
		return true;
	}


	/**
	 * Is the user owner of application data?
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private boolean isApplicationOwner(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(data.getNodeId()==0) {
			return true;
		}
		Concept conc = closureServ.loadConceptById(data.getNodeId());
		List<History> hlist = boilerServ.historyByActivitydata(conc);
		if(hlist.size()>0) {
			Concept owner = closureServ.getParent(hlist.get(0).getApplicationData());
			return sameEmail(owner.getIdentifier(), user.getEmail());
		}else {
			return false;
		}
	}

	/**
	 * Is this thing belong to the workflow
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean workflowThing(ThingDTO data) throws ObjectNotFoundException {
		if(data.getNodeId()>0) {
			//take a thing
			Concept node = closureServ.loadConceptById(data.getNodeId());
			Thing thing = new Thing();
			thing = boilerServ.thingByNode(node, thing);
			//build a list of activity node ids
			Map<String, List<Concept>> allActivities = boilerServ.workflowActivities(data.getActivityId());
			Set<Long> aIds = new LinkedHashSet<Long>();
			for(String key : allActivities.keySet()) {
				for(Concept conc :allActivities.get(key)) {
					aIds.add(conc.getID());
				}
			}
			//is this thing an activity in the workflow?
			if(aIds.contains(data.getNodeId())){
				return true;
			}
			//has the thing link to any activity in the workflow?
			for(ThingThing thth : thing.getThings()) {
				long id = thth.getConcept().getID();
				if(aIds.contains(id)) {
					return true;
				}
			}
			return false;
		}else {
			return false;
		}
	}

	/**
	 * is this thing in a current workflow? As a thing or as an activity
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean workflowExecutor(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(data.getActivityId()>0) {
			Map<String, List<Concept>> allActivities = boilerServ.workflowActivities(data.getActivityId());
			return allActivities.keySet().contains(user.getEmail());
		}else {
			return false;
		}
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
	private boolean isEmployee(UserDetailsDTO user) {
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
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isTerritoryUser(UserDetailsDTO user) throws ObjectNotFoundException {
		User u = userServ.findByEmail(user.getEmail());
		if(u!= null) {
			if(u.getConcept() != null) {
				if(u.getConcept().getActive()) {
					if(u.getOrganization() != null) {
						PublicOrganization porg=publicOrgServ.findByConcept(u.getOrganization());
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
	 * change Password by Admin Load
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ThingDTO changePassAdminLoad(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		data.setUrl(AssemblyService.SYSTEM_CHANGEPASS_ADMIN);
		Concept root = closureServ.loadRoot(data.getUrl());
		data.setParentId(root.getID());
		List<Concept> nodes = closureServ.loadLevel(root);
		if(nodes.size()>0) {
			data.setNodeId(nodes.get(0).getID());
		}
		if(data.getNodeId()==0) {
			data = thingServ.createThing(data, user);
		}else {
			data = thingServ.loadThing(data, user);
			data.setValid(true);
			data.setUrl(AssemblyService.SYSTEM_CHANGEPASS_ADMIN);
		}
		return data;
	}
	
	/**
	 * change Password by Admin Load
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ThingDTO changePassAdminSave(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		data.setValid(false);
		String err = "";

		Map<String, FormFieldDTO<String>> fields = data.getLiterals();
		if(fields != null && fields.keySet().size() == 2) {
			String p1 = fields.get(AssemblyService.CHANGEPASS_NEWPASS).getValue();
			String p2 = fields.get(AssemblyService.CHANGEPASS_NEWPASS_REPEAT).getValue();
			if(verifyPassword(p1, p2)) {
				// save
				user.setPassword(passwordEncoder.encode(p1));
				userServ.updateUser(user);
				if(thingServ.removeThing(data, user)) {
					data.setNodeId(0);
					data.setValid(true);
				}
			}else err = messages.get("password8andsame");
		}else err = messages.get("password8andsame");
		data.setIdentifier(err);
		return data;
	}
	
	private boolean verifyPassword(String str1, String str2) {
		str1 = str1.trim();
		str2 = str2.trim();
		if(str1 != null && str2 != null && 
				!str1.isEmpty() && !str2.isEmpty() &&
				str1.length() >= 7 && str2.length() >= 7 &&
				str1.equals(str2)) {
			return true;
		}
		return false;
	}
}
