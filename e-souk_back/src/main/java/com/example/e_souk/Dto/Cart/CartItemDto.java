package com.example.e_souk.Dto.Cart;
import java.util.Map;
import java.util.UUID;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDto {
    private UUID id;
    private String name;
    private Float price;
    private String picture;
    private String shopName;
    private Map<String, String> selectedAttributes;
    private Integer quantity;
} 