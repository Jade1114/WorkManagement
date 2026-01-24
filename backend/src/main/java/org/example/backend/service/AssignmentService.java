package org.example.backend.service;

import org.example.backend.dto.AssignmentCreateRequest;
import org.example.backend.vo.AssignmentResponse;
import org.example.backend.vo.PendingAssignmentResponse;
import org.example.backend.vo.RecentAssignmentResponse;
import org.example.backend.vo.TeacherAssignmentResponse;

import java.util.List;

public interface AssignmentService {

    AssignmentResponse createAssignment(AssignmentCreateRequest req, Long teacherId);

    List<AssignmentResponse> getAssignmentsByCourse(Long courseId);

    List<TeacherAssignmentResponse> getAllAssignments();

    List<TeacherAssignmentResponse> getAllAssignments(Long teacherId);

    List<PendingAssignmentResponse> getUnsubmittedAssignmentsByStudent(Long studentId);

    List<RecentAssignmentResponse> getRecentAssignments();

    List<RecentAssignmentResponse> getRecentAssignments(Long teacherId);

}
