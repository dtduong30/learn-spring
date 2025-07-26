package com.codewithmosh.store.controllers;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.dtos.JwtResponse;
import com.codewithmosh.store.dtos.LoginRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.mappers.UserMapper;
import com.codewithmosh.store.repositories.UserRepository;
import com.codewithmosh.store.services.AuthService;
import com.codewithmosh.store.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final JwtConfig jwtConfig;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletResponse response
  ) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = userRepository.findByEmailAndDeletedAtIsNull(request.getEmail()).orElseThrow();
    var accessToken = jwtService.generateAccessToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    // Add new cookie
    var cookie = new Cookie("refreshToken", refreshToken.toString());
    cookie.setPath("/auth/refresh");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(jwtConfig.getRefreshTokenExpiration());
    cookie.setSecure(true);
    response.addCookie(cookie);

    return ResponseEntity.ok(new JwtResponse(accessToken.toString()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtResponse> refresh(
      @CookieValue(value = "refreshToken") String refreshToken
  ) {
    var jwt = jwtService.parseToken(refreshToken);
    if (jwt == null || jwt.isExpired()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    var user = userRepository.findByIdAndDeletedAtIsNull(jwt.getUserId()).orElseThrow();
    var newAccessToken = jwtService.generateAccessToken(user);

    return ResponseEntity.ok(new JwtResponse(newAccessToken.toString()));
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> me() {
    var user = authService.getCurrentUser();
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    var userDto = userMapper.toUserDto(user);
    return ResponseEntity.ok(userDto);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Void> badCredentials() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }
}
