package com.thelearnhub.commercehub.product.mapper;

import com.thelearnhub.commercehub.product.domain.Product;
import com.thelearnhub.commercehub.product.dto.ProductResponse;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductResponse toResponse(Product product) {
        if (product == null) return null;
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getDescription(),
                product.getBasePrice(),
                product.getStockQuantity(),
                CategoryMapper.toResponse(product.getCategory()),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
