package com.online.food.services;

import com.online.food.modal.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OfferService {

    Offer save(Offer offer);
    Offer findBYId(Long offerId);

    List<Offer> findAll();

    void delete(Offer offer);

    Page<Offer> findByRestaurantID(Long restaurantId, Pageable pageable);

    Page<Offer> findAllWithPagination(Pageable pageable);

    List<Offer> findByOfferRestaurantId(Long restaurantId);
}
