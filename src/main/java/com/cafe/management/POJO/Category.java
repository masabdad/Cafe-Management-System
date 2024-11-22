package com.cafe.management.POJO;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;


@NamedQuery(name = "Category.getAllCategory", query = "select c from Category c where c.id in(select p.category from Product p where p.status='true')")
@Entity
@Data
@Table(name = "category")
@DynamicInsert
@DynamicUpdate


public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")

    private Integer id;


    @Column(name = "name")
    private String name;



}
