package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.category.id = :catId AND p.deletedAt IS NULL")
    @EntityGraph(attributePaths = "category")
    List<Product> findByCategoryIdAndDeletedAtIsNull(@Param("catId") Byte catId);

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    @EntityGraph(attributePaths = "category")
    List<Product> findByDeletedAtIsNull(Sort sort);

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);
}