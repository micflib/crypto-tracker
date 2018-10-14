package com.glovo.service;

import java.util.HashSet;

public interface ProductService {
	HashSet<String> getAllCommonProducts() throws Exception;
}
