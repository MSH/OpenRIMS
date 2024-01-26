package org.msh.pdex2.repository.i18n;



import java.util.List;

import org.msh.pdex2.model.i18n.ResourceMessage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ResourceMessageRepo extends CrudRepository<ResourceMessage, Long> {

	@Query("SELECT rm FROM ResourceMessage as rm where rm.message_key like :key and key_bundle=:keyBundle")
	List<ResourceMessage> findByMessage_key(String key, long keyBundle);
	
	@Query("SELECT rm FROM ResourceMessage as rm where key_bundle=:keyBundle")
	List<ResourceMessage> findByKeyBundle(long keyBundle);
}
