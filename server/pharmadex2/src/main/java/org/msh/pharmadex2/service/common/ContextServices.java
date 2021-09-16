package org.msh.pharmadex2.service.common;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.model.old.Context;
import org.msh.pdex2.model.old.Criteria;
import org.msh.pdex2.repository.common.ContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Despite we use REST API, sometimes it will be convenient to store user context information on the server
 * These information include, but not limited:
 * <ul>
 * <li> search criteria
 * <li> selected rows in some tables
 * <li> other preference
 * </ul>
 * The server - side application applies context for a user's REST API call by following algorithm:
 * <ul>
 * <li> Reads a cookie with known name, this cookie contains ID of user's context in the database. If context does not exist it will be created
 * <li> Reads the user's context from the database by the ID from the cookie. The context may contain at least one JSON object that represents stored Java object
 * <li> Each JSON object in context is linked to a name. This service can recognize object type and purpose by this name and provides restored Java object to other services and/or controllers
 * <li> Indeed, this service also provides feature to add (store) a Java object to the context :)
 * </ul>
 * We know that the usual way to link context to the user's record in the database, however, for some reason we prefer a computer where the cookie stored
 * 
 * @author Alex Kurasoff
 *
 */
@Service
public class ContextServices {


	/**
	 * name of cookie with context
	 */
	public static final String PDEX_CONTEXT = "PDex_Context";
	
	public static final String CURRENTROLE_ID = "ROLE_ID";
	/**
	 * criteria names for JSON storage
	 */
	@Autowired
	ContextRepository contextRepo;
	@Autowired
	ObjectMapper mapper;
	/**
	 * Create a context or load it from the database
	 * @param contextId
	 * @return
	 */
	public Context loadContext(Optional<String> contextId) {
		if(contextId.isPresent()){
			Long id = 0L;
			try {
				id = new Long(contextId.get());
			} catch (NumberFormatException e) {
				//nothing to do
			}
			Optional<Context> context = contextRepo.findById(id);
			if(context.isPresent()){
				return context.get();
			}else{
				return createContext();
			}

		}else{
			return createContext();
		}
	}
	/**
	 * Create a context and store it to the database
	 * @return
	 */
	private Context createContext() {
		Context context = new Context();
		context = contextRepo.save(context);
		return context;
	}


	/**
	 * Store any object as criteria
	 * @param context
	 * @param obj
	 * @throws JsonProcessingException
	 */
	private void storeCriteria(Context context, Object obj, String criteriaName) throws JsonProcessingException {
		if(context.getCriteria() == null){
			context.setCriteria(new HashSet<Criteria>());
		}
		String s = mapper.writeValueAsString(obj);
		Criteria crit = null;
		for(Criteria c : context.getCriteria()){
			if(c.getName().equals(criteriaName)){
				crit = c;
				break;
			}
		}
		if(crit==null){
			crit = new Criteria();
			context.getCriteria().add(crit);
		}
		crit.setName(criteriaName);
		crit.setCriteria(s);

		contextRepo.save(context);
	}


	/**
	 * Fetch any criteria, if not found, return an original object
	 * @param context
	 * @param condition
	 * @return 
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @throws ObjectNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private <T> T fetchCriteria(Context context, T condition, String criteriaName) throws JsonParseException, JsonMappingException, IOException{
		T ret = condition;
		if(context.getCriteria() != null){
			for(Criteria crit :context.getCriteria()){
				if(crit.getName().equals(criteriaName)){
					String jsonHeaders = crit.getCriteria();
					ret = (T) mapper.readValue(jsonHeaders, condition.getClass());
				}
			}
		}
		return ret;
	}
	
	/**
	 * Remove all context after application restarts
	 */
	public void removeAllContexts() {
		contextRepo.deleteAll();
	}

	/**
	 * Load saved headers by the criteria
	 * @param context
	 * @param criteria
	 * @return stored headers or new headers if not found
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public Headers loadHeaders(Context context, String criteriaName) throws JsonParseException, JsonMappingException, IOException {
		Headers ret = fetchCriteria(context, new Headers(), criteriaName);
		return ret;
	}
	
	
	public void saveHeaders(Context context, Headers headers, String criteria) throws JsonProcessingException {
		storeCriteria(context, headers , criteria);
	}
	
}
