package com.glovo.rest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glovo.service.ProductService;
import com.glovo.util.ProductData;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {
	
	  @Autowired
	  private ProductService productService;
	  
	  @Autowired
	  private ProductData productData;
	  
	  @GetMapping("/products")
	  public ResponseEntity<HashSet<String>> getProducts() {
		HashSet<String> pList = null;
		try {
			if(null == productData.getProductList()) {
				pList = productService.getAllCommonProducts();
			} else {
				pList = productData.getProductList();
			}
		} catch (Exception e) {
			//TODO error handling
		}
		return ResponseEntity.accepted().body(pList);
	  }
	  
	  @GetMapping("/products/{product}/prices")
	  public ResponseEntity<HashMap<String, BigDecimal>> getProductPrices(
			  @PathVariable(value="product") String product) {
		HashMap<String, BigDecimal> pList = new HashMap<String, BigDecimal>();	
		try {
			if(null == productData.getPriceList()) {
				pList = productService.getPricesPerProduct(product);
			} else {
				pList = productData.getPriceList().get(product);
			}
		} catch (Exception e) {
			//TODO error handling
		}
	    return ResponseEntity.accepted().body(pList);
	  }

}
