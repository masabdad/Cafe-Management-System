package com.cafe.management.POJO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private LocalTime startTime;
    private LocalTime endTime;
    private int numberOfGuests;
    private String customerName;
    private String customerContact;
    private int tableNumber;
    private String status;


    public int getNumberOfPeople() {
        return numberOfGuests;
    }


}
