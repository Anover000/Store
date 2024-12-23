package com.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String description;

    private Double price;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private Category category;

    //can put extra fields like images, discount, mfg details, and category specific details

}
