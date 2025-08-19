package com.example.e_souk.Dto.Review;
import java.time.LocalDateTime;
import java.util.UUID;

import com.example.e_souk.Model.ReviewType;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDTO {
    private UUID id;
    @NotBlank
    private String shopName;
    @NotBlank
    private String content;
    @NotBlank
    private String author;
    @NotBlank
    @Min(1)
    @Max(5)
    private Integer rating;
    private LocalDateTime createdAt;
    private ReviewType reviewType;
} 