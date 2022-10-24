package org.msh.pharmadex2.service.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.enums.YesNoNA;
import org.msh.pdex2.model.old.User;
import org.msh.pdex2.model.old.User_role;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.UserDict;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.common.UserRepo;
import org.msh.pdex2.repository.r2.ConceptRepo;
import org.msh.pdex2.repository.r2.UserDictRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.UserElementDTO;
import org.msh.pharmadex2.dto.UserFormDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.auth.UserRoleDto;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.r2.AssemblyService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.ImportAService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Get user details
 * @author Alex Kurasoff
 *
 */
@Service
public class UserService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	@Autowired
	UserRepo userRepo;

	@Autowired
	PasswordEncoder password;

	@Autowired
	Messages messages;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	DtoService dtoServ;
	@Autowired
	LiteralService literalServ;
	@Autowired
	ClosureService closureServ;
	@Autowired
	JdbcRepository jdbcRepo;
	@Autowired
	DictService dictServ;
	@Autowired
	ValidationService validationServ;
	@Autowired
	AssemblyService assemblyServ;
	@Autowired
	SystemService systemServ;

	@Autowired
	UserDictRepo userDictRepo;
	@Autowired
	ConceptRepo conceptRepo;

	/**
	 * To fit the Spring Security Interface UserDetails
	 */
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails ret =  loadUserDetailsDTO(username);
		return ret;
	}

	/**
	 * Extended UserDetails for pleasures any other then Spring Auth
	 * @param username
	 * @return
	 */
	@Transactional
	public UserDetailsDTO loadUserDetailsDTO(String login) {
		User u = loadUserByLogin(login);
		return userToDTO(u);
	}
	/**
	 * load a user by login name
	 * @param login
	 * @return
	 */
	@Transactional
	public User loadUserByLogin(String login) {
		Optional<User> usero = userRepo.findByUsername(login);
		if (!usero.isPresent()) {
			usero = userRepo.findByEmail(login);
			if(usero.isPresent()) {
				return usero.get();
			}else {
				throw new UsernameNotFoundException(login);
			}
		}else {
			return usero.get();
		}
	}
	/**
	 * Check user login and password by the REST API
	 * @param user
	 * @return
	 */
	public UserDetailsDTO authUser(UserDetailsDTO user) {
		Optional<User> usero = userRepo.findByUsername(user.getLogin());
		if(usero.isPresent()) {
			if(password.matches(user.getPassword(), usero.get().getPassword())) {
				user.setMessage("OK");
				user=userToDTO(usero.get());
			}else {
				user.setMessage("Invalid user name/password");
			}
		}else {
			user.setMessage("User not found : " + user.getLogin());
		}
		user.setPassword("");
		return user;
	}

	/**
	 * Assign default passwords=MD5 representation of user's raw password
	 */
	public void assignDefaultPasswords() {
		Iterable<User> users = userRepo.findAll();
		if(users != null){
			for(User u : users){
				if(u.getPassword()==null){
					u.setPassword(password.encode(u.getUsername()));
					userRepo.save(u);
				}
				if(u.getPassword().equalsIgnoreCase("123")) {
					u.setPassword(password.encode(";f,jtl12_hfp"));
					userRepo.save(u);
				}
			}
		}
	}


	/**
	 * User to UserDTO
	 * @param user
	 * @return
	 */
	@Transactional
	public UserDetailsDTO userToDTO(User user) {
		UserDetailsDTO ret = new UserDetailsDTO();
		ret.setActive(user.getEnabled());
		ret.setEmail(user.getEmail());
		ret.setExpired(!user.getEnabled());
		ret.setLocked(!user.getEnabled());
		ret.setLogin(user.getUsername());
		ret.setName(boilerServ.getFullUserName(user));
		ret.setPassword(user.getPassword());
		ret.setAllRoles((userGetAllAuthorities(user)));
		ret.getGranted().clear();
		ret.getGranted().addAll(userGetGrantedAuthorities(user));

		return ret;
	}
	/**
	 * For a user may be granted a subset of roles user assigned
	 * Typically it is one role
	 * If granted role assignment is inactive, then user will not granted to any role 
	 * @param user
	 * @return
	 */
	@Transactional
	private List<UserRoleDto> userGetGrantedAuthorities(User user) {
		List<UserRoleDto> ret = new ArrayList<UserRoleDto>();
		// текущую роль проверим сразу новую ConceptRole, а потом уже по старому CurrentRole
		Concept curRole = user.getConceptRole();
		if(curRole != null) {
			if(curRole.getActive()) {
				ret.add(createUserRoleDto(curRole));
			}
		}

		if(ret.size() == 0) {
			User_role ur = user.getCurrentRole();
			if(ur != null) {
				if(ur.getActive()) {
					ret.add(createUserRoleDto(ur));
				}
			}
		}
		return ret;
	}

	/**
	 * A user may be assigned to many roles
	 * All these assignments should be active
	 * Дополняем новые роли старыми
	 * @param user
	 * @return
	 */
	@Transactional
	private List<UserRoleDto> userGetAllAuthorities(User user) {
		List<UserRoleDto> ret = new ArrayList<UserRoleDto>();
		// load roles from Dictionary
		ret = allAuthFromDictionary(user, ret);

		// add roles from table user_role
		for(User_role ur :user.getRoles()) {
			if(ur.getActive()) {
				String role = ur.getRole().getRolename();
				boolean found = false;
				for(UserRoleDto rdto:ret) {
					if(rdto.getAuthority().equalsIgnoreCase(role)) {
						found = true;
						break;
					}
				}
				if(!found) {
					ret.add(createUserRoleDto(ur));
				}
			}
		}
		return ret;
	}

	/**
	 * Add new 
	 * @param ret
	 * @return
	 */
	@Transactional
	private List<UserRoleDto> allAuthFromDictionary(User user, List<UserRoleDto> ret) {
		for(UserDict ud : user.getDictionaries()) {
			if(ud.getUrl().equalsIgnoreCase(SystemService.DICTIONARY_SYSTEM_ROLES)) {
				ret.add(createUserRoleDto(ud.getConcept()));
			}
		}
		return ret;
	}

	/**
	 * Create a DTO for user role
	 * @param ur
	 * @return
	 */
	@Transactional
	private UserRoleDto createUserRoleDto(User_role ur) {
		UserRoleDto ret = new UserRoleDto();
		ret.setId(ur.getID());
		ret.setAuthority(ur.getRole().getRolename());
		ret.setActive(true);
		return ret;
	}

	@Transactional
	private UserRoleDto createUserRoleDto(Concept c) {
		UserRoleDto urd = new UserRoleDto();
		urd.setId(c.getID());
		urd.setActive(c.getActive());
		urd.setAuthority(c.getIdentifier());
		urd.setConceptId(c.getID());
		return urd;
	}

	@Transactional
	public UserDetailsDTO loadByEmail(String email) {
		User user = findByEmail(email);
		if(user != null) {
			return userToDTO(user);
		}else {
			return null;
		}
	}

	/**
	 * Find user model by eMail
	 * Unlike the most of such methods, doesn't rise an exception if user not found.
	 * A typical applicant user hasn't an account in the user table
	 * @param eMail
	 * @return null, if user not found
	 */
	@Transactional
	public User findByEmail(String email) {
		return boilerServ.findByEmail(email);
	}


	/**
	 * Create username and roles as fields to display and edit
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public UserFormDTO createFields(UserDetailsDTO authDTO) throws ObjectNotFoundException {
		Map<String, String> roleNames=systemServ.roleNames();
		UserFormDTO ret = new UserFormDTO();
		if(authDTO.getAllRoles() != null) {
			//granted role
			if(authDTO.getGranted() != null && authDTO.getGranted().size() == 0 && authDTO.getAllRoles().size() > 0) {
				authDTO.getGranted().add(authDTO.getAllRoles().get(0));
			}

			if(authDTO.getGranted() != null && authDTO.getGranted().size()==1) {
				OptionDTO ropt = UserRoleToOption(authDTO.getGranted().get(0),roleNames);
				ret.getUserRoleFld().setValue(ropt);
			}
			//all roles
			for(UserRoleDto ur : authDTO.getAllRoles()) {
				OptionDTO ropt = UserRoleToOption(ur,roleNames);
				ret.getUserRoleFld().getValue().getOptions().add(ropt);
			}
			// sort all roles
			Collections.sort(ret.getUserRoleFld().getValue().getOptions(), new Comparator<OptionDTO>() {
				@Override
				public int compare(OptionDTO o1, OptionDTO o2) {
					String n1 = o1.getCode();
					String n2 = o2.getCode();
					return n1.compareTo(n2);
				}
			});
			//name
			if(authDTO.getName() != null) {
				ret.setUserNameFld(FormFieldDTO.of(authDTO.getName()));
			}
		}
		return ret;
	}
	/**
	 * Convert UserRoleDTO to OptionDTO
	 * @param ur
	 * @param roleNames 
	 * @return
	 */
	public OptionDTO UserRoleToOption(UserRoleDto ur, Map<String, String> roleNames) {
		OptionDTO ropt = new OptionDTO();
		ropt.setId(ur.getId());
		String roleName=roleNames.get(ur.getAuthority());
		if(roleName==null) {
			roleName=messages.get(ur.getAuthority());
		}
		ropt.setCode(messages.get(roleName));
		return ropt;
	}
	/**
	 * Create UserDetailsDTO from OATH2 or form based Authentication object
	 * @param auth
	 * @param ret
	 * @return
	 */
	@Transactional
	public UserDetailsDTO userData(Authentication auth, UserDetailsDTO ret) {
		if(auth != null && auth.getPrincipal() != null && (auth.getPrincipal() instanceof UserDetailsDTO || auth.getPrincipal() instanceof OidcUser)) {
			if (auth.getPrincipal() instanceof OidcUser) {
				OidcUser oAuth = (OidcUser) auth.getPrincipal();
				ret = loadByEmail(oAuth.getEmail());
				if (ret == null) {
					ret = new UserDetailsDTO();
					ret.setEmail(oAuth.getEmail());
					ret.setName(oAuth.getFullName());
					if(oAuth.getAuthorities()!=null) {
						for(GrantedAuthority ga : oAuth.getAuthorities()) {
							UserRoleDto ur = new UserRoleDto();
							ur.setActive(true);
							ur.setAuthority(ga.getAuthority());
							ret.getGranted().add(ur);
						}
					}
				}else if(ret.getName() == null) {
					ret.setName(oAuth.getFullName());
				}
			}else {
				ret= (UserDetailsDTO) auth.getPrincipal();
			}
		}
		return ret;
	}
	/**
	 * Change user role, reload user auth object
	 * @param auth
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public UserFormDTO userRoleChange(Authentication auth, UserFormDTO data) throws ObjectNotFoundException {
		//load prncipal's data
		UserDetailsDTO udto = new UserDetailsDTO();
		udto = userData(auth, udto); 
		Optional<User> usero = userRepo.findByEmail(udto.getEmail());
		if(usero.isPresent()) {
			//try to change the role
			if(data.getUserRoleFld().getValue().getId()>0) {
				boolean isOldRole = false;
				for(User_role ur : usero.get().getRoles()) {
					if(ur.getID()==data.getUserRoleFld().getValue().getId()){
						isOldRole = true;
						usero.get().setCurrentRole(ur);
						userRepo.save(usero.get());
						udto =  userToDTO(usero.get());
						replaceAuthorization(auth, udto);
						break;
					}
				}
				if(!isOldRole) {
					Concept r = closureServ.loadConceptById(data.getUserRoleFld().getValue().getId());
					usero.get().setConceptRole(r);
					userRepo.save(usero.get());
					udto =  userToDTO(usero.get());
					replaceAuthorization(auth, udto);
				}
			}
		}
		//create the result
		data = createFields(udto);
		return data;
	}
	/**
	 * Replace principal authorization to the new one
	 * @param auth 
	 * @param udto
	 */
	private void replaceAuthorization(Authentication auth, UserDetailsDTO udto) {
		Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
		if (udto != null) {
			mappedAuthorities.addAll(udto.getGranted());
		}
		if(auth.getPrincipal() instanceof OidcUser) {
			OidcUser oidcUser = (OidcUser) auth.getPrincipal();
			OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(oidcUser, 
					mappedAuthorities,(((OAuth2AuthenticationToken)auth).getAuthorizedClientRegistrationId()));
			SecurityContextHolder.getContext().setAuthentication(newAuth);
		}
		if(auth.getPrincipal() instanceof UserDetailsDTO) {
			UsernamePasswordAuthenticationToken  newAuth = new UsernamePasswordAuthenticationToken (udto, 	
					new SimpleGrantedAuthority(udto.getGranted().get(0).getAuthority()),mappedAuthorities);
			SecurityContextHolder.getContext().setAuthentication(newAuth);
		}
	}
	/**
	 * List users from an organization
	 * The organization is defined by its concept database record
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public UserElementDTO listUsers(UserElementDTO data) throws ObjectNotFoundException {
		if(data.getNode().getTable().getHeaders().getHeaders().size()==0) {
			data.getNode().getTable().getHeaders().getHeaders().add(
					TableHeader.instanceOf(
							"pref",
							"username",
							true,
							true,
							true,
							TableHeader.COLUMN_LINK,
							0
							)
					);
			data.getNode().getTable().getHeaders().getHeaders().add(
					TableHeader.instanceOf(
							"description",
							"description",
							true,
							true,
							true,
							TableHeader.COLUMN_STRING,
							0
							)
					);
			data.getNode().getTable().getHeaders().getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
			data.getNode().getTable().setHeaders(boilerServ.translateHeaders(data.getNode().getTable().getHeaders()));
		}
		TableQtb table = data.getNode().getTable();
		jdbcRepo.userByOrganization(data.getConceptId());
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from users_org", "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		data.getNode().getTable().setSelectable(false);
		data.getNode().getTitle().clear();
		data.getNode().getTitle().addAll(createTitle(data));
		return data;
	}
	/**
	 * Create a title for users
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private List<String> createTitle(UserElementDTO data) throws ObjectNotFoundException {
		List<String> ret = new ArrayList<String>();
		ret.add(messages.get("persons"));
		if(data.getConceptId()>0) {
			Concept org=closureServ.loadConceptById(data.getConceptId());
			ret.add(literalServ.readValue("prefLabel", org));
		}
		return ret;
	}

	/**
	 * Load user data by userElement
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public UserElementDTO userLoad(UserElementDTO data) throws ObjectNotFoundException {
		long conceptId = data.getConceptId();
		User user = null;
		//try to get userId from the data or selection field 
		long userId=data.getId();
		if(userId==0) {
			userId = data.getAddPerson().getValue().getId();
		}
		if(userId==0) {									//nothing selected
			//first time call or reset data
			data = new UserElementDTO();
			data.setConceptId(conceptId);
			//re-create the selection from suspended users
			List<User> usersCanAdd = userRepo.findByOrganization(null);
			data.getAddPerson().setValue(dtoServ.userList(usersCanAdd));
		}else {
			//something selected
			data.setId(userId);
			user = loadUserById(userId);
		}
		data = userElementDTO(user, data);
		data.getNode().setUrl(SystemService.PERSON_URL);
		return data;
	}

	/**
	 * Create userElementDTO from User (person?)
	 * @param user can be null for new users!!!!
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public UserElementDTO userElementDTO(User user, UserElementDTO data) throws ObjectNotFoundException {
		//prepare literals
		data.getNode().getLiterals().clear();
		List<AssemblyDTO> auxLiterals = assemblyServ.auxUserLiterals();
		data.setNode(dictServ.createLiterals(auxLiterals, data.getNode()));
		//prepare dictionaries
		data.getApplDicts().clear();
		List<String> dictUrls = systemServ.applicationLifeCycleUrls();
		for(String url : dictUrls) {
			DictionaryDTO dict = new DictionaryDTO();
			dict.setUrl(url);
			dict.setVarName(url);
			dict.setRequired(true);
			dict.setMult(true);
			dict.setReadOnly(false);
			dict.setSelectedOnly(false);
			data.getApplDicts().put(url, dict);
		}
		//read data if one
		if(user != null) {
			Concept orga = user.getOrganization();
			if(orga != null) {
				data.setOrganization(literalServ.readPrefLabel(user.getOrganization()));
			}
			data.setId(user.getUserId());
			data.getNode().setNodeId(user.getConcept().getID());
			if(user.getEmail()!=null) {
				data.getUser_email().setValue(user.getEmail());
			}
			data.getNode().setLiterals(dtoServ.readAllLiterals(data.getNode().getLiterals(), user.getConcept()));
			data.getGlobal_enable().setValue(dtoServ.booleanToYesNo(user.getEnabled()));
			//read dictionaries
			for(UserDict dictVal : user.getDictionaries()) {
				if(dictVal.getUrl().equalsIgnoreCase(SystemService.DICTIONARY_SYSTEM_ROLES)) {
					data.getRoles().getPrevSelected().add(dictVal.getConcept().getID());
				}
				DictionaryDTO applDict = data.getApplDicts().get(dictVal.getUrl());
				if(applDict!=null) {
					applDict.getPrevSelected().add(dictVal.getConcept().getID());
				}
			}
			
		}else {
			data.getGlobal_enable().setValue(dtoServ.booleanToYesNo(false));
		}
		//load dictionaries
		data.setRoles(systemServ.userRolesDict(data.getRoles()));
		for(String key : data.getApplDicts().keySet()) {
			DictionaryDTO dict = data.getApplDicts().get(key);
			dict=dictServ.createDictionary(dict);
		}
		return data;
	}

	@Transactional
	public User loadUserById(long id) throws ObjectNotFoundException {
		Optional<User> usero= userRepo.findById(id);
		if(usero.isPresent()) {
			return usero.get();
		}else {
			throw new ObjectNotFoundException("User not found. userid is "+id);
		}
	}

	@Transactional
	public UserElementDTO userSave(UserElementDTO data) throws ObjectNotFoundException {
		data = validationServ.person(data, true);
		if(data.isValid()) {

			//get or create a user
			User user = new User();
			if(data.getId()>0) {
				user=loadUserById(data.getId());
			}
			if(user.getUserId()==0) {
				user.setPassword("123");
				user = userRepo.save(user);
			}
			try {
				InternetAddress ia = new InternetAddress(data.getUser_email().getValue(),true);
				user.setEmail(ia.getAddress());
			} catch (AddressException e) {
				throw new ObjectNotFoundException("Invalid email "+data.getUser_email().getValue());
			}
			//save literals
			Concept node = new Concept();
			node.setIdentifier(data.getUser_email().getValue());
			data.getNode().setUrl(SystemService.PERSON_URL);
			Concept root=closureServ.loadRoot(data.getNode().getUrl());
			if(data.getNode().getNodeId()>0) {
				node=closureServ.loadConceptById(data.getNode().getNodeId());
				node.setIdentifier(data.getUser_email().getValue());
				node=closureServ.save(node);
			}else {
				node=closureServ.saveToTree(root, node);
			}

			literalServ.saveFields(data.getNode().getLiterals(), node);
			// save organization
			Concept org = closureServ.loadConceptById(data.getConceptId()); 
			user.setOrganization(org);
			user.setConcept(node);
			//save dictionaries
			user.getDictionaries().clear();
			user.getDictionaries().addAll(createDictItems(data.getRoles()));
			for(String key : data.getApplDicts().keySet()) {
				user.getDictionaries().addAll(createDictItems(data.getApplDicts().get(key)));
			}
			//other fields
			YesNoNA enable = dtoServ.optionToEnum(YesNoNA.values(), data.getGlobal_enable().getValue());
			if(enable==YesNoNA.YES) {
				user.setEnabled(true);
			}else {
				user.setEnabled(false);
			}
			List<UserRoleDto> allRoles = userGetAllAuthorities(user);
			if(user.getCurrentRole()==null && user.getConceptRole()==null) {
				for(UserRoleDto urd : allRoles) {
					if (urd.getConceptId()>0) {
						Concept conc = closureServ.loadConceptById(urd.getConceptId());
						user.setConceptRole(conc);
					}
				}
			}
			if(allRoles.size()==0) {						//drop all roles
				user.setConceptRole(null);
				user.setCurrentRole(null);
			}
			user = userRepo.save(user);
		}
		return data;
	}
	
	@Transactional
	public UserDetailsDTO updateUser(UserDetailsDTO data) throws ObjectNotFoundException {
		User user = userRepo.findByEmail(data.getEmail()).get();
		user.setPassword(data.getPassword());
		user = userRepo.save(user);
		
		data = userToDTO(user);
		return data;
	}

	/**
	 * Create links to dictionary items
	 * @param dict
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private List<UserDict> createDictItems(DictionaryDTO dict) throws ObjectNotFoundException {
		List<UserDict> ret = new ArrayList<UserDict>();
		Set<Long> selected = dictServ.selectedItems(dict);
		for(Long id : selected) {
			UserDict ud=new UserDict();
			Concept d=closureServ.loadConceptById(id);
			ud.setConcept(d);
			ud.setUrl(dict.getUrl());
			ret.add(ud);
		}
		return ret;
	}

	/**
	 * Unlink a person from an organization
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public UserElementDTO userSuspend(UserElementDTO data) throws ObjectNotFoundException {
		if(data.getConceptId()>0 && data.getId()>0) {
			User user = loadUserById(data.getId());
			user.setOrganization(null);
			user.setEnabled(false);
			user=userRepo.save(user);
			data.setConceptId(0);
		}
		return data;
	}
	/**
	 * Is this user supervisor?
	 * @param user
	 * @return
	 */
	public boolean isSupervisor(UserDetailsDTO user) {
		List<UserRoleDto> roles= user.getGranted();
		if(roles.size()==1) {
			return roles.get(0).getAuthority().toUpperCase().contains("ADMIN");
		}else {
			return false;
		}
	}

	public List<User> loadUsersByRole(String role) throws ObjectNotFoundException{
		List<User> users = new ArrayList<User>();
		//conceptRepo.findByIdentifierIgnoreCase(node.getIdentifier());

		List<Concept> concepts = conceptRepo.findByIdentifierIgnoreCase(role);
		//closureServ.loadConceptById(4898);
		Concept roleConcept = concepts.get(0);
		List<UserDict> dicts = userDictRepo.findAllByConceptAndUrl(roleConcept, SystemService.DICTIONARY_SYSTEM_ROLES);
		if(dicts != null && dicts.size() > 0) {
			for(UserDict d:dicts) {
				List<User> list = userRepo.findAllByDictionaries(d);
				users.addAll(list);
			}
		}


		return users;
	}
	/**
	 * Search for NMRA users that suit criteria - role+responsibility+territory
	 * @deprecated
	 * @param roleNodes
	 * @param respNodes
	 * @param addr
	 * @return list of eMail
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<String> findUsers(List<Concept> roleNodes, List<Concept> respNodes, Concept addr) throws ObjectNotFoundException {
		//find possible users
		List<User> possibleUsers = new ArrayList<User>();
		for(Concept role :roleNodes) {			//for dictionary.system.roles role names are in identifiers
			List<User> users=loadUsersByRole(role.getIdentifier());
			possibleUsers.addAll(users);
		}

		//apply responsibility restriction
		List<User> respUsers = findByDictNodes(possibleUsers, respNodes);
		//apply territory restriction
		List<User> terrUsers = new ArrayList<User>();
		if(addr.getID()>0) {
			List<User> tResp = findTerritoryResponsible(respUsers);
			if(tResp.size()>0) {
				List<User> t1Resp = findClosestTo(tResp,addr);
				if(t1Resp.size()>0) {
					terrUsers.addAll(t1Resp);
					if(terrUsers.size()==0) {
						terrUsers.addAll(findAllCountryResponsible(respUsers));
					}
				}else {
					terrUsers.addAll(respUsers);
				}
			}else {
				terrUsers.addAll(respUsers);
			}
		}else {
			terrUsers.addAll(respUsers);
		}

		//enabled only
		List<String> ret = new ArrayList<String>();
		for(User user :terrUsers) {
			if(user.getEnabled()) {
				ret.add(user.getEmail());
			}
		}
		//if nobody found - supervisor is the last resort
		if(ret.size()==0) {
			List<User> admin = loadUsersByRole("ROLE_ADMIN");
			for(User user :admin) {
				if(user.getEnabled()) {
					ret.add(user.getEmail());
				}
			}
		}
		return ret;
	}
	/**
	 * Users, responsible for all country
	 * @param respUsers
	 * @return
	 */
	@Transactional
	private List<User> findAllCountryResponsible(List<User> respUsers) {
		List<User> ret = new ArrayList<User>();
		boolean found=false;
		for(User u :respUsers) {
			found=false;
			for(UserDict ud : u.getDictionaries()) {
				if(ud.getUrl().equalsIgnoreCase(SystemService.DICTIONARY_ADMIN_UNITS)) {
					found=true;
					break;
				}
			}
			if(!found) {
				ret.add(u);
			}
		}
		return ret;
	}

	/**
	 * Find the best suitable users responsible for territory in addr 
	 * @param users
	 * @param addr
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private List<User> findClosestTo(List<User> users, Concept addr) throws ObjectNotFoundException {
		List<User> ret = new ArrayList<User>();
		//determine address
		Thing athing = boilerServ.thingByNode(addr);
		Concept dictNode= new Concept();
		for(ThingDict td :athing.getDictionaries()) {
			if(td.getUrl().contentEquals(SystemService.DICTIONARY_ADMIN_UNITS)) {
				dictNode=td.getConcept();
				break;
			}
		}
		//calculate user closest to the address
		if(dictNode.getID()>0) {
			List<Concept> parents = closureServ.loadParents(dictNode);
			List<Long> parIds=closureServ.extractIds(parents);
			int max=-1;
			int index=-1;
			int j=0;
			for(User user :users) {
				for(UserDict ud : user.getDictionaries()) {
					int mind=parIds.indexOf(ud.getConcept().getID());
					if(mind>max) {
						index=j;
						max=mind;
					}
				}
				j++;
			}
			if(index>-1) {
				ret.add(users.get(index));
			}
		}
		return ret;
	}

	/**
	 * Select all users with responsible restricted by territory or for all territories
	 * @param users list of 
	 * @return
	 */
	private List<User> findTerritoryResponsible(List<User> users) {
		List<User> ret = new ArrayList<User>();
		for(User user  :users) {
			for(UserDict ud : user.getDictionaries()) {
				if(ud.getUrl().equalsIgnoreCase(SystemService.DICTIONARY_ADMIN_UNITS)) {
					ret.add(user);
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Select only users linked to dictionary nodes provided
	 * @param users
	 * @param dictNodes
	 * @return
	 */
	private List<User> findByDictNodes(List<User> users, List<Concept> dictNodes) {
		List<User> ret = new ArrayList<User>();
		List<Long> ids = closureServ.extractIds(dictNodes);
		for(User user :users) {
			if(user.getDictionaries()!=null) {
				for(UserDict ud : user.getDictionaries()) {
					if(ids.contains(ud.getConcept().getID())) {
						ret.add(user);
						break;
					}
				}
			}
		}
		return ret;
	}
	/**
	 * User responsibilities.
	 * @param u - user
	 * @return list of IDs of nodes from application life cycle dictionaries, i.e. applications allowed to the user 
	 */
	public List<Long> responsibilities(User u) {
		List<Long> ret = new ArrayList<Long>();
		List<String> dictUrls = systemServ.applicationLifeCycleUrls();
		for(UserDict ud :u.getDictionaries()) {
			if(dictUrls.contains(ud.getUrl())) {
				ret.add(ud.getConcept().getID());
			}
		}
		return ret;
	}

}
