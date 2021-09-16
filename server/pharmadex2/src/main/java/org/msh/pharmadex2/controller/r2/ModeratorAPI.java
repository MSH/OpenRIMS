package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Common API for all authenticated users
 * @author alexk
 *
 */
@RestController
public class ModeratorAPI {
	@Autowired
	UserService userService;
	@Autowired
	ContentService contentService;
	
	
	/**
	 * Tiles for landing page
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@PostMapping("/api/moderator/content")
	public ContentDTO loadContent(@RequestBody ContentDTO data) throws ObjectNotFoundException {
		data=contentService.loadContent(data, "moderator");
		return data;
	}
}
