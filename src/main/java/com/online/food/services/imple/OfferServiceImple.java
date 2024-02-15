package com.online.food.services.imple;

import com.online.food.modal.Offer;
import com.online.food.repository.OfferRepo;
import com.online.food.services.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OfferServiceImple implements OfferService {
    @Autowired
    private OfferRepo offerRepo;
    @Override
    public Offer save(Offer offer) {
        return this.offerRepo.save(offer);
    }

    @Override
    public Offer findBYId(Long offerId) {
        return this.offerRepo.findById(offerId).get();
    }

    @Override
    public List<Offer> findAll() {
        return this.offerRepo.findAll();
    }

    @Override
    public void delete(Offer offer) {
    this.offerRepo.delete(offer);
    }

    @Override
    public Page<Offer> findByRestaurantID(Long restaurantId, Pageable pageable) {
        return this.offerRepo.findByRestaurantId(restaurantId,pageable);
    }

    @Override
    public Page<Offer> findAllWithPagination(Pageable pageable) {
        return this.offerRepo.findAll(pageable);
    }

    @Override
    public List<Offer> findByOfferRestaurantId(Long restaurantId) {
        return this.offerRepo.findByOfferRestaurantId(restaurantId);
    }
}
