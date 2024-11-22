package com.cafe.management.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class VehicleWrapper {

    Integer id;

    String vehicleName;

    String vehicleModel;

    String vehicleType;

    String vehicleCondition;

    String ownerName;

    public VehicleWrapper() {

    }

    public VehicleWrapper(Integer id, String vehicleName, String vehicleModel, String vehicleType, String vehicleCondition, String ownerName) {
        this.id = id;
        this.vehicleName = vehicleName;
        this.vehicleModel = vehicleModel;
        this.vehicleType = vehicleType;
        this.vehicleCondition = vehicleCondition;
        this.ownerName = ownerName;
    }

    public VehicleWrapper(Integer id, String vehicleName) {
        this.id = id;
        this.vehicleName = vehicleName;

    }

    public VehicleWrapper(Integer id, String vehicleName, String vehicleCondition, String vehicleType) {

        this.id = id;
        this.vehicleCondition = vehicleCondition;
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
    }



}
