package com.cafe.management.restImpl;


import com.cafe.management.service.VehicleService;
import com.cafe.management.wrapper.VehicleWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vehicle")
public class VehicleRestImpl {
    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/add")
    public ResponseEntity<String> addVehicle(@RequestBody Map<String, String> requestMap) {
        return vehicleService.addNewVehicle(requestMap);
    }

    @GetMapping("/getAllVehicle")
    public ResponseEntity<List<VehicleWrapper>> getAllVehicle() {
        return vehicleService.getAllVehicle();
    }

    @PostMapping(path = "/update")
    public ResponseEntity<String> updateVehicle(@RequestBody Map<String, String> requestMap) {
        return vehicleService.updateVehicle(requestMap);
    }

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteVehicle(@PathVariable Integer id) {
        return vehicleService.deleteVehicle(id);
    }
}
