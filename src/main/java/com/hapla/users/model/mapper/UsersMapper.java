package com.hapla.users.model.mapper;

import com.hapla.users.model.vo.Users;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper {
    int checkUser(Users user);

    Users login(Users user);

    int join(Users user);
}
