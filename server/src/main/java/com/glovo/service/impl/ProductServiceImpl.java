package com.glovo.service.impl;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.tomcat.jni.SSLContext;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.glovo.service.ProductService;
import com.glovo.util.Constant;

@Service
public class ProductServiceImpl implements ProductService {

	@Value(Constant.Moneeda.TOKEN)
	private String MY_MONEEDA_TOKEN;
	
	@Value(Constant.Moneeda.URL)
	private String ROOT_URL;
	  
	@Override
	public HashSet<String> getAllCommonProducts() throws Exception {
		HashSet<String> productSet = new HashSet<String>();
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		hmap = this.convertToProductList(this.callMoneedaAPI(Constant.Exchange.BFX
				, Constant.Moneeda.PRODUCTS), hmap);
		hmap = this.convertToProductList(this.callMoneedaAPI(Constant.Exchange.BNB
				, Constant.Moneeda.PRODUCTS), hmap);
		hmap = this.convertToProductList(this.callMoneedaAPI(Constant.Exchange.BTX
				, Constant.Moneeda.PRODUCTS), hmap);
		hmap = this.convertToProductList(this.callMoneedaAPI(Constant.Exchange.GDX
				, Constant.Moneeda.PRODUCTS), hmap);
		hmap = this.convertToProductList(this.callMoneedaAPI(Constant.Exchange.KRK
				, Constant.Moneeda.PRODUCTS), hmap);
		hmap = this.convertToProductList(this.callMoneedaAPI(Constant.Exchange.LQI
				, Constant.Moneeda.PRODUCTS), hmap);
		for (String key : hmap.keySet()) {
			if(hmap.get(key) == 5) {
				productSet.add(key);
			}
		}
		return productSet;
	}
	
	
	private String callMoneedaAPI (String EXCHANGE, String endpoint) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer "+MY_MONEEDA_TOKEN);
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(SSLContexts.custom()
		        .loadTrustMaterial(null, acceptingTrustStrategy)
		        .build());

		CloseableHttpClient httpClient = HttpClients.custom()
		        .setSSLSocketFactory(csf)
		        .build();

		HttpComponentsClientHttpRequestFactory requestFactory =
		        new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		HttpEntity<String> entity = new HttpEntity<String>(null ,headers);
		String result = restTemplate.postForObject(ROOT_URL+EXCHANGE+endpoint, entity, String.class);
		return result;
	}
	
	private HashMap<String, Integer> convertToProductList(String result, HashMap<String
			, Integer> pHashMap) throws Exception {
		JSONArray jarr = new JSONArray(result);
		for(int i=0; i <= jarr.length(); i++) {
			String id = (String) jarr.getJSONObject(i).get("id");
			if(pHashMap.containsKey(id)) {
				pHashMap.getOrDefault(id, pHashMap.get(id)+1);
			} else {
				pHashMap.put(id, Constant.DEFAULT_N);
			}
		}
		return null;
	}
}
