package com.cafe.management.DAO;

import com.cafe.management.POJO.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ReservationDao extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateAndTimeAndTableNumber(LocalDate date, LocalTime time, int tableNumber);


    List<Reservation> findByStatus(String available);

    List<Reservation> findByDateAndTableNumber(LocalDate date, int tableNumber);
}

