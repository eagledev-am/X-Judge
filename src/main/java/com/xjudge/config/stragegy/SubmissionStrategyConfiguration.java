package com.xjudge.config.stragegy;

import com.xjudge.model.enums.OnlineJudgeType;
import com.xjudge.service.scraping.spoj.SpojSubmission;
import com.xjudge.service.scraping.strategy.SubmissionStrategy;
import com.xjudge.service.scraping.atcoder.AtCoderSubmission;
import com.xjudge.service.scraping.codeforces.CodeforcesSubmission;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SubmissionStrategyConfiguration {

    private final CodeforcesSubmission codeforcesSubmission;
    private final AtCoderSubmission atCoderSubmission;
    private final SpojSubmission spojSubmission;

    @Bean
    public Map<OnlineJudgeType, SubmissionStrategy> submissionStrategies() {
        Map<OnlineJudgeType, SubmissionStrategy> strategies = new HashMap<>();
        strategies.put(OnlineJudgeType.codeforces, codeforcesSubmission);
        strategies.put(OnlineJudgeType.atcoder, atCoderSubmission);
        strategies.put(OnlineJudgeType.spoj, spojSubmission);
        return strategies;
    }

}
