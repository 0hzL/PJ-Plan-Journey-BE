package com.pj.planjourney.domain.user.dto;

import com.pj.planjourney.domain.userPlan.entity.UserPlan;
import lombok.Getter;

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
    private Integer likeCount;
    private Integer commentCount;


    public MyUserPlanListResponseDto(Long planId,
                                     String nickname,
                                     String name,
                                     String title,
                                     Boolean isPublished,
                                     LocalDateTime createdAt,
                                     LocalDateTime publishedAt,
                                     Integer likeCount,
                                     Integer count) {
        this.planId = planId;
        this.nickname = nickname;
        this.cityname = name;
        this.title = title;
        this.isPublished = isPublished;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.likeCount = likeCount;
        this.commentCount = count;
    }
}
