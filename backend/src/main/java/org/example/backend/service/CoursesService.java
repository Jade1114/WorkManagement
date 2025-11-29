package org.example.backend.service;

import org.example.backend.vo.CoursesResponse;
import org.example.backend.vo.CourseWithCountResponse;
import org.example.backend.dto.UpdateCoursesRequest;

import java.util.List;

public interface CoursesService {
    CoursesResponse createCourses(String title);
    List<CoursesResponse> getAllCourses();
    List<CourseWithCountResponse> getCoursesWithAssignmentCount();
    CoursesResponse updateCourses(UpdateCoursesRequest req);
    void deleteCourse(Long id);
}
