package com.rnab.rnab.repository;

import com.rnab.rnab.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
