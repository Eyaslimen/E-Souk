package com.example.e_souk.Dto.Shop;

import com.example.e_souk.Model.ReviewType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewShopRequest {
    private Integer rating;
    private String comment;
    private ReviewType type;
}
