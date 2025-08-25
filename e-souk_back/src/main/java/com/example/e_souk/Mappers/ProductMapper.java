

package com.example.e_souk.Mappers;

import java.util.List;

import com.example.e_souk.Dto.Product.ProductDetailsDTO;
import com.example.e_souk.Dto.Product.ProductResponseDTO;
import com.example.e_souk.Model.Product;

public class ProductMapper {
    public static ProductDetailsDTO toProductDetails(Product product) {
        ProductDetailsDTO details = new ProductDetailsDTO();
        details.setId(product.getId());
        details.setName(product.getName());
        details.setDescription(product.getDescription());
        details.setPrice(product.getPrice());
        details.setCategoryName(product.getCategory().getName());
        details.setPicture(product.getPicture());
        details.setShopName(product.getShop().getBrandName());
        return details;
    }
    public static ProductResponseDTO toProductResponseDTO(Product product) {
		ProductResponseDTO dto = new ProductResponseDTO();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setDescription(product.getDescription());
		dto.setPicture(product.getPicture());
		dto.setPrice(product.getPrice());
		ProductResponseDTO.CategorySummaryDTO catDto = new ProductResponseDTO.CategorySummaryDTO();
		catDto.setId(product.getCategory().getId());
		catDto.setName(product.getCategory().getName());
		dto.setCategory(catDto);

		ProductResponseDTO.ShopSummaryDTO shopDto = new ProductResponseDTO.ShopSummaryDTO();
		shopDto.setId(product.getShop().getId());
		shopDto.setBrandName(product.getShop().getBrandName());
		dto.setShop(shopDto);

		if (product.getVariants() != null) {
			List<ProductResponseDTO.VariantSummaryDTO> variantDTOs = product.getVariants().stream().map(variant -> {
				ProductResponseDTO.VariantSummaryDTO vdto = new ProductResponseDTO.VariantSummaryDTO();
				vdto.setId(variant.getId());
				vdto.setSku(variant.getSku());
				vdto.setStock(variant.getStock());
				if (variant.getAttributeValues() != null) {
					List<ProductResponseDTO.AttributeValueDTO> attrVals = variant.getAttributeValues().stream().map(av -> {
						ProductResponseDTO.AttributeValueDTO avdto = new ProductResponseDTO.AttributeValueDTO();
						avdto.setAttributeName(av.getAttribute().getName());
						avdto.setValue(av.getValue());
						return avdto;
					}).toList();
					vdto.setAttributeValues(attrVals);
				}
				return vdto;
			}).toList();
			dto.setVariants(variantDTOs);
		}
		return dto;
	}

    }