package com.example.e_souk.Dto.Shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopLogoUploadDTO {
    private UUID shopId;
    private String logoUrl;
    private String message;
}
