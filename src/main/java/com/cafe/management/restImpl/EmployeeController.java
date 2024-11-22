package com.cafe.management.restImpl;


import com.cafe.management.constants.CafeConstants;
import com.cafe.management.service.UserService;
import com.cafe.management.utils.CafeUtils;
import com.cafe.management.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    UserService userService;

    @PostMapping(path = "/create")
    public ResponseEntity<String> addEmployee(@RequestBody Map<String, String> requestMap) {
        try {
            return userService.addEmployee(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int id) {
        try {
            return userService.deleteEmployee(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(path = "/getAll")
    public ResponseEntity<List<UserWrapper>> getAllEmployee() {
        try {
            return userService.getAllEmployee();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(path = "/updateStatus")
    public ResponseEntity<String> updateStatus(@PathVariable int id, @RequestParam String newStatus) {
        try {
            return userService.updateEmployeeStatus(id, newStatus);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
