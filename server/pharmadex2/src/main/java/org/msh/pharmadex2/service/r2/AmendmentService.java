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
import org.msh.pdex2.model.r2.ThingAmendment;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.dto.AmendmentDTO;
import org.msh.pharmadex2.dto.PersonSelectorDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Amendment related services
 * @author alexk
 *
 */
@Service
public class AmendmentService {
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	/**
	 * Create a table with amended objects
	 * @param dto
	 * @param user 
	 * @return
	 */
	public AmendmentDTO createTable(AmendmentDTO data, UserDetailsDTO user) {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(headersRegistered(table.getHeaders()));
		}
		jdbcRepo.registerdObjects(user.getEmail(),null);
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from _registered", "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		for(TableRow row :rows) {
			if(row.getDbID()==data.getDataNodeId()) {
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
	 * Save an amendment data
	 * @param user
	 * @param node
	 * @param thing
	 * @param data
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO save(UserDetailsDTO user, Concept node, Thing thing, ThingDTO data, AmendmentDTO dto) throws ObjectNotFoundException {
		//get selected
		long id = 0;
		for(TableRow row :dto.getTable().getRows()) {
			if(row.getSelected()) {
				id=row.getDbID();
			}
		}
		if(id>0) {
			Concept amended = closureServ.loadConceptById(id);
			String prefLabel = literalServ.readPrefLabel(amended);
			literalServ.createUpdatePrefLabel(prefLabel, node);
			data.setTitle(prefLabel);
			thing.getAmendments().clear();
			ThingAmendment ta = new ThingAmendment();
			ta.setConcept(amended);
			ta.setUrl(dto.getUrl());
			ta.setVarName(dto.getVarName());
			thing.getAmendments().add(ta);
		}
		return data;
	}
	/**
	 * Create a person selector table from an amended data
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public PersonSelectorDTO personSelectorTable(PersonSelectorDTO dto) throws ObjectNotFoundException {
		if(dto.getHistoryId()>0) {
			History his = boilerServ.historyById(dto.getHistoryId());
			Concept amended = amendedConcept(his.getApplicationData());
			if(amended.getID()>0) {
				TableQtb table = dto.getTable();
				if(table.getHeaders().getHeaders().size()==0) {
					table.setHeaders(boilerServ.headersPersonSelector(table.getHeaders()));
				}
				List<Long> selected = boilerServ.saveSelectedRows(table);
				//get data
				String lang = LocaleContextHolder.getLocale().toString().toUpperCase();
				String where = "appldataid='"+amended.getID() +
						//"' and personrooturl='"+dto.getPersonUrl()+
						"' and lang='"+lang+"'";
				List<TableRow> rows =jdbcRepo.qtbGroupReport("select * from personlist","", where, table.getHeaders());
				TableQtb.tablePage(rows, table);
				table=boilerServ.selectedRowsRestore(selected, table);
			}
		}
		return dto;
	}
	
	/**
	 * Search for root of amended data.
	 * It is presumed that the link to ameded data is always at the root thing!
	 * @param applicationData
	 * @return ID>0 if found, otherwise ID==0
	 */
	@Transactional
	public Concept amendedConcept(Concept applicationData) {
		Concept ret= new Concept();
		if(applicationData.getID()>0) {
			Thing thing = new Thing();
			thing=boilerServ.loadThingByNode(applicationData, thing);
			if(thing.getID()>0) {
				for(ThingAmendment ta : thing.getAmendments()) {
					ret=ta.getConcept();
					break;
				}
			}
		}
		return ret;
	}

}
