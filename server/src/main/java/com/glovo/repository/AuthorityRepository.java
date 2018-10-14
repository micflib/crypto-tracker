package com.glovo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.glovo.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
  Authority findByName(String name);
}
