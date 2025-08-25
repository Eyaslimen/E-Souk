package com.example.e_souk.Dto.Shop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopFilterDto {
    private String address;
    private String searchKeyword;
    private String categoryName;
    private String sortBy = "newest";
    private Integer page = 0;
    private Integer pageSize = 20;
}