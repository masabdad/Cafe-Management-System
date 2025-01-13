package com.cafe.management.POJO;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderPojo {

    private Category category;
    private Product product;
    private User user;
    private int quantity;


}
