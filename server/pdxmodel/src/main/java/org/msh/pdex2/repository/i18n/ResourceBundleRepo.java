package org.msh.pdex2.repository.i18n;



import java.util.List;
import java.util.Optional;

import org.msh.pdex2.model.i18n.ResourceBundle;
import org.springframework.data.repository.CrudRepository;

public interface ResourceBundleRepo extends CrudRepository<ResourceBundle, Long> {
	/**
	 * Find a bundle by the locale
	 * @param locale i.e. en-US, ru-RU
	 * @return
	 */
	Optional<ResourceBundle> findByLocale(String locale);
	List<ResourceBundle> findAllByOrderBySortOrder();

}
