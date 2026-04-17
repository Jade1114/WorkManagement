package org.example.workmanagement.cloud.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.workmanagement.cloud.user.entity.User;

@Mapper
public interface UserMapper {
    @Select("select * from user where id = #{id}")
    User selectById(Long id);
}
