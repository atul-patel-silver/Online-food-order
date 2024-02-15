package com.online.food.repository;

import com.online.food.modal.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfferRepo extends JpaRepository<Offer,Long> {
    @Query("select o from Offer o where o.restaurant.restaurantId =:restaurantId")
    Page<Offer>  findByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);
    @Query("select o from Offer o where o.restaurant.restaurantId =:restaurantId")
    List<Offer> findByOfferRestaurantId(@Param("restaurantId") Long restaurantId);
}
