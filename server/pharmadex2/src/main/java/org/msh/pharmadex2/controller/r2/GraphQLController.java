package org.msh.pharmadex2.controller.r2;

import java.util.List;

import org.msh.pharmadex2.dto.ReportPageGQL;
import org.msh.pharmadex2.service.r2.GraphQLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


@Controller
public class GraphQLController {
	@Autowired
	private GraphQLService graphQLService;
    
    /*@SchemaMapping(typeName="LegacyData", field="concept")
    public Concept getConcept(LegacyData ld) {
    	return assService.loadConceptByLD(ld);
    }*/
    
    @QueryMapping
    public List<ReportPageGQL> loadReportPages(@Argument String lang, @Argument String url, @Argument String district, @Argument String pharmtype) {
    	//String lang = LocaleContextHolder.getLocale().toString().toUpperCase();
		//String url = "retail.site.owned.persons";//"pharmacy.retail.registered";
		//String district = null;//"BAJURA";
		//String pharmtype = null;//"Allopathy";
		
    	List<ReportPageGQL> list = graphQLService.loadReportPages(lang, url, district, pharmtype);

    	return list;
    }
}
