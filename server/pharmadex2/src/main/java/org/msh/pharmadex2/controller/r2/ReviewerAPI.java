package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.ApplicationSelectDTO;
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
 * Reviewer's API
 * @author alexk
 *
 */
@RestController
public class ReviewerAPI {
	@Autowired
	UserService userService;
	@Autowired
	ContentService contentService;
	@Autowired
	private ApplicationService applServ;
	@Autowired
	private UserService userServ;
	/**
	 * Tiles for Screener page
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@PostMapping("/api/reviewer/content")
	public ContentDTO loadContent(@RequestBody ContentDTO data) throws ObjectNotFoundException {
		data=contentService.loadContent(data, "reviewer");
		return data;
	}
	

}
