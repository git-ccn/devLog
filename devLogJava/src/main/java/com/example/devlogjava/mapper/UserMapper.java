package com.example.devlogjava.mapper;

import com.example.devlogjava.entity.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    UserPo findByPhone(@Param("phone") String phone);

    int insert(UserPo user);

    int updatePasswordById(@Param("id") String id, @Param("password") String password);
}
