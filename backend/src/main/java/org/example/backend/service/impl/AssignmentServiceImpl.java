package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.dto.AssignmentCreateRequest;
import org.example.backend.entity.Assignment;
import org.example.backend.repository.AssignmentRepository;
import org.example.backend.service.AssignmentService;
import org.example.backend.vo.AssignmentResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Resource
    private AssignmentRepository assignmentRepository;

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
}

