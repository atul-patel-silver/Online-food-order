package com.online.food.repository;

import com.online.food.modal.Complain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplainRepo extends JpaRepository<Complain,Long> {
    @Query("select c from Complain c where c.restaurant.restaurantId =:restaurantId")
    Page<Complain> findByPainationWithRestaurantId(@Param("restaurantId")Long restaurantId,Pageable pageable);

    @Query("select c from Complain c where c.restaurant.restaurantId =:restaurantId")
    List<Complain> findByWithRestaurantId(@Param("restaurantId")Long restaurantId);
}
