package com.glovo.service;

import java.util.List;

import com.glovo.model.Authority;

public interface AuthorityService {
  List<Authority> findById(Long id);

  List<Authority> findByname(String name);

}
