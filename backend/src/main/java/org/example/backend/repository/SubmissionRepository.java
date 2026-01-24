package org.example.backend.repository;

import org.example.backend.entity.Submission;
import org.example.backend.vo.TopSubmitterResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Submission findByAssignmentIdAndStudentId(Long courseId, Long studentId);
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByAssignmentIdIn(Iterable<Long> assignmentIds);
    List<Submission> findByStudentId(Long studentId);
    long countByGradedFalseOrGradedIsNull();

    List<Submission> findTop2ByOrderBySubmitTimeDesc();

    @Query("SELECT new org.example.backend.vo.TopSubmitterResponse(s.studentId, '', COUNT(s.id), MAX(s.submitTime)) " +
            "FROM Submission s GROUP BY s.studentId ORDER BY COUNT(s.id) DESC")
    List<TopSubmitterResponse> findTopSubmitters(Pageable pageable);
}
