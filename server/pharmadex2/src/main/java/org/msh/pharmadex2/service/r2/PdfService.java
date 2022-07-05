package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingPerson;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PdfService {
	private static final Logger logger = LoggerFactory.getLogger(PdfService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private AssemblyService assemblyServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private ThingService thingServ;

	public ThingDTO printprev(UserDetailsDTO user, ThingDTO dto) throws ObjectNotFoundException {
		if(dto.getHistoryId()>0) {
			History his = boilerServ.historyById(dto.getHistoryId());
			logger.info("printprev start");
			dto.setNodeId(his.getApplicationData().getID());
		}
		if(dto.getNodeId()>0) {
			dto = thingServ.loadThing(dto, user);
			List<ThingDTO> newpath = buildListThings(user, dto);
			List<ThingDTO> realPath = new ArrayList<ThingDTO>();
			for(ThingDTO t : newpath) {
				if(t.getNodeId()>0) {
					realPath.add(t);
				}
			}
			dto.getPath().clear();
			dto.getPath().addAll(realPath);
			logger.info("printprev end");
			return dto;
		}else {
			throw new ObjectNotFoundException("printprev. Node ID and/or History Id is(are) ZERO",logger);
		}
	}

	public List<ThingDTO> buildListThings(UserDetailsDTO user, ThingDTO mainThing) throws ObjectNotFoundException{
		List<ThingDTO> list = new ArrayList<ThingDTO>();

		List<ThingDTO> path = thingServ.path(mainThing).getPath();
		for(ThingDTO thing:path) {
			if(thing.getNodeId() > 0) {
				//thing = thingServ.loadThing(thing, user);
				//thing = thingServ.path(thing);
				list.add(thing);
				List<Assembly> allAssms = assemblyServ.loadDataConfiguration(thing.getUrl());
				List<AssemblyDTO> persons = assemblyServ.auxPersons(thing.getUrl(),allAssms);
				if(persons != null && persons.size() > 0) {
					for(AssemblyDTO assembly:persons) {
						Concept pconcept = closureServ.loadConceptById(thing.getNodeId());
						Thing th = boilerServ.thingByNode(pconcept);
						for(ThingPerson tp:th.getPersons()) {
							if(tp.getVarName().equalsIgnoreCase(assembly.getPropertyName())) {
								ThingDTO thingPerson = new ThingDTO();
								thingPerson.setNodeId(tp.getConcept().getID());
								thingPerson.setReadOnly(true);
								thingPerson = thingServ.loadThing(thingPerson, user);
								thingPerson = thingServ.path(thingPerson);
								list.addAll(thingPerson.getPath());
								//list.add(thingPerson);
							}
						}

					}
				}
			}
		}

		return list;
	}
}
