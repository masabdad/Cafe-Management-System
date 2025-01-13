package com.cafe.management.POJO;


import lombok.*;
import lombok.extern.apachecommons.CommonsLog;
import org.hibernate.annotations.*;
import org.hibernate.annotations.NamedQuery;


import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
@NamedQuery(name = "Product.getAllProducts", query = "select new com.cafe.management.wrapper.ProductWrapper(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) from Product p")
@NamedQuery(name = "Product.updateProductStatus", query = "update Product p set p.status=:status where p.id=:id")
@NamedQuery(name = "Product.getProductByCategory", query = "select new com.cafe.management.wrapper.ProductWrapper(p.id, p.name) from Product p where p.category.id=:id and p.status='true'")
@NamedQuery(name = "Product.getProductById", query = "select new com.cafe.management.wrapper.ProductWrapper(p.id, p.name, p.description, p.price) from Product p where p.id=:id")




@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@Table(name = "product")

public class Product implements Serializable {
    public static final Long serialVersionID = 123456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;

}
