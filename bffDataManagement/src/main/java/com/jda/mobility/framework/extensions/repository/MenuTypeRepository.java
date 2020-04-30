package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.MenuType;

@Repository
@Transactional
public interface MenuTypeRepository extends CrudRepository<MenuType, UUID>{

	/**Find by Menu type
	 * @param type
	 * @return Optional&lt;MenuType&gt;
	 */
	Optional<MenuType> findByType(String type);
	
	/**Find by list of  Menu type
	 * @param type
	 * @return Optional&lt;MenuType&gt;
	 */
	List<MenuType> findByTypeIn(List<String> type);
}

