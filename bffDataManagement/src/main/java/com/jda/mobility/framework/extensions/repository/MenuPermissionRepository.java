package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.MenuPermission;

@Repository
@Transactional
public interface MenuPermissionRepository extends CrudRepository<MenuPermission, UUID>{

}

