package com.example.e_souk.Mappers;

import com.example.e_souk.Dto.Review.ReviewResponseDTO;
import com.example.e_souk.Model.Review;

public class ReviewMapper {
    public static ReviewResponseDTO toResponseDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setShopName(review.getShop().getBrandName());
        dto.setContent(review.getComment());
        dto.setAuthor(review.getUser().getUsername());
        dto.setRating(review.getRating());
        dto.setReviewType(review.getType());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
