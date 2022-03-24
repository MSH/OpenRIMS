package org.msh.pharmadex2.service.r2;

import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.AmendmentNewDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * De-registration, suspend , etc
 * @author alexk
 *
 */
@Service
public class DeregistrationService {
	private static final Logger logger = LoggerFactory.getLogger(DeregistrationService.class);
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	/**
	 * Create a table to list all objects that suit criteria applicant + dictItemId
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public AmendmentNewDTO proposeAdd(UserDetailsDTO user, AmendmentNewDTO data) throws ObjectNotFoundException {
		TableQtb table = data.getApplications();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(headersApplications(table.getHeaders()));
		}
		if(data.getDictItemId()>0) {
			Concept dictNode = closureServ.loadConceptById(data.getDictItemId());
			String url = literalServ.readValue("url", dictNode);
			jdbcRepo.applications_hosted_inactive(url, user.getEmail(), true);
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from applications_hosted_inactive", "", "", table.getHeaders());
			TableQtb.tablePage(rows, table);
			table.setSelectable(true);
			for (TableRow row : table.getRows()) {
				if (row.getDbID() == data.getDataNodeId()) {
					row.setSelected(true);
				} else {
					row.setSelected(false);
				}
			}
		}
		return data;
	}

	/**
	 * Headers for applications list
	 * 
	 * @param headers
	 * @return
	 */
	private Headers headersApplications(Headers headers) {
		headers.getHeaders()
		.add(TableHeader.instanceOf("prefLabel", "prefLabel", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders().add(
				TableHeader.instanceOf("description", "description", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers = boilerServ.translateHeaders(headers);
		headers.setPageSize(50);
		return headers;
	}
	/**
	 * Is the current activity should be de-register?
	 * @param curHis
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isDeregistrationActivity(History curHis) throws ObjectNotFoundException {
		if(curHis.getActConfig()!=null) {
			Thing thing = boilerServ.thingByNode(curHis.getActConfig());
			for(ThingDict  dict : thing.getDictionaries()) {
				if(dict.getUrl().equalsIgnoreCase(SystemService. DICTIONARY_SYSTEM_FINALIZATION)){
					if(dict.getConcept().getIdentifier().equalsIgnoreCase("deregistration")){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * The workflow is de-reg
	 * @param curHis
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isDeregisterWorkflow(History curHis) throws ObjectNotFoundException {
		//de-registration is a kind of amendment
		Concept dict = closureServ.getParent(curHis.getApplDict());
		if(dict.getIdentifier().equalsIgnoreCase(SystemService.DICTIONARY_GUEST_DEREGISTRATION)){
			return true;
		}else {
			return false;
		}
	}
}
