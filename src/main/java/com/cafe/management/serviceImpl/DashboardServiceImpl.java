package com.cafe.management.serviceImpl;

import com.cafe.management.DAO.BillDao;
import com.cafe.management.DAO.CategoryDao;
import com.cafe.management.DAO.ProductDao;
import com.cafe.management.rest.DashboardRest;
import com.cafe.management.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service

public class DashboardServiceImpl implements DashboardService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    BillDao billDao;


    @Override
    public ResponseEntity<Map<String, Object>> getCount() {

        Map<String, Object> map = new HashMap<>();
        map.put("category", categoryDao.count());
        map.put("product", productDao.count());
        map.put("bill", billDao.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
