package com.online.food.modal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_product")
@Builder
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name")
    private String productName;
    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    @JsonIgnore
    private SubCategory subCategory;

    @Column(name = "product_price")
    private Long productPrice;
    @Column(name = "product_discription")
    private String productDiscription;
    @Column(name = "product_image")
    private String imageName;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonBackReference
    private Restaurant restaurant;
}
