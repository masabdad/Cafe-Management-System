package com.cafe.management.restImpl;

import com.cafe.management.POJO.Analysis.Analysis1;
import com.cafe.management.POJO.Analysis.Analysis2;
import com.cafe.management.POJO.Bill;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.rest.BillRest;
import com.cafe.management.service.BillService;
import com.cafe.management.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class BillRestImpl implements BillRest {
    @Autowired
    BillService billService;

    @Override
    public ResponseEntity<String> generateBill(Map<String, Object> requestMap) {
        try {
            return billService.generateBill(requestMap);

        }catch (Exception ex){
            ex.printStackTrace(
            );
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try {
            return billService.getBills();

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
            return billService.getPdf(requestMap);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            return billService.deleteBill(id);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/getAllOrderCountByName")
    public ResponseEntity<List<Analysis1>> getAllOrderCountByUser() {
        try {
            return billService.getAllOrderCountByUser();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/getSpentMoneyTotalAll")
    public ResponseEntity<List<Analysis1>> getSpentMoneyTotalAll() {
        try {
            return billService.getSpentMoneyTotalAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/getProductSaleAll")
    public ResponseEntity<List<Analysis2>> getProductSaleAll() {
        try {
            return billService.getProductSaleAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/getCategorySaleAll")
    public ResponseEntity<List<Analysis2>> getCategorySaleAll() {
        try {
            return billService.getCategorySaleAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/autoPrintPdf")
    public ResponseEntity<String> printPdf(@RequestParam String uuid) {
        return billService.autoPrintPdf(uuid);
    }



}
