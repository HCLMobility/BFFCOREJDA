/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.entity.MenuType;
import com.jda.mobility.framework.extensions.entity.ProductProperty;


@Repository
@Transactional(readOnly = true)
public interface MenuMasterRepository extends JpaRepository<MenuMaster, UUID>{
	
	/**Find by Product
	 * @param productProperty
	 * @return
	 */
	public List<MenuMaster> findByProductProperty(ProductProperty productProperty);
	
	/**Find By MenuId - parent to get Sub menus
	 * @param parentMenuId
	 * @return
	 */
	public List<MenuMaster> findByParentMenuIdOrderBySequence(UUID parentMenuId);
	
	
	/**Find by Product and Menu type
	 * @param productProperty
	 * @param menuType
	 * @return
	 */
	public List<MenuMaster> findByProductPropertyAndMenuType(ProductProperty productProperty,MenuType menuType);
	
	/**Delete by product and menu type
	 * @param productProperty
	 * @param menuType
	 */
	@Transactional
	public void deleteByProductPropertyAndMenuType(ProductProperty productProperty,MenuType menuType);
	
	
	/** Delete by product
	 * @param productProperty
	 */
	@Transactional
	public void deleteByProductProperty(UUID productProperty);
	
	/**
	 * Find by product (Only menu and not sub menu). Order by MenuType and Sequence
	 * @param productProperty
	 * @return
	 */
	public List<MenuMaster> findByProductPropertyAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(ProductProperty productProperty);
	
	/**Find by product and Menu type(Only menu and not sub menu). Order by Sequence
	 * @param productProperty
	 * @param menuType
	 * @return
	 */
	public List<MenuMaster> findByProductPropertyAndMenuTypeAndParentMenuIdIsNullOrderBySequence(ProductProperty productProperty,MenuType menuType);

	/**Find by product and Menu types(Only menu and not sub menu). Order by Sequence
	 * @param productProperty
	 * @param menuTypList
	 * @return
	 */
	public List<MenuMaster> findByProductPropertyAndMenuTypeInAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(ProductProperty productProperty,List<MenuType> menuTypList);
	
	/**Find by Form Id
	 * @param linkedFormId
	 * @return List&lt;MenuMaster&gt;
	 */
	public List<MenuMaster> findByLinkedFormId(UUID linkedFormId);
	
	/**Find by Product,Form and Menu type
	 * @param menuType
	 * @param linkedFormID
	 * @return List&lt;MenuMaster&gt;
	 */
	public List<MenuMaster> findByMenuTypeAndLinkedFormId(MenuType menuType, UUID linkedFormID);

	/**Find by menu name
	 * @param menuName
	 * @return List&lt;MenuMaster&gt;
	 */
	public List<MenuMaster> findByMenuName(String menuName);

	List<MenuMaster> findAllByLinkedFormIdIsNullOrderByMenuName();

	/**
     * Delete by parent menu id
	 * @param menuId
	 */
	@Modifying
	@Transactional
	@Query("delete from MenuMaster m where m.parentMenuId=:menuId")
	public void deleteByParentMenuId(UUID menuId);

}
