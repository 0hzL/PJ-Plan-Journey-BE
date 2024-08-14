package com.pj.planjourney.domain.plandetail.repository;

import com.pj.planjourney.domain.plan.entity.Plan;
import com.pj.planjourney.domain.plandetail.entity.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlanDetailRepository extends JpaRepository<PlanDetail, Long> {
    List<PlanDetail> findByPlanIdAndDate(Long planId, LocalDate Date);

    List<PlanDetail> findByPlanIdOrderByDateAscSequenceAsc(Long planId);

    List<PlanDetail> findByPlanOrderBySequenceAsc(Plan plan);
}
