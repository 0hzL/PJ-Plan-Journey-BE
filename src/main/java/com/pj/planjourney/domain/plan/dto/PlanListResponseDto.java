package com.pj.planjourney.domain.plan.dto;

import com.pj.planjourney.domain.plan.entity.Plan;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Getter
public class PlanListResponseDto {
    private Long planId;
    private String title;
    private String author;
    private Boolean isPublished;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private Integer likeCount;
    private String cityName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer commentCount;

    public PlanListResponseDto(Plan plan) {
        this.planId = plan.getId();
        this.title = plan.getTitle();
        this.author = plan.getAuthor();
        this.isPublished = plan.getIsPublished();
        this.publishedAt = plan.getPublishedAt();
        this.createdAt = plan.getCreatedAt();
        this.likeCount = plan.getLikeCount();
        this.cityName = plan.getCity().getName();
        this.startDate = plan.getStartDate();
        this.endDate = plan.getEndDate();
        this.commentCount = plan.getComments().size();
    }
}
