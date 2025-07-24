package com.codewithmosh.store.services;

import com.codewithmosh.store.entities.Role;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtCustom {
  private final Claims claims;
  private final SecretKey secretKey;
  private final String token;

  public JwtCustom(Claims claims, SecretKey secretKey, String token) {
    this.claims = claims;
    this.secretKey = secretKey;
    this.token = token;
  }

  public boolean isExpired() {
    System.out.println("getExpiration" + claims.getExpiration());
    return claims.getExpiration().before(new Date());
  }

  public Long getUserId() {
    return Long.valueOf(claims.getSubject());
  }

  public Role getRole() {
    return Role.valueOf(claims.get("role", String.class));
  }

  @Override
  public String toString() {
    return token;
  }

//  public String toString() {
//    return Jwts.builder().claims(claims).signWith(secretKey).compact();
//  }
}
