package com.cafe.management.service;

import com.cafe.management.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

public interface UserService {

    ResponseEntity<String> signUp(Map<String, String> requestMap);

    ResponseEntity<String> login(Map<String, String> requestMap);

    ResponseEntity<String> update(Map<String, String> requestMap);

    ResponseEntity<String > checkToken();

    ResponseEntity<String> changePassword(Map<String,String> requestMap);


    ResponseEntity<String> forgotPassword(Map<String, String> requestMap);

    ResponseEntity<List<UserWrapper>> getAllUser();

    ResponseEntity<String> addEmployee(Map<String, String> requestMap);

    ResponseEntity<String> deleteEmployee(int id);

    ResponseEntity<List<UserWrapper>> getAllEmployee();

    ResponseEntity<String> getRole();

    ResponseEntity<String> updateEmployeeStatus(int id, String newStatus);

    ResponseEntity<String> verifyVerificationCode(Map<String, String> requestMap);

    ResponseEntity<String> getUserByEmail(Map<String, String> requestMap);


    ResponseEntity<String> updateEmailContact(int id, Map<String, String> requestMap);
}
