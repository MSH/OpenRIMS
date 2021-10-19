package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.AtcDTO;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ExcipientsDTO;
import org.msh.pharmadex2.dto.InnsDTO;
import org.msh.pharmadex2.dto.UserFormDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.AtcService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.PubOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Common API for all authenticated users
 * @author alexk
 *
 */
@RestController
public class CommonAPI {
	@Autowired
	UserService userService;
	@Autowired
	ContentService contentService;
	@Autowired
	DictService dictServ;
	@Autowired
	PubOrgService orgServ;
	@Autowired
	AtcService atcServ;
	
	/**
	 * Get user's details for just authenticated user. For edit/display
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */	
	@RequestMapping(value= {"/api/common/user/role/change"}, method = RequestMethod.POST)
	public UserFormDTO userRoleChange(Authentication auth,
			@RequestBody UserFormDTO data) throws ObjectNotFoundException {
		data = userService.userRoleChange(auth, data);
		return data;
	}
	
	/**

	 * Tiles for landing page
	 * @param data
	 * @return
	 */
	@PostMapping("/api/common/guest/content")
	public ContentDTO landingContent(@RequestBody ContentDTO data) throws DataNotFoundException {
		try {
			data=contentService.loadContent(data, "guest");
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	
	/**
	 * Load the level of a dictionary
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/common/dictionary/level/load")
	public DictNodeDTO dictionaryLevelLoad(@RequestBody DictNodeDTO data) throws DataNotFoundException {
		try {
			data=dictServ.loadLevel(data);
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}	
	
	/**
	 * Load a dictionary
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/common/dictionary/load")
	public DictionaryDTO dictionaryLoad(@RequestBody DictionaryDTO data) throws DataNotFoundException {
			try {
				data=dictServ.load(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}	
	
	/**
	 * re-load a table of dictionary
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/common/dictionary/load/table")
	public DictionaryDTO dictionaryLoadTable(@RequestBody DictionaryDTO data) throws DataNotFoundException {
			try {
				data=dictServ.loadTable(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}
	
	/**
	 * Create a dictionary from path selection
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/common/dictionary/load/path")
	public DictionaryDTO dictionaryLoadPath(@RequestBody DictionaryDTO data) throws DataNotFoundException {
			try {
				data=dictServ.loadPath(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}	
	
	/**
	 * Load only a root level of a dictionary
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/common/dictionary/load/root")
	public DictionaryDTO dictionaryLoadRoot(@RequestBody DictionaryDTO data) throws DataNotFoundException {
			try {
				data=dictServ.rootDictionary(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}	
	
	/**
	 * Load next level of a dictionary
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/common/dictionary/load/next")
	public DictionaryDTO dictionaryLoadNext(@RequestBody DictionaryDTO data) throws DataNotFoundException {
			try {
				data=dictServ.nextDictionary(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}	
	
	/**
	 * Load from the database literals for defined node
	 * Or create empty literals for url. Created literals will not be stored to the database 
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/common/literals/load")
	public DictNodeDTO literalsLoad(@RequestBody DictNodeDTO data) throws DataNotFoundException {
		try {
			data=dictServ.literalsLoad(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	@PostMapping("/api/common/atc/load/table")
	public AtcDTO atcLoadTable(@RequestBody AtcDTO data) throws DataNotFoundException {
			try {
				data=atcServ.loadTable(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}
	
	@PostMapping("/api/common/excipient/load/table")
	public ExcipientsDTO excipientLoadTable(@RequestBody ExcipientsDTO data) throws DataNotFoundException {
			try {
				data=atcServ.loadTable(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}
	
	@PostMapping("/api/common/excipient/add")
	public ExcipientsDTO excipientAdd(@RequestBody ExcipientsDTO data) throws DataNotFoundException {
			try {
				data=atcServ.addExcipient(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}
	
	@PostMapping("/api/common/inn/load/table")
	public InnsDTO innLoadTable(@RequestBody InnsDTO data) throws DataNotFoundException {
			try {
				data=atcServ.loadTable(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}
	
	@PostMapping("/api/common/inn/add")
	public InnsDTO innAdd(@RequestBody InnsDTO data) throws DataNotFoundException {
			try {
				data=atcServ.addInn(data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
			return data;
	}
}
