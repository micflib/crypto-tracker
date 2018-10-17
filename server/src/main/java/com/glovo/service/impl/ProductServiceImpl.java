package com.glovo.service.impl;

import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.glovo.service.ProductService;
import com.glovo.util.Constant;
import com.glovo.util.ProductData;


@Service
public class ProductServiceImpl implements ProductService {

	@Value(Constant.Moneeda.TOKEN)
	private String MY_MONEEDA_TOKEN;
	
	@Value(Constant.Moneeda.URL)
	private String ROOT_URL;
	
	@Autowired
	private ProductData productData;
	
	/**
	 *Sets data on server start-up
	 * @throws Exception
	 */
	@PostConstruct
	public void setProductData() throws Exception {
		productData.setProductList(this.getAllCommonProducts());
		HashMap<String, HashMap<String, BigDecimal>> hmapp = new HashMap<String, HashMap<String, BigDecimal>>();
		Iterator<String> itrProd = productData.getProductList().iterator();
		while(itrProd.hasNext()) {
			hmapp.put(itrProd.next(), this.getPricesPerProduct(itrProd.next()));
		}
		productData.setPriceList(hmapp);
	}
	
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
		for (String key : hmap.keySet()) {
			if(hmap.get(key) == 3) {
				productSet.add(key);
			}
		}
		return productSet;
	}
	
	@Override
	public HashMap<String, BigDecimal> getPricesPerProduct(String product) throws Exception {
		HashMap<String, BigDecimal> hmap = new HashMap<String, BigDecimal>();
		hmap = this.convertToPriceList(this.callMoneedaAPI(Constant.Exchange.BFX
				, Constant.Moneeda.TICKER+product), Constant.Exchange.BFX, hmap);
		hmap = this.convertToPriceList(this.callMoneedaAPI(Constant.Exchange.BNB
				, Constant.Moneeda.TICKER+product), Constant.Exchange.BNB, hmap);
		hmap = this.convertToPriceList(this.callMoneedaAPI(Constant.Exchange.BTX
				, Constant.Moneeda.TICKER+product), Constant.Exchange.BTX, hmap);
		return hmap;
	}
	
	
	private String callMoneedaAPI (String EXCHANGE, String endpoint) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		System.out.println("-------- "+ROOT_URL+EXCHANGE+endpoint);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer "+MY_MONEEDA_TOKEN);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		/**
		 * For developer's use only; need to configure on server side
		 */
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
		        .loadTrustMaterial(null, acceptingTrustStrategy)
		        .build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom()
		        .setSSLSocketFactory(csf)
		        .build();
		HttpComponentsClientHttpRequestFactory requestFactory =
		        new HttpComponentsClientHttpRequestFactory();

		requestFactory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		String result = null;
		
		try {
			result = restTemplate.exchange(ROOT_URL+EXCHANGE+endpoint, HttpMethod.GET, entity, String.class).getBody(); 
		} catch(HttpStatusCodeException e) {
			// do nothing
		}
		
		return result;
	}
	
	private HashMap<String, Integer> convertToProductList(String result
			, HashMap<String, Integer> pHashMap) throws Exception {
		JSONArray jarr = new JSONArray(result);
		for(int i=0; i < jarr.length(); i++) {
			String id = (String) jarr.getJSONObject(i).get("id");
			if(pHashMap.containsKey(id)) {
				pHashMap.put(id, pHashMap.get(id)+1);
			} else {
				pHashMap.put(id, Constant.DEFAULT_N);
			}
		}
		return pHashMap;
	}
	
	private HashMap<String, BigDecimal> convertToPriceList(String result
			, String EXCHANGE, HashMap<String, BigDecimal> pHashMap) throws Exception {
		if (result != null) {
			JSONObject jobj = new JSONObject(result);
			BigDecimal price = BigDecimal.valueOf((Double) jobj.get("price"));
			pHashMap.put(EXCHANGE, price);
		}
		return pHashMap;
	}
}
