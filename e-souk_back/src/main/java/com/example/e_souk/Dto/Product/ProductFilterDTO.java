package com.example.e_souk.Dto.Product;

import lombok.Data;

@Data
public class ProductFilterDTO {
    private String categoryName;
    private Float priceMin;
    private Float priceMax;
    private String searchKeyword;
    private String sortBy = "newest";
    private Integer page = 0;
    private Integer pageSize = 20;
}