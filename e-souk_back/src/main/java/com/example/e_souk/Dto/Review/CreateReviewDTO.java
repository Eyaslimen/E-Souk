package com.example.e_souk.Dto.Review;
import com.example.e_souk.Model.ReviewType;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDTO {
    @NotBlank
    private String content;
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;
    private ReviewType reviewType;
}