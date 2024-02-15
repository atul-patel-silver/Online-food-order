package com.online.food.services;

import com.online.food.modal.Complain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ComplainService {


    Complain save(Complain complain);

    Complain findById(Long id);

    List<Complain> findAll();

    void delete(Complain complain);

    Page<Complain> findByPainationWithRestaurantId(Long restaurantId,Pageable pageable);
    Page<Complain> findAllBYPagination(Pageable pageable);

    List<Complain> findByWithRestaurantId(Long restaurantId);
}
