package com.example.e_souk.Mappers;

import java.util.List;
import java.util.stream.Collectors;

import com.example.e_souk.Dto.Product.ProductDetailsDTO;
import com.example.e_souk.Dto.Shop.ShopDetailsDTO;
import com.example.e_souk.Dto.Shop.ShopGeneralDetailsDTO;
import com.example.e_souk.Dto.Shop.ShopOwnerDTO;
import com.example.e_souk.Dto.Shop.ShopResponseDTO;
import com.example.e_souk.Dto.Shop.ShopSummaryDTO;
import com.example.e_souk.Model.Shop;

public class ShopMapper {
    public static ShopResponseDTO toResponseDTO(Shop shop, long productCount, long orderCount, long followerCount) {
        ShopOwnerDTO ownerDTO = new ShopOwnerDTO(
                shop.getOwner().getId(),
                shop.getOwner().getUsername(),
                shop.getOwner().getPicture()
        );
        return new ShopResponseDTO(
                shop.getId(),
                shop.getBrandName(),
                shop.getBio(),
                shop.getDescription(),
                shop.getLogoPicture(),
                shop.getDeliveryFee(),
                shop.getAddress(),
                shop.getCategoryName(),
                shop.getIsActive(),
                shop.getCreatedAt(),
                shop.getUpdatedAt(),
                shop.getInstagramLink(),
                shop.getFacebookLink(),
                shop.getPhone(),
                ownerDTO,
                productCount,
                orderCount,
                followerCount
        );
    }

    public static ShopDetailsDTO toShopDetails(Shop shop, long orderCount, long followerCount) {
        ShopOwnerDTO ownerDTO = new ShopOwnerDTO(
                shop.getOwner().getId(),
                shop.getOwner().getUsername(),
                shop.getOwner().getPicture()
        );
        List<ProductDetailsDTO> productDetails = shop.getProducts().stream()
                .map(ProductMapper::toProductDetails)
                .collect(Collectors.toList());
        return new ShopDetailsDTO(
                shop.getId(),
                shop.getBrandName(),
                shop.getDescription(),
                shop.getLogoPicture(),
                shop.getDeliveryFee(),
                shop.getAddress(),
                shop.getIsActive(),
                shop.getCreatedAt(),
                shop.getUpdatedAt(),
                ownerDTO.getUsername(),
                ownerDTO.getPicture(),
                productDetails,
                orderCount,
                followerCount
        );
    }

    public static ShopSummaryDTO toSummaryDTO(Shop shop, long productCount, long followerCount) {
        return new ShopSummaryDTO(
                shop.getId(),
                shop.getBrandName(),
                shop.getDescription(),
                shop.getLogoPicture(),
                shop.getOwner().getUsername(),
                shop.getOwner().getPicture(),
                productCount,
                followerCount
        );
    }

    public static ShopGeneralDetailsDTO toShopDetailsDTO(Shop shop, long productCount, long followerCount) {
        return new ShopGeneralDetailsDTO(
                shop.getBrandName(),
                shop.getBio(),
                shop.getDescription(),
                shop.getLogoPicture(),
                shop.getDeliveryFee(),
                shop.getAddress(),
                shop.getCreatedAt(),
                shop.getOwner().getUsername(),
                shop.getOwner().getPicture(),
                productCount,
                followerCount,
                shop.getCategoryName(),
                shop.getPhone(),
                shop.getInstagramLink(),
                shop.getFacebookLink()
        );
    }

}
