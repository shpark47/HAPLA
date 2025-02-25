package com.hapla.schedule.model.service;

import org.springframework.stereotype.Service;

import com.hapla.schedule.model.mapper.ScheduleMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {
	private final ScheduleMapper mapper;
	
}
