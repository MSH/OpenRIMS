package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Responsible for accountant's things
 * @author alexk
 *
 */
@RestController
public class AccountantAPI {
	@Autowired
	ContentService contentService;
	
	
	@PostMapping("/api/accountant/content")
	public ContentDTO adminContent(@RequestBody ContentDTO data) throws DataNotFoundException {
		try {
			data=contentService.loadContent(data, "accountant");
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	

}
