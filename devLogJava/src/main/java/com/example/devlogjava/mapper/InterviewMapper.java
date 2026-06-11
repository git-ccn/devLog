package com.example.devlogjava.mapper;

import com.example.devlogjava.entity.interview.InterviewAnsPo;
import com.example.devlogjava.entity.interview.InterviewPo;
import com.example.devlogjava.entity.interview.InterviewQueryPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InterviewMapper {
    List<InterviewPo> query(@Param("query") InterviewQueryPo query);

    String queryQuestionById(@Param("interviewId") String interviewId);

    int insertAns(InterviewAnsPo ans);

    int updateAnsByInterviewId(@Param("interviewId") String interviewId,
                               @Param("answer") String answer,
                               @Param("accuracy") Double accuracy,
                               @Param("solution") String solution);

    int updateStatusByInterviewId(@Param("interviewId") String interviewId, @Param("status") String status);
}
