package com.glovo.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.stereotype.Component;

@Component
public class ProductData {
	private HashSet<String> productList;
	private HashMap<String, HashMap<String, BigDecimal>> priceList;

	public HashSet<String> getProductList() {
		return productList;
	}

	public void setProductList(HashSet<String> productList) {
		this.productList = productList;
	}

	public HashMap<String, HashMap<String, BigDecimal>> getPriceList() {
		return priceList;
	}

	public void setPriceList(HashMap<String, HashMap<String, BigDecimal>> priceList) {
		this.priceList = priceList;
	}
}
