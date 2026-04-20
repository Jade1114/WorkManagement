package org.example.workmanagement.cloud.education.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.workmanagement.cloud.education.entity.Course;

@Mapper
public interface CourseMapper {

    @Select("select id, title, deleted from course where id = #{id} and deleted = 0")
    Course selectAvailableById(Long id);

    @Select("select id, title, deleted from course where deleted = 0 order by id asc")
    List<Course> selectAllAvailableOrderById();

    @Select("""
            <script>
            select id, title, deleted
            from course
            where deleted = 0 and id in
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
            """)
    List<Course> selectAvailableByIds(@Param("ids") List<Long> ids);

    @Select("select count(1) from course where deleted = 0")
    long countAvailable();

    @Select("select count(1) from course where title = #{title} and deleted = 0")
    int countAvailableByTitle(String title);

    @Select("select count(1) from course where title = #{title} and id != #{id} and deleted = 0")
    int countAvailableByTitleExceptId(@Param("id") Long id, @Param("title") String title);

    @Insert("insert into course (title, deleted) values (#{title}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Course course);

    @Update("update course set title = #{title} where id = #{id} and deleted = 0")
    int updateTitle(@Param("id") Long id, @Param("title") String title);

    @Update("update course set deleted = 1 where id = #{id}")
    int softDelete(Long id);
}
