package com.cafe.management.DAO;

import com.cafe.management.POJO.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderDao extends JpaRepository<Order,Integer> {
}
