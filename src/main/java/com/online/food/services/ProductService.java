package com.online.food.services;

import com.online.food.modal.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    Product save(Product product);

    Product findById(Long productId);

    List<Product> findAll();


    void delete(Product product);

    Page<Product> findByPagination(Pageable pageable);

    Page<Product> findProductForRestaurant(Long restaurantId,Pageable pageable);

    //not a pagination include
    List<Product> findProductsForRestaurant(Long restaurantId);
}
