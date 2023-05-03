package org.msh.pharmadex2.service.r2;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.dto.ReportPageGQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * By work GraphQL 
 * @author khomenska
 *
 */
@Service
public class GraphQLService {

	private static final Logger logger = LoggerFactory.getLogger(GraphQLService.class);
	
	@Autowired
	private JdbcRepository jdbcRepo;
	
	public List<ReportPageGQL> loadReportPages(String lang, String url, String district, String pharmtype) {
		List<ReportPageGQL> result = new ArrayList<ReportPageGQL>();

		List<LinkedCaseInsensitiveMap<Object>> list = jdbcRepo.reportpagesQraphql(lang, url, district, pharmtype);
		if(list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++) {
				LinkedCaseInsensitiveMap<Object> item = list.get(i);
				ReportPageGQL pageItem = new ReportPageGQL();
				pageItem.setLang((String)item.get("lang"));
				pageItem.setUrl((String)item.get("url"));
				pageItem.setPharmacy((String)item.get("pharmacy"));
				pageItem.setDistrict((String)item.get("district"));
				pageItem.setAddress((String)item.get("address"));
				pageItem.setPharmtype((String)item.get("pharmtype"));
				pageItem.setRegisterNo((String)item.get("registerNo"));
				Date d = (Date)item.get("registered");
				pageItem.setRegistered(d.toLocalDate());
				d = (Date)item.get("expired");
				pageItem.setExpired(d.toLocalDate());
				pageItem.setOwners((String)item.get("owners"));
				pageItem.setPharmacists((String)item.get("pharmacists"));
				
				result.add(pageItem);
			}
		}
		
		return result;
	}
}

