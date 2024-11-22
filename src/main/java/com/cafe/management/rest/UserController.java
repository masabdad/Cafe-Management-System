package com.cafe.management.rest;

import com.cafe.management.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/user")
public interface UserController {

    @PostMapping("/signup")
    ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);

    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestBody(required = true) Map<String, String> requestMap);

    @GetMapping(path = "/checkToken")
    ResponseEntity<String> checkToken();

    @PostMapping(path = "/changePassword")
    ResponseEntity<String> changePassword(@RequestBody Map<String, String> requestMap);

    @PostMapping(path = "/forgotPassword")
    ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/get")
    public ResponseEntity<List<UserWrapper>> getAllUser();

    @GetMapping(path = "/getRole")
    ResponseEntity<String> getRole();

    @PostMapping("/verifyCode")
    ResponseEntity<String> verifyVerificationCode(@RequestBody Map<String, String> requestMap);

    @GetMapping("/getByEmail")
    ResponseEntity<String> getUserByEmail(@RequestBody Map<String, String> requestMap);

    @PostMapping("/update-email-contact/{id}")
    ResponseEntity<String> updateEmailContact(@PathVariable int id, @RequestBody Map<String, String> requestMap);





}
