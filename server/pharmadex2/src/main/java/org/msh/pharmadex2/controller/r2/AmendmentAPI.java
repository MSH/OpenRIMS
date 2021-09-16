package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.AmendmentDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.AmendmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Amendment/Modifications controls
 * @author alexk
 *
 */
@RestController
public class AmendmentAPI {
	@Autowired
	private AmendmentService amendServ;
	@Autowired
	private UserService userServ;
	/**
	 * Load my amendments
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/amendments")
	public AmendmentDTO amendments(Authentication auth, @RequestBody AmendmentDTO data) throws DataNotFoundException{
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		data=amendServ.amendments(user, data);
		return data;
	}
	
	/**
	 * Load an amendment
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/application/amendment/load")
	public AmendmentDTO load(Authentication auth, @RequestBody AmendmentDTO data) throws DataNotFoundException{
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		data=amendServ.amendmentLoad(user, data);
		return data;
	}
	
	/**
	 * Load variables table
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/amendment/chapter/variables")
	public AmendmentDTO chapterVariables(Authentication auth, @RequestBody AmendmentDTO data) throws DataNotFoundException{
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=amendServ.chapterVariables(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/amendment/save")
	public AmendmentDTO save(Authentication auth, @RequestBody AmendmentDTO data) throws DataNotFoundException{
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
			try {
				data=amendServ.save(user, data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
		return data;
	}

}
