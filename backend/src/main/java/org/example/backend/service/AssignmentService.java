package org.example.backend.service;

import org.example.backend.dto.AssignmentCreateRequest;
import org.example.backend.vo.AssignmentResponse;
import org.example.backend.vo.PendingAssignmentResponse;
import org.example.backend.vo.TeacherAssignmentResponse;

import java.util.List;

public interface AssignmentService {

    AssignmentResponse createAssignment(AssignmentCreateRequest req);

    List<AssignmentResponse> getAssignmentsByCourse(Long courseId);

    /**
     * 获取所有作业（教师视角）
     */
    List<TeacherAssignmentResponse> getAllAssignments();

    /**
     * 获取学生尚未提交的作业（遍历作业，过滤掉已有提交记录）
     */
    List<PendingAssignmentResponse> getUnsubmittedAssignmentsByStudent(Long studentId);

}
