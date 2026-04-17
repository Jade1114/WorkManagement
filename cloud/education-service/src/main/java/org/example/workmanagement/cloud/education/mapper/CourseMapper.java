package org.example.workmanagement.cloud.education.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.workmanagement.cloud.education.entity.Course;

@Mapper
public interface CourseMapper {

    @Select("select id, title, deleted from course where id = #{id} and deleted = 0")
    Course selectAvailableById(Long id);
}
