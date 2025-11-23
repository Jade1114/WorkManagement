package org.example.backend.service;

import org.example.backend.dto.AssignmentCreateRequest;
import org.example.backend.vo.AssignmentResponse;
import org.example.backend.vo.PendingAssignmentResponse;
import org.example.backend.vo.TeacherAssignmentResponse;

import java.util.List;

public interface AssignmentService {

    AssignmentResponse createAssignment(AssignmentCreateRequest req);

    List<AssignmentResponse> getAssignmentsByCourse(Long courseId);


    List<TeacherAssignmentResponse> getAllAssignments();


    List<PendingAssignmentResponse> getUnsubmittedAssignmentsByStudent(Long studentId);

}
