package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.dto.SubmissionCreateRequest;
import org.example.backend.dto.SubmissionGradeRequest;
import org.example.backend.entity.Submission;
import org.example.backend.entity.User;
import org.example.backend.repository.SubmissionRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.SubmissionService;
import org.example.backend.vo.SubmissionListItemResponse;
import org.example.backend.vo.SubmissionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Resource
    private SubmissionRepository submissionRepository;

    @Resource
    private UserRepository userRepository;

    @Override
    public SubmissionResponse submit(Long studentId, SubmissionCreateRequest req) {

        // 1. 参数校验
        if (req.getAssignmentId() == null) {
            throw new RuntimeException("assignmentId 不能为空");
        }
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw new RuntimeException("提交内容不能为空");
        }

        // 2. 检查学生是否已经提交过该作业
        Submission existed = submissionRepository
                .findByAssignmentIdAndStudentId(req.getAssignmentId(), studentId);

        if (existed != null) {
            throw new RuntimeException("你已经提交过该作业");
        }

        // 3. 创建新的提交记录
        Submission s = new Submission();
        s.setAssignmentId(req.getAssignmentId());
        s.setStudentId(studentId);
        s.setContent(req.getContent());

        // 初始：未批改
        s.setScore(null);
        s.setComment(null);
        s.setGraded(false);

        Submission saved = submissionRepository.save(s);

        // 4. 转换成 Response 返回
        return new SubmissionResponse(
                saved.getId(),
                saved.getAssignmentId(),
                saved.getStudentId(),
                saved.getContent(),
                saved.getScore(),
                saved.getComment(),
                saved.getGraded()
        );
    }

    @Override
    public SubmissionResponse getSubmissionByStudent(Long studentId, Long assignmentId){

        if (assignmentId == null) {
            throw new RuntimeException("assignmentId 不能为空");
        }

        // 1. 查询该学生对该作业的提交
        Submission s = submissionRepository
                .findByAssignmentIdAndStudentId(assignmentId, studentId);

        if (s == null) {
            throw new RuntimeException("你还没有提交该作业");
        }

        // 2. 封装返回
        return new SubmissionResponse(
                s.getId(),
                s.getAssignmentId(),
                s.getStudentId(),
                s.getContent(),
                s.getScore(),
                s.getComment(),
                s.getGraded()
        );
    }

    @Override
    public List<SubmissionListItemResponse> listByAssignment(Long assignmentId) {

        if (assignmentId == null) {
            throw new RuntimeException("assignmentId 不能为空");
        }

        // 1. 查询所有提交
        List<Submission> list = submissionRepository.findByAssignmentId(assignmentId);

        // 2. 遍历组装响应列表
        return list.stream()
                .map(s -> {

                    // 查询学生信息
                    User user = userRepository.findById(s.getStudentId())
                            .orElse(null);

                    String username = user != null ? user.getUsername() : "未知用户";

                    return new SubmissionListItemResponse(
                            s.getId(),
                            s.getStudentId(),
                            username,
                            s.getContent(),
                            s.getScore(),
                            s.getGraded()
                    );
                })
                .collect(Collectors.toList());
    }


    @Override
    public void grade(SubmissionGradeRequest req) {

        if (req.getSubmissionId() == null) {
            throw new RuntimeException("submissionId 不能为空");
        }

        if (req.getScore() == null) {
            throw new RuntimeException("score 不能为空");
        }

        // 1. 查询提交记录
        Submission s = submissionRepository.findById(req.getSubmissionId())
                .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        // 2. 批改：设置评分和评语
        s.setScore(req.getScore());
        s.setComment(req.getComment());
        s.setGraded(true);

        // 3. 保存修改
        submissionRepository.save(s);
    }
}

