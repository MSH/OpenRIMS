package org.msh.pharmadex2.service.r2;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IrkaServices {
	private static final Logger logger = LoggerFactory.getLogger(IrkaServices.class);
	@Autowired
	private ClosureService closureServ;
	/**
	 * Get new number from a journal
	 * @param user
	 * @param data
	 * @return
	 */
	@Transactional
	public RegisterDTO registerNumberNew(UserDetailsDTO user, RegisterDTO data) throws ObjectNotFoundException {
		data.clearErrors();
				//Long num = jdbcRepo.register_number(data.getUrl()); //from 2022-03-07 on save, currently not needed at all
		List<Concept> conList= closureServ.loadAllConceptsByIdentifier(data.getUrl());
		//do we need a new concept?
		Concept concept = new Concept();
		if(conList.size()>0) {
			concept=conList.get(0);
		}
		String numStr="";
		String yearStr="";
		int regYear=data.getRegistration_date().getValue().getYear();
		if(conList.size()==0) {
			Long num=(long) 1;
			 numStr=RegisterDTO.EMPTY+num;
			 yearStr=String.valueOf(regYear);
		}else {
			for(Concept conc : conList) {
				String label=conc.getLabel();
				if(label!=null && !label.isEmpty()) {
					String l[]=label.split(",");
					int year = Integer.parseInt(l[1]);
					if(regYear==year) {
						numStr=RegisterDTO.EMPTY+l[0];
						yearStr=l[1];
					}
				}
			}
		}		
	if(numStr.isEmpty() && yearStr.isEmpty()) {
		Long num=(long) 1;
		 numStr=RegisterDTO.EMPTY+num;
		 yearStr=String.valueOf(regYear);
	}
	concept.setActive(true);
	concept.setIdentifier(data.getUrl());
	int numNext=Integer.parseInt(numStr)+1;
	concept.setLabel(numNext+","+yearStr);
	closureServ.save(concept);
		numStr=StringUtils.right(numStr, 6);
		data.getReg_number().setValue(data.getNumberPrefix()+numStr+"-"+yearStr);
		return data;
	}

}
