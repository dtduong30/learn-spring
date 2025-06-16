package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ProductDto;
import com.codewithmosh.store.dtos.ProductRequestDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public Iterable<ProductDto> list(
            @RequestParam(name = "categoryId", required = false) Byte categoryId
    ) {
        List<Product> products;
        if (categoryId != null) {
            products = productRepository.findByCategoryIdAndDeletedAtIsNull(categoryId);
        } else {
            products = productRepository.findByDeletedAtIsNull(Sort.by("id"));
        }
        return products.stream().map(productMapper::toProductDto).toList();
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(
            @RequestBody ProductRequestDto request,
            UriComponentsBuilder uriBuilder
    ) {
        if (request.getCategoryId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }
        var product = productMapper.toEntity(request);
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            product.setDescription("");
        }
        product.setCategory(category);
        productRepository.save(product);
        product.setId(product.getId());
        var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).body(productMapper.toProductDto(product));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable(name= "productId") Long productId,
            @RequestBody ProductRequestDto request
    ) {
        var productExisted = productRepository.findByIdAndDeletedAtIsNull(productId);
        System.out.println("product: " + productExisted);
        if (productExisted.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var product = productExisted.get();
        if (request.getCategoryId() != null) {
            var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            if (category == null) {
                return ResponseEntity.badRequest().build();
            }
            product.setCategory(category);
        }

        productMapper.update(request, product);
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            product.setDescription("");
        }
        productRepository.save(product);
        return ResponseEntity.ok(productMapper.toProductDto(product));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable(name= "productId") Long productId
    ) {
        try {
            if (productId == null || productId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            var product = productRepository.findByIdAndDeletedAtIsNull(productId).orElse(null);
            System.out.println("product: " + product);
            if (product == null) {
                return ResponseEntity.notFound().build();
            }
            product.setDeletedAt(LocalDateTime.now());
            System.out.println("deleted at: " + product.getDeletedAt());
            productRepository.save(product);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            System.err.println("Data integrity violation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
