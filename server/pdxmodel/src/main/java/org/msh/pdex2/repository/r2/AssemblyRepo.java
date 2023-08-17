package org.msh.pdex2.repository.r2;

import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface AssemblyRepo extends CrudRepository<Assembly, Long> {
	Optional<Assembly> findByPropertyName(Concept var);

	List<Assembly> findAllByPropertyNameIn(List<Concept> vars, Sort sort);
	List<Assembly> findAllByPropertyNameInAndClazz(List<Concept> vars, Sort sort, String cl);

	List<Assembly> findAllByUrl(String url);

	@Query(
			value ="select asm.ID\r\n" + 
					"from concept root\r\n" + 
					"join closure clo on clo.parentID=root.ID and clo.`Level`=1\r\n" + 
					"join concept conf on conf.ID=clo.childID and conf.Identifier=:thingNodeUrl and conf.Active\r\n" + 
					"join closure clo1 on clo1.parentID=conf.ID and clo.`Level`=1\r\n" + 
					"join concept var on var.ID=clo1.childID and conf.Active and var.Identifier=:varName \r\n" + 
					"join assembly asm on asm.conceptID=var.ID\r\n" + 
					"where root.Identifier = 'configuration.data'",
			nativeQuery = true)
	List<Long> findAllByByThingNodeUrlAndVarName(@Param("thingNodeUrl") String thingNodeUrl, 
																									@Param("varName") String varName);


}
