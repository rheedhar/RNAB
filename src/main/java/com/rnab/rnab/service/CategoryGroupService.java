package com.rnab.rnab.service;

import com.rnab.rnab.model.CategoryGroup;
import com.rnab.rnab.repository.CategoryGroupRepository;
import com.rnab.rnab.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryGroupService {

    private CategoryGroupRepository categoryGroupRepository;

    @Autowired
    public CategoryGroupService(CategoryGroupRepository categoryGroupRepository) {
        this.categoryGroupRepository = categoryGroupRepository;
    }


    public void createCategoryGroup(CategoryGroup categoryGroup) {
        // save the group to the db
        // add the group to plan
    }

    public void getAllUsersCategoryGroup() {

    }


}
