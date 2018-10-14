package com.glovo.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glovo.service.ProductService;

@RestController
//@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {
	
	  @Autowired
	  private ProductService productService;
	  
	  @GetMapping("/products")
	  public ResponseEntity<HashSet<String>> getProducts() {
		HashSet<String> pList = null;
		try {
			pList = productService.getAllCommonProducts();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.accepted().body(pList);
	  }
	  
	  @GetMapping("/products/{product}/prices")
	  public ResponseEntity<Map<String, String>> getProductPrices() {
	    Map<String, String> result = new HashMap<>();
	    result.put("result", "success");
	    return ResponseEntity.accepted().body(result);
	  }

}
