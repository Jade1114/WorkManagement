package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.entity.Courses;
import org.example.backend.repository.CoursesRepository;
import org.example.backend.service.CoursesService;
import org.example.backend.vo.CoursesResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoursesServiceImpl implements CoursesService {

    @Resource
    private CoursesRepository coursesRepository;

    @Override
    public CoursesResponse createCourses(String title) {

        if (title == null) {
            throw new RuntimeException("课程标题不能为空");
        }

        Courses courses = new Courses();
        courses.setTitle(title);

        Courses saved = coursesRepository.save(courses);

        return new CoursesResponse(saved.getId(), saved.getTitle());
    }

    @Override
    public List<CoursesResponse> getAllCourses() {

        List<Courses> list = coursesRepository.findAll();

        return list.stream()
                .map(c -> new CoursesResponse(c.getId(), c.getTitle()))
                .collect(Collectors.toList());
    }
}

