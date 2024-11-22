package com.cafe.management.rest;


import com.cafe.management.POJO.Bill;
import com.itextpdf.text.pdf.security.TSAClient;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "/bill")
public interface BillRest {
    @PostMapping(path = "/generateBill")
    ResponseEntity<String> generateBill(@RequestBody Map<String, Object> requestMap);

    @GetMapping(path = "/getBills")
    ResponseEntity<List<Bill>> getBills();

    @PostMapping(path = "/getPdf")
    ResponseEntity<byte[]> getPdf(@RequestBody Map<String, Object> requestMap);

    @PostMapping(path = "/deleteBill/{id}")
    ResponseEntity<String> deleteBill(@PathVariable Integer id);




}
