package com.glovo.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

public interface ProductService {
	HashSet<String> getAllCommonProducts() throws Exception;
	HashMap<String, BigDecimal> getPricesPerProduct(String product) throws Exception;
}
