package com.pj.planjourney.domain.plan.repository;

import com.pj.planjourney.domain.plan.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByIsPublishedTrue();
    Page<Plan> findByIsPublishedTrue(Pageable pageable);

}
