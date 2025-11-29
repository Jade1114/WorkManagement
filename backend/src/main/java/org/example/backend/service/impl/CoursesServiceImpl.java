package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.dto.UpdateCoursesRequest;
import org.example.backend.entity.Courses;
import org.example.backend.repository.AssignmentRepository;
import org.example.backend.repository.CoursesRepository;
import org.example.backend.service.CoursesService;
import org.example.backend.vo.CourseWithCountResponse;
import org.example.backend.vo.CoursesResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoursesServiceImpl implements CoursesService {

    @Resource
    private CoursesRepository coursesRepository;

    @Resource
    private AssignmentRepository assignmentRepository;

    @Override
    public CoursesResponse createCourses(String title) {

        if (title == null) {
            throw new RuntimeException("课程标题不能为空");
        }

        Courses courses = new Courses();
        courses.setTitle(title);
        courses.setDeleted(false);

        Courses saved = coursesRepository.save(courses);

        return new CoursesResponse(saved.getId(), saved.getTitle());
    }

    @Override
    public List<CoursesResponse> getAllCourses() {

        List<Courses> list = coursesRepository.findByDeletedFalse();

        return list.stream()
                .map(c -> new CoursesResponse(c.getId(), c.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseWithCountResponse> getCoursesWithAssignmentCount() {
        List<Courses> list = coursesRepository.findByDeletedFalse();
        return list.stream()
                .map(c -> new CourseWithCountResponse(
                        c.getId(),
                        c.getTitle(),
                        assignmentRepository.countByCourseId(c.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public CoursesResponse updateCourses(UpdateCoursesRequest req) {
        if (req.getId() == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        Courses courses = coursesRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        if (Boolean.TRUE.equals(courses.getDeleted())) {
            throw new RuntimeException("课程已删除");
        }
        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            courses.setTitle(req.getTitle());
        }
        Courses saved = coursesRepository.save(courses);
        return new CoursesResponse(saved.getId(), saved.getTitle());
    }

    @Override
    public void deleteCourse(Long id) {
        if (id == null) {
            throw new RuntimeException("课程ID不能为空");
        }
        Courses courses = coursesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));
        courses.setDeleted(true);
        coursesRepository.save(courses);
    }
}
