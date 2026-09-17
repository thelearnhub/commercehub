package com.thelearnhub.commercehub.product.mapper;

import com.thelearnhub.commercehub.product.domain.Category;
import com.thelearnhub.commercehub.product.dto.CategoryResponse;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        if (category == null) return null;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt()
        );
    }
}
