package org.example.backend.repository;

import org.example.backend.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Submission findByAssignmentIdAndStudentId(Long courseId, Long studentId);
    List<Submission> findByAssignmentId(Long assignmentId);
}

