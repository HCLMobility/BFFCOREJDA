package com.jda.mobility.framework.extensions.service;

import java.util.List;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.dto.HotKeyCodeDto;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;

@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface HotKeyCodeService {
	

	BffCoreResponse getHotKeyList();
	
	List<HotKeyCodeDto> getHotKeyCodeDtoList();

}
