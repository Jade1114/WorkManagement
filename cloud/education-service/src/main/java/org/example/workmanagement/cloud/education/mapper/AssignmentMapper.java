package org.example.workmanagement.cloud.education.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.example.workmanagement.cloud.education.entity.Assignment;

@Mapper
public interface AssignmentMapper {

    @Insert("""
            insert into assignment (course_id, teacher_id, title, content, deadline, created_at)
            values (#{courseId}, #{teacherId}, #{title}, #{content}, #{deadline}, #{createdAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Assignment assignment);
}
