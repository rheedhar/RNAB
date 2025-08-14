package com.rnab.rnab.repository;

import com.rnab.rnab.model.Plan;
import com.rnab.rnab.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByUserAndPlanDate(User user, LocalDate planDate);
    Optional<Plan> findByIdAndUser(Long id, User user);
    List<Plan> findByUserAndPlanDateGreaterThanEqual(User user, LocalDate planDate);
    List<Plan> findByUserAndPlanDateGreaterThan(User user, LocalDate planDate);
    Optional<Plan> findTopByUserOrderByPlanDateDesc(User user);
}
