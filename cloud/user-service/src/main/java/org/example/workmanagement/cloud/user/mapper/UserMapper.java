package org.example.workmanagement.cloud.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.workmanagement.cloud.user.entity.User;

@Mapper
public interface UserMapper {

    @Select("select id, username, password, role, active from user where id = #{id}")
    User selectById(Long id);

    @Select("select id, username, password, role, active from user where username = #{username}")
    User selectByUsername(String username);

    @Select("select count(1) from user where username = #{username}")
    int countByUsername(String username);

    @Select("select id, username, password, role, active from user order by id asc")
    List<User> selectAllOrderById();

    @Select("select id, username, password, role, active from user where role = #{role} order by id asc")
    List<User> selectByRoleOrderById(String role);

    @Select("""
            <script>
            select id, username, password, role, active
            from user
            where id in
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
            """)
    List<User> selectByIds(@Param("ids") List<Long> ids);

    @Select("select count(1) from user where role = #{role}")
    long countByRole(String role);

    @Insert("""
            insert into user (username, password, role, active)
            values (#{username}, #{password}, #{role}, #{active})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("update user set password = #{password} where id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("""
            update user
            set role = coalesce(#{role}, role),
                active = coalesce(#{active}, active)
            where id = #{id}
            """)
    int updateRoleAndActive(@Param("id") Long id,
            @Param("role") String role,
            @Param("active") Boolean active);
}
