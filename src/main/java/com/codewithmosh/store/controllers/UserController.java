package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.RegisterUserRequestDto;
import com.codewithmosh.store.dtos.UpdateUserRequestDto;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
@Tag(name="Users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Iterable<UserDto> getAllUser(
            @RequestParam(required = false, defaultValue = "", name= "sort") String sort
    ) {
        if (!Set.of("name", "email").contains(sort)) {
            sort = "name";
        }
        return userRepository.findByDeletedAtIsNull(Sort.by(sort))
                .stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        var user = userRepository.findByIdAndDeletedAtIsNull(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }

    @PostMapping
    public ResponseEntity<?> createUser(
            @Valid @RequestBody RegisterUserRequestDto request,
            UriComponentsBuilder uriBuilder
    ) {
        // Tự tạo bên repo
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(
                    Map.of("email", "Email is already register")
            );
        }
        var user = userMapper.toUserEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        var userDto = userMapper.toUserDto(user);
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        // Xuat hien o header location
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name= "userId") Long userId,
            @RequestBody UpdateUserRequestDto request
    ) {
        var user = userRepository.findById(userId).orElse(null);
        System.out.println("user: " + user);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
//        System.out.println("Request name: '" + request.getName() + "'");
//        System.out.println("Request email: '" + request.getEmail() + "'");
        userMapper.update(request, user);

//        System.out.println("Request name is null: " + (request.getName() == null));
//        System.out.println("Request name is empty: " + (request.getName() != null && request.getName().isEmpty()));
        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable(name= "userId") Long userId
    ) {
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().build();
            }
            var user = userRepository.findById(userId).orElse(null);
            System.out.println("user: " + user);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            user.setDeletedAt(LocalDateTime.now());
            System.out.println("deleted at: " + user.getDeletedAt());
            userRepository.save(user);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            System.err.println("Data integrity violation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
//            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
