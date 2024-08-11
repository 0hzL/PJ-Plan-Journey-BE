package com.pj.planjourney.domain.plan.dto;

import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plandetail.entity.PlanDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlanInfoResponseDto {
    private Long id;
    private String title;
    private Boolean isPublished;
    private LocalDateTime publishedAt;
    private String nickName;
    private String cityName;
    private List<PlanDetail> planDetails;
    private LocalDateTime createAt;
    private Integer likeCount;
    private LocalDate startDate;
    private LocalDate endDate;


    public PlanInfoResponseDto(Plan plan, List<PlanDetail> planDetails) {
        this.id = plan.getId();
        this.title = plan.getTitle();
        this.isPublished = plan.getIsPublished();
        this.publishedAt = plan.getPublishedAt();
//        this.nickName =
        this.cityName = plan.getCity().getName();
        this.planDetails = planDetails;
        this.createAt = LocalDateTime.now();
        this.likeCount = plan.getLikeCount();
        this.startDate = plan.getStartDate();
        this.endDate = plan.getEndDate();
    }
}
