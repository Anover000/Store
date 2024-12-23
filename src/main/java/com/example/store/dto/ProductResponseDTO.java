package com.example.store.dto;

import com.example.store.model.Category;
import lombok.Data;

@Data
public class ProductResponseDTO {

    private String id;

    private String name;

    private String description;

    private Double price;

    private Category category;
}
