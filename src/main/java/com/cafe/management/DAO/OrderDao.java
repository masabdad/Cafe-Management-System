package com.cafe.management.DAO;

import com.cafe.management.POJO.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.criteria.CriteriaBuilder;

public interface OrderDao extends JpaRepository<Order,Integer> {
}
