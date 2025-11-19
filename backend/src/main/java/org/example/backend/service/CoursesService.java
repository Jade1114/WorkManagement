package org.example.backend.service;

import org.example.backend.vo.CoursesResponse;

import java.util.List;

public interface CoursesService {
    CoursesResponse createCourses(String title);
    List<CoursesResponse> getAllCourses();
}
