package com.xjudge.service.submission;

import com.xjudge.entity.Problem;
import com.xjudge.entity.Submission;
import com.xjudge.entity.User;
import com.xjudge.exception.XJudgeException;
import com.xjudge.mapper.SubmissionMapper;
import com.xjudge.model.contest.ContestStatusPageModel;
import com.xjudge.model.enums.OnlineJudgeType;
import com.xjudge.model.submission.SubmissionModel;
import com.xjudge.model.submission.SubmissionPageModel;
import com.xjudge.repository.SubmissionRepo;
import com.xjudge.service.contest.contestproblem.ContestProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepo submissionRepo;
    private final SubmissionMapper submissionMapper;
    private final ContestProblemService contestProblemService;

    @Override
    public SubmissionModel getSubmissionById(Long submissionId , Authentication authentication) {
        Submission submission = submissionRepo.findById(submissionId).orElseThrow(() -> new XJudgeException("Submission not found." , SubmissionServiceImpl.class.getName() , HttpStatus.NOT_FOUND));
        if((authentication != null && isUserSubmission(submission.getUser() , authentication.getName()))
                || submission.getIsOpen())
            return submissionMapper.toOpenSubmissionModel(submission , submission.getSolution());
        return submissionMapper.toModel(submission);
    }

    @Override
    public Page<SubmissionPageModel> getAllSubmissions(Pageable pageable) {
        Page<Submission> submissions = submissionRepo.findAll(pageable);
        return submissions.map(submission ->
            submissionMapper.toPageModel(submission, submission.getProblem().getCode(), submission.getUser().getHandle())
        );
    }

    @Override
    public Submission save(Submission submission) {
        return submissionRepo.save(submission);
    }

    @Override
    public boolean updateSubmissionOpen(Long submissionId , Authentication authentication) {
        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new XJudgeException("Submission not found." , SubmissionServiceImpl.class.getName() , HttpStatus.NOT_FOUND));
        if(!isUserSubmission(submission.getUser() , authentication.getName())){
            throw new XJudgeException("Un Authenticated User" , SubmissionServiceImpl.class.getName() , HttpStatus.FORBIDDEN);
        }
        submission.setIsOpen(!submission.getIsOpen());
        submissionRepo.save(submission);
        return submission.getIsOpen();
    }

    @Override
    public List<Submission> getSubmissionsByContestId(Long contestId) {
        return submissionRepo.findAllByContestId(contestId);
    }

    @Override
    public List<Submission> getSubmissionsByContestIdAndUserId(Long contestId, Long userId) {
        return submissionRepo.findSubmissionsByContestIdAndUserId(contestId , userId);
    }

    @Override
    public Integer getSolvedCount(String problemCode, OnlineJudgeType onlineJudgeType) {
        return submissionRepo.getSolvedCount(problemCode, onlineJudgeType);
    }

    @Override
    public Page<SubmissionPageModel> filterSubmissions(String userHandle, String oj, String problemCode, String language, Pageable pageable) {
        Page<Submission> submissions = submissionRepo.filterSubmissions(userHandle, oj, problemCode, language, pageable);
        return submissions.map(submission ->
                submissionMapper.toPageModel(submission, submission.getProblem().getCode(), submission.getUser().getHandle())
        );
    }

    @Override
    public Page<ContestStatusPageModel> filterSubmissionsInContest(Long contestId, String userHandle , String problemCode,String result ,String language, Pageable pageable) {
        Page<Submission> submissions = submissionRepo.filterContestSubmissions(contestId , userHandle, problemCode, result ,language, pageable);
        return submissions.map(submission ->
                submissionMapper
                        .toContestStatusPageModel(
                               submission , getProblemIndex(submission.getContest().getId() , submission.getProblem().getId())
                               )
        );
    }

    @Override
    public List<Submission> findByUserAndProblem(User user, Problem problem) {
        return submissionRepo.findByUserAndProblem(user, problem);
    }

    private String getProblemIndex(Long contestId , Long problemId) {
        return  contestProblemService
                .getContestProblemByContestIdAndProblemId(contestId  , problemId)
                .getProblemHashtag();
    }

    private boolean isUserSubmission(User user , String loginUserHandle){
        return user.getHandle()
                .equals(loginUserHandle);
    }
}
