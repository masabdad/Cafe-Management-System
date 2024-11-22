package com.cafe.management.DAO;

import com.cafe.management.POJO.Product;
import com.cafe.management.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {

    List<ProductWrapper> getAllProducts();


    List<ProductWrapper> getProductByCategory(@Param("id") Integer id);

    ProductWrapper getProductById(@Param("id") Integer id);
    @Modifying
    @Query("UPDATE Product p SET p.status = :status WHERE p.id = :id")
    void updateProductStatus(@Param("status") String status, @Param("id") Integer id);

}
