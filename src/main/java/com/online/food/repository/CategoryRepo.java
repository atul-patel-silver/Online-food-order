package com.online.food.repository;

import com.online.food.modal.Category;
import com.online.food.modal.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category,Long> {

    @Query("select c from Category c where c.categoryName =:categoryName")
    public Category getByCategoryName(@Param("categoryName") String categoryName);

    @Query(" select c from Category as c where c.categoryName LIKE %:query%")
    public List<Category> searchByCategoryName(@Param("query") String query);
}
