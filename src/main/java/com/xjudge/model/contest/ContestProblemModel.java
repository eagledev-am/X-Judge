package com.xjudge.model.contest;

import com.xjudge.model.enums.OnlineJudgeType;

public record ContestProblemModel(
        String problemAlias ,

        OnlineJudgeType source ,

        String problemCode,

        String problemHashtag ,

        String problemLink,

        long numberOfSubmission,

        long numberOfAccepted
) {

}
