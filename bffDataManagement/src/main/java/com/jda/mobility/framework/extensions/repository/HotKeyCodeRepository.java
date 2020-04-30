package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.HotKeyCode;

@Repository
@Transactional
public interface HotKeyCodeRepository extends JpaRepository<HotKeyCode,UUID>{

	
	List<HotKeyCode> findAllByOrderByTypeAscSequenceAsc();
}