package com.cafe.management.serviceImpl;


import com.cafe.management.DAO.ReservationDao;
import com.cafe.management.JWT.JwtFilter;
import com.cafe.management.POJO.Reservation;
import com.cafe.management.constants.CafeConstants;
import com.cafe.management.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReservationService {
    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    JwtFilter jwtFilter;

    public ResponseEntity<String> createReservation(Reservation reservation) {
        try {
            int totalTables = 8;
            if (reservation.getTableNumber() < 1 || reservation.getTableNumber() > totalTables) {
                return CafeUtils.getResponseEntity("Invalid table number. Please choose a valid table.", HttpStatus.BAD_REQUEST);
            }

            int tableCapacity = 10;
            int totalCapacity = tableCapacity * totalTables;
            LocalDateTime requestedStartTime = LocalDateTime.of(reservation.getDate(), reservation.getTime());
            LocalDateTime requestedEndTime = requestedStartTime.plusMinutes(30);
            List<Reservation> existingReservations = reservationDao.findByDateAndTableNumber(reservation.getDate(), reservation.getTableNumber());
            for (Reservation existingReservation : existingReservations) {
                LocalDateTime existingStartTime = LocalDateTime.of(existingReservation.getDate(), existingReservation.getTime());
                LocalDateTime existingEndTime = existingStartTime.plusMinutes(30);
                if (requestedStartTime.isBefore(existingEndTime) && requestedEndTime.isAfter(existingStartTime)) {
                    return CafeUtils.getResponseEntity("Table is not available within 30 minutes of the previous reservation.", HttpStatus.BAD_REQUEST);
                }
            }

            int totalReservedCapacity = existingReservations.stream()
                    .mapToInt(existingReservation -> existingReservation.getNumberOfPeople())
                    .sum();
            int remainingCapacity = tableCapacity - totalReservedCapacity;
            if (reservation.getNumberOfPeople() > remainingCapacity) {
                return CafeUtils.getResponseEntity("Table does not have enough space for this reservation.", HttpStatus.BAD_REQUEST);
            }

            reservation.setStatus("RESERVED");
            reservationDao.save(reservation);
            return CafeUtils.getResponseEntity("Reservation Created Successfully", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return CafeUtils.getResponseEntity("An error occurred while creating the reservation.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    public List<Reservation> getReservations() {
        return reservationDao.findAll();
    }

    public Reservation updateReservation(Long id, Reservation reservation) throws Exception {
        Reservation existingReservation = reservationDao.findById(id).orElseThrow(() -> new Exception("Reservation not found"));
        LocalDateTime currentDateTime = LocalDateTime.now();
        existingReservation.setDate(currentDateTime.toLocalDate());
        existingReservation.setTime(currentDateTime.toLocalTime());
        existingReservation.setNumberOfGuests(reservation.getNumberOfGuests());
        existingReservation.setCustomerName(reservation.getCustomerName());
        existingReservation.setCustomerContact(reservation.getCustomerContact());
        return reservationDao.save(existingReservation);
    }

    public void cancelReservation(Long id) {
        reservationDao.deleteById(id);
    }

    public List<Reservation> getAvailableTables() {
        return reservationDao.findByStatus("AVAILABLE");
    }

    public ResponseEntity<String> eReserveTable(Reservation reservation) {
        try {
            if (jwtFilter.isEmployee()) {
                List<Reservation> existingReservations = reservationDao.findByDateAndTimeAndTableNumber(reservation.getDate(), reservation.getTime(), reservation.getTableNumber());
                if (!existingReservations.isEmpty()) {
                    return CafeUtils.getResponseEntity("Table is not available for the given Time", HttpStatus.BAD_REQUEST);
                } else {
                    reservation.setStatus("RESERVED");
                    reservationDao.save(reservation);
                    return CafeUtils.getResponseEntity("Reservation Confirmed Successfully", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity("You are not an Employee", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<List<Reservation>> getReservationsEmployee() {
        try {
            if (jwtFilter.isEmployee()) {
                return new ResponseEntity<>(reservationDao.findByStatus("AVAILABLE"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
