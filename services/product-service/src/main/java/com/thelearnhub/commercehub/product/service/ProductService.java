package com.thelearnhub.commercehub.product.service;

import com.thelearnhub.commercehub.product.dto.*;
import com.thelearnhub.commercehub.product.pricing.PriceContext;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    CategoryResponse createCategory(CreateCategoryRequest request);
    List<CategoryResponse> listCategories();

    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse getProductById(UUID id);
    List<ProductResponse> listProducts();
    List<ProductResponse> listProductsByCategory(UUID categoryId);
    ProductResponse updateProduct(UUID id, UpdateProductRequest request);

    PriceCalculationResponse calculatePrice(UUID id, PriceContext context);
}
