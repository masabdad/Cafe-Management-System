package com.cafe.management.service;

import com.cafe.management.POJO.OrderPojo;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface OrderService {
    ResponseEntity<String> createOrder(OrderPojo requestMap);
}
