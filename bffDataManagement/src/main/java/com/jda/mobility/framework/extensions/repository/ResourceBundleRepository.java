/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;

import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The class ResourceBundleRepository.java
 * 
 * @author HCL Technologies.
 */
@Repository
@Transactional
public interface ResourceBundleRepository extends JpaRepository<ResourceBundle, UUID> {

	/**
	 * @param searchTerm
	 * @param locale
	 * @return List&lt;ResourceBundle&gt;
	 */
	@Query("SELECT r FROM ResourceBundle r WHERE r.type = 'MOBILE_RENDERER' AND r.locale = :locale AND (r.rbkey LIKE CONCAT(:searchTerm, '%') "
			+ "OR r.rbvalue LIKE CONCAT(:searchTerm, '%'))")
	List<ResourceBundle> search(@Param("searchTerm") String searchTerm, String locale);

	/**
	 * @param locale
	 * @param rbkey
	 * @return List&lt;ResourceBundle&gt;
	 */
	List<ResourceBundle> findByLocaleAndRbkey(String locale, String rbkey);

	List<ResourceBundle> findByRbkeyIn(Collection<String> rbKeys);

	/**
	 * @param locale
	 * @param rbkey
	 * @param type
	 * @return ResourceBundle
	 */
	ResourceBundle findByLocaleAndRbkeyAndType(String locale, String rbkey,String type);
	
	/**
	 * @param locale
	 * @param type
	 * @return List&lt;ResourceBundle&gt;
	 */
	List<ResourceBundle> findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(String locale, String type);

	/**
	 * @param rbkey
	 * @param locale
	 * @return ResourceBundle
	 */
	ResourceBundle findByRbkeyAndLocale(String rbkey, String locale);

	/**
	 * @return List&lt;String&gt;
	 */
	@Query("SELECT DISTINCT  r.locale from ResourceBundle r")
	List<String> getDistinctLocaleList();
	
	/**
	 * @param type
	 * @return List&lt;ResourceBundle&gt;
	 */
	List<ResourceBundle> findDistinctByTypeOrderByCreationDateDesc(String type);

	/**
	 * @param locale
	 * @param rbKeys
	 * @return List&lt;ResourceBundle&gt;
	 */
	List<ResourceBundle> findByLocaleAndRbkeyIn(String locale, Set<String> rbKeys);

	List<ResourceBundle> findByTypeOrderByLocaleAscRbkeyAscRbvalueAsc(String type);
}
