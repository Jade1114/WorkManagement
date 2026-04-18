package org.example.workmanagement.cloud.education.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
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

    @Select("""
            select id, course_id, teacher_id, title, content, deadline, created_at
            from assignment
            where course_id = #{courseId}
            order by created_at desc
            """)
    List<Assignment> selectByCourseIdOrderByCreatedAtDesc(Long courseId);

    @Select("""
            <script>
            select id, course_id, teacher_id, title, content, deadline, created_at
            from assignment
            where id in
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
            """)
    List<Assignment> selectByIds(@Param("ids") List<Long> ids);

    @Select("select count(1) from assignment")
    long countAll();

    @Select("select count(1) from assignment where teacher_id = #{teacherId}")
    long countByTeacherId(Long teacherId);

    @Select("select count(1) from assignment where course_id = #{courseId}")
    long countByCourseId(Long courseId);

    @Select("""
            select id, course_id, teacher_id, title, content, deadline, created_at
            from assignment
            order by created_at desc
            limit #{limit}
            """)
    List<Assignment> selectRecentOrderByCreatedAtDesc(int limit);

    @Select("""
            select id, course_id, teacher_id, title, content, deadline, created_at
            from assignment
            where teacher_id = #{teacherId}
            order by created_at desc
            limit #{limit}
            """)
    List<Assignment> selectRecentByTeacherIdOrderByCreatedAtDesc(@Param("teacherId") Long teacherId,
            @Param("limit") int limit);
}
