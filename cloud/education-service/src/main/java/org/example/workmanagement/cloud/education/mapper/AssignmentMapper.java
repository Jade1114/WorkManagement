package org.example.workmanagement.cloud.education.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.example.workmanagement.cloud.education.entity.Assignment;

@Mapper
public interface AssignmentMapper {

    @Insert("""
            insert into assignment (course_id, teacher_id, title, content, deadline, created_at)
            values (#{courseId}, #{teacherId}, #{title}, #{content}, #{deadline}, #{createdAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Assignment assignment);

    @Select("""
            select id, course_id, teacher_id, title, content, deadline, created_at
            from assignment
            order by created_at desc
            """)
    List<Assignment> selectAllOrderByCreatedAtDesc();

    @Select("""
            select id, course_id, teacher_id, title, content, deadline, created_at
            from assignment
            where teacher_id = #{teacherId}
            order by created_at desc
            """)
    List<Assignment> selectByTeacherIdOrderByCreatedAtDesc(Long teacherId);

    @Select("""
            select id, course_id, teacher_id, title, content, deadline, created_at
            from assignment
            where id = #{id}
            limit 1
            """)
    Assignment selectById(Long id);
}
