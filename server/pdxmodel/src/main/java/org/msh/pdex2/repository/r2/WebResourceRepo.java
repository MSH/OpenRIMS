package org.msh.pdex2.repository.r2;

import java.util.List;

import org.msh.pdex2.model.r2.WebResource;
import org.springframework.data.repository.CrudRepository;

public interface WebResourceRepo extends CrudRepository<WebResource, Long> {
	List<WebResource> findByUrl(String url);
}
