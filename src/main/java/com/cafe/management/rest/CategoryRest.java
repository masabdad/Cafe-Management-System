package com.cafe.management.rest;


import com.cafe.management.POJO.Category;
import jdk.jfr.consumer.RecordedStackTrace;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/category")
public interface CategoryRest {

    @PostMapping(path = "/add")
    ResponseEntity<String> addNewCategory(@RequestBody(required = true) Map<String, String> requestMap);



    @PostMapping(path = "/update")
    ResponseEntity<String> updateCategory(@RequestBody(required = true) Map<String, String> requestMap);


    @GetMapping(path = "/get")
    ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false)
                                                  String filterValue);

    @PostMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteCategoryById(@PathVariable int id);



}
