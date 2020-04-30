package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.Locale;

@Repository
@Transactional
public interface LocaleRepository extends JpaRepository<Locale,UUID>{

	

}