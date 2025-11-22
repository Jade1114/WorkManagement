package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.dto.AssignmentCreateRequest;
import org.example.backend.entity.Assignment;
import org.example.backend.entity.Submission;
import org.example.backend.entity.Courses;
import org.example.backend.repository.AssignmentRepository;
import org.example.backend.repository.SubmissionRepository;
import org.example.backend.repository.CoursesRepository;
import org.example.backend.service.AssignmentService;
import org.example.backend.vo.AssignmentResponse;
import org.example.backend.vo.PendingAssignmentResponse;
import org.example.backend.vo.TeacherAssignmentResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Resource
    private AssignmentRepository assignmentRepository;

    @Resource
    private SubmissionRepository submissionRepository;

    @Resource
    private CoursesRepository coursesRepository;

    @Override
    public AssignmentResponse createAssignment(AssignmentCreateRequest req) {

        if (req.getTitle() == null) {
            throw new RuntimeException("作业标题不能为空");
        }

        Assignment a = new Assignment();
        a.setCourseId(req.getCourseId());
        a.setTitle(req.getTitle());
        a.setContent(req.getContent());
        a.setDeadline(req.getDeadline());

        Assignment saved = assignmentRepository.save(a);

        return new AssignmentResponse(
                saved.getId(),
                saved.getCourseId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getDeadline()
        );
    }

    @Override
    public List<AssignmentResponse> getAssignmentsByCourse(Long courseId) {

        List<Assignment> list = assignmentRepository.findByCourseId(courseId);

        return list.stream()
                .map(a -> new AssignmentResponse(
                        a.getId(),
                        a.getCourseId(),
                        a.getTitle(),
                        a.getContent(),
                        a.getDeadline()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherAssignmentResponse> getAllAssignments() {
        List<Assignment> assignments = assignmentRepository.findAll();
        Set<Long> courseIds = assignments.stream()
                .map(Assignment::getCourseId)
                .collect(Collectors.toSet());
        Map<Long, String> courseTitleMap = coursesRepository.findAllById(courseIds).stream()
                .collect(Collectors.toMap(Courses::getId, Courses::getTitle));

        return assignments.stream()
                .map(a -> new TeacherAssignmentResponse(
                        a.getId(),
                        a.getCourseId(),
                        courseTitleMap.getOrDefault(a.getCourseId(), "未关联课程"),
                        a.getTitle(),
                        a.getContent(),
                        a.getDeadline()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<PendingAssignmentResponse> getUnsubmittedAssignmentsByStudent(Long studentId) {
        if (studentId == null) {
            throw new RuntimeException("studentId 不能为空");
        }

        // 查出该学生已提交的 assignmentId
        List<Submission> submissions = submissionRepository.findByStudentId(studentId);
        Set<Long> submittedIds = submissions.stream()
                .map(Submission::getAssignmentId)
                .collect(Collectors.toSet());

        // 找出所有作业中未提交的部分
        List<Assignment> allAssignments = assignmentRepository.findAll();

        // 减少 N+1：提前批量查出课程标题
        Set<Long> courseIds = allAssignments.stream()
                .map(Assignment::getCourseId)
                .collect(Collectors.toSet());
        Map<Long, String> courseTitleMap = coursesRepository.findAllById(courseIds).stream()
                .collect(Collectors.toMap(Courses::getId, Courses::getTitle));

        return allAssignments.stream()
                .filter(a -> !submittedIds.contains(a.getId()))
                .map(a -> new PendingAssignmentResponse(
                        a.getId(),
                        a.getCourseId(),
                        courseTitleMap.getOrDefault(a.getCourseId(), "未关联课程"),
                        a.getTitle(),
                        a.getContent(),
                        a.getDeadline()
                ))
                .collect(Collectors.toList());
    }
}
