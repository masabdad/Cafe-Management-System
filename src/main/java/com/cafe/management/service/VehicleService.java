package com.cafe.management.service;


import com.cafe.management.wrapper.VehicleWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface VehicleService {
    ResponseEntity<String> addNewVehicle(Map<String, String> requestMap);

    ResponseEntity<List<VehicleWrapper>> getAllVehicle();

    ResponseEntity<String> updateVehicle(Map<String, String> requestMap);

    ResponseEntity<String> deleteVehicle(Integer id);
}
