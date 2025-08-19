package com.example.e_souk.Dto.Category;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CategorySummaryDTO {
    private UUID id;
    private String name;

    public CategorySummaryDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}