package com.cafe.management.restImpl;

import com.cafe.management.service.ProductService;
import com.cafe.management.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<String> addProduct(@RequestBody Map<String, String> requestMap) {
        return productService.addNewProduct(requestMap);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductWrapper>> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping(path = "/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap)
    {
        return productService.updateProduct(requestMap);
    }

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }

    @PostMapping(path = "/updateStatus")
    ResponseEntity<String> updateStatus(@RequestBody Map<String, String> requestMap) {
        return productService.updateStatus(requestMap);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<ProductWrapper>> getProductsByCategory(@PathVariable Integer id) {
        return productService.getByCategory(id);
    }

    @GetMapping("/getProductById/{id}")
    public ResponseEntity<ProductWrapper> getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }
}
