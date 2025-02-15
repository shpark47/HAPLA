package com.hapla.comm.model.service;

import com.hapla.comm.model.mapper.CommMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommService {
    private final CommMapper mapper;
}
