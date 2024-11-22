package com.cafe.management.serviceImpl;

import com.cafe.management.DAO.VehicleDao;
import com.cafe.management.JWT.JwtFilter;
import com.cafe.management.POJO.Vehicle;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.service.VehicleService;
import com.cafe.management.utils.CafeUtils;
import com.cafe.management.wrapper.VehicleWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    VehicleDao vehicleDao;


    @Override
    public ResponseEntity<String> addNewVehicle(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateVehicleMap(requestMap, false)) {
                    vehicleDao.save(getVehicleFromMap(requestMap, false));
                    return CafeUtils.getResponseEntity("Vehicle Added Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateVehicleMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("vehicleName") && requestMap.containsKey("vehicleModel")) {
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId) {
                return true;
            }
        }
        return false;
    }

    private Vehicle getVehicleFromMap(Map<String, String> requestMap, boolean isAdd) {
        Vehicle vehicle = new Vehicle();
        if (isAdd) {
            vehicle.setId(Integer.parseInt(requestMap.get("id")));
        }
        vehicle.setVehicleName(requestMap.get("vehicleName"));
        vehicle.setVehicleModel(requestMap.get("vehicleModel"));
        vehicle.setVehicleType(requestMap.get("vehicleType"));
        vehicle.setVehicleCondition(requestMap.get("vehicleCondition"));
        vehicle.setOwnerName(requestMap.get("ownerName"));
        return vehicle;
    }

    @Override
    public ResponseEntity<List<VehicleWrapper>> getAllVehicle() {
        try {
            return new ResponseEntity<>(vehicleDao.getAllVehicle(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateVehicle(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                if (validateVehicleMap(requestMap, true)) {
                    Optional<Vehicle> optional = vehicleDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()) {
                        Vehicle vehicle = getVehicleFromMap(requestMap, true);
                        vehicleDao.save(vehicle);
                        return CafeUtils.getResponseEntity("Vehicle Updated Successfully.", HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Vehicle ID Does Not Exist.", HttpStatus.OK);
                    }
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteVehicle(Integer id) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional optional = vehicleDao.findById(id);
                if (!optional.isEmpty()) {
                    vehicleDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Vehicle Deleted Successfully.", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Vehicle ID Does Not Exist.", HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}