package com.example.e_souk.Mappers;

import com.example.e_souk.Dto.ShopResponseDTO;
import com.example.e_souk.Dto.ShopDetailsDTO;

import java.util.List;
import java.util.stream.Collectors;

import com.example.e_souk.Dto.ProductDetailsDTO;

import com.example.e_souk.Dto.ShopOwnerDTO;
import com.example.e_souk.Dto.ShopSummaryDTO;
import com.example.e_souk.Model.Shop;
import com.example.e_souk.Service.ProductService;

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
                shop.getDescription(),
                shop.getLogoPicture(),
                shop.getDeliveryFee(),
                shop.getAddress(),
                shop.getIsActive(),
                shop.getCreatedAt(),
                shop.getUpdatedAt(),
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
}
