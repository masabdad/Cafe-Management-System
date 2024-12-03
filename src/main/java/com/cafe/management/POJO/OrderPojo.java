package com.cafe.management.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPojo {

    private Category category;
    private Product product;
    private User user;
    private int quantity;


}
