package com.rnab.rnab.service;

import com.rnab.rnab.dto.CreateCategoryGroupRequest;
import com.rnab.rnab.exception.DuplicateCategoryGroupNameException;
import com.rnab.rnab.exception.PlanNotFoundException;
import com.rnab.rnab.model.Category;
import com.rnab.rnab.model.CategoryGroup;
import com.rnab.rnab.model.Plan;
import com.rnab.rnab.model.User;
import com.rnab.rnab.repository.PlanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlanService {

    private final PlanRepository planRepository;
    private final UserService userService;

    public PlanService(PlanRepository planRepository, UserService userService) {
        this.planRepository = planRepository;
        this.userService = userService;
    }


    public List<Plan> initializePlansForUser(String userEmail) {
        User user = userService.findByEmail(userEmail);

        YearMonth currentYearMonth = YearMonth.now(ZoneOffset.UTC);
        YearMonth nextYearMonth = currentYearMonth.plusMonths(1);
        List<Plan> createdPlans = new ArrayList<>();

        // create current month's plan
        Plan currentPlan = createPlanForMonth(user, currentYearMonth);
        createdPlans.add(currentPlan);

        // create next month's plan
        Plan nextPlan = createPlanForMonth(user, nextYearMonth);
        createdPlans.add(nextPlan);

        return createdPlans;
    }


    public Plan getCurrentMonthPlan(String email) {
        User user = userService.findByEmail(email);
        return getPlanForMonthOffset(user, 0);
    }


    public Plan getNextMonthPlan(String email) {
        User user = userService.findByEmail(email);
        return getPlanForMonthOffset(user, 1);
    }


    public List<Plan> getPlansToDisplay(String email) {
        User user = userService.findByEmail(email);
        List<Plan> availablePlans = new ArrayList<>();

        // load 6 months back data
        for(int i = 6; i > 0; i--) {
            Plan existingPlan = getPlanForMonthOffset(user, -i);
            if(existingPlan != null) {
                availablePlans.add(existingPlan);
            }
        }

        // add current and next month
        availablePlans.add(getPlanForMonthOffset(user,0));
        availablePlans.add(getPlanForMonthOffset(user, 1));

        //load all data
        availablePlans.forEach(this::loadPlanData);

        return availablePlans;
    }


    public List<Plan> getAdditionalPreviousMonths(String email, YearMonth earliestLoadedMonth) {
        User user = userService.findByEmail(email);
        List<Plan> additionalPlans = new ArrayList<>();

        for(int i = 6; i > 0; i--) {
            YearMonth monthToLoad = earliestLoadedMonth.minusMonths(i);
            LocalDate planDate = monthToLoad.atDay(1);

            Optional<Plan> existingPlan = planRepository.findByUserAndPlanDate(user, planDate);
            if(existingPlan.isPresent()) {
                Plan plan = existingPlan.get();
                loadPlanData(plan);
                additionalPlans.add(plan);
            }
        }
        return additionalPlans;
    }

    public Plan addCategoryGroupToPlan(Long planId, CreateCategoryGroupRequest request, String email) {
        // find plan for user
        User user = userService.findByEmail(email);
        Plan targetPlan = planRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new PlanNotFoundException("Plan for user: " + email + "not found"));

        // check that category group name doesn't already exist on target plan
        String newGroupName = request.getGroupName().trim();
        for(CategoryGroup categoryGroup: targetPlan.getCategoryGroups()) {
            if(categoryGroup.getGroupName().equalsIgnoreCase(newGroupName)) {
                throw new DuplicateCategoryGroupNameException("Category group: " + newGroupName + " already exist in this plan");
            }
        }

        // add new group for get target plan and future plans
        List<Plan> targetAndFuturePlans = planRepository.findByUserAndPlanDateGreaterThanEqual(user, targetPlan.getPlanDate());
        for (Plan plan: targetAndFuturePlans) {
            if(!categoryGroupExists(plan, newGroupName)) {
                CategoryGroup newGroup = new CategoryGroup();
                newGroup.setGroupName(newGroupName);
                plan.addCategoryGroup(newGroup);
            }
        }

        // save all plans and return
        planRepository.saveAll(targetAndFuturePlans);
        loadPlanData(targetPlan);
        return targetPlan;
    }

    // batch job method should be inserted here


    private Plan createPlanForMonth(User user, YearMonth yearMonth) {
        LocalDate planDate  = yearMonth.atDay(1);

        // check if user already has a plan for the date
        Optional<Plan> existingPlan = planRepository.findByUserAndPlanDate(user, planDate);
        if (existingPlan.isPresent()) {
            return existingPlan.get();
        }

        // else create new plan
        Plan newPlan = new Plan();
        newPlan.setUser(user);
        newPlan.setPlanDate(planDate);
        newPlan.setReadyToAssignAmount(BigDecimal.ZERO);


        // find most recent plan and copy category groups and categories
        Optional<Plan> mostRecentPlan = planRepository.findTopByUserOrderByPlanDateDesc(user);
        if(mostRecentPlan.isPresent()) {
            for (CategoryGroup existingCategoryGroup: mostRecentPlan.get().getCategoryGroups()) {
                CategoryGroup newCategoryGroup = new CategoryGroup();
                newCategoryGroup.setGroupName(existingCategoryGroup.getGroupName());
                newCategoryGroup.setPlan(newPlan);
                newPlan.addCategoryGroup(newCategoryGroup);

                for (Category existingCategory: existingCategoryGroup.getCategories()) {
                    Category newCategory = new Category();
                    newCategory.setCategoryName(existingCategory.getCategoryName());
                    newCategory.setPlannedAmount(existingCategory.getPlannedAmount());
                    newCategory.setDefaultPlannedAmount(existingCategory.getDefaultPlannedAmount());
                    newCategory.setGroup(newCategoryGroup);
                    newCategoryGroup.addCategory(newCategory);
                }
            }
        }

        // save plan and return
        Plan createdPlan = planRepository.save(newPlan);
        loadPlanData(createdPlan);
        return createdPlan;
    }



    private Plan getPlanForMonthOffset(User user, int monthOffset) {
        YearMonth targetMonth = YearMonth.now(ZoneOffset.UTC).plusMonths(monthOffset);
        LocalDate planDate = targetMonth.atDay(1);

        Optional<Plan> existingPlan = planRepository.findByUserAndPlanDate(user, planDate);
        if (existingPlan.isPresent()) {
            Plan plan = existingPlan.get();
            loadPlanData(plan);
            return plan;

        }
        if (monthOffset >= 0) {
             throw new PlanNotFoundException("No plan found for month:" + targetMonth);
        }
        return null;
    }



    private void loadPlanData(Plan plan) {
        plan.getCategoryGroups().size();

        for(CategoryGroup group: plan.getCategoryGroups()) {
            group.getCategories().size();
        }
    }

    private boolean categoryGroupExists(Plan plan, String newGroupName) {
        return plan.getCategoryGroups().stream()
                .anyMatch(group -> group.getGroupName().equalsIgnoreCase(newGroupName));
    }


}
