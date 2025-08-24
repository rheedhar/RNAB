package com.rnab.rnab.service;

import com.rnab.rnab.dto.plan.*;
import com.rnab.rnab.exception.plan.*;
import com.rnab.rnab.model.Category;
import com.rnab.rnab.model.CategoryGroup;
import com.rnab.rnab.model.Plan;
import com.rnab.rnab.model.User;
import com.rnab.rnab.repository.PlanRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
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
    private static final Logger logger = LoggerFactory.getLogger(PlanService.class);

    public PlanService(PlanRepository planRepository, UserService userService) {
        this.planRepository = planRepository;
        this.userService = userService;
    }


    public List<Plan> initializePlansForUser(String email) {
        User user = userService.findByEmail(email);

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
        return getPlanForMonthsFromOffset(user, YearMonth.now(ZoneOffset.UTC), 0);
    }


    public Plan getNextMonthPlan(String email) {
        User user = userService.findByEmail(email);
        return getPlanForMonthsFromOffset(user, YearMonth.now(ZoneOffset.UTC), 1);
    }


    public List<Plan> getPlansToDisplay(String email) {
        User user = userService.findByEmail(email);
        YearMonth currentMonth = YearMonth.now(ZoneOffset.UTC);
        List<Plan> availablePlans = getAdditionalPreviousMonths(email, currentMonth);

        // add current and next month
        availablePlans.add(getPlanForMonthsFromOffset(user, currentMonth, 0));
        availablePlans.add(getPlanForMonthsFromOffset(user, currentMonth, 1));

        return availablePlans;
    }


    public List<Plan> getAdditionalPreviousMonths(String email, YearMonth startingMonth) {
        User user = userService.findByEmail(email);
        List<Plan> additionalPlans = new ArrayList<>();

        for(int i = 6; i > 0; i--) {
            Plan existingPlan = getPlanForMonthsFromOffset(user, startingMonth, -i);
            if(existingPlan != null) {
                additionalPlans.add(existingPlan);
            }
        }
        return additionalPlans;
    }


    public Plan addCategoryGroupToPlan(Long planId, CreateCategoryGroupRequest request, String email) {
        // find plan for user
        PlanAndUser result = findPlanAndUser(planId, email);

        // check that category group name doesn't already exist on target plan
        String newGroupName = request.getGroupName().trim();
        for(CategoryGroup categoryGroup: result.targetPlan.getCategoryGroups()) {
            if(categoryGroup.getGroupName().equalsIgnoreCase(newGroupName)) {
                throw new DuplicateCategoryGroupNameException("Category group: " + newGroupName + " already exist in this plan");
            }
        }

        // add new group for target plan and future plans
        List<Plan> targetAndFuturePlans = planRepository.findByUserAndPlanDateGreaterThanEqual(result.user, result.targetPlan.getPlanDate());
        for (Plan plan: targetAndFuturePlans) {
            if(!categoryGroupExists(plan, newGroupName)) {
                CategoryGroup newGroup = new CategoryGroup();
                newGroup.setGroupName(newGroupName);
                plan.addCategoryGroup(newGroup);
            }
        }

        // save all plans and return target plan
        planRepository.saveAll(targetAndFuturePlans);
        loadPlanData(result.targetPlan);
        return result.targetPlan;
    }


    public Plan deleteCategoryGroupFromPlan(Long planId, Long categoryGroupId, String email, boolean deleteFromFutureMonths) {
        PlanAndCategoryGroup result = findPlanAndCategoryGroup(planId, categoryGroupId, email);
        String categoryGroupNameToDelete = result.categoryGroup.getGroupName();

        List<Plan> plansToModify = getPlansToModify(result.user, result.targetPlan, deleteFromFutureMonths);

        // remove category group from plans
        for(Plan plan: plansToModify) {
            plan.getCategoryGroups().stream()
                    .filter(group -> group.getGroupName().equalsIgnoreCase(categoryGroupNameToDelete))
                    .findFirst().ifPresent(plan::removeCategoryGroup);
        }

        // save all plans and return target plan
        planRepository.saveAll(plansToModify);
        loadPlanData(result.targetPlan);
        return result.targetPlan;
    }


    public Plan updateCategoryGroupName(Long planId, Long categoryGroupId, UpdateCategoryGroupRequest request, String email, boolean updateInFutureMonths) {
        PlanAndCategoryGroup result = findPlanAndCategoryGroup(planId, categoryGroupId, email);

        String oldGroupName = result.categoryGroup.getGroupName();
        String newGroupName = request.getGroupName().trim();

        // check if new group name exists in current plan
        boolean nameExistsInCurrentPlan = categoryGroupExistsExcludingCategoryGroupWithId(result.targetPlan, newGroupName, categoryGroupId);
        if (nameExistsInCurrentPlan) {
            throw new DuplicateCategoryGroupNameException("Category group name: " + newGroupName + " already exists in this plan");

        }

        // check if new group name exists in future plan
        if (updateInFutureMonths) {
            categoryGroupExistsInFuturePlans(result, oldGroupName, newGroupName);
        }

        // update category group name in selected plans
        List<Plan> plansToModify = getPlansToModify(result.user, result.targetPlan, updateInFutureMonths);
        for (Plan plan: plansToModify) {
            plan.getCategoryGroups().stream()
                    .filter(group -> group.getGroupName().equalsIgnoreCase(oldGroupName))
                    .findFirst()
                    .ifPresent(group -> group.setGroupName(newGroupName));
        }

        // save all plans and return target plan
        planRepository.saveAll(plansToModify);
        loadPlanData(result.targetPlan);
        return result.targetPlan;

    }


    public Plan addCategoryToGroup(Long planId, Long categoryGroupId, CreateCategoryRequest request, String email) {
        PlanAndCategoryGroup result = findPlanAndCategoryGroup(planId, categoryGroupId, email);

        String newCategoryName = request.getCategoryName().trim();

        // check if category name already exist in the category group
        boolean categoryExists = categoryExists(result.categoryGroup, newCategoryName);
        if (categoryExists) {
            throw new DuplicateCategoryNameException("Category: " + newCategoryName + " already exists in this category group. ");
        }

        // get current and all future plans
        List<Plan> targetAndFuturePlans = planRepository.findByUserAndPlanDateGreaterThanEqual(result.user, result.targetPlan.getPlanDate());
        for (Plan plan: targetAndFuturePlans) {
            Optional<CategoryGroup> targetGroup = plan.getCategoryGroups().stream()
                    .filter(group -> group.getGroupName().equalsIgnoreCase(result.categoryGroup.getGroupName()))
                    .findFirst();

            if (targetGroup.isPresent() && !categoryExists(targetGroup.get(), newCategoryName)) {
                Category newCategory = new Category();
                newCategory.setCategoryName(newCategoryName);
                targetGroup.get().addCategory(newCategory);
            }
        }

        // save all plans and return target plan
        planRepository.saveAll(targetAndFuturePlans);
        loadPlanData(result.targetPlan);
        return result.targetPlan;
    }


    public Plan deleteCategoryFromGroup(Long planId, Long categoryGroupId, Long categoryId, String email, boolean deleteFromFutureMonths) {
        // find plan and category group
        PlanAndCategoryGroup result = findPlanAndCategoryGroup(planId, categoryGroupId, email);

        // find category within category group
        Category categoryToDelete = findCategoryWithinCategoryGroupById(result, categoryId);
        String categoryNameToDelete = categoryToDelete.getCategoryName();

        // get this plan and future plans
        List<Plan> plansToModify = getPlansToModify(result.user, result.targetPlan, deleteFromFutureMonths);
        List<Long> categoryIdsToDeleteAndRemoveTransactions = new ArrayList<>();

        // delete category in plans
        for (Plan plan: plansToModify) {
            Optional<CategoryGroup> targetGroup = plan.getCategoryGroups().stream()
                    .filter(group -> group.getGroupName().equalsIgnoreCase(result.categoryGroup.getGroupName()))
                    .findFirst();

            if (targetGroup.isPresent()) {
                Optional<Category> categoryToRemove = targetGroup.get().getCategories().stream()
                        .filter(category -> category.getCategoryName().equalsIgnoreCase(categoryNameToDelete))
                        .findFirst();

                if (categoryToRemove.isPresent()) {
                    Category category = categoryToRemove.get();

                    // move any assigned amount in category back to ready to assign
                    if (category.getAssignedAmount().compareTo(new BigDecimal("0.00")) > 0) {
                        plan.addToReadyToAssign(category.getAssignedAmount());
                    }

                    categoryIdsToDeleteAndRemoveTransactions.add(categoryToRemove.get().getId());
                    targetGroup.get().removeCategory(categoryToRemove.get());
                }
            }
        }

        // set category id for all transactions with these categories to null
        if (!categoryIdsToDeleteAndRemoveTransactions.isEmpty()) {
            //uncategorizeTransactionsForCategories(categoryIdsToDeleteAndRemoveTransactions);
        }

        planRepository.saveAll(plansToModify);
        loadPlanData(result.targetPlan);
        return result.targetPlan;
    }

    public Plan updateCategoryName(Long planId, Long categoryGroupId, Long categoryId, UpdateCategoryRequest request, String email, boolean updateInFutureMonths) {
        // find plan and category group
        PlanAndCategoryGroup result = findPlanAndCategoryGroup(planId, categoryGroupId, email);

        // find the category within the category group
        Category categoryToUpdate = findCategoryWithinCategoryGroupById(result, categoryId);

        // store the old category name and new category name
        String newCategoryName = request.getCategoryName().trim();
        String oldCategoryName = categoryToUpdate.getCategoryName();

        // check if new category name already exists in the category group
        boolean categoryExists = categoryExistsExcludingCategoryWithId(result.categoryGroup, newCategoryName, categoryId);
        if (categoryExists) {
                throw new DuplicateCategoryNameException("Category: " + newCategoryName + " already exists in this category group. ");
        }

        // check if category name exists in category groups for future plan
        if (updateInFutureMonths) {
            categoryExistsInFuturePlanGroups(result, oldCategoryName, newCategoryName);
        }

        // update category name in plans
        List<Plan> plansToModify = getPlansToModify(result.user, result.targetPlan, updateInFutureMonths);
        for (Plan plan: plansToModify) {
            Optional<CategoryGroup> targetGroup = plan.getCategoryGroups().stream()
                    .filter(group -> group.getGroupName().equalsIgnoreCase(result.categoryGroup.getGroupName()))
                    .findFirst();

            targetGroup.ifPresent(group -> group.getCategories().stream()
                    .filter(category -> category.getCategoryName().equalsIgnoreCase(oldCategoryName))
                    .findFirst()
                    .ifPresent(category -> category.setCategoryName(newCategoryName)));
        }

        // save all plans and return target plan
        planRepository.saveAll(plansToModify);
        loadPlanData(result.targetPlan);
        return result.targetPlan;

    }

    public Plan assignMoneyToCategory(Long planId, Long categoryGroupId, Long categoryId,
                                      AssignMoneyToCategoryRequest request, String email) {

        PlanAndCategoryGroup result = findPlanAndCategoryGroup(planId, categoryGroupId, email);
        Category categoryToUpdate = findCategoryWithinCategoryGroupById(result, categoryId);

        BigDecimal amountToAssign = request.getAmountToAssign();

        // return existing assigned amount back to ready to assign and set category to 0
        if (categoryToUpdate.getAssignedAmount().compareTo(new BigDecimal("0.00")) > 0) {
            result.targetPlan.addToReadyToAssign(categoryToUpdate.getAssignedAmount());
            categoryToUpdate.setAssignedAmount(new BigDecimal("0.00"));
        }

        // validate sufficient funds in ready to assign
        if (amountToAssign.compareTo(result.targetPlan.getReadyToAssignAmount()) > 0) {
            throw new InsufficientFundsException("Amount to assign is greater than amount in ready to assign");
        }

       // assign new amount
        result.targetPlan.subtractFromReadyToAssign(amountToAssign);
        categoryToUpdate.setAssignedAmount(amountToAssign);

        categoryToUpdate.setAssignedAmount(new BigDecimal(amountToAssign.toString()).setScale(2));


        // save all plans and return target plan
        planRepository.save(result.targetPlan);
        loadPlanData(result.targetPlan);
        return result.targetPlan;
    }

    public Plan assignToReadyToAssign(Long planId, AssignToReadyToAssignRequest request, String email) {

        PlanAndUser result = findPlanAndUser(planId, email);
        BigDecimal amountToAssign = request.getAmountToAssign();

        // assign new amount
        result.targetPlan.setReadyToAssignAmount(amountToAssign);

        // save all plans and return target plan
        planRepository.save(result.targetPlan);
        loadPlanData(result.targetPlan);
        return result.targetPlan;
    }


    public Plan setCategoryTargetAmount(Long planId, Long categoryGroupId, Long categoryId,
                                        SetCategoryTargetAmountRequest request, String email, boolean updateFuture) {

        // find plan, category group, and category
        PlanAndCategoryGroup result = findPlanAndCategoryGroup(planId, categoryGroupId, email);
        Category categoryToUpdate = findCategoryWithinCategoryGroupById(result, categoryId);

        BigDecimal amountToSet = request.getTargetAmount();

        // set planned amount and default planned amount
        categoryToUpdate.setPlannedAmount(amountToSet);
        categoryToUpdate.setDefaultPlannedAmount(amountToSet);

        // update future planned amount
        if (updateFuture) {
            updateFutureMonthsWithNewTargetAmount(result, categoryToUpdate.getCategoryName(), amountToSet);
        }

        // save all plans and return target plan
        planRepository.save(result.targetPlan);
        loadPlanData(result.targetPlan);
        return result.targetPlan;
    }


    /**
     * Runs every month on the 1st at midnight UTC
     * Create next month's plan for all users
     */
    @Scheduled(cron = "0 0 0 1 * ?", zone = "UTC")
    public void createNextMonthPlanForAllUsers() {
        YearMonth nextMonth = YearMonth.now(ZoneOffset.UTC).plusMonths(1);

        List<User> allUsers = userService.getAllUsers();
        int plansCreated = 0;
        int errors = 0;

        for (User user: allUsers) {
            try {
                Plan createdPlan = createPlanForMonth(user, nextMonth);
                plansCreated++;
            } catch (Exception e) {
                errors++;
                logger.error("Failed to create plan for user {} for month {}: {}", user.getEmail(), nextMonth, e.getMessage(), e);
            }
        }

        logger.info("Monthly plan creation completed. Total users: {}, Plans processed: {}, Errors: {}",
                allUsers.size(), plansCreated, errors);

    }


    private record PlanAndCategoryGroup(Plan targetPlan, CategoryGroup categoryGroup, User user) {}

    private record PlanAndUser(Plan targetPlan, User user) {}

    private PlanAndCategoryGroup findPlanAndCategoryGroup(Long planId, Long categoryGroupId, String email) {
        User user = userService.findByEmail(email);
        Plan targetPlan = planRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new PlanNotFoundException("Plan for user: " + email + " not found"));

        CategoryGroup categoryGroup = targetPlan.getCategoryGroups().stream()
                .filter(group -> group.getId().equals(categoryGroupId))
                .findFirst()
                .orElseThrow(() -> new CategoryGroupNotFoundException("Category group with id " + categoryGroupId + " not found in this plan"));

        return new PlanAndCategoryGroup(targetPlan, categoryGroup, user);
    }

    private PlanAndUser findPlanAndUser(Long planId, String email) {
        User user = userService.findByEmail(email);
        Plan targetPlan = planRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new PlanNotFoundException("Plan for user: " + email + " not found"));

        return new PlanAndUser(targetPlan, user);
    }

    private List<Plan> getPlansToModify(User user, Plan targetPlan, boolean includeFutureMonths) {
        if (includeFutureMonths) {
            return planRepository.findByUserAndPlanDateGreaterThanEqual(user, targetPlan.getPlanDate());
        } else {
            return List.of(targetPlan);
        }
    }


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
                    newCategory.setAssignedAmount(existingCategory.getDefaultPlannedAmount());
                    newCategory.setDefaultPlannedAmount(existingCategory.getDefaultPlannedAmount());
                    newCategory.setCategoryGroup(newCategoryGroup);
                    newCategoryGroup.addCategory(newCategory);
                }
            }
        }

        // save plan and return
        Plan createdPlan = planRepository.save(newPlan);
        loadPlanData(createdPlan);
        return createdPlan;
    }


    private Plan getPlanForMonthsFromOffset(User user, YearMonth yearMonth, int monthOffset) {
        YearMonth targetMonth = yearMonth.plusMonths(monthOffset);
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


    private boolean categoryGroupExistsExcludingCategoryGroupWithOldName(Plan plan, String newGroupName, String oldGroupName) {
        return plan.getCategoryGroups().stream()
                .anyMatch(group -> !group.getGroupName().equalsIgnoreCase(oldGroupName) && group.getGroupName().equalsIgnoreCase(newGroupName));
    }


    private boolean categoryGroupExistsExcludingCategoryGroupWithId(Plan plan, String newGroupName, Long categoryGroupId) {
        return plan.getCategoryGroups().stream()
                .anyMatch(group -> !group.getId().equals(categoryGroupId) && group.getGroupName().equalsIgnoreCase(newGroupName));
    }

    private boolean categoryExists(CategoryGroup group, String newCategoryName) {
        return group.getCategories().stream()
                .anyMatch(category -> category.getCategoryName().equalsIgnoreCase(newCategoryName));
    }


    private boolean categoryExistsExcludingCategoryWithOldName(CategoryGroup group, String newCategoryName, String oldCategoryName) {
        return group.getCategories().stream()
                .anyMatch(category -> !category.getCategoryName().equalsIgnoreCase(oldCategoryName) &&
                        category.getCategoryName().equalsIgnoreCase(newCategoryName));
    }


    private boolean categoryExistsExcludingCategoryWithId(CategoryGroup group, String newCategoryName, Long categoryId) {
        return group.getCategories().stream()
                .anyMatch(category -> !category.getId().equals(categoryId) && category.getCategoryName().equalsIgnoreCase(newCategoryName));
    }



    private Category findCategoryWithinCategoryGroupById(PlanAndCategoryGroup result, Long categoryId) {
        return result.categoryGroup.getCategories().stream()
                .filter(category -> category.getId().equals(categoryId))
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException("Category with id " + categoryId + " not found in this category group"));
    }



    private void categoryExistsInFuturePlanGroups(PlanAndCategoryGroup result, String oldCategoryName, String newCategoryName) {
        // find all future plans from current plan data
        List<Plan> futurePlans = planRepository.findByUserAndPlanDateGreaterThan(result.user, result.targetPlan.getPlanDate());

        // for each plan find the category group with same name as current plan category name
        for (Plan futurePlan: futurePlans) {
            Optional<CategoryGroup> futureGroup = futurePlan.getCategoryGroups().stream()
                    .filter(group -> group.getGroupName().equalsIgnoreCase(result.categoryGroup.getGroupName()))
                    .findFirst();

            // if found, check if the category name already exists
            if (futureGroup.isPresent()) {
                boolean categoryExistsInFutureGroup = categoryExistsExcludingCategoryWithOldName(futureGroup.get(), newCategoryName, oldCategoryName);

                if(categoryExistsInFutureGroup) {
                    YearMonth conflictMonth = YearMonth.from(futurePlan.getPlanDate());
                    throw new DuplicateCategoryNameException("Category name: " + newCategoryName + " already exists in "
                            + conflictMonth + " plan within this category group. Please choose a new category name");
                }
            }
        }
    }


    private void categoryGroupExistsInFuturePlans(PlanAndCategoryGroup result, String oldGroupName, String newGroupName) {
        List<Plan> futurePlans = planRepository.findByUserAndPlanDateGreaterThan(result.user, result.targetPlan.getPlanDate());
        for (Plan futurePlan: futurePlans) {
            boolean nameExistsInFuturePlan = categoryGroupExistsExcludingCategoryGroupWithOldName(futurePlan, newGroupName, oldGroupName);
            if(nameExistsInFuturePlan) {
                YearMonth conflictMonth = YearMonth.from(futurePlan.getPlanDate());
                throw new DuplicateCategoryGroupNameException("Category group name: " + newGroupName + " already exists in " + conflictMonth + " plan. Please choose a new group name");
            }
        }
    }

    private void updateFutureMonthsWithNewTargetAmount(PlanAndCategoryGroup result, String categoryName, BigDecimal newTargetAmount) {
        List<Plan> futurePlans = planRepository.findByUserAndPlanDateGreaterThan(result.user, result.targetPlan.getPlanDate());

        for (Plan futurePlan : futurePlans) {
            // find category group
            Optional<CategoryGroup> futureGroup = futurePlan.getCategoryGroups().stream()
                    .filter(group -> group.getGroupName().equalsIgnoreCase(result.categoryGroup.getGroupName()))
                    .findFirst();

            if (futureGroup.isPresent()) {
                // find category
                Optional<Category> futureCategory = futureGroup.get().getCategories().stream()
                        .filter(category -> category.getCategoryName().equalsIgnoreCase(categoryName))
                        .findFirst();

                if (futureCategory.isPresent()) {
                    Category category = futureCategory.get();
                    category.setPlannedAmount(newTargetAmount);
                    category.setDefaultPlannedAmount(newTargetAmount);
                }
            }
        }

        // save all future plans
        planRepository.saveAll(futurePlans);
    }

}
