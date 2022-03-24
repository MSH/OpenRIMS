package org.msh.pharmadex2.service.r2;

import org.apache.commons.lang3.StringUtils;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IrkaServices {
	private static final Logger logger = LoggerFactory.getLogger(IrkaServices.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private Messages messages;
	
	public RegisterDTO registerNumberNew(UserDetailsDTO user, RegisterDTO data) {
		
		Long num = jdbcRepo.register_number(data.getUrl()); //from 2022-03-07 on save
		String numStr=RegisterDTO.EMPTY+num;
		numStr=StringUtils.right(numStr, 6);
		data.getReg_number().setValue(data.getNumberPrefix()+numStr);
		return data;
	}

}
