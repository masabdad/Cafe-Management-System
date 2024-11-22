package com.cafe.management.serviceImpl;

import com.cafe.management.DAO.CategoryDao;
import com.cafe.management.DAO.OrderDao;
import com.cafe.management.DAO.ProductDao;
import com.cafe.management.DAO.UserDao;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.POJO.*;
import com.cafe.management.service.OrderService;
import com.cafe.management.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    UserDao userDao;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public ResponseEntity<String> createOrder(OrderPojo requestMap) {
        try {
            if (checkExistenceOfAll(requestMap.getCategory().getId(), requestMap.getProduct().getId(), requestMap.getUser().getId())) {
                Order order = new Order();
                order.setCategory(requestMap.getCategory());
                order.setUser(requestMap.getUser());
                order.setProduct(requestMap.getProduct());
                order.setQuantity(requestMap.getQuantity());
                orderDao.save(order);
                return CafeUtils.getResponseEntity("Order was Placed Successfully", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity("Not Found anything ion Given Foreign Keys.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean checkExistenceOfAll(int category_fk, int product_fk, int user_fk) {
        boolean opt1 = userDao.existsById(user_fk);
        boolean opt2 = categoryDao.existsById(category_fk);
        boolean opt3 = productDao.existsById(product_fk);

        if (opt1 && opt2 && opt3) {
            return true;
        }
        return false;
    }


}
