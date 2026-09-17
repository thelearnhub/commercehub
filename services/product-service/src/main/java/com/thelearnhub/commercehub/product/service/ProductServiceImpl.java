package com.thelearnhub.commercehub.product.service;

import com.thelearnhub.commercehub.product.domain.Category;
import com.thelearnhub.commercehub.product.domain.Product;
import com.thelearnhub.commercehub.product.dto.*;
import com.thelearnhub.commercehub.product.exception.CategoryNotFoundException;
import com.thelearnhub.commercehub.product.exception.DuplicateSkuException;
import com.thelearnhub.commercehub.product.exception.ProductNotFoundException;
import com.thelearnhub.commercehub.product.mapper.CategoryMapper;
import com.thelearnhub.commercehub.product.mapper.ProductMapper;
import com.thelearnhub.commercehub.product.pricing.*;
import com.thelearnhub.commercehub.product.repository.CategoryRepository;
import com.thelearnhub.commercehub.product.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Category already exists: " + request.name());
        }
        Category category = new Category(request.name(), request.description());
        categoryRepository.save(category);
        return CategoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + request.categoryId()));

        Product product = new Product(
                request.name(), request.sku(), request.description(),
                request.basePrice(), request.stockQuantity(), category
        );
        productRepository.save(product);
        return ProductMapper.toResponse(product);
    }

    /**
     * Cache-Aside Pattern: Product lookups are cached in Redis.
     */
    @Override
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        return ProductMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> listProductsByCategory(UUID categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    /**
     * Cache-Aside Pattern: Evicts Redis cache when product is updated.
     */
    @Override
    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + request.categoryId()));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setBasePrice(request.basePrice());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);

        productRepository.save(product);
        return ProductMapper.toResponse(product);
    }

    /**
     * Decorator Pattern Pricing Pipeline Assembly:
     * BasePrice -> DiscountDecorator -> PromoCodeDecorator -> TaxDecorator
     */
    @Override
    @Transactional(readOnly = true)
    public PriceCalculationResponse calculatePrice(UUID id, PriceContext context) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        PriceCalculator pipeline = new TaxPriceDecorator(
                new PromoCodePriceDecorator(
                        new DiscountPriceDecorator(
                                new BasePriceCalculator()
                        )
                )
        );

        BigDecimal finalPrice = pipeline.calculatePrice(product, context);

        return new PriceCalculationResponse(
                product.getId(),
                product.getSku(),
                product.getBasePrice(),
                finalPrice,
                context.discountPercentage(),
                context.taxPercentage(),
                context.promoCode(),
                context.promoDiscountAmount()
        );
    }
}
