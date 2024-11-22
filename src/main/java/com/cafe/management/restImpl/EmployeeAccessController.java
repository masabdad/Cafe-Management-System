package com.cafe.management.restImpl;


import com.cafe.management.POJO.Reservation;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.serviceImpl.ReservationService;
import com.cafe.management.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/eAccess")
public class EmployeeAccessController {
    @Autowired
    ReservationService reservationService;

    @PostMapping("/reserveTable")
    public ResponseEntity<String> eReserveTable(@RequestBody Reservation reservation){
        try {
            return reservationService.eReserveTable(reservation);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/getAllAvailableTables")
    public ResponseEntity<List<Reservation>> getAllAvailableTables(){
        try {
            return new ResponseEntity<>(reservationService.getAvailableTables(),HttpStatus.OK);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/getAllReservations")
    public ResponseEntity<List<Reservation>> getAllReservation(){
        try {
            return reservationService.getReservationsEmployee();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
