package com.example.e_souk.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopOwnerDTO {
    private UUID id;
    private String username;
    private String picture;
}
