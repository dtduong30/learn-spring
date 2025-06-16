package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByDeletedAtIsNull(Sort sort);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
}
