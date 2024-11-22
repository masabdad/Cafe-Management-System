package com.cafe.management.service;


import com.cafe.management.POJO.Analysis.Analysis1;
import com.cafe.management.POJO.Analysis.Analysis2;
import com.cafe.management.POJO.Bill;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface BillService {

    ResponseEntity<String> generateBill(Map<String, Object> requestMap);

    ResponseEntity<List<Bill>> getBills();

    ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap);


    ResponseEntity<String> deleteBill(Integer id);

    ResponseEntity<List<Analysis1>> getAllOrderCountByUser();

    ResponseEntity<List<Analysis1>> getSpentMoneyTotalAll();

    ResponseEntity<List<Analysis2>> getProductSaleAll();

    ResponseEntity<List<Analysis2>> getCategorySaleAll();

    ResponseEntity<String> autoPrintPdf(String uuid);







}
