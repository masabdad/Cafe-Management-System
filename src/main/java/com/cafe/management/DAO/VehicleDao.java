package com.cafe.management.DAO;

import com.cafe.management.POJO.Vehicle;
import com.cafe.management.wrapper.UserWrapper;
import com.cafe.management.wrapper.VehicleWrapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleDao extends JpaRepository<Vehicle, Integer> {
    List<VehicleWrapper> getAllVehicle();
}
