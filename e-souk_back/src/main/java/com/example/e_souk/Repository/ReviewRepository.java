
package com.example.e_souk.Repository;

import com.example.e_souk.Model.Review;
import com.example.e_souk.Model.Shop;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour gérer les opérations CRUD sur les boutiques
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByShopId(UUID shopId);
    @Query("select r from Review r where lower(r.shop.brandName) = lower(:shopName)")
    List<Review> findByShop_BrandNameIgnoreCase(@Param("shopName") String shopName);
}