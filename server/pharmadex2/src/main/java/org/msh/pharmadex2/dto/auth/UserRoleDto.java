package org.msh.pharmadex2.dto.auth;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * User role description to display and using for Spring Security
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserRoleDto extends AllowValidation implements GrantedAuthority {
	private static final long serialVersionUID = -6990177762624983658L;
	private String authority="";
	private long id=0;							//id of the user_role
	private long conceptId;				//id of the role concept
	private boolean active=false;

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getConceptId() {
		return conceptId;
	}

	public void setConceptId(long conceptId) {
		this.conceptId = conceptId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * Observer role
	 * @return
	 */
	public static UserRoleDto Observer() {
		UserRoleDto ret = new UserRoleDto();
		ret.setActive(true);
		ret.setAuthority("ROLE_OBSERVER");
		ret.setId(0L);
		return ret;
	}

	@Override
	public String toString() {
		return "UserRoleDto [authority=" + authority + ", id=" + id + ", active=" + active + "]";
	}
	/**
	 * Guest user role
	 * @return
	 */
	public static UserRoleDto guestUser() {
		UserRoleDto urd = new UserRoleDto();
		urd.setActive(true);
		urd.setAuthority("ROLE_GUEST");
		return urd;
	}

}
