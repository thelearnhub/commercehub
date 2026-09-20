package com.thelearnhub.commercehub.product.controller;

import com.thelearnhub.commercehub.product.dto.*;
import com.thelearnhub.commercehub.product.pricing.PriceContext;
import com.thelearnhub.commercehub.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@Tag(name = "Products", description = "Product catalog and Decorator Pattern pricing pipeline.")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "List all products")
    public ResponseEntity<List<ProductResponse>> listProducts(@RequestParam(required = false) UUID categoryId) {
        if (categoryId != null) {
            return ResponseEntity.ok(productService.listProductsByCategory(categoryId));
        }
        return ResponseEntity.ok(productService.listProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID (cached via Redis Cache-Aside)")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Create a new product (ADMIN or SELLER only)")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Update an existing product (evicts Redis cache)")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @GetMapping("/{id}/price")
    @Operation(summary = "Calculate dynamic price using the Decorator Pattern pipeline (discount, tax, promo code)")
    public ResponseEntity<PriceCalculationResponse> calculatePrice(
            @PathVariable UUID id,
            @RequestParam(required = false) BigDecimal discountPercentage,
            @RequestParam(required = false) BigDecimal taxPercentage,
            @RequestParam(required = false) String promoCode,
            @RequestParam(required = false) BigDecimal promoDiscountAmount
    ) {
        PriceContext context = new PriceContext(discountPercentage, taxPercentage, promoCode, promoDiscountAmount);
        return ResponseEntity.ok(productService.calculatePrice(id, context));
    }
}
