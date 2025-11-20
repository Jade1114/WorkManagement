package org.example.backend.service;

import org.example.backend.dto.SubmissionCreateRequest;
import org.example.backend.dto.SubmissionGradeRequest;
import org.example.backend.vo.SubmissionListItemResponse;
import org.example.backend.vo.SubmissionResponse;

import java.util.List;

public interface SubmissionService {
    SubmissionResponse submit(Long studentId, SubmissionCreateRequest req);
    SubmissionResponse getSubmissionByStudent(Long studentId, Long assignmentId);
    List<SubmissionListItemResponse> listByAssignment(Long assignmentId);
    void grade(SubmissionGradeRequest req);

}

