package com.rnab.rnab.controller;

import com.rnab.rnab.dto.CreateCategoryGroupRequest;
import com.rnab.rnab.model.Plan;
import com.rnab.rnab.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }


    /**
     * Create/initialize plans for a new user
     * @param auth : authentication object
     * @return Response entity with 201 code and plan data
     */
    @PostMapping("/initialize")
    public ResponseEntity<List<Plan>> initializePlansForUser(Authentication auth) {
        String email = auth.getName();
        List<Plan> plans = planService.initializePlansForUser(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(plans);
    }


    /**
     * Gets the plans to display when user logs in. includes
     * previous six months plan, current month and next month
     * @param auth : authentication object
     * @return : Response entity with 200 code and plan data
     */
    @GetMapping("/display")
    public ResponseEntity<List<Plan>> getPlansToDisplay(Authentication auth) {
        String email = auth.getName();
        List<Plan> plans = planService.getPlansToDisplay(email);
        return ResponseEntity.ok(plans);

    }


    /**
     * Get the plan for the current month
     * @param auth : authentication object
     * @return : Response entity with 200 code and plan data
     */
    @GetMapping("/current")
    public ResponseEntity<Plan> getCurrentMonthPlan(Authentication auth) {
        String email = auth.getName();
        Plan plan = planService.getCurrentMonthPlan(email);
        return ResponseEntity.ok(plan);
    }


    /**
     * Get the plan for the next month
     * @param auth : authentication object
     * @return : Response entity with 200 code and plan data
     */
    @GetMapping("/next")
    public ResponseEntity<Plan> getNextMonthPlan(Authentication auth) {
        String email = auth.getName();
        Plan plan = planService.getNextMonthPlan(email);
        return ResponseEntity.ok(plan);
    }


    /**
     * Get 6 months plan history from the month specified
     * @param yearMonth : the starting month to load history before
     * @param auth : authentication object
     * @return : Response entity with 200 code and plan data
     */
    @GetMapping("/history")
    public ResponseEntity<List<Plan>> getPlanHistory(@RequestParam String yearMonth, Authentication auth) {
        String email = auth.getName();
        YearMonth beforeMonth = YearMonth.parse(yearMonth);
        List<Plan> plans = planService.getAdditionalPreviousMonths(email, beforeMonth);
        return ResponseEntity.ok(plans);
    }


    /**
     * Add category group to plan
     * @param planId : the plan id for which the category group will be added
     * @param request : the request body containing the category group data
     * @param auth: authentication object
     * @return : response entity with status of 200 and plan data with category group added
     */
    @PostMapping("/{planId}/category-groups")
    public ResponseEntity<Plan> addCategoryGroupToPlan(
            @PathVariable Long planId,
            @Valid @RequestBody CreateCategoryGroupRequest request,
            Authentication auth) {
        String email = auth.getName();
        Plan updatedPlan = planService.addCategoryGroupToPlan(planId, request, email);
        return ResponseEntity.ok(updatedPlan);
    }

}
