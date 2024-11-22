package com.cafe.management.POJO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@NamedQuery(
        name = "Vehicle.updateVehicle",
        query = "UPDATE Vehicle v SET v.vehicleName = :vehicleName, v.vehicleType = :vehicleType, v.vehicleModel = :vehicleModel, v.vehicleCondition = :condition, v.ownerName = :ownerName WHERE v.id = :id"
)
@NamedQuery(
        name = "Vehicle.getAllVehicle",
        query = "SELECT NEW com.cafe.management.wrapper.VehicleWrapper(v.id, v.vehicleName, v.vehicleModel, v.vehicleType, v.vehicleCondition, v.ownerName) FROM Vehicle v"
)
@NamedQuery(
        name = "Vehicle.deleteVehicle",
        query = "DELETE FROM Vehicle v WHERE v.id = :id"
)


@Entity
@Data
@Table(name = "vehicle")
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
public class Vehicle implements Serializable {

    private static final long serialVersionUId = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "vehicleName")
    private String vehicleName;

    @Column(name = "vehicleType")
    private String vehicleType;

    @Column(name = "vehicleModel")
    private String vehicleModel;

    @Column(name = "vehicleCondition")
    private String vehicleCondition;

    @Column(name = "ownerName")
    private String ownerName;

}
