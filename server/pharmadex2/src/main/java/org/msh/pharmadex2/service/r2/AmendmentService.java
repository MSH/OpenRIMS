package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.dto.AmendmentDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DataConfigDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible for amendments
 * @author alexk
 *
 */
@Service
public class AmendmentService {
	private static final Logger logger = LoggerFactory.getLogger(AmendmentService.class);
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private Messages messages;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private AssemblyService assemServ;
	@Autowired
	private ValidationService validator;
	/**
	 * Load amendments initiated by a user given
	 * @param user
	 * @param data
	 */
	public AmendmentDTO amendments(UserDetailsDTO user, AmendmentDTO data) {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(headersList(table.getHeaders()));
		}
		//TODO load a table
		table.setSelectable(false);
		return data;
	}
	/**
	 * Headers for a list of applicant's amendments
	 * @param headers
	 * @return
	 */
	private Headers headersList(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"descr",
				"description",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"chapter",
				"chapter",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"amendment",
				"amendment",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"created",
				"global_createdate",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"state",
				"state",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers=boilerServ.translateHeaders(headers);
		return headers;
	}
	/**
	 * Load an amendment or create new one
	 * @param user
	 * @param data
	 * @return
	 */
	public AmendmentDTO amendmentLoad(UserDetailsDTO user, AmendmentDTO data) {
		if(data.getNodeId()==0) {
			//TODO the new amendment
			data.setTitle(messages.get("process_amdmt"));
		}else {
			//TODO load existing one

		}
		return data;
	}
	/**
	 * Load objects registered by the current user
	 * @param user
	 * @param data
	 * @return
	 */
	public AmendmentDTO objectRegisteredLoad(UserDetailsDTO user, AmendmentDTO data) {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0){
			table.setHeaders(headersRegistered(table.getHeaders()));
		}
		jdbcRepo.registerdObjects(user.getEmail(),null);
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from _registered", "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		for(TableRow row :rows) {
			if(row.getDbID()==data.getAppl().getNodeId()) {
				row.setSelected(true);
			}
		}
		return data;
	}
	/**
	 * headers for registered objects
	 * @param headers
	 * @return
	 */
	private Headers headersRegistered(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regno",
				"reg_number",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regdate",
				"registration_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"expdate",
				"expiry_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				0));

		headers=boilerServ.translateHeaders(headers);
		return headers;
	}

	/**
	 * load chapters configured for this thing.
	 * Chapters are:
	 * <ul>
	 * <li> the main chapter - thing itself
	 * <li> the rest of chapters - all included things. Persons and, in a future others aux should be expanded
	 * </ul>
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DataConfigDTO thingChaptersLoad(DataConfigDTO data) throws ObjectNotFoundException {
		if(data.getNodeId()>0) {
			//determine this
			Concept conc = closureServ.loadConceptById(data.getNodeId());
			String title=literalServ.readPrefLabel(conc);
			List<Concept> parents = closureServ.loadParents(conc);
			String url=parents.get(0).getIdentifier();
			//determine excluded
			List<AssemblyDTO> assms = assemServ.auxThings(url);
			List<String> exclUrls = new ArrayList<String>();
			for(AssemblyDTO assm :assms) {
				if(assm.isReadOnly()) {
					exclUrls.add(assm.getUrl());
				}
			}
			//determine path
			ThingDTO thing = new ThingDTO();
			thing.setNodeId(data.getNodeId());
			thing.setUrl(url);
			List<ThingDTO> path = thingServ.createPath(thing, new ArrayList<ThingDTO>(),-1);
			//create a table
			data.getTable().getHeaders().getHeaders().clear();
			data.getTable().getHeaders().getHeaders().add(TableHeader.instanceOf(
					"prefLabel",
					"prefLabel",
					true,
					false,
					false,
					TableHeader.COLUMN_LINK,
					0));
			boilerServ.translateHeaders(data.getTable().getHeaders());
			data.getTable().getRows().clear();
			for(int i=0;i<path.size();i++) {
				String u=path.get(i).getUrl();
				if(!exclUrls.contains(u)) {
					TableRow row = TableRow.instanceOf(path.get(i).getNodeId());
					if(i==0) {
						row.getRow().add(TableCell.instanceOf(thing.getUrl(), title));
					}else {
						row.getRow().add(TableCell.instanceOf(path.get(i).getUrl(), path.get(i).getTitle()));
					}
					if(row.getDbID()==data.getVarNodeId()) {
						row.setSelected(true);
					}
					data.getTable().getRows().add(row);
				}
			}
			data.getTable().getHeaders().setPageSize(200);
			return data;
		}else {
			throw new ObjectNotFoundException("thingChaptersLoad. Node ID iz ZERO",logger);
		}
	}
	/**
	 * Table of chapter variables to select which are have been amended
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public AmendmentDTO chapterVariables(UserDetailsDTO user, AmendmentDTO data) throws ObjectNotFoundException {
		if(data.getChapter().getNodeId()>0) {
			TableQtb table = data.getVariables();
			if(table.getHeaders().getHeaders().size()==0) {
				table.setHeaders(headersVariables(table.getHeaders()));
			}
			Concept root = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
			List<Concept> collection = literalServ.loadOnlyChilds(root);
			long nodeId=0;
			for(Concept elem : collection) {
				if(elem.getIdentifier().equalsIgnoreCase(data.getChapter().getUrl())) {
					nodeId=elem.getID();
				}
			}
			if(nodeId>0) {
				String where="Clazz not in ('heading', 'things','personselector', 'resources') and Active=true and nodeID='"+nodeId+ "' and lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
				List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from assm_var", "", where, table.getHeaders());
				TableQtb.tablePage(rows, table);
				table.setSelectable(true);
				table=boilerServ.translateRows(table);
			}
		}
		return data;
	}
	/**
	 * Create headers for variables amended table
	 * @param headers
	 * @return
	 */
	private Headers headersVariables(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"propertyName",
				"prefLabel",
				true,
				false,
				false,
				TableHeader.COLUMN_I18LINK,
				0)
				);
		headers=boilerServ.translateHeaders(headers);
		return headers;
	}
	/**
	 * Validate and save an amendment
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public AmendmentDTO save(UserDetailsDTO user, AmendmentDTO data) throws ObjectNotFoundException {
		data =validator.amendment(data);
		if(data.isValid()) {
			
		}
		return data;
	}

}
