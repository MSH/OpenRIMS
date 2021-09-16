package org.msh.pharmadex2.dto.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * User details to display on screen and to use for Spring Security
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserDetailsDTO extends AllowValidation implements UserDetails {
	private static final long serialVersionUID = -6035017843014009134L;
	private String login = "";
	private String name="";
	private String password="";
	private String email="";
	private String state="";
	private String language="en_US";
	private boolean expired=false;
	private boolean locked=false;
	private boolean active=true;
	private List<UserRoleDto> allRoles = new ArrayList<UserRoleDto>();	//the list of possible roles may be granted
	private List<UserRoleDto> granted = new ArrayList<UserRoleDto>();	//only one role may be granted at any given moment
	private String message="";



	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<UserRoleDto> getGranted() {
		return granted;
	}

	public void setGranted(List<UserRoleDto> granted) {
		this.granted.clear();
		this.granted.addAll(granted);
	}

	public List<UserRoleDto> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(List<UserRoleDto> allRoles) {
		this.allRoles.clear();
		this.allRoles.addAll(allRoles);
	}



	/**
	 * For rest api login
	 * @return
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * For rest api login
	 * @return
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getGranted();
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return login;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !isExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return ! isLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isActive();
	}

}
