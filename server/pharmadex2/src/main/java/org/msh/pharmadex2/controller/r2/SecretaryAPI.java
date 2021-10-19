package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Common API for role Secretary
 * @author alexk
 *
 */
@RestController
public class SecretaryAPI {
	@Autowired
	UserService userService;
	@Autowired
	ContentService contentService;
	
	
	/**
	 * Tiles for Secretary page
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@PostMapping("/api/secretary/content")
	public ContentDTO loadContent(@RequestBody ContentDTO data) throws ObjectNotFoundException {
		data=contentService.loadContent(data, "secretary");
		return data;
	}
}
