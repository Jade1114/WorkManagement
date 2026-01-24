package org.example.backend.service;

import org.example.backend.dto.SubmissionCreateRequest;
import org.example.backend.dto.SubmissionGradeRequest;
import org.example.backend.vo.RecentSubmissionResponse;
import org.example.backend.vo.SubmissionListItemResponse;
import org.example.backend.vo.SubmissionResponse;
import org.example.backend.vo.StudentSubmissionResponse;
import org.example.backend.vo.TopSubmitterResponse;
import org.example.backend.vo.TeacherSubmissionItemResponse;

import java.util.List;

public interface SubmissionService {
    SubmissionResponse submit(Long studentId, SubmissionCreateRequest req);
    SubmissionResponse getSubmissionByStudent(Long studentId, Long assignmentId);
    List<SubmissionListItemResponse> listByAssignment(Long assignmentId);
    List<SubmissionListItemResponse> listByAssignment(Long assignmentId, Long teacherId);
    void grade(SubmissionGradeRequest req);
    void grade(SubmissionGradeRequest req, Long teacherId);
    List<StudentSubmissionResponse> listByStudent(Long studentId);
    List<TeacherSubmissionItemResponse> listAllSubmissionsForTeacher();
    List<TeacherSubmissionItemResponse> listAllSubmissionsForTeacher(Long teacherId);

    List<RecentSubmissionResponse> listRecentSubmissions();

    List<TopSubmitterResponse> listTopSubmitters(int limit);
}
