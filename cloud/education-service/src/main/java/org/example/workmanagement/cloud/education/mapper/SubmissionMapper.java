package org.example.workmanagement.cloud.education.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.workmanagement.cloud.education.entity.Submission;

@Mapper
public interface SubmissionMapper {

    @Select("""
            select id, assignment_id, student_id, content, score, comment, graded, submit_time
            from submission
            where assignment_id = #{assignmentId} and student_id = #{studentId}
            limit 1
            """)
    Submission selectByAssignmentIdAndStudentId(@Param("assignmentId") Long assignmentId,
            @Param("studentId") Long studentId);

    @Select("""
            select id, assignment_id, student_id, content, score, comment, graded, submit_time
            from submission
            where assignment_id = #{assignmentId}
            order by submit_time desc, id desc
            """)
    List<Submission> selectByAssignmentIdOrderBySubmitTimeDesc(Long assignmentId);

    @Select("""
            select id, assignment_id, student_id, content, score, comment, graded, submit_time
            from submission
            where id = #{id}
            limit 1
            """)
    Submission selectById(Long id);

    @Select("""
            select id, assignment_id, student_id, content, score, comment, graded, submit_time
            from submission
            where student_id = #{studentId}
            order by submit_time desc, id desc
            """)
    List<Submission> selectByStudentIdOrderBySubmitTimeDesc(Long studentId);

    @Insert("""
            insert into submission (assignment_id, student_id, content, score, comment, graded, submit_time)
            values (#{assignmentId}, #{studentId}, #{content}, #{score}, #{comment}, #{graded}, #{submitTime})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Submission submission);

    @Update("""
            update submission
            set score = #{score}, comment = #{comment}, graded = #{graded}
            where id = #{id}
            """)
    int updateGrade(Submission submission);
}
