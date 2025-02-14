package com.hapla.users.model.service;

import com.hapla.users.model.mapper.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersMapper mapper;
}
