package com.online.food.services.imple;

import com.online.food.modal.Product;
import com.online.food.repository.ProductRepo;
import com.online.food.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImple implements ProductService {
    @Autowired
    private ProductRepo  productRepo;
    @Override
    public Product save(Product product) {
        return this.productRepo.save(product);
    }

    @Override
    public Product findById(Long productId) {
        return this.productRepo.findById(productId).get();
    }

    @Override
    public List<Product> findAll() {
        return this.productRepo.findAll();
    }

    @Override
    public void delete(Product product) {
        this.productRepo.delete(product);
    }

    @Override
    public Page<Product> findByPagination(Pageable pageable) {
        return this.productRepo.findAll(pageable);
    }

    @Override
    public Page<Product> findProductForRestaurant(Long restaurantId, Pageable pageable) {
        return this.productRepo.findProductForRestaurant(restaurantId,pageable);
    }

    @Override
    public List<Product> findProductsForRestaurant(Long restaurantId) {
        return this.productRepo.findProductsForRestaurant(restaurantId);
    }
}
