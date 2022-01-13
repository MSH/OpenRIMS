package org.msh.pharmadex2.service.r2;

import java.util.List;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Scheduler;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingScheduler;
import org.msh.pharmadex2.dto.SchedulerDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible for follow up actions initiated by Scheduler components
 * @author alexk
 *
 */
@Service
public class FollowUpService {
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private Messages mess;
	/**
	 * Load schedulers from thing
	 * @param thing
	 * @param sdto 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public SchedulerDTO schedulerFromThing(Thing thing, SchedulerDTO sdto){
		if(thing!=null && thing.getID()>0) {
			for(ThingScheduler ts : thing.getSchedulers()) {
				if(ts.getVarName().equalsIgnoreCase(sdto.getVarName())) {
					Scheduler sc;
					try {
						sc = boilerServ.schedulerByNode(ts.getConcept());
						sdto.setNodeId(thing.getConcept().getID());
						sdto.setConceptId(ts.getConcept().getID());
						sdto.getSchedule().setValue(boilerServ.convertToLocalDateViaMilisecond(sc.getScheduled()));
						sdto.setCreatedAt(boilerServ.convertToLocalDateViaMilisecond(sc.getCreatedAt()));
					} catch (ObjectNotFoundException e) {
						//nothing to do
					}
				}
			}
		}
		return sdto;
	}

	/**
	 * Is this scheduler empty
	 * @param sdto
	 * @return
	 */
	public boolean isEmpty(SchedulerDTO sdto) {
		return sdto.getConceptId()==0;
	}

	/**
	 * Get a data for schedulerDTO from a scheduler found by application data an and scheduler's data URL
	 * @param applData
	 * @param dataUrl
	 * @return the scheduler or throw an exception
	 * @throws ObjectNotFoundException 
	 */
	public SchedulerDTO schedulerFromSchedulers(Concept applData, String dataUrl, SchedulerDTO data) throws ObjectNotFoundException {
		if(applData != null && applData.getID()>0) {
			List<Scheduler> sl = boilerServ.schedulerList(dataUrl,applData.getID());
			if(sl.size()<=1) {
				if(sl.size()==1) {
					Scheduler sc = sl.get(0);
					data.getSchedule().setValue(boilerServ.convertToLocalDateViaMilisecond(sc.getScheduled()));
					data.setConceptId(sc.getConcept().getID());
				}
			}else {
				data.setValid(false);
				data.setIdentifier(mess.get("error_duplicatedscheduler"));
			}
		}
		return data;
	}

}
