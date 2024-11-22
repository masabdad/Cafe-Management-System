package com.cafe.management.service;

import com.cafe.management.POJO.Product;
import com.cafe.management.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<List<ProductWrapper>> getAllProducts();

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<String> deleteProduct(Integer id);

    ResponseEntity<String> updateStatus(Map<String, String>requestMap);

    ResponseEntity<List<ProductWrapper>> getByCategory(Integer id);


    @PreAuthorize("permitAll")
    ResponseEntity<ProductWrapper> getProductById(Integer id);
}
