package com.pj.planjourney.domain.plandetail.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plandetail.dto.EditPlanDetailRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "plan_details")
public class PlanDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_detail_id")
    private Long id;

    private Integer sequence;

    private LocalDate date;

    private String placeName;

    private Double latitude;

    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    @JsonIgnore
    private Plan plan;

    public PlanDetail(PlanDetail original, Plan newPlan) {
        this.plan = newPlan;
        this.sequence = original.getSequence();
        this.date = original.getDate();
        this.placeName = original.getPlaceName();
        this.latitude = original.getLatitude();
        this.longitude = original.getLongitude();
    }

    public PlanDetail(Integer sequence, LocalDate date, String placeName, Double latitude, Double longitude, Plan plan) {
        this.sequence = sequence;
        this.date = date;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.plan = plan;
    }

    public PlanDetail(Integer sequence, Plan plan, EditPlanDetailRequestDto request) {
        this.sequence = sequence;
        this.date = request.getFromDate();
        this.placeName = request.getPlaceName();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.plan = plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
