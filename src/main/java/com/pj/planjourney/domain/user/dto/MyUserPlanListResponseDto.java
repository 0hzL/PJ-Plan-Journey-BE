package com.pj.planjourney.domain.user.dto;

import com.pj.planjourney.domain.userPlan.entity.UserPlan;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class MyUserPlanListResponseDto {

    private Long planId;
    private String nickname;
    private String cityname;
    private String title;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer likeCount;
    private Integer commentCount;



    public MyUserPlanListResponseDto(Long id, String nickname, String title, Boolean isPublished, LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, LocalDateTime publishedAt, Integer likeCount, Integer commentCount) {
        this.planId = planId;
        this.nickname = nickname;
        this.title = title;
        this.isPublished = isPublished;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.startDate = startDate;
        this.endDate = endDate;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
